package checkThreads;

import java.io.PrintStream;
import java.util.ArrayList;

public class CatchException
{

    public static ArrayList ListExcept[];
    public static String output[];

    public static void initialize(int sessNo)
    {
        output = new String[sessNo + 1];
        ListExcept = new ArrayList[sessNo + 1];
        output[sessNo] = "";
        ListExcept[sessNo] = new ArrayList();
    }

    public static void StoreExcept(String Except, int sessNo)
    {
        ListExcept[sessNo].add(Except);
    }

    public static String Result(int sessNo)
    {
        for (int i = 0; i < ListExcept[sessNo].size(); i++) {
        	System.out.println(ListExcept[sessNo].get(i));
			output[sessNo]+=ListExcept[sessNo].get(i);
		}
        return output[sessNo];
    }
}
