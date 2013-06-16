package Unload;

import checkThreads.checkThreads;
import io.output.FileWriterWithEncoding;
import java.io.*;
import java.nio.charset.Charset;
import java.sql.*;
import java.util.ArrayList;

// Referenced classes of package Unload:
//            MyThread

public class UnloadTable
{

    private static int i = 1;
    private static Connection conn;
    private static int totalvalues = 0;
    private static ArrayList filename = new ArrayList();
    private static ArrayList delim = new ArrayList();
    private static ArrayList rowsep = new ArrayList();
    private static ArrayList characterset = new ArrayList();
    private static ArrayList fetchsize = new ArrayList();
    private static ArrayList header = new ArrayList();
    private static ArrayList rs = new ArrayList();
    private static MyThread mt;
    private static String prop[];
    private static Statement stmt;
    private static ResultSet rs1;
    private static FileWriterWithEncoding writer;
    private static String finalFileName;
    private static String finalSQL;

    public UnloadTable()
    {
    }

    public static String[] property(Connection conn1, String delimiter1, String RowSep1, String CharacterSet1, String FetchSize1, String Header1)
    {
        prop = new String[5];
        conn = conn1;
        if(CharacterSet1 == null)
        {
            CharacterSet1 = "UTF-8";
        }
        if(FetchSize1 == null)
        {
            FetchSize1 = "1000";
        }
        if(RowSep1 == null)
        {
            RowSep1 = "\n";
        }
        prop[0] = delimiter1;
        prop[1] = RowSep1;
        prop[2] = CharacterSet1;
        prop[3] = FetchSize1;
        prop[4] = Header1;
        return prop;
    }

    public static void ProcessUnloading(String fileName1, String sql1, String prop[], String cmndonsrc)
        throws Exception
    {
        if(cmndonsrc.length() > 0)
        {
            filename = new ArrayList();
            delim = new ArrayList();
            rowsep = new ArrayList();
            characterset = new ArrayList();
            fetchsize = new ArrayList();
            header = new ArrayList();
            rs = new ArrayList();
            Statement st = conn.createStatement();
            ResultSet rssrc = st.executeQuery(cmndonsrc);
            ResultSetMetaData rsmeta = rssrc.getMetaData();
            int colcnt = rsmeta.getColumnCount();
            while(rssrc.next()) 
            {
                finalFileName = "";
                if(fileName1.indexOf("#") >= 0)
                {
                    for(int i = 0; i < colcnt; i++)
                    {
                        if(fileName1.indexOf((new StringBuilder("#")).append(rsmeta.getColumnName(i + 1)).toString()) >= 0)
                        {
                            finalFileName = fileName1.replace((new StringBuilder("#")).append(rsmeta.getColumnName(i + 1)).toString(), rssrc.getString(i + 1));
                        }
                    }

                }
                filename.add(finalFileName);
                delim.add(prop[0]);
                rowsep.add(prop[1]);
                characterset.add(prop[2]);
                fetchsize.add(prop[3]);
                header.add(prop[4]);
                stmt = conn.createStatement();
                finalSQL = "";
                finalSQL = sql1;
                if(sql1.indexOf("#") > 0)
                {
                    for(int i = 0; i < colcnt; i++)
                    {
                        if(sql1.indexOf((new StringBuilder("#")).append(rsmeta.getColumnName(i + 1)).toString()) >= 0)
                        {
                            finalSQL = finalSQL.replace((new StringBuilder("#")).append(rsmeta.getColumnName(i + 1)).toString(), rssrc.getString(i + 1));
                        }
                    }

                }
                rs1 = stmt.executeQuery(finalSQL);
                rs.add(rs1);
                totalvalues = filename.size();
            }
        } else
        {
            if(i == 1)
            {
                filename = new ArrayList();
                delim = new ArrayList();
                rowsep = new ArrayList();
                characterset = new ArrayList();
                fetchsize = new ArrayList();
                header = new ArrayList();
                rs = new ArrayList();
            }
            i++;
            filename.add(fileName1);
            delim.add(prop[0]);
            rowsep.add(prop[1]);
            characterset.add(prop[2]);
            fetchsize.add(prop[3]);
            header.add(prop[4]);
            stmt = conn.createStatement();
            rs1 = stmt.executeQuery(sql1);
            rs.add(rs1);
            totalvalues = filename.size();
        }
    }

