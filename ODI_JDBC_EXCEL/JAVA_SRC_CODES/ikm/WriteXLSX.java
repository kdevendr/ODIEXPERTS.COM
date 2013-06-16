package ikm;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.ParseException;

import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


public class WriteXLSX {

	public static void writeXLSXCellData(String tableName, XSSFRow DataRow,
			ResultSet rs, XSSFSheet sheet, int rows, int colno,
			XSSFWorkbook workBook, ResultSetMetaData md, int sheetNo,
			XSSFCell DataCell, XSSFCellStyle cellStyleSheet, String HdrAlign,
			String HdrBold, String HdrColor, String datatype, String dateformat, String timeformat,
			String currencysymbol) throws SQLException, ParseException,
			IOException {
		
		String[] dt = datatype.split(",");
		DataFormat format = workBook.createDataFormat();
		XSSFCellStyle[] style = new XSSFCellStyle[colno];
		for (int i = 0; i < colno; i++) {
			style[i] = workBook.createCellStyle();
		}
		while (rs.next()) {
			rows+=1;
			DataRow = sheet.createRow(rows);
			if (rows == 1048575) {
				
				autoSizeColXLSX(sheet, colno);
				sheetNo++;
				sheet = workBook.createSheet((new StringBuilder(String
						.valueOf(tableName.toUpperCase()))).append(sheetNo)
						.toString());
				createXLSXHeaderRows(sheet, workBook, md, colno, HdrAlign,
						HdrBold, HdrColor);
				rows = 0;
			}
			
			//System.out.println(rows);
			
			for (int i = 0; i < colno; i++) {
				DataCell = DataRow.createCell(i);
				if (rs.getString(md.getColumnName(i + 1)) != null) {
					if (dt[i].startsWith("NUMERIC") || dt[i].startsWith("SCIENTIFIC")|| dt[i].startsWith("CURRENCY")) {
						DataCell.setCellValue(rs.getDouble(md
								.getColumnName(i + 1)));
						style[i].setDataFormat(format
								.getFormat(ExcelDatatypeFormating.num_format(dt[i])));
						DataCell.setCellStyle(style[i]);
					} else if (dt[i].startsWith("DATETIME")) {
						DataCell.setCellValue(rs.getTimestamp(md
								.getColumnName(i + 1)));
						style[i].setDataFormat(format.getFormat(timeformat));
						DataCell.setCellStyle(style[i]);
					} else if (dt[i].startsWith("DATE")) {
						DataCell.setCellValue(rs.getDate(md
								.getColumnName(i + 1)));
						style[i].setDataFormat(format.getFormat(dateformat));
						DataCell.setCellStyle(style[i]);
					}else if (dt[i].startsWith("PERCENTAGE")) {
						DataCell.setCellValue(rs.getDouble(md
								.getColumnName(i + 1)) / 100);
						style[i].setDataFormat(format
								.getFormat(ExcelDatatypeFormating.perc_format(dt[i])));
						DataCell.setCellStyle(style[i]);
					}
					// varchar
					else {
						int length = rs.getString(md.getColumnName(i + 1))
								.length();
						if (length > 100) {

							DataCell.setCellValue(rs.getString(md
									.getColumnName(i + 1)));
							style[i].setWrapText(true);
							DataCell.setCellStyle(style[i]);
							sheet.autoSizeColumn(i);

						} else {
							DataCell.setCellValue(rs.getString(md
									.getColumnName(i + 1)));
						}
					}

				} else {
					DataCell.setCellValue("");
				}
			}
		}
		//autoSizeColXLSX(sheet, colno);
	}

	
	public static XSSFCellStyle setXLSXHeaderStyle(
			XSSFWorkbook sampleWorkbook, String hdrAlign, String hdrBold,
			String hdrColor) {
		XSSFFont font = sampleWorkbook.createFont();
		font.setColor(IndexedColors.valueOf(hdrColor).getIndex());
		if (hdrBold.equals("1")) {
			font.setBoldweight((short) 700);
		}
		XSSFCellStyle cellStyle1 = sampleWorkbook.createCellStyle();
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

	public static void createXLSXHeaderRows(XSSFSheet sheet,
			XSSFWorkbook workBook, ResultSetMetaData md, int colno,
			String HdrAlign, String HdrBold, String HdrColor)
			throws SQLException {
		int rows = 0;
		XSSFRow HdrRow = sheet.createRow(rows);
		XSSFCellStyle cellStyleSheet = setXLSXHeaderStyle(workBook, HdrAlign,
				HdrBold, HdrColor);
		for (int i = 0; i < colno; i++) {
			XSSFCell HdrCell = HdrRow.createCell(i);
			HdrCell.setCellStyle(cellStyleSheet);
			HdrCell.setCellValue(md.getColumnName(i + 1).toString().trim());
			sheet.autoSizeColumn(i);
		}
	}
	
	public static void autoSizeColXLSX(XSSFSheet sheet, int colno) {
			for (int i = 0; i < colno; i++) {
				sheet.autoSizeColumn(i);
			}
	}
}
