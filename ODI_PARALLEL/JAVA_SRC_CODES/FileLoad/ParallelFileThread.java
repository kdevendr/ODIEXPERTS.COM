package FileLoad;

import checkThreads.CatchException;
import io.FileUtils;
import io.LineIterator;
import java.io.*;
import java.sql.*;
import java.util.Date;

// Referenced classes of package FileLoad:
//            HandleData

class ParallelFileThread extends Thread
{

    private static String Line;
    private static String delimiter;
    private static String data;
    private LineIterator it;
    private int i;
    private int Heading;
    private int position;
    private int commitSize;
    private int val;
    private String Datacol[];
    private String pos[];
    private String file[];
    private PreparedStatement ps[];
    private Connection conn;
    private String textDelimiter;
    private StringWriter errors;
    private String errorMsg[];
    private int SessNo;
    private String ReplaceCharacter;
    private int RowNo[];

    ParallelFileThread(String name1, Connection conn1, String file1, String targetSQL, int commitSize1, int Heading1, String pos1[], 
            String delimiter1, int val1, String textDelimiter1, int sessNo1, String ReplaceCharacter1)
        throws SQLException
    {
        val = val1;
        conn = conn1;
        file = new String[val + 1];
        file[val] = file1;
        ps = new PreparedStatement[val + 1];
        ps[val] = conn1.prepareStatement(targetSQL);
        errorMsg = new String[val + 1];
        RowNo = new int[val + 1];
        RowNo[val] = 0;
        errorMsg[val] = "";
        commitSize = commitSize1;
        Heading = Heading1;
        pos = pos1;
        delimiter = delimiter1;
        textDelimiter = textDelimiter1;
        SessNo = sessNo1;
        ReplaceCharacter = ReplaceCharacter1;
    }

    public void run()
    {
        Date date = new Date();
        System.out.println((new StringBuilder("Loading File ")).append(file[val]).append(" Started at ").append(date).toString());
        try
        {
            it = FileUtils.lineIterator(new File(file[val]), "UTF-8");
        }
        catch(IOException e)
        {
            errors = new StringWriter();
            e.printStackTrace(new PrintWriter(errors));
            CatchException.StoreExcept((new StringBuilder("File ")).append(file[val]).append(" threw the following Exception \n").append(errors.toString()).toString(), SessNo);
            e.printStackTrace();
        }
        int rowNo = 0;
        while(it.hasNext()) 
        {
            i++;
            Line = it.nextLine();
            if(i <= Heading)
            {
                continue;
            }
            RowNo[val]++;
            Datacol = Line.split(delimiter, -1);
            try
            {
                ps[val].setString(1, file[val]);
                ps[val].setInt(2, RowNo[val]);
            }
            catch(SQLException e1)
            {
                e1.printStackTrace();
            }
            for(int j = 0; j < pos.length; j++)
            {
                position = Integer.parseInt(pos[j]) - 1;
                data = HandleData.TrimData(Datacol[position], textDelimiter, ReplaceCharacter);
                try
                {
                    ps[val].setObject(j + 3, data);
                    continue;
                }
                catch(SQLException e)
                {
                    errors = new StringWriter();
                    e.printStackTrace(new PrintWriter(errors));
                    CatchException.StoreExcept((new StringBuilder("File ")).append(file[val]).append(" threw the following Exception \n").append(errors.toString()).toString(), SessNo);
                }
                break;
            }

            try
            {
                ps[val].addBatch();
            }
            catch(SQLException e)
            {
                errors = new StringWriter();
                e.printStackTrace(new PrintWriter(errors));
                CatchException.StoreExcept((new StringBuilder("File ")).append(file[val]).append(" threw the following Exception \n").append(errors.toString()).toString(), SessNo);
                break;
            }
            if(++rowNo != commitSize)
            {
                continue;
            }
            rowNo = 0;
            try
            {
                ps[val].executeBatch();
                conn.commit();
                continue;
            }
            catch(BatchUpdateException e)
            {
                errors = new StringWriter();
                e.printStackTrace(new PrintWriter(errors));
                CatchException.StoreExcept((new StringBuilder("File ")).append(file[val]).append(" threw the following Exception \n").append(errors.toString()).toString(), SessNo);
            }
            catch(SQLException e)
            {
                errors = new StringWriter();
                e.printStackTrace(new PrintWriter(errors));
                CatchException.StoreExcept((new StringBuilder("File ")).append(file[val]).append(" threw the following Exception \n").append(errors.toString()).toString(), SessNo);
            }
            break;
        }
        try
        {
            ps[val].executeBatch();
            conn.commit();
            ps[val].close();
        }
        catch(SQLException e)
        {
            errors = new StringWriter();
            e.printStackTrace(new PrintWriter(errors));
            CatchException.StoreExcept((new StringBuilder("File ")).append(file[val]).append(" threw the following Exception \n").append(errors.toString()).toString(), SessNo);
            e.printStackTrace();
        }
        it.close();
        date = new Date();
        System.out.println((new StringBuilder("Loading File ")).append(file[val]).append(" completed at ").append(date).toString());
    }
}
