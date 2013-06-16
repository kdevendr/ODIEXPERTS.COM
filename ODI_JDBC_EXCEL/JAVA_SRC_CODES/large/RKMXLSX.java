package large;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.BuiltinFormats;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.eventusermodel.ReadOnlySharedStringsTable;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.model.StylesTable;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;


/**

 */
public class RKMXLSX {

	/**
	 * The type of the data value is indicated by an attribute on the cell. The
	 * value is usually in a "v" element within the cell.
	 */
	enum xssfDataType {
		BOOL, ERROR, FORMULA, INLINESTR, SSTINDEX, NUMBER,
	}

	class MyXSSFSheetHandler extends DefaultHandler {

		/**
		 * Table with styles
		 */
		private StylesTable stylesTable;

		/**
		 * Table with unique strings
		 */
		private ReadOnlySharedStringsTable sharedStringsTable;

		// Set when V start element is seen
		private boolean vIsOpen;

		// Set when cell start element is seen;
		// used when cell close element is seen.
		private xssfDataType nextDataType;

		// Used to format numeric cell values.
		private short formatIndex;
		private String formatString;
		private final DataFormatter formatter;

		private int thisColumn = -1;
		// Gathers characters as they are seen.
		private StringBuffer value;
		private int count = 0;
		private final int HdrRow;
		private String sheetName;
		private PreparedStatement pscol;

		private int ModelId;

		private int[] maxlength = new int[100];
		
		int countrows = 0;
		int psloop;
		boolean process = false;
		int nullcol;
		int maxcolumncount;
		String col;
		String colNo;
		String maxValue;
		String tempValue;
		String checkHeader;
		private int zz = 0;

		private String thisStr="";

		private String datatype="";

		private String scaleValue="";

		private String scale="";

		private int[] maxScale;

		private String[] dt;

		private String scaleValues="";

		private String datatypes="";

