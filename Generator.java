import java.util.*;

class Generator
{
    public static void main(String[] args)
    {
        String[] accepted = new String[]{"¬","v","^","->","(",")"}; // All the accepted operators and brackets
        String[] banned = new String[]{"v"}; // Values that are banned (e.g. they are used as expressions). Not used yet

        String expression = getString("Please enter the expression"); // Get the expression from the user
        expression.replaceAll("\\s+",""); // Remove all spaces, makes calculating the expression easier later

        if (!validExpression(expression, accepted))
        {
            System.out.println("Invalid expression");
            return; // Will end the program if the expression is invalid
        }
        else
        {
            System.out.println("Expression valid");
        }

        String[] expressionArr = splitExpression(expression);

        System.out.println(Arrays.toString(expressionArr));

        ArrayList<String> values = findValues(expression, accepted); // Fill arraylist with all values in expression

        if (values.size() == 0)
        {
            System.out.println("No values in expression");
            return;
        }

        int numRows = (int)Math.round(findNumRows(values.size()-1, 0) + 2);
        int numCols = values.size()+1;

        String[][] table = new String[numRows][numCols];

        addHeaders(table,values);

        fillTable(table, numCols-1, expressionArr, values);

        printTable(table, numRows);
    }

    public static String[] splitExpression(String expression)
    {
        if (!expression.contains("->"))
        {
            String[] expArr = new String[expression.length()];

            for (int i = 0; i < expression.length(); i++)
            {
                expArr[i] = Character.toString(expression.charAt(i));
            }

            return expArr;
        }
        else
        {
            int numImplies = findNumImplies(expression); // Need to find the number of implies statements as it takes 2 chars rather than the usual 1

            String[] expArr = new String[expression.length()-numImplies];

            int arrI = 0;

            for (int i = 0; i < expression.length(); i++)
            {
                if (expression.charAt(i) != ">".charAt(0)) // If current value is not the second half of an implies
                {
                    if (expression.charAt(i) == "-".charAt(0))
                    {
                        expArr[arrI] = Character.toString(expression.charAt(i)) + Character.toString(expression.charAt(i+1));
                    }
                    else
                    {
                        expArr[arrI] = Character.toString(expression.charAt(i));
                    }

                    arrI++;
                }
            }

            return expArr;
        }
    }

    public static int findNumImplies(String exp)
    {
        int num = 0;

        for (int i = 0; i < exp.length(); i++)
        {
            Character current = exp.charAt(i);

            if (current == "-".charAt(0)) num++;
        }

        return num;
    }

    public static void fillTable(String[][] table, int numCols, String[] expressionArr, ArrayList<String> values) // Calculate the expression and fill the table array
    {
        String[] binary = new String[numCols];

        Arrays.fill(binary, "0");

        for (int r = 1; r < table.length; r++)
        {
            addToRow(table,r,binary);

            String result = calcResult(table[r], expressionArr, values);

            table[r][numCols] = result;

            incBinary(binary);
        }
    }

    public static String calcResult(String[] row, String[] expressionArr, ArrayList<String> values)
    {
        String result = "";

        String[][] valuesData = new String[values.size()][2];

        String[] exp = copyStringArr(expressionArr); // Copying the expression as changing the values in expressionArr here will change it everywhere (yey stack and heap)

        fillValuesData(valuesData, row, values);

        // System.out.println(Arrays.deepToString(valuesData));

        placeValues(exp, valuesData); // Replace the letter values with the check values in this row

        System.out.println(Arrays.toString(exp));

        calcExp(exp, 0, exp.length-1);

        System.out.println("Finished exp: " + Arrays.toString(exp));

        result = findResult(exp);

        return result;
    }

    public static String findResult(String[] exp)
    {
        for (int i = 0; i < exp.length; i++)
        {
            if (exp[i].equals("1") || exp[i].equals("0")) return exp[i];
        }

        return "Error";
    }

    public static void calcExp(String[] exp, int start, int end) // Actually run the calculation, needs to be a separate function as it may be recursive (due to brackets)
    {
        for (int i = start; i < end; i++)
        {
            if (!exp[i].equals(""))
            {
                if (!exp[i].equals("1") && !exp[i].equals("0"))
                {
                    if (exp[i+1].equals("("))
                    {
                        exp[i+1] = "";
                        calcExp(exp, i+1, findCloseBrackets(exp, i+1));
                    }

                    boolean prevVal = findPrevVal(exp, i);
                    boolean nextVal = findNextVal(exp, i);

                    System.out.println("preVal: " + prevVal + " nextVal: " + nextVal);

                    switch(exp[i])
                    {
                        case "v": exp[i] = boolToString(prevVal || nextVal); System.out.println("or"); break;
                        case "^": exp[i] = boolToString(prevVal && nextVal); System.out.println("and"); break;
                        case "¬": exp[i+1] = boolToString(!nextVal); System.out.println("not"); break;
                        case "->": exp[i] = boolToString(!prevVal || nextVal); System.out.println("implies"); break;
                        default: System.out.println("unrecognised symbol"); break;
                    }
                }
            }
        }
    }

    public static boolean findPrevVal(String[] exp, int start)
    {
        for (int i = start; i >= 0; i--)
        {
            if (exp[i].equals("1") || exp[i].equals("0"))
            {
                boolean value = stringToBool(exp[i]);

                exp[i] = "";

                return value;
            }
        }

        return false;
    }

