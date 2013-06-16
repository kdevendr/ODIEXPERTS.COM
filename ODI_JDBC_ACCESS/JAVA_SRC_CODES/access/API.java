package access;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.healthmarketscience.jackcess.Database;

public class API {

	public static void CopyTable(String Dir,String AccessFile,String tableName,Connection conn,String SQL) throws IOException, SQLException{
			
			Database db = IKMFetchDB.FetchDb(Dir, AccessFile);
			Statement stmt=conn.createStatement();
			ResultSet rs=stmt.executeQuery(SQL);
			db.copyTable(tableName, rs);
			db.flush();
			db.close();
	}
	
	public static void CopyFile(String Dir,String AccessFile,String tableName,String srcFile,String delim) throws IOException, SQLException{
		
			Database db = IKMFetchDB.FetchDb(Dir, AccessFile);
			BufferedReader bf=new BufferedReader( new FileReader(srcFile));
			db.importReader(tableName, bf, delim);	
			db.flush();
			db.close();
	}
	
}
