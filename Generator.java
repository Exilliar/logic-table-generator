import java.util.*;

class Generator
{
    public static void main(String[] args)
    {
        String[] multiChar = new String[]{"->"}; // All the operators that contain multiple charaters

        Expression testExpression = new Expression(getString("Please enter an expression"));

        String[] expressionArr = testExpression.getExpArr();
        ArrayList<String> values = testExpression.getValues();

        int numRows = (int)Math.round(findNumRows(values.size()-1, 0) + 2);
        int numCols = values.size()+1;

        // String[][] table = new String[numRows][numCols];

        Table table = new Table(numCols, numRows-1);

        String[] headers = findHeaders(values);

        table.setHeaders(headers);

        calcTable(table, expressionArr, values);

        table.printTable();

        // addHeaders(table,values);

        // fillTable(table, numCols-1, expressionArr, values);

        // printTable(table, numRows);
    }

    // public static void fillTable(String[][] table, int numCols, String[] expressionArr, ArrayList<String> values) // Calculate the expression and fill the table array
    // {
    //     String[] binary = new String[numCols];

    //     Arrays.fill(binary, "0");

    //     for (int r = 1; r < table.length; r++)
    //     {
    //         addToRow(table,r,binary);

    //         String result = calcResult(table[r], expressionArr, values);

    //         table[r][numCols] = result;

    //         incBinary(binary);
    //     }
    // }

    public static void calcTable(Table table, String[] expressionArr, ArrayList<String> values)
    {
        for (int r = 0; r < table.getNumValueRows(); r++)
        {
            boolean[] realValues = removeLastIndex(table.getValuesRow(r));

            boolean result = calcResult(realValues, expressionArr, values);

            table.setResult(result, r);
        }
    }

    public static boolean calcResult(boolean[] row, String[] expressionArr, ArrayList<String> values)
    {
        boolean result = false;

        ValuesData valuesData = new ValuesData(values.size());

        // String[][] valuesData = new String[values.size()][2];

        String[] exp = copyStringArr(expressionArr); // Copying the expression as changing the values in expressionArr here will change it everywhere (yey stack and heap)

        fillValuesData(valuesData, row, values);

        placeValues(exp, valuesData); // Replace the letter values with the check values in this row

        calcExp(exp, 0, exp.length-1);

        result = findResult(exp);

        return result;
    }

    public static boolean[] removeLastIndex(boolean[] b)
    {
        boolean[] res = new boolean[b.length-1];

        for (int i = 0; i < res.length; i++)
        {
            res[i] = b[i];
        }

        return res;
    }

    public static String[] findHeaders(ArrayList<String> values)
    {
        String[] h = new String[values.size()+1];

        for (int i = 0; i < values.size(); i++)
        {
            h[i] = values.get(i);
        }

        h[values.size()] = "Result";

        return h;
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

    

    // public static String calcResult(String[] row, String[] expressionArr, ArrayList<String> values)
    // {
    //     String result = "";

    //     String[][] valuesData = new String[values.size()][2];

    //     String[] exp = copyStringArr(expressionArr); // Copying the expression as changing the values in expressionArr here will change it everywhere (yey stack and heap)

    //     fillValuesData(valuesData, row, values);

    //     placeValues(exp, valuesData); // Replace the letter values with the check values in this row

    //     calcExp(exp, 0, exp.length-1);

    //     result = findResult(exp);

    //     return result;
    // }

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

                    switch(exp[i])
                    {
                        case "v": exp[i] = boolToString(prevVal || nextVal); break; // Or
                        case "^": exp[i] = boolToString(prevVal && nextVal); break; // And
                        case "~": exp[i+1] = boolToString(!nextVal); break; // Not
                        case "->": exp[i] = boolToString(!prevVal || nextVal); break; // Implies
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

    public static void placeValues(String[] exp, ValuesData valuesData)
    {
        for (int i = 0; i < exp.length; i++)
        {
            if (Character.isLetter(exp[i].charAt(0)) && !exp[i].equals("v")) exp[i] = getCheckValue(valuesData,exp[i]); // Check that the value is a letter and is not 'v'(or symbol)
        }
    }

    public static String getCheckValue(ValuesData valuesData, String value)
    {
        for (int i = 0; i < valuesData.size(); i++)
        {
            if (valuesData.getValue(i).equals(value)) return boolToString(valuesData.getData(i));
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

    public static void fillValuesData(ValuesData valuesData, boolean[] row, ArrayList<String> values)
    {
        for (int v = 0; v < values.size(); v++)
        {
            valuesData.setValue(v, values.get(v));
            valuesData.setData(v, row[v]);
            // valuesData[v][0] = values.get(v);
            // valuesData[v][1] = row[v];
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
            else binary[i] = "0";
        }

        return;
    }

    // public static void addHeaders(String[][] table, ArrayList<String> values)
    // {
    //     for (int i = 0; i < values.size(); i++)
    //     {
    //         table[0][i] = values.get(i);
    //     }

    //     table[0][values.size()] = "Result";
    // }

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

    public static String getString(String msg)
    {
        Scanner s = new Scanner(System.in);

        System.out.println(msg);

        String res = s.nextLine();

        s.close();

        return res;
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
            if (c.equals(arr[i])) return true;
        }
        return false;
    }
}