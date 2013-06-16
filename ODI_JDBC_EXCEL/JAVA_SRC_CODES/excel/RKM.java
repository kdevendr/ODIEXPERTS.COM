package excel;

import java.sql.Connection;
import java.sql.PreparedStatement;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

public class RKM {

	private static int nullcol = 1;
	private static Sheet sheet;
	private static Workbook workbook;

	public static void ProcessRKM(String fileName, String mask,
			Connection conn, int ModelId, int HdrRow, String Sheet_No)
			throws Exception {

		//workbook = (HSSFWorkbook) WorkBook.getworkbook(fileName);

		
		if (fileName.endsWith(".xls")) {
			workbook = WorkBook.getXLSworkbook(fileName);
		} else {
			workbook = WorkBook.getXLSXworkbook(fileName);
		}
		 

		// SINGLE
		if (Sheet_No.equals("%")) {
			int no = workbook.getNumberOfSheets();
			// System.out.println(no);
			for (int i = 0; i < no; i++) {
				sheet = workbook.getSheetAt(i);
				Row row = sheet.getRow(HdrRow - 1);
				if (row != null) {
					String output = "";
					for (Cell cell : row) {
						if (cell != null) {
							output += cell;
						}
					}
					if (output.length() > 1) {
						System.out.println("Reversing Sheet - "+sheet.getSheetName());
						ReadExcelSheet(fileName, mask, sheet, conn, ModelId,
								HdrRow);
					}
				}
			}
		} else if (Sheet_No.indexOf("-") > 0) {
			String[] sheets = Sheet_No.split("-");
			for (int i = Integer.parseInt(sheets[0]); i < Integer
					.parseInt(sheets[1]) + 1; i++) {
				sheet = workbook.getSheetAt(i-1);
				Row row = sheet.getRow(HdrRow - 1);
				if (row != null) {
					String output = "";
					for (Cell cell : row) {
						if (cell != null) {
							output += cell;
						}
					}
					if (output.length() > 1) {
						System.out.println("Reversing Sheet - "+sheet.getSheetName());
						ReadExcelSheet(fileName, mask, sheet, conn, ModelId,
								HdrRow);
					}
				}

			}
		}

		// MULTIPLE
		else {
			String[] No = Sheet_No.split(",");
			for (int i = 0; i < No.length; i++) {
				sheet = workbook.getSheetAt(Integer.parseInt(No[i]) - 1);
				Row row = sheet.getRow(HdrRow - 1);
				if (row != null) {
					String output = "";
					for (Cell cell : row) {
						if (cell != null) {
							output += cell;
						}
					}
					if (output.length() > 1) {
						System.out.println("Reversing Sheet - "+sheet.getSheetName());
						ReadExcelSheet(fileName, mask, sheet, conn, ModelId,
								HdrRow);
					}
				}
			}
		}

	}

