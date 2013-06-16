package FileLoad;

import java.util.ArrayList;

public class getStatus
{

    public static ArrayList DebugListExcept[];
    public static String DebugOutput[];
    public static ArrayList ParallelListExcept[];
    public static String ParallelLOutput[];

    public getStatus()
    {
    }

    public static void DebugFileInitialize(int sessNo)
    {
        DebugOutput = new String[sessNo + 1];
        DebugListExcept = new ArrayList[sessNo + 1];
        DebugOutput[sessNo] = "";
        DebugListExcept[sessNo] = new ArrayList();
    }

    public static void DebugFileStoreExcept(String Except, int sessNo)
    {
        DebugListExcept[sessNo].add(Except);
    }

    public static String DebugFileResult(int sessNo)
    {       
    	StringBuilder str = new StringBuilder();
        for (int j = 0; j < DebugListExcept[sessNo].size(); j++) {
        	str.append(DebugListExcept[sessNo].get(j));
		}
        DebugOutput[sessNo]  = str.toString();
        return DebugOutput[sessNo];
    }

    public static void ParallelFileInitialize(int sessNo)
    {
        ParallelLOutput = new String[sessNo + 1];
        ParallelListExcept = new ArrayList[sessNo + 1];
        ParallelLOutput[sessNo] = "";
        ParallelListExcept[sessNo] = new ArrayList();
    }

    public static void ParallelFileStoreExcept(String Except, int sessNo)
    {
        ParallelListExcept[sessNo].add(Except);
    }

    public static String ParallelFileResult(int sessNo)
    {
    	StringBuilder str = new StringBuilder();
        for (int j = 0; j < ParallelListExcept[sessNo].size(); j++) {
        	str.append(ParallelListExcept[sessNo].get(j));
		}
        ParallelLOutput[sessNo]  = str.toString();
        return ParallelLOutput[sessNo];
    }
}
