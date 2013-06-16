package access;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.healthmarketscience.jackcess.Column;
import com.healthmarketscience.jackcess.Database;

public class CreateTable {
      
      public static void TableCreation(String dir,String AccessFile,String tableName,String cols,String PKName,String PKCol) throws IOException, SQLException {
            
    	    Database db=IKMFetchDB.FetchDb(dir, AccessFile);
    	    
    	    boolean checkTbl = TableCheck(db,tableName);
    	    if (checkTbl == false) {
			            List<Column> collist= new ArrayList<Column>();
			            
			            // Cols have to be in the format COL NAME, DATATYPE ,LENGTH ,PRECISION 
			            String[] columns=cols.split(",");
			            for (String singleCol : columns) {
			                  String[] col=singleCol.trim().split("/");
			                  Column c=new Column();
			                  c.setName(col[0].trim());
			                  c.setSQLType(AccessDataType.getSQLType(col[1].trim()));
			                  if (AccessDataType.getSQLType(col[1].trim()) == 12) { c.setLength((short) (Short.parseShort(col[2])*2)); }
			                  else {c.setLength(Short.parseShort(col[2]));}
			                  c.setScale(Byte.parseByte(col[3]));
			                  collist.add(c);
			            }
			            
			            db.createTable(tableName, collist);
    	    }          
            
            
      }

      
      public static boolean TableCheck(Database db,String tableName) throws IOException{
            
            boolean checkTable =false;
            Set<String> tables = db.getTableNames();
            for (String table : tables) {
                  if (table.equals(tableName))
                        checkTable=true;
            }
            return checkTable;
      }
}