		/**
		 * Accepts objects needed while parsing.
		 * 
		 * @param styles
		 *            Table of styles
		 * @param strings
		 *            Table of shared strings
		 * @param cols
		 *            Minimum number of columns to show
		 * @param target
		 *            Sink for output
		 */
		public MyXSSFSheetHandler(PreparedStatement pscol, int HeaderRow,
				int Model, String sheetNm, StylesTable styles,
				ReadOnlySharedStringsTable strings, int cols, PrintStream target) {
			process = true;
			nullcol = 0;
			maxcolumncount = 0;
			col = "";
			colNo = "";
			maxValue = "";
			tempValue = "";
			checkHeader = "";
			this.ModelId = Model;
			this.pscol = pscol;
			this.sheetName = sheetNm;
			this.HdrRow = HeaderRow;
			this.stylesTable = styles;
			this.sharedStringsTable = strings;
			this.value = new StringBuffer();
			this.nextDataType = xssfDataType.NUMBER;
			this.formatter = new DataFormatter();

		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String,
		 * java.lang.String, java.lang.String, org.xml.sax.Attributes)
		 */
		public void startElement(String uri, String localName, String name,
				Attributes attributes) throws SAXException {

			// output.println(name);
			if ("row".equals(name)) {
				//output.println("Row" + count+"\t"+vvalue+"\t"+cvalue);
				count++;
				
			}

			if ("pageMargins".equals(name)) {

				//output.println(tempValue);
				
				if (tempValue.length() >0) {
				try {
					Rev.SnpRevColXLSX(pscol, sheetName, ModelId, tempValue, maxcolumncount);
				} catch (SQLException e) {e.printStackTrace();}

				}
			}

			if ("inlineStr".equals(name) || "v".equals(name)) {
				vIsOpen = true;
				// Clear contents cache
				value.setLength(0);

			}
			// c => cell
			else if ("c".equals(name)) {

				// Get the cell reference
				String r = attributes.getValue("r");
				int firstDigit = -1;
				for (int c = 0; c < r.length(); ++c) {
					if (Character.isDigit(r.charAt(c))) {
						firstDigit = c;
						break;
					}
				}
				thisColumn = nameToColumn(r.substring(0, firstDigit));

				// Set up defaults.
				this.nextDataType = xssfDataType.NUMBER;
				this.formatIndex = -1;
				this.formatString = null;
				String cellType = attributes.getValue("t");
				String cellStyleStr = attributes.getValue("s");
				if ("b".equals(cellType))
					nextDataType = xssfDataType.BOOL;
				else if ("e".equals(cellType))
					nextDataType = xssfDataType.ERROR;
				else if ("inlineStr".equals(cellType))
					nextDataType = xssfDataType.INLINESTR;
				else if ("s".equals(cellType))
					nextDataType = xssfDataType.SSTINDEX;
				else if ("str".equals(cellType))
					nextDataType = xssfDataType.FORMULA;
				else if (cellStyleStr != null) {
					// It's a number, but almost certainly one
					// with a special style or format
					int styleIndex = Integer.parseInt(cellStyleStr);
					XSSFCellStyle style = stylesTable.getStyleAt(styleIndex);
					this.formatIndex = style.getDataFormat();
					this.formatString = style.getDataFormatString();
					if (this.formatString == null)
						this.formatString = BuiltinFormats
								.getBuiltinFormat(this.formatIndex);

				}
			}

		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String,
		 * java.lang.String, java.lang.String)
		 */
		public void endElement(String uri, String localName, String name)
				throws SAXException {

			// Header
			if (count == HdrRow) {

				// v => contents of a cell
				if ("v".equals(name)) {
					
					thisStr = "";
					// Process the value contents as required.
					// Do now, as characters() may be called more than once
					switch (nextDataType) {

					case BOOL:
						char first = value.charAt(0);
						thisStr = first == '0' ? "FALSE" : "TRUE";
						break;

					case ERROR:
						thisStr = "\"ERROR:" + value.toString() + '"';
						break;

					case FORMULA:
						// A formula could result in a string value,
						// so always add double-quote characters.
						thisStr = '"' + value.toString() + '"';
						break;

					case INLINESTR:
						XSSFRichTextString rtsi = new XSSFRichTextString(
								value.toString());
						thisStr = '"' + rtsi.toString() + '"';
						break;

					case SSTINDEX:
						String sstIndex = value.toString();
						// output.print(sstIndex);
						try {
							int idx = Integer.parseInt(sstIndex);
							XSSFRichTextString rtss = new XSSFRichTextString(
									sharedStringsTable.getEntryAt(idx));
							thisStr = '"' + rtss.toString() + '"';
						} catch (NumberFormatException ex) {
							ex.printStackTrace();
							/*
							 * output.println("Failed to parse SST index '" +
							 * sstIndex + "': " + ex.toString());
							 */
						}
						break;

					case NUMBER:
						String n = value.toString();

						if (this.formatString != null) {
							// output.println(this.formatString);
							thisStr = formatter.formatRawCellContents(
									Double.parseDouble(n), this.formatIndex,
									this.formatString);
						} else
							thisStr = n;
						break;

					default:
						thisStr = "(TODO: Unexpected type: " + nextDataType
								+ ")";
						break;
					}

					// PROCESS HEADER
					nullcol++;
					//output.println(thisStr);
					checkHeader += thisStr.trim();
					col += ProcessColumn.getCol(thisStr, nullcol) + "-";
					colNo += thisColumn + 1 + "-";
					if (thisColumn < 100) {
						maxlength = new int[250];
						maxScale=new int[250];
						dt=new String[250];
					} else if (thisColumn > 250) {
						maxlength = new int[2500];
						maxScale=new int[2500];
						dt=new String[2500];
					} else if (thisColumn > 2500) {
						maxlength = new int[10000];
						maxScale=new int[10000];
						dt=new String[10000];
					} else if (thisColumn > 10000) {
						maxlength = new int[25000];
						maxScale=new int[25000];
						dt=new String[25000];
					}
					zz = 0;
					
					
					for (int i = 0; i < maxlength.length; i++) {
						dt[i] = "";
						maxlength[i]=0;
						maxScale[i]=0;
					}
				}
				
			}
			// Datatypes
			else if (count > HdrRow && count <500) {

				// output.println(name + "\t" + nextDataType);
				// v => contents of a cell
				if ("v".equals(name)) {
					thisStr = "";
					if (zz == 0) {
						// output.print(col);
						if (checkHeader.length() == 0) {
							try {
								throw new Exception(
										"\n\n The Header Row ("
												+ HdrRow
												+ ") for the Sheet - "
												+ sheetName
												+ " is blank.\n Please provide another Header row or check if its valid Sheet");
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						col = col.substring(0, col.length() - 1);
						colNo = colNo.substring(0, colNo.length() - 1);
						zz++;
					}

					// Process the value contents as required.
					// Do now, as characters() may be called more than once
					switch (nextDataType) {

					case BOOL:
						char first = value.charAt(0);
						thisStr = first == '0' ? "FALSE" : "TRUE";
						datatype="VARCHAR";
						break;

					case ERROR:
						thisStr = "\"ERROR:" + value.toString() + '"';
						datatype="VARCHAR";
						break;

					case FORMULA:
						// A formula could result in a string value,
						// so always add double-quote characters.
						thisStr = '"' + value.toString() + '"';
						datatype="VARCHAR";
						break;

					case INLINESTR:
						XSSFRichTextString rtsi = new XSSFRichTextString(
								value.toString());
						thisStr = '"' + rtsi.toString() + '"';
						datatype="VARCHAR";
						break;

					case SSTINDEX:
						String sstIndex = value.toString();
						// output.println(sstIndex);
						try {
							int idx = Integer.parseInt(sstIndex);
							XSSFRichTextString rtss = new XSSFRichTextString(
									sharedStringsTable.getEntryAt(idx));
							thisStr = '"' + rtss.toString() + '"';
							datatype="VARCHAR";
						} catch (NumberFormatException ex) {
							ex.printStackTrace();
							/*
							 * output.println("Failed to parse SST index '" +
							 * sstIndex + "': " + ex.toString());
							 */
						}
						break;

					case NUMBER:
						String n = value.toString();
						scaleValue =n.replace(".0", "");
						if (scaleValue.indexOf(".") > 0) {
							scale = scaleValue.substring(scaleValue.indexOf("."),
									scaleValue.length());
						} else {
							scale = "";
						}

						if (this.formatString != null) {
							thisStr = formatter.formatRawCellContents(
									Double.parseDouble(n), this.formatIndex,
									this.formatString);
							
							datatype="DATE";
							//needs to add DATETIME
							
						} else {
							thisStr = n;
						    datatype="NUMERIC";
						}
						
						
						break;

					default:
						thisStr = "(TODO: Unexpected type: " + nextDataType
								+ ")";
						break;
					}

				} else if ("c".equals(name)) {
					
					if (thisStr.length() > maxlength[thisColumn]) {
						maxlength[thisColumn] = thisStr.length();
					}
					
					if ((scale.length()) > maxScale[thisColumn]) {
						maxScale[thisColumn] = (scale.length() - 1);
					}
					
					if (count == (HdrRow)) {
						dt[thisColumn] = datatype;
					}
					if (count >= (HdrRow + 1)) {
						if (dt[thisColumn].equals(datatype)) {
							dt[thisColumn] = datatype;
						} else {
							dt[thisColumn] = "VARCHAR";
						}
					}
					
					if (thisColumn > maxcolumncount) {
						maxcolumncount = thisColumn;
					}
					maxValue = "";
					scaleValues="";
					datatypes="";
					for (int i = 0; i < maxlength.length; i++) {
						maxValue += maxlength[i] + "-";
						scaleValues+=maxScale[i]+"-";
						datatypes+=dt[i]+"-";
					}
					maxValue = maxValue.substring(0, maxValue.length() - 1);
					scaleValues = scaleValues
							.substring(0, scaleValues.length() - 1);
					datatypes = datatypes.substring(0, datatypes.length() - 1);
					tempValue = col + ";" + colNo + ";" + maxValue+ ";"
							+ scaleValues + ";" + datatypes;
					
				} else {
					thisStr = "";
				}
			}

		}

		/**
		 * Captures characters only if a suitable element is open. Originally
		 * was just "v"; extended for inlineStr also.
		 */
		public void characters(char[] ch, int start, int length)
				throws SAXException {
			if (vIsOpen)
				value.append(ch, start, length);
		}

		/**
		 * Converts an Excel column name like "C" to a zero-based index.
		 * 
		 * @param name
		 * @return Index corresponding to the specified name
		 */
		private int nameToColumn(String name) {
			int column = -1;
			for (int i = 0; i < name.length(); ++i) {
				int c = name.charAt(i);
				column = (column + 1) * 26 + c - 'A';
			}
			return column;
		}

	}

	// /////////////////////////////////////

	private OPCPackage xlsxPackage;
	private int minColumns;
	private PrintStream output;
	private String sheetName;

	/**
	 * Creates a new XLSX -> CSV converter
	 * 
	 * @param pkg
	 *            The XLSX package to process
	 * @param output
	 *            The PrintStream to output the CSV to
	 * @param minColumns
	 *            The minimum number of columns to output, or -1 for no minimum
	 */
	public RKMXLSX(OPCPackage pkg, PrintStream output, int minColumns) {
		this.xlsxPackage = pkg;
		this.output = output;
		this.minColumns = minColumns;
	}

	/**
	 * Parses and shows the content of one sheet using the specified styles and
	 * shared-strings tables.
	 * 
	 * @param styles
	 * @param strings
	 * @param sheetInputStream
	 */
	public void processSheet(PreparedStatement pscol, int HeaderRow,
			int ModelNo, StylesTable styles,
			ReadOnlySharedStringsTable strings, InputStream sheetInputStream)
			throws IOException, ParserConfigurationException, SAXException {

		InputSource sheetSource = new InputSource(sheetInputStream);
		SAXParserFactory saxFactory = SAXParserFactory.newInstance();
		SAXParser saxParser = saxFactory.newSAXParser();
		XMLReader sheetParser = saxParser.getXMLReader();
		MyXSSFSheetHandler sheetHandler = new MyXSSFSheetHandler(pscol,
				HeaderRow, ModelNo, sheetName, styles, strings,
				this.minColumns, this.output);
		ContentHandler handler = sheetHandler;
		sheetParser.setContentHandler(handler);
		sheetParser.parse(sheetSource);

	}

	/**
	 * Initiates the processing of the XLS workbook file to CSV.
	 * 
	 * @throws IOException
	 * @throws OpenXML4JException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws SQLException
	 */
	public void process(Connection conn, String sheetNo, int modelNo,
			int headerRow, String Mask,String ContinuousSheet) throws IOException, OpenXML4JException,
			ParserConfigurationException, SAXException, SQLException {

		ReadOnlySharedStringsTable strings = new ReadOnlySharedStringsTable(
				this.xlsxPackage);
		XSSFReader xssfReader = new XSSFReader(this.xlsxPackage);
		StylesTable styles = xssfReader.getStylesTable();
		XSSFReader.SheetIterator iter = (XSSFReader.SheetIterator) xssfReader
				.getSheetsData();

		// REV_TABLE
		String sqltxt = "insert into SNP_REV_TABLE (I_MOD,TABLE_NAME,RES_NAME,TABLE_ALIAS,R_COUNT,IND_SHOW,TABLE_TYPE) values (?,?,?,?,?,?,?)";
		PreparedStatement pstable = conn.prepareStatement(sqltxt);
		sqltxt = "insert into SNP_REV_COL (I_MOD,TABLE_NAME,COL_NAME,DT_DRIVER,POS,LONGC,SCALEC,CHECK_FLOW,CHECK_STAT) values (?,?,?,?,?,?,?,?,?)";
		PreparedStatement pscol = conn.prepareStatement(sqltxt);
		int index = 1;
		// All the Sheets

		while (iter.hasNext()) {
			// this.output.println(index);
			InputStream stream = iter.next();
			sheetName = iter.getSheetName();

			try {
				if (sheetNo.equals("%")) {
					this.output.println("Reversing Sheet " + sheetName);
					Rev.SnpRevTable(pstable, sheetName, modelNo, headerRow,
							Mask);
					processSheet(pscol, headerRow, modelNo, styles, strings,
							stream);

				} // - based values
				else if (sheetNo.indexOf("-") > 0) {
					String[] sheets = sheetNo.split("-");
					for (int i = Integer.parseInt(sheets[0]); i < Integer
							.parseInt(sheets[1]) + 1; i++) {
						if (index == i) {
							this.output.println("Reversing Sheet " + sheetName);
							// SNP_REV_TABLE
							Rev.SnpRevTable(pstable, sheetName, modelNo,
									headerRow, Mask);
							processSheet(pscol, headerRow, modelNo, styles, strings,
									stream);


						}
					}
				} // , based Values
				else {
					String[] sheets = sheetNo.split(",");
					for (int i = 0; i < sheets.length; i++) {
						if (index == Integer.parseInt(sheets[i])) {
							this.output.println("Reversing Sheet " + sheetName);
							// SNP_REV_TABLE
							if (ContinuousSheet.equals("1")) {
								sheetName=sheetName.substring(0,3)+"**";
							} 
							Rev.SnpRevTable(pstable, sheetName, modelNo,
									headerRow, Mask);
							
							processSheet(pscol, headerRow, modelNo, styles, strings,
									stream);


						}
					}
				}

				++index;
				stream.close();

			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		pstable.close();
		pscol.close();

	}
}