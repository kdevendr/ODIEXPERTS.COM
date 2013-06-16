package FileLoad;


public class HandleData
{

    private static String data;

    public HandleData()
    {
    }

    public static String TrimData(String Data, String textDelim, String Remove_Special_characters)
    {
        data = Data.replace(textDelim, "").trim();
        String special[] = Remove_Special_characters.split(",");
        String as[];
        int j = (as = special).length;
        for(int i = 0; i < j; i++)
        {
            String character = as[i];
            data = data.replace(character, "");
        }

        return data;
    }
}
