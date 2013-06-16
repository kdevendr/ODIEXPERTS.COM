package DataLoad;


import java.sql.Connection;
import java.sql.SQLException;

import checkThreads.CatchException;

// Referenced classes of package DataLoad:
//            ParallelLoadThread

public class LoadDataParallel
{

    private static ParallelLoadThread mt;

    public LoadDataParallel()
    {
    }

    public static void load(Connection srcConn, String srcSQL, String SrcFetchSize, Connection trgtConn, String trgtSQL, int val, int sessNo, int commitSize)
        throws SQLException, InterruptedException
    {
        CatchException.initialize(sessNo);
        mt = new ParallelLoadThread(srcConn, srcSQL, SrcFetchSize, trgtConn, trgtSQL, val, commitSize, sessNo);
        mt.setName((new StringBuilder("Load")).append(sessNo).toString());
        mt.start();
    }
}
