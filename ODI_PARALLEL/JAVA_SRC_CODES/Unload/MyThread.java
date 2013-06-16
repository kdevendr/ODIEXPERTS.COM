package Unload;

import io.output.FileWriterWithEncoding;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.*;
import java.util.Date;

class MyThread extends Thread
{

    private FileWriterWithEncoding write[];
    private String delimiter;
    private String RowSep;
    private int FetchSize;
    private int val;
    private ResultSet rs;
    private String header;
    private Date date;
    private String filename;

    MyThread(String name1, FileWriterWithEncoding writer1, ResultSet rs1, int val1, String FetchSize1, String RowSep1, String delimiter1, 
            String header1, String filename1)
    {
        val = val1;
        write = new FileWriterWithEncoding[val + 1];
        write[val] = writer1;
        FetchSize = Integer.parseInt(FetchSize1);
        RowSep = RowSep1;
        delimiter = delimiter1;
        rs = rs1;
        header = header1;
        filename = filename1;
    }

    public void run()
    {
        date = new Date();
        System.out.println((new StringBuilder("Unloading - ")).append(filename).append(" started at ").append(date).toString());
        try
        {
            rs.setFetchSize(FetchSize);
            if(header.equals("1"))
            {
                write[val].write(getHeader(rs, delimiter, RowSep, val));
            }
            for(; rs.next(); write[val].write(getData(rs, delimiter, RowSep, val))) { }
            write[val].flush();
            write[val].close();
            rs.close();
            date = new Date();
            System.out.println((new StringBuilder("Unloading - ")).append(filename).append(" completed at ").append(date).toString());
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }

    public static String getHeader(ResultSet rs, String delimiter, String RowSep, int total)
        throws SQLException
    {
        String header = "";
        ResultSetMetaData md = rs.getMetaData();
        int t[] = new int[total + 1];
        for(t[total] = 0; t[total] < md.getColumnCount(); t[total]++)
        {
            header = (new StringBuilder(String.valueOf(header))).append(md.getColumnName(t[total] + 1)).append(delimiter).toString();
        }

        if(delimiter.length() > 0)
        {
            header = (new StringBuilder(String.valueOf(header.substring(0, header.length() - 1)))).append(RowSep).toString();
        } else
        {
            header = (new StringBuilder(String.valueOf(header))).append(RowSep).toString();
        }
        return header;
    }

    public static String getData(ResultSet rs, String delimiter, String RowSep, int total)
        throws SQLException, IOException
    {
        String value = "";
        String singlecol = "";
        ResultSetMetaData md = rs.getMetaData();
        int t[] = new int[total + 1];
        for(t[total] = 0; t[total] < md.getColumnCount(); t[total]++)
        {
            singlecol = rs.getString(t[total] + 1);
            if(singlecol != null)
            {
                value = (new StringBuilder(String.valueOf(value))).append(singlecol).append(delimiter).toString();
            } else
            {
                value = (new StringBuilder(String.valueOf(value))).append(delimiter).toString();
            }
        }

        if(delimiter.length() > 0)
        {
            value = (new StringBuilder(String.valueOf(value.substring(0, value.length() - 1)))).append(RowSep).toString();
        } else
        {
            value = (new StringBuilder(String.valueOf(value))).append(RowSep).toString();
        }
        return value;
    }
}
