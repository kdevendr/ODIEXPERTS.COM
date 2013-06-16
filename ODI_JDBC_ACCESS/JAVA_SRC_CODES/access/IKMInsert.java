package access;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.Table;

public class IKMInsert {
      
      public static void InsertAccess(String dir,String AccessFile,String tableName,Connection conn,String SQL) throws IOException, SQLException{
            
            Database db=IKMFetchDB.FetchDb(dir, AccessFile);
            Table table=db.getTable(tableName);
            
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(SQL);    
            ResultSetMetaData md = rs.getMetaData();
    		int colno = md.getColumnCount(); 		
    		
            List<Object[]> rows = new ArrayList<Object[]>(5000);
            Object[] row = null;
            int rowno=0;
            while (rs.next()){
            	row = new Object[colno];
            	for (int i = 0; i < colno; i++) {
            		row[i] =rs.getString(md.getColumnName(i + 1));
    			}
            	rows.add(row);
            	rowno+=1;
                  if (rowno == 5000) {
                        table.addRows(rows);
                        rowno=0;
                        rows.clear();
                  }       
            }          
            
            if (rows.size() > 0) {
                table.addRows(rows);
              }
            
            db.flush();
            db.close();
            
      }

}