    public static void StartUnloadingParallel(int SessNo)
        throws IOException, InterruptedException, SQLException
    {
        System.out.println((new StringBuilder("Unloading Files for Session - ")).append(SessNo).append(" is Started").toString());
        for(int i = 0; i < totalvalues; i++)
        {
            int val = i + 1;
            writer = new FileWriterWithEncoding(new File((String)filename.get(i)), Charset.forName((String)characterset.get(i)));
            mt = new MyThread((new StringBuilder("Unload")).append(SessNo).append(val).toString(), writer, (ResultSet)rs.get(i), val, (String)fetchsize.get(i), (String)rowsep.get(i), (String)delim.get(i), (String)header.get(i), (String)filename.get(i));
            mt.setName((new StringBuilder("Unload")).append(SessNo).append(val).toString());
            mt.start();
            while(checkThreads.check_unload_completion((new StringBuilder("Unload")).append(SessNo).toString(), "N") > 3) ;
        }

        checkThreads.check_unload_completion((new StringBuilder("Unload")).append(SessNo).toString(), "Y");
        System.out.println((new StringBuilder("Unloading Files for Session - ")).append(SessNo).append(" is Completed").toString());
        System.out.println("Process Completed");
        stmt.close();
        filename = new ArrayList();
        delim = new ArrayList();
        rowsep = new ArrayList();
        characterset = new ArrayList();
        fetchsize = new ArrayList();
        header = new ArrayList();
        rs = new ArrayList();
    }

    public static void StartUnloading(int SessNo)
        throws IOException, InterruptedException, SQLException
    {
        System.out.println((new StringBuilder("Unloading Files for Session - ")).append(SessNo).append(" is Started").toString());
        for(int i = 0; i < totalvalues; i++)
        {
            int val = i + 1;
            writer = new FileWriterWithEncoding(new File((String)filename.get(i)), Charset.forName((String)characterset.get(i)));
            mt = new MyThread((new StringBuilder("Unload")).append(SessNo).append(val).toString(), writer, (ResultSet)rs.get(i), val, (String)fetchsize.get(i), (String)rowsep.get(i), (String)delim.get(i), (String)header.get(i), (String)filename.get(i));
            mt.setName((new StringBuilder("Unload")).append(SessNo).append(val).toString());
            mt.start();
            mt.join();
        }

        System.out.println((new StringBuilder("Unloading Files for Session - ")).append(SessNo).append(" is Completed").toString());
        System.out.println("Process Completed");
        stmt.close();
        filename = new ArrayList();
        delim = new ArrayList();
        rowsep = new ArrayList();
        characterset = new ArrayList();
        fetchsize = new ArrayList();
        header = new ArrayList();
        rs = new ArrayList();
    }

    public static void StartUnloadingAppend(int SessNo)
        throws IOException, InterruptedException, SQLException
    {
        System.out.println((new StringBuilder("Unloading Files for Session - ")).append(SessNo).append(" is Started").toString());
        for(int i = 0; i < totalvalues; i++)
        {
            int val = i + 1;
            writer = new FileWriterWithEncoding(new File((String)filename.get(i)), Charset.forName((String)characterset.get(i)), true);
            mt = new MyThread((new StringBuilder("Unload")).append(SessNo).append(val).toString(), writer, (ResultSet)rs.get(i), val, (String)fetchsize.get(i), (String)rowsep.get(i), (String)delim.get(i), (String)header.get(i), (String)filename.get(i));
            mt.setName((new StringBuilder("Unload")).append(SessNo).append(val).toString());
            mt.start();
            mt.join();
        }

        System.out.println((new StringBuilder("Unloading Files for Session - ")).append(SessNo).append(" is Completed").toString());
        System.out.println("Process Completed");
        stmt.close();
        filename = new ArrayList();
        delim = new ArrayList();
        rowsep = new ArrayList();
        characterset = new ArrayList();
        fetchsize = new ArrayList();
        header = new ArrayList();
        rs = new ArrayList();
    }

}
