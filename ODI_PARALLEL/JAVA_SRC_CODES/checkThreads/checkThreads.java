package checkThreads;


public class checkThreads
{

    private static int threadcount = 0;

    private static int printGroupInfo(ThreadGroup g, String indent, String ThreadName)
        throws InterruptedException
    {
        if(g != null)
        {
            int numThreads = g.activeCount();
            int numGroups = g.activeGroupCount();
            Thread threads[] = new Thread[numThreads];
            ThreadGroup groups[] = new ThreadGroup[numGroups];
            g.enumerate(threads, false);
            g.enumerate(groups, false);
            for(int i = 0; i < numThreads; i++)
            {
                if(threads[i] != null && threads[i].getName().indexOf(ThreadName) >= 0)
                {
                    threadcount++;
                }
            }

            for(int i = 0; i < numGroups; i++)
            {
                printGroupInfo(groups[i], (new StringBuilder(String.valueOf(indent))).append("    ").toString(), ThreadName);
            }

        }
        return threadcount;
    }

    public static int check_unload_completion(String ThreadName, String checkCompletion)
        throws InterruptedException
    {
        int checkcount = 0;
        threadcount = 0;
        ThreadGroup currentThreadGroup = Thread.currentThread().getThreadGroup();
        ThreadGroup rootThreadGroup = currentThreadGroup;
        for(ThreadGroup parent = rootThreadGroup.getParent(); parent != null; parent = parent.getParent())
        {
            rootThreadGroup = parent;
        }

        if(checkCompletion.equals("Y"))
        {
            ThreadCompletion(rootThreadGroup, "", ThreadName);
        } else
        {
            checkcount = printGroupInfo(rootThreadGroup, "", ThreadName);
        }
        return checkcount;
    }

    private static void ThreadCompletion(ThreadGroup g, String indent, String ThreadName)
        throws InterruptedException
    {
        if(g != null)
        {
            int numThreads = g.activeCount();
            int numGroups = g.activeGroupCount();
            Thread threads[] = new Thread[numThreads];
            ThreadGroup groups[] = new ThreadGroup[numGroups];
            g.enumerate(threads, false);
            g.enumerate(groups, false);
            for(int i = 0; i < numThreads; i++)
            {
                if(threads[i] != null && threads[i].getName().indexOf(ThreadName) >= 0)
                {
                    threads[i].join();
                }
            }

            for(int i = 0; i < numGroups; i++)
            {
                ThreadCompletion(groups[i], (new StringBuilder(String.valueOf(indent))).append("    ").toString(), ThreadName);
            }

        }
    }

}
