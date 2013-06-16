package excel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class CatchException
{

    public static List ListExcept;
    public static String output = "";

    public static void initialize()
    {
        ListExcept = new ArrayList();
        output = "";
    }

    public static void StoreExcept(String Except)
    {
        ListExcept.add(Except);
    }

    public static String Result()
    {
        for (int i = 0; i < ListExcept.size(); i++) {
			output+=ListExcept.get(i);
		}
        return output;
    }

}
