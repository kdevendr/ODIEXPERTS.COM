package access;

import java.io.IOException;

import com.healthmarketscience.jackcess.Database;

public class CloseDB {
	
	public static void close(Database db) throws IOException {
		db.flush();
		db.close();
	}

}
