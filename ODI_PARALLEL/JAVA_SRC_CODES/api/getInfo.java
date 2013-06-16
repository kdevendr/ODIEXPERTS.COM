package api;

import io.FileUtils;
import io.LineIterator;
import io.comparator.LastModifiedFileComparator;
import io.filefilter.WildcardFileFilter;
import io.input.ReversedLinesFileReader;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

public class getInfo
{

    private static String JythonOutput;


    public static File[] getFiles(String DirectoryName, String Mask)
        throws SecurityException, IllegalArgumentException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException
    {
        File dir = new File(DirectoryName);
        FileFilter fileFilter = new WildcardFileFilter(Mask);
        File files[] = dir.listFiles(fileFilter);
        return files;
    }

    public static File getLatestFile(String DirectoryName, String Mask)
    {
        File dir = new File(DirectoryName);
        FileFilter fileFilter = new WildcardFileFilter(Mask);
        File files[] = dir.listFiles(fileFilter);
        Arrays.sort(files, LastModifiedFileComparator.LASTMODIFIED_REVERSE);
        File file = files[0];
        return file;
    }

    public static int getFileRowCount(String FileName)
        throws IOException
    {
        System.out.println((new StringBuilder("Fetching Rows Count for File - ")).append(FileName).toString());
        LineIterator it = FileUtils.lineIterator(new File(FileName));
        int RowNo;
        for(RowNo = 0; it.hasNext(); RowNo++)
        {
            String Line = it.nextLine();
        }

        it.close();
        return RowNo;
    }

    public static String getFileFirst100Rows(String FileName)
        throws IOException
    {
        LineIterator it = FileUtils.lineIterator(new File(FileName));
        StringBuilder build = new StringBuilder();
        int RowNo = 0;
        String Line;
        for(; it.hasNext(); build.append((new StringBuilder(String.valueOf(Line))).append('\n').toString()))
        {
            if(RowNo == 100)
            {
                break;
            }
            RowNo++;
            Line = it.nextLine();
        }

        it.close();
        return build.toString();
    }

    public static String getFileLast100Rows(String FileName)
        throws IOException
    {
        ReversedLinesFileReader revFL = new ReversedLinesFileReader(new File(FileName));
        String Line = "";
        StringBuilder build = new StringBuilder();
        int RowNo = 0;
        while((Line = revFL.readLine()) != null) 
        {
            if(RowNo == 100)
            {
                break;
            }
            RowNo++;
            build.append((new StringBuilder(String.valueOf(Line.toString()))).append('\n').toString());
        }
        revFL.close();
        return build.toString();
    }

    public static String getFilesinFolder(String Folder, String Mask)
    {
        StringBuilder build = new StringBuilder();
        build.append("\n\n");
        if(Mask.trim().length() == 0)
        {
            Mask = "*.*";
            build.append((new StringBuilder("The Files Under the Folder ")).append(Folder).append('\n').toString());
        } else
        {
            build.append((new StringBuilder("The Files Under the Folder ")).append(Folder).append(" for the  Mask  ").append(Mask).append("\n").toString());
        }
        File dir = new File(Folder);
        FileFilter fileFilter = new WildcardFileFilter(Mask);
        File files[] = dir.listFiles(fileFilter);
        String tempfile = "";
        File afile[];
        int j = (afile = files).length;
        for(int i = 0; i < j; i++)
        {
            File file = afile[i];
            tempfile = (new StringBuilder(String.valueOf(tempfile))).append(file.getAbsolutePath().toString()).toString();
            build.append((new StringBuilder(String.valueOf(file.getAbsolutePath().toString()))).append('\n').toString());
        }

        build.append("\n\n");
        if(tempfile.trim().length() == 0)
        {
            build = new StringBuilder();
            build.append("\n\n");
            build.append((new StringBuilder("There are no Files Under the Folder ")).append(Folder).append(" for the  Mask  ").append(Mask).append("\n").toString());
            build.append("\n\n");
        }
        return build.toString();
    }

    public static int getNoofInsert(int Sessno, int InsertNo)
    {
        return InsertNo;
    }

    public static String getLineSpace()
    {
        return "\n\n";
    }

    public static void setJythonVariable(String Value)
    {
        JythonOutput = Value;
    }

    public static String getJythonVariable()
    {
        return JythonOutput;
    }
}
