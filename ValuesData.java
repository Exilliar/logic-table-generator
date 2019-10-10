public class ValuesData
{
    String[] values;
    boolean[] data;

    public ValuesData(int size)
    {
        values = new String[size];
        data = new boolean[size];
    }

    public String getValue(int i)
    {
        return values[i];
    }

    public boolean getData(int i)
    {
        return data[i];
    }

    public int size()
    {
        return values.length;
    }

    public void setValue(int i, String val)
    {
        values[i] = val;
    }

    public void setData(int i, boolean d)
    {
        data[i] = d;
    }
}