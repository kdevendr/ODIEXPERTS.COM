package excel;

import java.io.FileInputStream;
import java.io.IOException;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class WorkBook {

	public static Workbook getXLSworkbook(String fileName)
	        throws IOException
	    {
	        FileInputStream inputFile = new FileInputStream(fileName);
	        Workbook WorkBook = new HSSFWorkbook(inputFile);
	        inputFile.close();
	        return WorkBook;
	    }

	    public static Workbook getXLSXworkbook(String fileName)
	        throws IOException
	    {
	        FileInputStream inputFile = new FileInputStream(fileName);
	        Workbook WorkBook = new XSSFWorkbook(inputFile);
	        inputFile.close();
	        return WorkBook;
	    }
	    
	    
	    public static Workbook getworkbook(String fileName)
		        throws IOException, InvalidFormatException
		    {
		        FileInputStream inputFile = new FileInputStream(fileName);
		        Workbook WorkBook = WorkbookFactory.create(inputFile);
		        inputFile.close();
		        return WorkBook;
		    }
	    
	    
}