	private static void ReadExcelSheet(String fileName, String mask,
			Sheet sheet, Connection conn, int ModelId, int HdrRow)
			throws Exception {

		// SNP_REV_TABLE
		String sqltxt = "insert into SNP_REV_TABLE (I_MOD,TABLE_NAME,RES_NAME,TABLE_ALIAS,TABLE_DESC,IND_SHOW,TABLE_TYPE) values (?,?,?,?,?,?,?)";
		PreparedStatement ps = conn.prepareStatement(sqltxt);
		ps.clearParameters();
		ps.setInt(1, ModelId);
		ps.setString(2, sheet.getSheetName().toUpperCase().trim());
		ps.setString(3, mask.trim());
		ps.setString(4, sheet.getSheetName().toUpperCase().trim());
		ps.setString(5,
				(new StringBuilder("Header Row -" + HdrRow + "\tEXCEL FILE -"))
						.append(fileName.trim()).append("\tSHEET NAME - ")
						.append(sheet.getSheetName().trim()).toString());
		ps.setString(6, "1");
		ps.setString(7, "T");
		ps.executeUpdate();

		// SNP_REV_COL

		sqltxt = "insert into SNP_REV_COL (I_MOD,TABLE_NAME,COL_NAME,DT_DRIVER,POS,LONGC,SCALEC,CHECK_FLOW,CHECK_STAT) values (?,?,?,?,?,?,?,?,?)";
		ps = conn.prepareStatement(sqltxt);
		Row row = sheet.getRow(HdrRow - 1);
		Cell headcell = null;
		Cell cell = null;
		Row datarow = getRow(HdrRow, sheet);
		CellStyle cellstyle;
		// Catch Exceptions
		if (row != null) {
		} else {
			throw new Exception(
					"\n\n The Header Row ("
							+ HdrRow
							+ ") for the Sheet - "
							+ sheet.getSheetName()
							+ " is blank.\n Please provide another Header row or check if its valid Sheet");
		}
		if (datarow != null) {
		} else {
			throw new Exception(
					"\n\n The Sheet - "
							+ sheet.getSheetName()
							+ " does not seems to be valid rows .\n Please check if its valid Sheet");
		}

		// System.out.println(maxrows+"\t"+sheet.getSheetName()+"\t"+row.getPhysicalNumberOfCells());
		for (int j = 0; j < row.getLastCellNum(); j++) {
			headcell = row.getCell(j, Row.RETURN_BLANK_AS_NULL);
			cell = datarow.getCell(j, Row.RETURN_BLANK_AS_NULL);
			// System.out.println(j + "\t" + headcell + "\t cell" + cell);
			if (cell != null) {
				cellstyle = cell.getCellStyle();
				if (cell.getCellType() == 0) {
					// Date and time Only
					if (DateUtil.isCellDateFormatted(cell) == true
							&& DateUtil.isInternalDateFormat(cellstyle
									.getDataFormat()) == false) {

						ps.clearParameters();
						ps.setInt(1, ModelId);
						ps.setString(2, sheet.getSheetName().toUpperCase()
								.trim());
						ps.setString(3, getCol(headcell));
						ps.setString(4, "DATETIME");
						ps.setInt(5, j + 1);
						ps.setInt(6, 15);
						ps.setInt(7, 0);
						ps.setInt(8, 0);
						ps.setInt(9, 0);
						ps.executeUpdate();
					}
					// Date only
					else if (DateUtil.isCellDateFormatted(cell) == true
							&& DateUtil.isInternalDateFormat(cellstyle
									.getDataFormat()) == true) {
						ps.clearParameters();
						ps.setInt(1, ModelId);
						ps.setString(2, sheet.getSheetName().toUpperCase()
								.trim());
						ps.setString(3, getCol(headcell));
						ps.setString(4, "DATE");
						ps.setInt(5, j + 1);
						ps.setInt(6, 15);
						ps.setInt(7, 0);
						ps.setInt(8, 0);
						ps.setInt(9, 0);
						ps.executeUpdate();
					} else if (cellstyle.getDataFormatString().endsWith("%")) {
						ps.clearParameters();
						ps.setInt(1, ModelId);
						ps.setString(2, sheet.getSheetName().toUpperCase()
								.trim());
						ps.setString(3, getCol(headcell));
						ps.setString(4, "PERCENTAGE");
						ps.setInt(5, j + 1);
						ps.setInt(6, cell.toString().length());
						ps.setInt(7, cell.toString().length()
								- cell.toString().lastIndexOf(".") - 3);
						ps.setInt(8, 0);
						ps.setInt(9, 0);
						ps.executeUpdate();
					} else if (cellstyle.getDataFormatString().contains("E")) {
						ps.clearParameters();
						ps.setInt(1, ModelId);
						ps.setString(2, sheet.getSheetName().toUpperCase()
								.trim());
						ps.setString(3, getCol(headcell));
						ps.setString(4, "SCIENTIFIC");
						ps.setInt(5, j + 1);
						ps.setInt(6, cell.toString().length());
						ps.setInt(7, cell.toString().length()
								- cell.toString().lastIndexOf(".") - 1);
						ps.setInt(8, 0);
						ps.setInt(9, 0);
						ps.executeUpdate();
					} else if (cellstyle.getDataFormatString().contains("$")) {
						ps.clearParameters();
						ps.setInt(1, ModelId);
						ps.setString(2, sheet.getSheetName().toUpperCase()
								.trim());
						ps.setString(3, getCol(headcell));
						ps.setString(4, "CURRENCY");
						ps.setInt(5, j + 1);
						ps.setInt(6, cell.toString().length());
						ps.setInt(7, cell.toString().length()
								- cell.toString().lastIndexOf(".") - 1);
						ps.setInt(8, 0);
						ps.setInt(9, 0);
						ps.executeUpdate();
					} else {
						ps.clearParameters();
						ps.setInt(1, ModelId);
						ps.setString(2, sheet.getSheetName().toUpperCase()
								.trim());
						ps.setString(3, getCol(headcell));
						ps.setString(4, "NUMERIC");
						ps.setInt(5, j + 1);
						ps.setInt(6, cell.toString().length());
						ps.setInt(7, cell.toString().length()
								- cell.toString().lastIndexOf(".") - 1);
						ps.setInt(8, 0);
						ps.setInt(9, 0);
						ps.executeUpdate();
					}
				}
				// other Data Type
				else {
					// System.out.println(j + "\t" + headcell + "\t cell" +
					// cell);
					ps.clearParameters();
					ps.setInt(1, ModelId);
					ps.setString(2, sheet.getSheetName().toUpperCase().trim());
					// ps.setString(3,
					// headcell.toString().toUpperCase().trim().replace("\"",
					// ""));
					ps.setString(3, getCol(headcell));
					ps.setString(4, "VARCHAR");
					ps.setInt(5, j + 1);
					ps.setInt(6,
							getStrLength(sheet, datarow.getRowNum(), j + 1));
					ps.setInt(7, 0);
					ps.setInt(8, 0);
					ps.setInt(9, 0);
					ps.executeUpdate();

				}
			}
		}
	}

