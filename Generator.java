import java.util.*;

class Generator
{
    public static void main(String[] args)
    {
        String[] multiChar = new String[]{"->"}; // All the operators that contain multiple charaters

        Expression expression;

        if (args.length > 0) expression = new Expression(args[0]);
        else expression = new Expression(getString("Please enter an expression"));

        String[] expressionArr = expression.getExpArr();
        ArrayList<String> values = expression.getValues();

        int numRows = (int)Math.pow(2,values.size())+1;
        int numCols = values.size()+1;

        Table table = new Table(numCols, numRows-1, findHeaders(values));

        calcTable(table, expressionArr, values);

        table.printTable();
    }

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
        ValuesData valuesData = new ValuesData(values.size());

        String[] exp = copyStringArr(expressionArr); // Copying the expression as changing the values in expressionArr here will change it everywhere (yey stack and heap)

        fillValuesData(valuesData, row, values);

        placeValues(exp, valuesData); // Replace the letter values with the check values in this row

        calcExp(exp, 0, exp.length-1);

        return findResult(exp);
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

    public static boolean findResult(String[] exp)
    {
        for (int i = 0; i < exp.length; i++)
        {
            if (exp[i].equals("true") || exp[i].equals("false")) return Boolean.parseBoolean(exp[i]);
        }

        System.out.println("Error");

        return false;
    }

    public static void calcExp(String[] exp, int start, int end) // Actually run the calculation, needs to be a separate function as it may be recursive (due to brackets)
    {
        for (int i = start; i < end; i++)
        {
            if (!exp[i].equals(""))
            {
                if (!exp[i].equals("false") && !exp[i].equals("true"))
                {
                    if (exp[i].equals("("))
                    {
                        handleBrackets(exp, i);
                    }
                    else if (exp[i+1].equals("("))
                    {
                        handleBrackets(exp, i+1);

                        defaultCalc(exp, i);
                    }
                    else
                    {
                        defaultCalc(exp, i);
                    }
                }
            }
        }
    }

    public static void handleBrackets(String[] exp, int i)
    {
        exp[i] = "";

        calcExp(exp, i+1, findCloseBrackets(exp, i));
    }

    public static void defaultCalc(String[] exp, int i)
    {
        if (exp[i+1].equals("~") && exp[i+2].equals("(")) handleBrackets(exp,i+2);

        boolean prevVal = findPrevVal(exp, i);
        boolean nextVal = exp[i+1].equals("~") ? !findNextVal(exp, i) : findNextVal(exp,i);

        if (exp[i+1].equals("~")) exp[i+1] = "";

        switch(exp[i])
        {
            case "v": exp[i] = String.valueOf(prevVal || nextVal); break; // Or
            case "^": exp[i] = String.valueOf(prevVal && nextVal); break; // And
            case "~": exp[i] = String.valueOf(!nextVal); break; // Not
            case "->": exp[i] = String.valueOf(!prevVal || nextVal); break; // Implies
            default: System.out.println("unrecognised symbol: " + exp[i]); break;
        }
    }

    public static boolean findPrevVal(String[] exp, int start)
    {
        for (int i = start; i >= 0; i--)
        {
            if (exp[i].equals("true") || exp[i].equals("false"))
            {
                boolean value = Boolean.parseBoolean(exp[i]);

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
            if (exp[i].equals("true") || exp[i].equals("false"))
            {
                boolean value = Boolean.parseBoolean(exp[i]);

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
            if (valuesData.getValue(i).equals(value)) return String.valueOf(valuesData.getData(i));
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
        }
    }

    public static String getString(String msg)
    {
        Scanner s = new Scanner(System.in);

        System.out.println(msg);

        String res = s.nextLine();

        s.close();

        return res;
    }
}