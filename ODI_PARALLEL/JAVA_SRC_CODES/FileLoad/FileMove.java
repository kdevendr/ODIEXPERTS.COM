package FileLoad;

import io.FileUtils;
import io.IOUtils;
import java.io.*;

public class FileMove
{

    public FileMove()
    {
    }

    public static void CopyFile(String Dir, String Move_copy_dir, String FileName)
        throws IOException
    {
        System.out.println(Dir);
        new File(Move_copy_dir);
        java.io.InputStream input = new FileInputStream(FileName);
        java.io.OutputStream output = new FileOutputStream((new StringBuilder(String.valueOf(String.valueOf(Move_copy_dir)))).append("/").append(FileName.replace(Dir, "")).toString());
        new IOUtils();
        IOUtils.copyLarge(input, output);
    }

    public static void MoveFile(String Dir, String Move_copy_dir, String FileName)
        throws IOException
    {
        new File(Move_copy_dir);
        FileUtils.moveFile(new File(FileName), new File((new StringBuilder(String.valueOf(Move_copy_dir))).append("/").append(FileName.replace(Dir, "")).toString()));
    }
}
