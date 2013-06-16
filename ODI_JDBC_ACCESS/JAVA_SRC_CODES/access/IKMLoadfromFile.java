package access;

import java.io.File;
import java.io.IOException;

import com.healthmarketscience.jackcess.Database;

public class IKMLoadfromFile {
      
      public static void WriteAccess(String dir,String AccessFile,String tableName,String SrcFile,String delim) throws IOException{
            
            Database db=IKMFetchDB.FetchDb(dir, AccessFile);
            db.importFile(tableName, new File(SrcFile), delim);
            db.flush();
            db.close();
      }

}