	public static Row getRow(int HdrRow, Sheet sheet) {
		String output = "";
		Row row = null;
		for (int i = HdrRow; i < HdrRow + 100; i++) {
			row = sheet.getRow(i);
			if (row != null) {
				for (int j = row.getFirstCellNum(); j < row.getLastCellNum(); j++) {
					output = (new StringBuilder(String.valueOf(output)))
							.append(row.getCell(j)).toString();
				}
				if (output.length() > 1) {
					break;
				}
			} else {
				row = null;
			}
		}
		return row;
	}

	private static String getCol(Cell cell) {
		String column = "";
		if (cell != null) {
			column = cell.toString().toUpperCase().trim().replace("\"", "")
					.replaceAll("[~`!@#%^&*():;/?.,]", "");
			column = column.replace(" ", "_").replace("-", "_");
		} else {
			column = "C" + nullcol;
			nullcol++;
		}
		return column;
	}

	private static int getStrLength(Sheet sheet, int RowNo, int pos) {
		int len = 50;
		Row row = null;
		Cell cell = null;
		CellStyle cellstyle = null;
		for (int i = RowNo; i < RowNo + 100; i++) {
			row = sheet.getRow(i);
			if (row != null) {
				cell = row.getCell(pos);
				if (cell != null) {
					cellstyle = cell.getCellStyle();
					// System.out.println(cellstyle.getWrapText());
					if (cellstyle.getWrapText() == true) {
						int width = sheet.getColumnWidth(pos) / 255;
						int length = (int) (row.getHeightInPoints() / 15);

						if (length * width > len) {
							len = length * width;
							// System.out.println(len);

						}
					} else {
						if (cell != null && cell.toString().length() > len) {
							len = cell.getStringCellValue().length();
						}
					}
				}
			}
		}
		return len;

	}
}