package DataLoad;

import checkThreads.CatchException;

public class check
{

    private static void printGroupInfo(ThreadGroup g, String indent, int sessNo)
        throws Exception
    {
        if(g == null)
        {
            return;
        }
        int numThreads = g.activeCount();
        int numGroups = g.activeGroupCount();
        Thread threads[] = new Thread[numThreads];
        ThreadGroup groups[] = new ThreadGroup[numGroups];
        g.enumerate(threads, false);
        g.enumerate(groups, false);
        for(int i = 0; i < numThreads; i++)
        {
            if(threads[i] != null && threads[i].getName().equals((new StringBuilder("Load")).append(sessNo).toString()))
            {
                if(CatchException.Result(sessNo).length() > 0)
                {
                    throw new Exception(CatchException.Result(sessNo));
                }
                threads[i].join();
            }
        }

        for(int i = 0; i < numGroups; i++)
        {
            printGroupInfo(groups[i], (new StringBuilder(String.valueOf(indent))).append("    ").toString(), sessNo);
        }

    }

    public static void check_c_completion(int sessNo)
        throws Exception
    {
        ThreadGroup currentThreadGroup = Thread.currentThread().getThreadGroup();
        ThreadGroup rootThreadGroup = currentThreadGroup;
        for(ThreadGroup parent = rootThreadGroup.getParent(); parent != null; parent = parent.getParent())
        {
            rootThreadGroup = parent;
        }

        printGroupInfo(rootThreadGroup, "", sessNo);
    }
}
