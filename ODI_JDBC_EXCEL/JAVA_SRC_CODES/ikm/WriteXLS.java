package ikm;

import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.IndexedColors;


public class WriteXLS {

	public static void writeXLSCellData(String fileName,FileOutputStream outputFile,String tableName, HSSFRow DataRow,
			ResultSet rs, HSSFSheet sheet, int rows, int colno,
			HSSFWorkbook workBook, ResultSetMetaData md, int sheetNo,
			HSSFCell DataCell, String HdrAlign,
			String HdrBold, String HdrColor,
			String datatype, String dateformat, String timeformat,
			String currencysymbol) throws SQLException, InvalidFormatException, IOException {

		String[] dt = datatype.split(",");
		DataFormat format = workBook.createDataFormat();
		HSSFCellStyle[] style = new HSSFCellStyle[colno];
		for (int i = 0; i < colno; i++) {
			style[i] = workBook.createCellStyle();
		}

		while (rs.next()) {
			rows+=1;
			DataRow = sheet.createRow(rows);
			if (rows == 65535) {
				
				//autoSizeColXLS(sheet, colno);
				sheetNo++;
				
  			    sheet = workBook.createSheet((new StringBuilder(String
						.valueOf(tableName.toUpperCase()))).append(sheetNo)
						.toString()); 
  			    
				createXLSHeaderRows(sheet, workBook, md, colno, HdrAlign,
					HdrBold, HdrColor);
				rows = 0;
			}
			
			for (int i = 0; i < colno; i++) {
				DataCell = DataRow.createCell(i);
				if (rs.getString(md.getColumnName(i + 1)) != null) {
					if (dt[i].startsWith("NUMERIC") || dt[i].startsWith("SCIENTIFIC")|| dt[i].startsWith("CURRENCY") ) {
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
					} 
					else if (dt[i].startsWith("PERCENTAGE")) {
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
						if (length > 200) {

							DataCell.setCellValue(rs.getString(md
									.getColumnName(i + 1)));
							style[i].setWrapText(true);
							DataCell.setCellStyle(style[i]);

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
		//autoSizeColXLS(sheet, colno);
	}
	
	
	private static HSSFCellStyle setXLSHeaderStyle(
			HSSFWorkbook sampleWorkbook, String hdrAlign, String hdrBold,
			String hdrColor) {
		HSSFFont font = sampleWorkbook.createFont();
		font.setColor(IndexedColors.valueOf(hdrColor).getIndex());
		if (hdrBold.equals("1")) {
			font.setBoldweight((short) 700);
		}
		HSSFCellStyle cellStyle1 = sampleWorkbook.createCellStyle();
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
	
	
	public static void createXLSHeaderRows(HSSFSheet sheet,
			HSSFWorkbook workBook, ResultSetMetaData md, int colno,
			String HdrAlign, String HdrBold, String HdrColor)
			throws SQLException {
		int rows = 0;
		HSSFRow HdrRow = sheet.createRow(rows);
		HSSFCellStyle cellStyleSheet = setXLSHeaderStyle(workBook, HdrAlign,
				HdrBold, HdrColor);
		for (int i = 0; i < colno; i++) {
			HSSFCell HdrCell = HdrRow.createCell(i);
			HdrCell.setCellStyle(cellStyleSheet);
			HdrCell.setCellValue(md.getColumnName(i + 1).toString().trim());
			sheet.autoSizeColumn(i);
		}
	}
	
	
	public static void autoSizeColXLS(HSSFSheet sheet, int colno) {
			for (int i = 0; i < colno; i++) {
				sheet.autoSizeColumn(i);
		}
	}
	
	
	
}
