package access;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;

import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.Table;

public class LKMAccess {

	private static int colno=0;
	
	public static void LKM(Connection conn,String fileName,String tableName,String columns,String sqltxt) throws IOException, SQLException{
		
		Database db=Database.open(new File(fileName), false);
		Table table=db.getTable(tableName);
		
		PreparedStatement ps = conn.prepareStatement(sqltxt);
		String[] colmn=columns.split(",");
		int rows=0;
		for(Map<String, Object> row : table) {
			ps.clearParameters();
			colno=0;
			for (String col : colmn) {
				 colno+=1;
				 ps.setString(colno,row.get(col).toString());
			}
			ps.addBatch();
			rows+=1;
				if (rows == 1000){
					ps.executeBatch();
					conn.commit();
					ps.clearBatch();
				}
		}
		ps.executeBatch();
		conn.commit();
		ps.clearBatch();
		ps.clearParameters();
		ps.close();
		
		db.flush();
		db.close();
		
	}
}


