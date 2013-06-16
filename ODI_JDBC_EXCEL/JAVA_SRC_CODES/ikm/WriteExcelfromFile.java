package ikm;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import excel.WorkBook;

public class WriteExcelfromFile {
	
						public static void WriteExcel(String fileName,String tableName,String FileAlreadyExists,String technology,String SrcFileName,String delim) throws Exception {
							

							
							//Loading from File 
							 if (technology.toUpperCase().equals("FILE")) {
											 if (fileName.endsWith(".xls")) {
												 WriteExcelfromFile.WriteXLSExcelfromFile(FileAlreadyExists, fileName, tableName, SrcFileName, delim);
											 }else {
												 WriteExcelfromFile.WriteXLSXExcelfromFile(FileAlreadyExists, fileName, tableName, SrcFileName, delim);
											 }
											 
									} 
							
						}
	
	
	
	public static void WriteXLSExcelfromFile(String FileAlreadyExists, String fileName, String tableName, String SrcFileName, String delim) throws IOException, InvalidFormatException {
        FileOutputStream outputFile = null;
        HSSFWorkbook workBook;
        HSSFRow DataRow = null;
        HSSFCell DataCell = null;
        int sheetNo=1;
        if (FileAlreadyExists.equals("1")) {
              workBook = (HSSFWorkbook) WorkBook.getworkbook(fileName);
        } else {
              workBook = new HSSFWorkbook();
        }
        
        HSSFSheet sheet = workBook.createSheet((new StringBuilder(String
                    .valueOf(tableName.toUpperCase()))).append("_")
                    .append(sheetNo).toString());
        
        String strLine;
        int colno=0;
        BufferedReader br = new BufferedReader(new FileReader(SrcFileName));
        int rows=0;
         while ((strLine = br.readLine()) != null)   {    
               DataRow = sheet.createRow(rows);
               rows+=1;
               if (rows == 100) {
            	   break;
               }
               if (rows == 65535) {
                     WriteXLS.autoSizeColXLS(sheet, colno);
                          sheetNo++;
                          sheet = workBook.createSheet((new StringBuilder(String
                                      .valueOf(tableName.toUpperCase()))).append(sheetNo)
                                      .toString());
                                      rows = 0;
                                }
                           String[] col=strLine.split(delim);
                           int i=0;
                           for (String filecol : col) {
                                 DataCell = DataRow.createCell(i);
                                 DataCell.setCellValue(filecol);
                                 i+=1;
                          }
                           WriteXLS.autoSizeColXLS(sheet, DataRow.getLastCellNum());
                    }
                    br.close();
                    outputFile = new FileOutputStream(fileName);
                    workBook.write(outputFile);
                    outputFile.flush();
                    outputFile.close();
              }
  
  
  public static void WriteXLSXExcelfromFile(String FileAlreadyExists, String fileName, String tableName, String SrcFileName, String delim) throws IOException, InvalidFormatException {
        FileOutputStream outputFile = null;
        XSSFWorkbook workBook;
        XSSFRow DataRow = null;
        XSSFCell DataCell = null;
        int sheetNo=1;
        if (FileAlreadyExists.equals("1")) {
              workBook = (XSSFWorkbook) WorkBook.getworkbook(fileName);
        } else {
              workBook = new XSSFWorkbook();
        }
        
        XSSFSheet sheet = workBook.createSheet((new StringBuilder(String
                    .valueOf(tableName.toUpperCase()))).append("_")
                    .append(sheetNo).toString());
        
        String strLine;
        int colno=0;
        BufferedReader br = new BufferedReader(new FileReader(SrcFileName));
        int rows=0;
         while ((strLine = br.readLine()) != null)   {    
               DataRow = sheet.createRow(rows);
               rows+=1;
               if (rows == 100) {
            	   break;
               }
               //Data 
               if (rows == 1048575) {
                     WriteXLSX.autoSizeColXLSX(sheet, colno);
                          sheetNo++;
                          sheet = workBook.createSheet((new StringBuilder(String
                                      .valueOf(tableName.toUpperCase()))).append(sheetNo)
                                      .toString());
                                      rows = 0;
                                }
                           String[] col=strLine.split(delim);
                           int i=0;
                           for (String filecol : col) {
                                 DataCell = DataRow.createCell(i);
                                 DataCell.setCellValue(filecol);
                                 i+=1;
                          }
                           WriteXLSX.autoSizeColXLSX(sheet, DataRow.getLastCellNum());
                    }
                    br.close();
                    outputFile = new FileOutputStream(fileName);
                    
                    workBook.write(outputFile);
                    outputFile.flush();
                    outputFile.close();
              }


}
