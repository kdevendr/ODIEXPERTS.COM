package ikm;

import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import excel.WorkBook;



public class WriteExcel {

	public static void WriteExcelData(String fileName, String tableName,
			Connection conn, String SQL, String HdrAlign, String HdrBold,
			String HdrColor, String datatype,String dateformat, String timeformat, String currencysymbol,
			String FileAlreadyExists,String LargeXLSXFile) throws Exception {

		
		FileOutputStream outputFile = null;
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(SQL);
		ResultSetMetaData md = rs.getMetaData();
		int colno = md.getColumnCount();
		int sheetNo = 1;
		int rows = 0;

		if (fileName.endsWith(".xls")) {
			HSSFWorkbook workBook;
			HSSFRow DataRow = null;
			HSSFCell DataCell = null;
			if (FileAlreadyExists.equals("1")) {
				workBook = (HSSFWorkbook) WorkBook.getworkbook(fileName);
			} else {
				workBook = new HSSFWorkbook();
			}

			HSSFSheet sheet = workBook.createSheet((new StringBuilder(String
					.valueOf(tableName.toUpperCase()))).append("_")
					.append(sheetNo).toString());

			WriteXLS.createXLSHeaderRows(sheet, workBook, md, colno,
					HdrAlign, HdrBold, HdrColor);
			
			WriteXLS
					.writeXLSCellData(fileName,outputFile,
							(new StringBuilder(String.valueOf(tableName
									.toUpperCase()))).append("_").toString(),
							DataRow, rs, sheet, rows, colno, workBook, md,
							sheetNo, DataCell, HdrAlign, HdrBold, HdrColor,
							datatype, dateformat, timeformat,
							currencysymbol);
			outputFile = new FileOutputStream(fileName);
			workBook.write(outputFile);

		} 
					
		else {
			
			if (LargeXLSXFile.equals("0")) {
						//Smaller Load
				
						XSSFWorkbook workBook;
						XSSFCellStyle cellStyleSheet = null;
						XSSFRow DataRow = null;
						XSSFCell DataCell = null;
						if (FileAlreadyExists.equals("1")) {
							workBook = (XSSFWorkbook) WorkBook.getworkbook(fileName);
						} else {
							workBook = new XSSFWorkbook();
						}
			
						XSSFSheet sheet = workBook.createSheet((new StringBuilder(String
								.valueOf(tableName.toUpperCase()))).append("_")
								.append(sheetNo).toString());
			
						WriteXLSX.createXLSXHeaderRows(sheet, workBook, md, colno,
								HdrAlign, HdrBold, HdrColor);
						
							WriteXLSX
									.writeXLSXCellData(
											(new StringBuilder(String.valueOf(tableName
													.toUpperCase()))).append("_").toString(),
											DataRow, rs, sheet, rows, colno, workBook, md,
											sheetNo, DataCell, cellStyleSheet, HdrAlign,
											HdrBold, HdrColor, datatype,
											dateformat, timeformat, currencysymbol);
							outputFile = new FileOutputStream(fileName);
							workBook.write(outputFile);
			} else {

					//Huge Load
					    SXSSFWorkbook workBook = new SXSSFWorkbook(100);
					    workBook.setCompressTempFiles(true);
					   
					    SXSSFSheet sheet = (SXSSFSheet) workBook.createSheet((new StringBuilder(String
								.valueOf(tableName.toUpperCase()))).append("_")
								.append(sheetNo).toString());
					     WriteXLSXLarge.createXLSXHeaderRows(sheet, workBook, md, colno, HdrAlign,HdrBold, HdrColor);
					     
					   // WriteXLSXLarge.autoSizeColXLSX(sheet, colno);
					    
						WriteXLSXLarge.writeXLSXCellData(
							(new StringBuilder(String.valueOf(tableName
									.toUpperCase()))).append("_").toString(),
							rs, sheet, rows, colno, workBook, md,
							sheetNo, HdrAlign,HdrBold, HdrColor, datatype,
							dateformat, timeformat, currencysymbol,outputFile);
						
						//WriteXLSXLarge.autoSizeColXLSX(sheet, colno);
					    outputFile = new FileOutputStream(fileName);
					    workBook.write(outputFile);
			}
		}

		outputFile.flush();
		outputFile.close();
	}

}
