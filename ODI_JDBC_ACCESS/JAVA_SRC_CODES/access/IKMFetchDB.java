package access;

import java.io.File;
import java.io.IOException;

import com.healthmarketscience.jackcess.Database;

public class IKMFetchDB {
    
     public static Database FetchDb(String Dir,String file) throws IOException{
          Database db;
          String extension =file.substring(file.indexOf(".")+1,file.length());
          boolean check = CheckFile.getFiles(Dir, extension,file );
          if (check == true){
               db=Database.open(new File(Dir+"/"+file), false);
          }else {
               db=Database.create(new File(Dir+"/"+file), true);
          }
          return db;         
     }

}