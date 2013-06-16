package excel;

import java.sql.Connection;

import large.LKMLarge;

public class LKM {
	
	public static void LoadExcel(String fileName, String SheetName,
			String collist, String sqltxt, Connection conn, String trim,
			String HeaderRow, String datatype,String LargeExcel) throws Exception {
		
		if(SheetName.indexOf("**") >=0 ) {
			CatchException.initialize();
			LKMLarge.ReadExcel(fileName, SheetName, collist, sqltxt, conn, trim, HeaderRow, datatype,"1");
			
		} else if (LargeExcel.equals("1")) {
			CatchException.initialize();
			LKMLarge.ReadExcel(fileName, SheetName, collist, sqltxt, conn, trim, HeaderRow, datatype,"0");
		} else {
			ReadExcel.ProcessExcel(fileName, SheetName, collist, sqltxt, conn, trim, HeaderRow, datatype);
		}
		
	}

}
