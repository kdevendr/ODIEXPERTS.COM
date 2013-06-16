package large;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackageAccess;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.xssf.eventusermodel.ReadOnlySharedStringsTable;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.model.StylesTable;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import excel.CatchException;

public class LKMLarge {

	private static int HdrRow;
	private static PreparedStatement ps;
	private static boolean trim;
	private static String shName;
	private static InputSource sheetSource;
	private static SAXParserFactory saxFactory;
	private static InputStream stream;
	private static SAXParser saxParser;
	private static XMLReader sheetParser;
	private static LKMXLSX sheetHandler;

	public static void ReadExcel(String fileName, String SheetName,
			String collist, String sqltxt, Connection conn, String trimvalue,
			String HeaderRow, String datatype,String CountSheet) throws Exception {

		// All the columns needs to be Mapped to Staging only
		String[] col = collist.split(",");
		if (col.length == 0) {
				throw new Exception(
						"\n\n All columns needs to be mapped to Staging only in Interface. \n Please make the desired changes and reexecute your interface");
			
		} else {
			for (String string : col) {
				if (string.length() == 0) {
					
						throw new Exception(
								"\n\n All columns needs to be mapped to Staging only in Interface. \n Please make the desired changes and reexecute your interface");
				}
			}
		}
		if (HeaderRow != null) {
			HdrRow = Integer.parseInt(HeaderRow);
		} else {
			HdrRow = 1;
		}

		if (trimvalue.equals("1")) {
			trim = true;
		} else {
			trim = false;
		}
			ps = conn.prepareStatement(sqltxt);
		
		if (fileName.endsWith(".xls")) {
			POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(fileName));
			
			LKMXLS xls = new LKMXLS(fs, System.out, 2, ps, SheetName, collist,
					trim, HdrRow, datatype,CountSheet);
			
			
			try {
				xls.process();
			} catch (Exception e) {
				
			}
				if (CatchException.Result().length() >0) {
					throw new Exception(CatchException.Result());
				}
			
		} else {
			File xlsxFile = new File(fileName);
			OPCPackage pkg = OPCPackage.open(xlsxFile.getPath(),
					PackageAccess.READ);

			ReadOnlySharedStringsTable strings = new ReadOnlySharedStringsTable(
					pkg);
			XSSFReader xssfReader = new XSSFReader(pkg);
			StylesTable styles = xssfReader.getStylesTable();
			XSSFReader.SheetIterator iter = (XSSFReader.SheetIterator) xssfReader
					.getSheetsData();

			while (iter.hasNext()) {
				// this.output.println(index);
				stream = iter.next();
				shName = iter.getSheetName();
				// Countinuous Sheet 
				
				if (CountSheet.equals("1")) {
					
					System.out.println("Loading Excel Sheet - " + shName);
					// Processing
					sheetSource = new InputSource(stream);
					saxFactory = SAXParserFactory.newInstance();
					saxParser = saxFactory.newSAXParser();
					sheetParser = saxParser.getXMLReader();

					try {
						 sheetHandler=new LKMXLSX(ps, collist, HdrRow, styles, strings, datatype);
						 ContentHandler handler = sheetHandler;
						 sheetParser.setContentHandler(handler);
						 sheetParser.parse(sheetSource);
						
						 } catch(SAXException e) {
							 // Exception
							/* if (CatchException.Result().indexOf("InvalidException") >=0) {
								 throw new Exception(CatchException.Result().replace("InvalidException", ""));
							 } */
						}
					
					if (CatchException.Result().length() >0) {
						throw new Exception(CatchException.Result());
					}
					
				} else { 
					if (shName.toUpperCase().equals(SheetName.toUpperCase())) {
						System.out.println("Loading Excel Sheet - " + shName);
						// Processing
						sheetSource = new InputSource(stream);
						saxFactory = SAXParserFactory.newInstance();
						saxParser = saxFactory.newSAXParser();
						sheetParser = saxParser.getXMLReader();
	
						
						 try {
						 sheetHandler=new LKMXLSX(ps, collist, HdrRow, styles, strings, datatype);
						 ContentHandler handler = sheetHandler;
						 sheetParser.setContentHandler(handler);
						 sheetParser.parse(sheetSource);
						
						 } catch(SAXException e) {
							 // Exception
							/* if (CatchException.Result().indexOf("InvalidException") >=0) {
								 throw new Exception(CatchException.Result().replace("InvalidException", ""));
							 } */
						}
						
						if (CatchException.Result().length() >0) {
							throw new Exception(CatchException.Result());
						}
					}
				}
				stream.close();				
			}
		}
			try {
				ps.executeBatch();
				ps.close();
			} catch (SQLException e) {

				e.printStackTrace();
			}
			
		
	}
	
	
	
}
