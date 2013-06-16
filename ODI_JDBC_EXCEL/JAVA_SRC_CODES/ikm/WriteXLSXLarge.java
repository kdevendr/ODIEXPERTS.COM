package ikm;

import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.ParseException;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;


public class WriteXLSXLarge {

	public static void writeXLSXCellData(String tableName,
			ResultSet rs, SXSSFSheet sheet, int rows, int colno,
			SXSSFWorkbook workBook, ResultSetMetaData md, int sheetNo,
			String HdrAlign,String HdrBold, String HdrColor, 
			String datatype, String dateformat, String timeformat,
			String currencysymbol,FileOutputStream outputFile) throws SQLException, ParseException,
			IOException {
			
		String[] dt = datatype.split(",");
		DataFormat format = workBook.createDataFormat();
		CellStyle[] style=new CellStyle[colno];
		for (int i = 0; i < colno; i++) {
			style[i] = workBook.createCellStyle();
		}
		while (rs.next()) {
			rows+=1;
			Row row = sheet.createRow(rows);
			//1048575
			if (rows == 1048575 ) {

				//autoSizeColXLSX(sheet, colno);
				sheetNo++;
				sheet = (SXSSFSheet) workBook.createSheet((new StringBuilder(String
						.valueOf(tableName.toUpperCase()))).append(sheetNo)
						.toString());
				createXLSXHeaderRows(sheet, workBook, md, colno, HdrAlign,HdrBold, HdrColor);
				rows = 0;
			}
			
			for (int i = 0; i < colno; i++) {
				Cell cell = row.createCell(i);
				if (rs.getString(md.getColumnName(i + 1)) != null) {
					if (dt[i].startsWith("NUMERIC") || dt[i].startsWith("SCIENTIFIC")|| dt[i].startsWith("CURRENCY")) {
						cell.setCellValue(rs.getDouble(md
								.getColumnName(i + 1)));
						style[i].setDataFormat(format
								.getFormat(ExcelDatatypeFormating.num_format(dt[i])));
						cell.setCellStyle(style[i]);
					} else if (dt[i].startsWith("DATETIME")) {
						cell.setCellValue(rs.getTimestamp(md
								.getColumnName(i + 1)));
						style[i].setDataFormat(format.getFormat(timeformat));
						cell.setCellStyle(style[i]);
					} else if (dt[i].startsWith("DATE")) {
						cell.setCellValue(rs.getDate(md
								.getColumnName(i + 1)));
						style[i].setDataFormat(format.getFormat(dateformat));
						cell.setCellStyle(style[i]);
					} else if (dt[i].startsWith("PERCENTAGE")) {
						cell.setCellValue(rs.getDouble(md
								.getColumnName(i + 1)) / 100);
						style[i].setDataFormat(format
								.getFormat(ExcelDatatypeFormating.perc_format(dt[i])));
						cell.setCellStyle(style[i]);
					}
					// varchar
					else {
						int length = rs.getString(md.getColumnName(i + 1))
								.length();
						if (length > 255) {

							cell.setCellValue(rs.getString(md
									.getColumnName(i + 1)));
							style[i].setWrapText(true);
							cell.setCellStyle(style[i]);

						} else {
							cell.setCellValue(rs.getString(md
									.getColumnName(i + 1)));
						}
					}

				} else {
					cell.setCellValue("");
				}
			}
			
			if(rows % 100 == 0) {
                ((SXSSFSheet)sheet).flushRows(100); 
			}
		}
		
	}

	
	private static CellStyle setXLSXHeaderStyle(
			Workbook sampleWorkbook, String hdrAlign, String hdrBold,
			String hdrColor) {
		Font font = sampleWorkbook.createFont();
		font.setColor(IndexedColors.valueOf(hdrColor).getIndex());
		if (hdrBold.equals("1")) {
			font.setBoldweight((short) 700);
		}
		CellStyle cellStyle1 = sampleWorkbook.createCellStyle();
		if (hdrAlign.toUpperCase().equals("LEFT")) {
			cellStyle1.setAlignment((short) 1);
		} else if (hdrAlign.toUpperCase().equals("RIGHT")) {
			cellStyle1.setAlignment((short) 3);
		} else if (hdrAlign.toUpperCase().equals("CENTER")) {
			cellStyle1.setAlignment((short) 2);
		} else {
			cellStyle1.setAlignment((short) 1);
		}
		cellStyle1.setFont(font);
		return cellStyle1;
	}

	public static void createXLSXHeaderRows(SXSSFSheet sheet,
			Workbook workBook, ResultSetMetaData md, int colno,
			String HdrAlign, String HdrBold, String HdrColor)
			throws SQLException {
		int rows = 0;
		Row HdrRow = sheet.createRow(rows);
		CellStyle cellStyleSheet = setXLSXHeaderStyle(workBook, HdrAlign,
				HdrBold, HdrColor);
		for (int i = 0; i < colno; i++) {
			Cell HdrCell = HdrRow.createCell(i);
			HdrCell.setCellStyle(cellStyleSheet);
			HdrCell.setCellValue(md.getColumnName(i + 1).toString().trim());
			sheet.autoSizeColumn(i);
		}

	}
	
	public static void autoSizeColXLSX(SXSSFSheet sheet, int colno) {
			for (int i = 0; i < colno; i++) {
				sheet.setColumnWidth(i, sheet.getColumnWidth(i));
			}
	}

	
}
