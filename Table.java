import java.util.*;

public class Table
{
    String[] headers;
    boolean[][] values;

    int numValueRows;

    public Table(int numCols, int numRows)
    {
        headers = new String[numCols];
        values = new boolean[numRows][numCols];

        numValueRows = numRows;

        fillValues(values);
    }

    public int getNumValueRows()
    {
        return numValueRows;
    }

    public boolean[] getValuesRow(int i)
    {
        return values[i];
    }

    public void setHeaders(String[] h)
    {
        headers = h;
    }

    public void setResult(boolean b, int i)
    {
        values[i][values[0].length-1] = b;
    }

    public void printTable()
    {
        String row = "| ";

        for (int i = 0; i < headers.length; i++)
        {
            row += headers[i] + " | ";
        }

        System.out.println(row);

        for (int r = 0; r < values.length; r++)
        {
            row = "| ";

            for (int c = 0; c < values[1].length; c++)
            {
                row += boolToString(values[r][c]) + " | ";
            }

            System.out.println(row);
        }
    }

    public static void fillValues(boolean[][] values)
    {
        boolean[] binary = new boolean[values[1].length-1];

        Arrays.fill(binary, false);

        for (int r = 0; r < values.length; r++)
        {
            for (int c = 0; c < binary.length; c++)
            {
                values[r][c] = binary[c];
            }

            incBinary(binary);
        }
    }

    public static void incBinary(boolean[] binary) // Increment a binary number by 1
    {
        int len = binary.length;

        if (!binary[len-1])
        {
            binary[len-1] = true;

            return;
        }

        binary[len-1] = false;

        for (int i = len-2; i >= 0; i--)
        {
            if (!binary[i])
            {
                binary[i] = true;

                return;
            }
            else binary[i] = false;
        }

        return;
    }

    public static String boolToString(boolean b)
    {
        if (b) return "1";
        else return "0";
    }
}