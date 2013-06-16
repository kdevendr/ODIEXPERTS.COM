package access;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import com.healthmarketscience.jackcess.Column;
import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.Index;
import com.healthmarketscience.jackcess.Table;
import com.healthmarketscience.jackcess.IndexData.ColumnDescriptor;


public class RKMAccess {

		private static PreparedStatement ps;
		
		public static void RKM(String fileName,String mask,String tableName,Connection conn,int modelID) throws IOException, SQLException {
			
				Database db=Database.open(new File(fileName), true);
				
				if (tableName != "%") {
					Table table = db.getTable(tableName);
					RKMTable(fileName,mask,conn,modelID,table);
				}else {
						for(String tables : db.getTableNames()) {
							Table table = db.getTable(tables);
							RKMTable(fileName,mask,conn,modelID,table);
						}
				}
				
				db.close();
		}
		

		public static void RKMTable(String fileName,String mask,Connection conn,int modelID,Table table) throws IOException, SQLException{
			
			//Table Name
			String sqltxt = "insert into SNP_REV_TABLE (I_MOD,TABLE_NAME,RES_NAME,TABLE_ALIAS,TABLE_DESC,IND_SHOW,TABLE_TYPE) values (?,?,?,?,?,?,?)";
			ps = conn.prepareStatement(sqltxt);
			ps.clearParameters();
			ps.setInt(1, modelID);
			ps.setString(2, table.getName());
			ps.setString(3,mask);
			ps.setString(4,table.getName());
			ps.setString(5,"");
			ps.setString(6, "1");
			ps.setString(7, "T");
			ps.executeUpdate();
			ps.close();
			
			// Columns
			sqltxt = "insert into SNP_REV_COL (I_MOD,TABLE_NAME,COL_NAME,DT_DRIVER,POS,LONGC,SCALEC,CHECK_FLOW,CHECK_STAT) values (?,?,?,?,?,?,?,?,?)";
			ps = conn.prepareStatement(sqltxt);
			List<Column> col = table.getColumns();
	          for (Column column : col) {
        	  	ps.clearParameters();
        	    ps.setInt(1, modelID);
        	    ps.setString(2, table.getName());
        	    ps.setString(3, column.getName());
        	    ps.setString(4,AccessDataType.getDatatypefromSQLType(column.getSQLType()));   
        	    ps.setInt(5,column.getColumnIndex()+1);
        	     if (AccessDataType.getDatatypefromSQLType(column.getSQLType()).equals("TEXT")) {
        	    			  ps.setInt(6, column.getLength()/2);}
        	    		else {ps.setInt(6, column.getLength());}
        	    ps.setInt(7, column.getScale());
        	    ps.setInt(8, 0);
        	    ps.setInt(9, 0);
        	    ps.executeUpdate();
			}       
	          ps.close();
	          
	          
	          List<Index> idxs = table.getIndexes();
	          if (idxs.isEmpty() == false) {
	        	  for (Index index : idxs) {
	        		  if (index.isPrimaryKey()){
	        			  
	        			//PRIMARY KEY TABLE
	        	          sqltxt = "insert into SNP_REV_KEY (I_MOD,TABLE_NAME,KEY_NAME,CONS_TYPE,IND_ACTIVE,CHECK_FLOW,CHECK_STAT) values (?,?,?,?,?,?,?)";
	        				ps = conn.prepareStatement(sqltxt);
	        				ps.setInt(1, modelID);
	        				ps.setString(2, table.getName());
	        				ps.setString(3,table.getName()+"_PK");
	        				ps.setString(4,"PK");
	        				ps.setString(5,"1");
	        				ps.setInt(6, 0);
	                	    ps.setInt(7, 0);
	        				ps.executeUpdate();
	        				ps.close();
	        				
	        			// PRIMARY KEY COL	
	        				sqltxt = "insert into SNP_REV_KEY_COL (I_MOD,TABLE_NAME,KEY_NAME,COL_NAME,POS) values (?,?,?,?,?)";
	        				ps = conn.prepareStatement(sqltxt);
	        				ps.setInt(1, modelID);
	        				ps.setString(2, table.getName());
	        				ps.setString(3,table.getName()+"_PK");
	        				List<ColumnDescriptor> colidx = index.getColumns();
	   					 	for (ColumnDescriptor columnDescriptor : colidx) {
			   					 	ps.setString(4,columnDescriptor.getColumn().getName());
			   					 	ps.setInt(5,columnDescriptor.getColumn().getColumnIndex()+1);
	   					 	}
	        				
	        				ps.executeUpdate();
	        				ps.close();
	        		  }
	        	  }
	          }
	          
		}
		
	
}