    public static boolean findNextVal(String[] exp, int start)
    {
        for (int i = start; i < exp.length; i++)
        {
            if (exp[i].equals("1") || exp[i].equals("0"))
            {
                boolean value = stringToBool(exp[i]);

                exp[i] = "";

                return value;
            }
        }

        return false;
    }

    public static int findCloseBrackets(String[] exp, int start)
    {
        int open = 0; // increments if another open brackets is found (will change which close brackets needs to be found)

        for (int i = start; i < exp.length; i++)
        {
            if (exp[i].equals(")") && open == 0)
            {
                exp[i] = "";
                return i;
            }
            else if (exp[i].equals(")")) open--;
            else if (exp[i].equals("(")) open++;
        }

        return -1;
    }

    public static void placeValues(String[] exp, String[][] valuesData)
    {
        for (int i = 0; i < exp.length; i++)
        {
            if (Character.isLetter(exp[i].charAt(0)) && !exp[i].equals("v")) // Check that the value is a letter and is not 'v'(or symbol)
            {
                exp[i] = getCheckValue(valuesData,exp[i]);
            }
        }
    }

    public static String getCheckValue(String[][] valuesData, String value)
    {
        for (int i = 0; i < valuesData.length; i++)
        {
            if (valuesData[i][0].equals(value))
            {
                return valuesData[i][1];
            }
        }

        return "null";
    }

    public static String[] copyStringArr(String[] arr)
    {
        String[] newArr = new String[arr.length];

        for (int i = 0; i < arr.length; i++)
        {
            newArr[i] = arr[i];
        }

        return newArr;
    }

    public static void fillValuesData(String[][] valuesData, String[] row, ArrayList<String> values)
    {
        for (int v = 0; v < values.size(); v++)
        {
            valuesData[v][0] = values.get(v);
            valuesData[v][1] = row[v];
        }
    }

    public static void addToRow(String[][] table, int r, String[] binary)
    {
        for (int c = 0; c < binary.length; c++)
        {
            table[r][c] = binary[c];
        }
    }


    public static void incBinary(String[] binary) // Increment a binary number by 1
    {
        int len = binary.length;

        if (binary[len-1] == "0")
        {
            binary[len-1] = "1";

            return;
        }

        binary[len-1] = "0";

        for (int i = len-2; i >= 0; i--)
        {
            if (binary[i] == "0")
            {
                binary[i] = "1";

                return;
            }
            else
            {
                binary[i] = "0";
            }
        }

        return;
    }

    public static void printTable(String[][] table, int numRows)
    {
        for (int r = 0; r < numRows; r++)
        {
            String row = "| ";

            for (int c = 0; c < table[1].length; c++)
            {
                row += table[r][c] + " | ";
            }

            System.out.println(row);
        }
    }

    public static void addHeaders(String[][] table, ArrayList<String> values)
    {
        for (int i = 0; i < values.size(); i++)
        {
            table[0][i] = values.get(i);
        }

        table[0][values.size()] = "Result";
    }

    public static double findNumRows(int size, double total) // Finds the number of columns required for the logic table. Will calculate by finding max binary number possible with binary number length "size" + 1 (+1 is done outside of function)
    {
        if (size != 0) return findNumRows(size-1, total + Math.pow(2,size));
        else return total + 1;
    }

    public static boolean stringToBool(String s) // Converts string to boolean
    {
        if (s.equals("1")) return true;
        else return false;
    }

    public static String boolToString(boolean b) // Converts boolean to string
    {
        if (b) return "1";
        else return "0";
    }

    public static ArrayList<String> findValues(String exp, String[] accepted)
    {
        ArrayList<String> values = new ArrayList<String>();

        for (int i = 0; i < exp.length(); i++)
        {
            String current = String.valueOf(exp.charAt(i));

            if (!current.equals("-") && !current.equals(">")) // Check if the value is an implies
            {
                if (!arrayContains(current, accepted)) // Check that current is not an operator
                {
                    if (!values.contains(current))
                    {
                        values.add(current);
                    }
                }
            }
        }

        return values;
    }

    public static String getString(String msg)
    {
        Scanner s = new Scanner(System.in);

        System.out.println(msg);

        return s.nextLine();
    }

    public static boolean validExpression(String exp, String[] accepted)
    {
        boolean wasLetter = false; // Stores whether the last value was a letter (2 letters are not allowed to be next to each other)
        int openBracket = 0; // increases if bracket is opened, decreases if bracket is closed, value should be 0 at the end of the function (bracket has been opened and not closed)

        for (int i = 0; i < exp.length(); i++)
        {
            String current = String.valueOf(exp.charAt(i));

            if (current.equals("-")) current += ">"; // Handle implies (implies takes 2 characters)

            if (!current.equals(">"))
            {
                if (!arrayContains(current, accepted))
                {
                    if (wasLetter) return false;

                    wasLetter = true;

                    if (!Character.isLetter(current.charAt(0)))
                    {
                        return false;
                    }
                }
                else
                {
                    wasLetter = false;

                    openBracket += checkBrackets(current);
                }
            }
        }

        if (openBracket != 0) return false;

        return true;
    }

    public static int checkBrackets(String c)
    {
        if (c.equals("(")) return 1;
        else if (c.equals(")")) return -1;
        else return 0;
    }

    public static boolean arrayContains(String c, String[] arr) // Couldn't find a cleaner way to check if array contains a value
    {
        for (int i = 0; i < arr.length; i++)
        {
            if (c.equals(arr[i]))
            {
                return true;
            }
        }
        return false;
    }
}