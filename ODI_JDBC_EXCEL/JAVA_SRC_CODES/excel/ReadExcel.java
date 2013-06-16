package excel;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

public class ReadExcel {

	private static int cellno;
	private static String output;
	private static int pos;
	private static Cell cell;
	private static int HdrRow;
	private static Sheet sheet;
	private static Workbook workbook;

	public static void ProcessExcel(String fileName, String SheetName,
			String collist, String sqltxt, Connection conn, String trim,
			String HeaderRow, String datatype) throws Exception {
		
		//All the columns needs to be Mapped to Staging only
		String[] col=collist.split(",");
		if (col.length ==0) {
			throw new Exception("\n\n All columns needs to be mapped to Staging only in Interface. \n Please make the desired changes and reexecute your interface");
		}
		else {
			for (String string : col) {
				if (string.length() ==0){
					throw new Exception("\n\n All columns needs to be mapped to Staging only in Interface. \n Please make the desired changes and reexecute your interface");
				}
			}
		}

		if (HeaderRow != null) {
			HdrRow = Integer.parseInt(HeaderRow);
		} else {
			HdrRow = 1;
		}
		
		workbook = WorkBook.getworkbook(fileName);
		readExcel(workbook, SheetName, collist, sqltxt, conn, trim, HdrRow,
				datatype);
		/*if (fileName.endsWith(".xls")) {
			Workbook workbook = WorkBook.getXLSworkbook(fileName);
			readExcel(workbook, SheetName, collist, sqltxt, conn, trim, HdrRow,
					datatype);
		} else {
			Workbook workbook = WorkBook.getXLSXworkbook(fileName);
			readExcel(workbook, SheetName, collist, sqltxt, conn, trim, HdrRow,
					datatype);
		}*/
	}

	public static void readExcel(Workbook WorkBook, String SheetName,
			String collist, String sqltxt, Connection conn, String trim,
			int HdrRow, String datatype) throws SQLException, IOException {

		PreparedStatement ps = conn.prepareStatement(sqltxt);
		sheet = WorkBook.getSheet(SheetName);
		String dt[] = datatype.split(",");
		String[] lst = collist.split(",");
		for (Row row : sheet) {
			// DEMO VERSION SO READ ONLY FIRST 100 ROWS
			if (row != null && row.getRowNum() >= HdrRow) {
				cellno = 0;
				output = "";
				// CHECKING IF ALL THE ROWS ARE NULL
				for (String string : lst) {
					pos = Integer.parseInt(string) - 1;
					cell = row.getCell(pos, Row.RETURN_BLANK_AS_NULL);
					if (cell != null) {
						output += cell.toString();
					}
				}

				// ROWS WHERE ATLEAST ONE/MORE COL IS NOT NULL
				if (output.length() > 1) {
					for (String string : lst) {
						cellno++;
						int pos = Integer.parseInt(string) - 1;
						Cell cell = row.getCell(pos, Row.RETURN_BLANK_AS_NULL);
										

						if (cell != null) {
							//System.out.println(cell);
							if (dt[cellno - 1].equals("NUMERIC")
									|| dt[cellno - 1].equals("SCIENTIFIC")
									|| dt[cellno - 1].equals("CURRENCY")) {
								ps.setDouble(cellno, cell.getNumericCellValue());
							}
							// PERCENTAGE
							else if (dt[cellno - 1].equals("PERCENTAGE")) {
								ps.setDouble(cellno,
										cell.getNumericCellValue() * 100);
							}
							// DATE
							else if (dt[cellno - 1].equals("DATE")) {
								Date date = DateUtil.getJavaDate(cell
										.getNumericCellValue());
								java.sql.Date sqlDate = new java.sql.Date(
										date.getTime());
								ps.setDate(cellno, sqlDate);
							}
							// DATETIME
							else if (dt[cellno - 1].equals("DATETIME")) {
								Date date = DateUtil.getJavaDate(cell
										.getNumericCellValue());
								java.sql.Timestamp sqlDate = new java.sql.Timestamp(
										date.getTime());
								ps.setTimestamp(cellno, sqlDate);
							} // varchar
							else {
								ps.setString(
										cellno,
										getVarCharcolValue(cell.getRichStringCellValue().toString(),
												trim));
							}
						}
						// Handling Nulls and Blanks
						else { /* System.out.println("Null or Blank" ); */
							ps.setString(cellno, "");
						}
					}
					ps.executeUpdate();
					ps.clearParameters();
				}
			}
		}
		conn.commit();
		ps.close();

	}

	// VARCHAR DATATYPE
	public static String getVarCharcolValue(String cellcol, String trim) {
		String colValue = "";
		// Trim - Yes
		if (trim.equals("1"))
			if (cellcol.endsWith(".0")) {
				colValue = cellcol.substring(0, cellcol.trim()
						.lastIndexOf(".0"));
			} else {
				colValue = cellcol.trim();
			}
		// Trim - No
		else {
			if (cellcol.endsWith(".0")) {
				colValue = cellcol.substring(0, cellcol.lastIndexOf(".0"));
			} else {
				colValue = cellcol;
			}
		}
		return colValue;
	}

}
