package DataLoad;


import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.*;

import checkThreads.CatchException;

class ParallelLoadThread extends Thread
{

    private Connection srcConn;
    private Connection trgtConn;
    private String srcSQL;
    private String trgtSQL;
    private PreparedStatement ps[];
    private String SrcFetchSize;
    private int val;
    private ResultSet srcRS[];
    private int commitSize;
    private StringWriter errors;
    private int sessNo;

    ParallelLoadThread(Connection srcConn1, String srcSQL1, String SrcFetchSize1, Connection trgtConn1, String trgtSQL1, int val1, int commitSize1, 
            int sessNo1)
    {
        srcConn = srcConn1;
        trgtConn = trgtConn1;
        srcSQL = srcSQL1;
        SrcFetchSize = SrcFetchSize1;
        trgtSQL = trgtSQL1;
        val = val1;
        commitSize = commitSize1;
        sessNo = sessNo1;
    }

    public void run()
    {
        try
        {
            Statement srcStmt = srcConn.createStatement();
            srcRS = new ResultSet[val + 1];
            srcRS[val] = srcStmt.executeQuery(srcSQL);
            srcRS[val].setFetchSize(Integer.parseInt(SrcFetchSize));
            ResultSetMetaData srcMD = srcRS[val].getMetaData();
            int col = srcMD.getColumnCount();
            trgtConn.setAutoCommit(false);
            ps = new PreparedStatement[val + 1];
            ps[val] = trgtConn.prepareStatement(trgtSQL);
            int rows = 0;
            while(srcRS[val].next()) 
            {
                for(int i = 0; i < col; i++)
                {
                    ps[val].setObject(i + 1, srcRS[val].getObject(i + 1));
                }

                ps[val].addBatch();
                if(++rows == commitSize)
                {
                    ps[val].executeBatch();
                    trgtConn.commit();
                    rows = 0;
                }
            }
            ps[val].executeBatch();
            trgtConn.commit();
            ps[val].close();
            srcRS[val].close();
            srcStmt.close();
        }
        catch(SQLException e)
        {
            errors = new StringWriter();
            e.printStackTrace(new PrintWriter(errors));
            CatchException.StoreExcept(errors.toString(), sessNo);
        }
    }
}
