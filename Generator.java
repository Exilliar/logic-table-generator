import java.util.*;

class Generator
{
    public static void main(String[] args)
    {
        String[] accepted = new String[]{"¬","v","^","->","(",")"}; // All the accepted operators and brackets
        String[] banned = new String[]{"v"}; // Values that are banned (e.g. they are used as expressions). Not used yet

        String expression = getString("Please enter the expression"); // Get the expression from the user

        if (!validExpression(expression, accepted))
        {
            System.out.println("Invalid expression");
            return; // Will end the program if the expression is invalid
        }
        else
        {
            System.out.println("Expression valid");
        }

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

        fillTable(table, numCols-1);

        printTable(table, numRows);
    }

    public static void fillTable(String[][] table, int numCols) // Calculate the expression and fill the table array
    {
        String[] binary = new String[numCols];

        Arrays.fill(binary, "0");

        for (int r = 1; r < table.length; r++)
        {
            addToRow(table,r,binary);

            incBinary(binary);
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

            if (!arrayContains(current, accepted)) // Check that current is not an operator
            {
                if (!values.contains(current))
                {
                    values.add(current);
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