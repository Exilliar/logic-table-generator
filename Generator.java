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

        System.out.println(values);
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