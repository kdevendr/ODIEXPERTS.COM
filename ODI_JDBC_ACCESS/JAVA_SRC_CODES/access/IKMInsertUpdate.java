package access;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.healthmarketscience.jackcess.Column;
import com.healthmarketscience.jackcess.Cursor;
import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.Index;
import com.healthmarketscience.jackcess.IndexCursor;
import com.healthmarketscience.jackcess.IndexData.ColumnDescriptor;
import com.healthmarketscience.jackcess.Table;

public class IKMInsertUpdate {
	
	private static Cursor cur;

	public static void InsUpd(String dir,String AccessFile,String tableName,Connection conn,String SQL,String PKkey,String UpdateKey,String TrgtCol) throws IOException, SQLException {
		
		Database db=IKMFetchDB.FetchDb(dir, AccessFile);
        Table table=db.getTable(tableName);
         
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(SQL);    
        ResultSetMetaData md = rs.getMetaData();
		int colno = md.getColumnCount(); 		
		
		// Insert /Update 
		 cur = Cursor.createCursor(table); 
		 Map<String, Object> m = new HashMap<String, Object>(); 
		 Object[]  colmns = null;
		 
		 if (PKkey.trim().equals("")) {
		 				
		 				//Using Normal Keys
						 while (rs.next()) {
													// Update Row
														if (UpdateKey.indexOf(",") > 0) {
															System.out.println("hit1");
															colmns = UpdateKey.split(",");
															for (int i = 0; i < colmns.length; i++) {
																Column column =table.getColumn(colmns[i].toString().trim());
																m.put(column.getName(),
																		AccessDataType. getConvertedDATA ( rs.getString( column.getName()), column.getSQLType()));
															}
														} else {	
															Column column =table.getColumn(UpdateKey.trim());
															m.put(column.getName(),
																	AccessDataType. getConvertedDATA ( rs.getString(column.getName()), column.getSQLType()));
														}
														if (cur.findRow(m)) {
															colmns = TrgtCol.split(",");
															for (int i = 0; i < colno; i++) {
																			cur.setCurrentRowValue(table.getColumn(colmns[i].toString().trim()),
																					rs.getString(md.getColumnName(i + 1)));
															}
											
														} else {
															// Add Row
															System.out.println("not match");
															List<String> collist = new ArrayList<String>();
															for (int i = 0; i < colno; i++) {
																collist.add(rs.getString(md.getColumnName(i + 1)));
															}
															table.addRow(collist.toArray());
														}
						 					}
								 
		 } else {
							 Index idx=table.getPrimaryKeyIndex();
					            List<ColumnDescriptor> lst = idx.getColumns();
					            colmns = new Object[lst.size()*2];
					             int j=0;
					             for (ColumnDescriptor columnDescriptor : lst) {
					                  Column lstcol = columnDescriptor.getColumn();
					                  colmns[j]=lstcol.getName();
					                  colmns[j+1]=lstcol.getSQLType();
					                  j+=2;
					            }
					             boolean val=false;
					             IndexCursor idxcur=IndexCursor. createCursor(table, idx);
					             Object[] s= new Object[lst.size()];
					             while (rs.next()) {
					            	     int zz=0;
							             for (int i = 0; i < colmns.length; i=i+2) {
							                  s[zz]=AccessDataType. getConvertedDATA ( rs.getString( colmns[i].toString().trim()), (Integer) colmns[i+1]);
							                  zz+=1;
							            }
									             val = idxcur.findRowByEntry(s); 
									             if (val == true) {
														for (int i = 0; i < colno; i++) {
																			idxcur.setCurrentRowValue(table	.getColumn(md.getColumnName(i + 1)),
																					rs.getString(md.getColumnName(i + 1)));
														}
													} else {
														// Add Row
														List<String> collist = new ArrayList<String>();
														for (int i = 0; i < colno; i++) {
															collist.add(rs.getString(md.getColumnName(i + 1)));
														}
														table.addRow(collist.toArray());
													}			 
							             	}		 
					             }
		 				}
	}


