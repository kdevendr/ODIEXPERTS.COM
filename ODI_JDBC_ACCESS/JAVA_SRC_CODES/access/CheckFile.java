package access;
import java.io.File;
public class CheckFile {

public static boolean getFiles(String DirectoryName,String extension,String fileSearched){
            
            boolean present = false;
        String files;
        File folder = new File(DirectoryName);
          File[] listOfFiles = folder.listFiles();
          for (int i = 0; i < listOfFiles.length; i++)
          {
           if (listOfFiles[i].isFile())
           {
           files = listOfFiles[i].getName();
               if (files.endsWith("."+extension.toLowerCase()) || files.endsWith("."+extension.toUpperCase()))
               {
                  if (files.equals(fileSearched)) { present= true;break;}
                }
           }}
        return present;
  }

}
