package FileLoad;

import api.getInfo;
import checkThreads.CatchException;
import checkThreads.checkThreads;
import java.io.File;
import java.io.PrintStream;
import java.sql.Connection;

// Referenced classes of package FileLoad:
//            ParallelFileThread

public class LoadParallel
{

    private static String pos[];
    private static ParallelFileThread mt;
    private static int val = 0;

    public LoadParallel()
    {
    }

    public static void LoadFiles(Connection conn, String Dir, String Mask, String targetSQL, int CommitSize, int Heading, int SessNo, String colpos, 
            String delimiter, String textDelimiter, String ReplaceCharacter)
        throws Exception
    {
        System.out.println((new StringBuilder("File(s) Loading started for Session - ")).append(SessNo).toString());
        CatchException.initialize(SessNo);
        conn.setAutoCommit(false);
        pos = colpos.split(",");
        File files[] = getInfo.getFiles(Dir, Mask);
        for(int i = 0; i < files.length; i++)
        {
            val++;
            System.out.println((new StringBuilder("Loading File ")).append(files[i].toString()).toString());
            mt = new ParallelFileThread((new StringBuilder("FileLoad")).append(SessNo).append(i).toString(), conn, files[i].toString(), targetSQL, CommitSize, Heading, pos, delimiter, val, textDelimiter, SessNo, ReplaceCharacter);
            mt.setName((new StringBuilder("FileLoad")).append(SessNo).append(i).toString());
            mt.start();
            while(checkThreads.check_unload_completion((new StringBuilder("FileLoad")).append(SessNo).toString(), "N") > 3) ;
        }

        checkThreads.check_unload_completion((new StringBuilder("FileLoad")).append(SessNo).toString(), "Y");
        if(CatchException.Result(SessNo).length() > 0)
        {
            throw new Exception(CatchException.Result(SessNo).toString());
        } else
        {
            System.out.println((new StringBuilder("File(s) Loading completed for Session - ")).append(SessNo).toString());
            return;
        }
    }

}
