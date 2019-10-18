import java.util.*;

public class Expression
{
    String[] accepted = new String[]{"~","v","^","->","(",")"}; // All the accepted operators and brackets

    String exp;
    String[] expArr;

    ArrayList<String> values;

    public Expression(String e)
    {
        exp = e;

        exp = exp.replaceAll("\\s+",""); // Remove all spaces, makes calculating the expression easier later

        if (validExpression(exp, accepted))
        {
            expArr = splitExpression(exp);

            values = findValues(exp, accepted); // Fill arraylist with all values in expression

            if (values.size() == 0)
            {
                System.out.println("No values in expression");

                System.exit(1);
            }
        }
        else
        {
            System.out.println("Expression invalid");
            System.exit(1);
        }
    }

    public String[] getExpArr()
    {
        return expArr;
    }

    public ArrayList<String> getValues()
    {
        return values;
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
                    if (!values.contains(current)) values.add(current);
                }
            }
        }

        return values;
    }

    public static boolean validExpression(String exp, String[] accepted)
    {
        boolean wasLetter = false; // Stores whether the last value was a letter (2 letters are not allowed to be next to each other)
        int openBracket = 0; // increases if bracket is opened, decreases if bracket is closed, value should be 0 at the end of the function (bracket has been opened and not closed)

        for (int i = 0; i < exp.length(); i++)
        {
            String current = String.valueOf(exp.charAt(i));

            if (current.equals("-")) current += ">"; // Handle implies (implies takes 2 characters)

            if (!current.equals(">") && !current.equals("~"))
            {
                if (!arrayContains(current, accepted))
                {
                    if (wasLetter) return false;

                    wasLetter = true;

                    if (!Character.isLetter(current.charAt(0))) return false;
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
            if (c.equals(arr[i])) return true;
        }
        return false;
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
                    if (expression.charAt(i) == "-".charAt(0)) expArr[arrI] = Character.toString(expression.charAt(i)) + Character.toString(expression.charAt(i+1));
                    else expArr[arrI] = Character.toString(expression.charAt(i));

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
}