package large;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;

import org.apache.poi.ss.usermodel.BuiltinFormats;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.xssf.eventusermodel.ReadOnlySharedStringsTable;
import org.apache.poi.xssf.model.StylesTable;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import excel.CatchException;

public class LKMXLSX extends DefaultHandler {

	private StylesTable stylesTable;
	private ReadOnlySharedStringsTable sharedStringsTable;
	private boolean vIsOpen;
	private xssfDataType nextDataType;
	private short formatIndex;
	private String formatString;
	private final DataFormatter formatter;

	private int thisColumn = -1;
	private int lastColumnNumber = -1;
	private StringBuffer value;
	private int count = 0;
	private final int HdrRow;
	private PreparedStatement ps;
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
	private String thisStr;

	private String[] columnlist;

	private String[] dt;
	private String n;

	private int index;
	private String rowdata = "";
	private int rowno = 0;
	private StringWriter errors;
	private boolean processloop = true ;

	enum xssfDataType {
		BOOL, ERROR, FORMULA, INLINESTR, SSTINDEX, NUMBER,
	}

	public LKMXLSX(PreparedStatement ps, String collist, int HeaderRow,
			StylesTable styles, ReadOnlySharedStringsTable strings,
			String datatype) {
		
		
		this.ps = ps;
		this.HdrRow = HeaderRow;
		this.stylesTable = styles;
		this.sharedStringsTable = strings;
		this.value = new StringBuffer();
		this.nextDataType = xssfDataType.NUMBER;
		this.formatter = new DataFormatter();
		columnlist = collist.split(",");
		dt = datatype.split(",");
		}
	

	public void startElement(String uri, String localName, String name,
			Attributes attributes) throws SAXException {
		// output.println(name + "\t" + nextDataType);
		
		if (CatchException.Result().length() >0) {
			processloop  = false;
			//CatchException.StoreExcept("InvalidException");
			throw new SAXException();
			
		}

  if (processloop == true) {
		if ("row".equals(name)) {
			count++;
			index = 0;
			rowdata = "";
			 errors = new StringWriter();
			// System.out.println(count);
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
	}
	

	public void endElement(String uri, String localName, String name)
			throws SAXException {
	
 if (processloop == true) {
		// v => contents of a cell
		if ("v".equals(name) && count > HdrRow) {
			thisStr = "";
			n = "";
			
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
				// output.println(sstIndex);
				try {
					int idx = Integer.parseInt(sstIndex);
					XSSFRichTextString rtss = new XSSFRichTextString(
							sharedStringsTable.getEntryAt(idx));
					thisStr = '"' + rtss.toString() + '"';
				} catch (NumberFormatException e) {
					
					e.printStackTrace(new PrintWriter(errors));
					CatchException.StoreExcept(errors.toString());
					/*
					 * output.println("Failed to parse SST index '" + sstIndex +
					 * "': " + ex.toString());
					 */
				}
				break;

			case NUMBER:
				n = value.toString();
				if (this.formatString != null) {
					thisStr = formatter.formatRawCellContents(
							Double.parseDouble(n), this.formatIndex,
							this.formatString);

				} else {
					thisStr = n;
				}

				break;

			default:
				thisStr = "(TODO: Unexpected type: " + nextDataType + ")";
				break;
			}

			// Output after we've seen the string contents
			// Emit commas for any fields that were missing on this row

			if (lastColumnNumber == -1) {
				lastColumnNumber = 0;
			}

			for (int zz = lastColumnNumber; zz < thisColumn; ++zz) {
				rowdata += "-,-";
			}

			if (thisStr.startsWith("\"")) {
				thisStr = thisStr.substring(1, thisStr.length() - 1);
			}
			rowdata += thisStr;
			// Update column
			if (thisColumn > -1)
				lastColumnNumber = thisColumn;

		} else if ("row".equals(name) && count > HdrRow) {
			//System.out.println(thisColumn);
			//System.out.println(rowdata);

			String[] cols = rowdata.split("-,-");
			//System.out.println(cols[0]);
			for (int zz = 0; zz < thisColumn + 1; zz++) {
				for (int i = 0; i < columnlist.length; i++) {
					if ((zz + 1) == Integer.parseInt(columnlist[i])) {
						index++;

						// output.println(index+"\t"+cols[zz]);
						try {
						if (zz >= cols.length) {
								ps.setObject(index, "");
						} else {
							if (cols[zz].length() == 0) {
									ps.setObject(index, "");
							} else {
								thisStr = cols[zz];
								//System.out.println(thisStr);
								// Datatype
								if ((dt[i].toUpperCase().equals("NUMERIC"))
										|| (dt[i].toUpperCase()
												.equals("CURRENCY"))
										|| (dt[i].toUpperCase()
												.equals("SCIENTIFIC"))) {
									
										ps.setDouble(index,
												Double.parseDouble(thisStr));
									
								}
								// PERCENTAGE
								else if (dt[i].toUpperCase().equals(
										"PERCENTAGE")) {
									
										ps.setDouble(
												index,
												Double.parseDouble(thisStr) * 100);
									
								}
								// DATE
								else if (dt[i].toUpperCase().equals("DATE")) {
									Date date = DateUtil.getJavaDate(Double
											.parseDouble(n));
									java.sql.Date sqlDate = new java.sql.Date(
											date.getTime());
									
										ps.setDate(index, sqlDate);
									
								}
								// DATETIME
								else if (dt[i].toUpperCase().equals("DATETIME")) {
									Date date = DateUtil.getJavaDate(Double
											.parseDouble(n));
									java.sql.Timestamp sqlDate = new java.sql.Timestamp(
											date.getTime());
									
										ps.setTimestamp(index, sqlDate);
									
								}
								// VARCHAR
								else {
										ps.setString(index, thisStr);
								}
							} 
						}

					} catch (SQLException e) {
						errors=new StringWriter();
						e.printStackTrace(new PrintWriter(errors));
						CatchException.StoreExcept(errors.toString());
						break;
					}
				}
			}
			}
			try {
				ps.addBatch();
			} catch (SQLException e) {
				errors=new StringWriter();
				e.printStackTrace(new PrintWriter(errors));
				CatchException.StoreExcept(errors.toString());	
				//e.printStackTrace();
			}

			if (rowno == 100) {
				//System.out.println(rowno);
				rowno = 0;
				try {
					ps.executeBatch();
				} catch (SQLException e) {
					errors=new StringWriter();
					e.printStackTrace(new PrintWriter(errors));
					CatchException.StoreExcept(errors.toString());
					//e.printStackTrace();
				}
			}
			rowno++;
			lastColumnNumber = -1;
		}
      }
	}

	public void characters(char[] ch, int start, int length)
			throws SAXException {
		if (vIsOpen)
			value.append(ch, start, length);
	}

	private int nameToColumn(String name) {
		int column = -1;
		for (int i = 0; i < name.length(); ++i) {
			int c = name.charAt(i);
			column = (column + 1) * 26 + c - 'A';
		}
		return column;
	}
	
	/*public StringWriter errorMessage() {
		
		return errors;
	}*/

}
