package large;

import java.io.IOException;
import java.io.PrintStream;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

import org.apache.poi.hssf.eventusermodel.EventWorkbookBuilder.SheetRecordCollectingListener;
import org.apache.poi.hssf.eventusermodel.FormatTrackingHSSFListener;
import org.apache.poi.hssf.eventusermodel.HSSFEventFactory;
import org.apache.poi.hssf.eventusermodel.HSSFListener;
import org.apache.poi.hssf.eventusermodel.HSSFRequest;
import org.apache.poi.hssf.eventusermodel.MissingRecordAwareHSSFListener;
import org.apache.poi.hssf.eventusermodel.dummyrecord.LastCellOfRowDummyRecord;
import org.apache.poi.hssf.eventusermodel.dummyrecord.MissingCellDummyRecord;
import org.apache.poi.hssf.model.HSSFFormulaParser;
import org.apache.poi.hssf.record.BOFRecord;
import org.apache.poi.hssf.record.BlankRecord;
import org.apache.poi.hssf.record.BoolErrRecord;
import org.apache.poi.hssf.record.BoundSheetRecord;
import org.apache.poi.hssf.record.FormulaRecord;
import org.apache.poi.hssf.record.LabelRecord;
import org.apache.poi.hssf.record.LabelSSTRecord;
import org.apache.poi.hssf.record.NoteRecord;
import org.apache.poi.hssf.record.NumberRecord;
import org.apache.poi.hssf.record.RKRecord;
import org.apache.poi.hssf.record.Record;
import org.apache.poi.hssf.record.SSTRecord;
import org.apache.poi.hssf.record.StringRecord;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;


public class RKMXLS implements HSSFListener {

	private int minColumns;
	private POIFSFileSystem fs;
	private PrintStream output;
	private int lastRowNumber;
	private int lastColumnNumber;
	/** Should we output the formula, or the value it has? */
	private boolean outputFormulaValues = true;
	/** For parsing Formulas */
	private SheetRecordCollectingListener workbookBuildingListener;
	private HSSFWorkbook stubWorkbook;
	// Records we pick up as we process
	private SSTRecord sstRecord;
	private FormatTrackingHSSFListener formatListener;
	/** So we known which sheet we're on */
	private int sheetIndex = 0;
	private BoundSheetRecord[] orderedBSRs;
	private ArrayList boundSheetRecords = new ArrayList();
	// For handling formulas with string results
	private int nextRow;
	private int nextColumn;
	private boolean outputNextStringRecord;
	private String sheetName;
	private String sheetNo;
	private PreparedStatement pstable;
	private int modelNo;
	private int headerRow;
	private String Mask;
	private PreparedStatement pscol;
	private int nullcol = 0;
	private int maxcolumncount;
	private int[] maxlength;
	private int[] maxScale;
	private String col = "";
	private String colNo = "";
	private int zz;
	private String maxValue = "";
	private String tempValue = "";
	private int psloop = 0;
	private boolean processSheet = false;
	private String checkHeader="";
	private String scaleValue="";
	private String scale="";
	private String scaleValues="";
	private String datatype="";
	private String[] dt;
	private String datatypes="";
	private String ContinuousSheet;
	
	public RKMXLS(POIFSFileSystem fs, PrintStream output, int minColumns,
			String sheetNo, PreparedStatement pstable, PreparedStatement pscol,
			int modelNo, int headerRow, String Mask,String ContinuousSheet) {
		this.fs = fs;
		this.output = output;
		this.minColumns = minColumns;
		this.sheetNo = sheetNo;
		this.pstable = pstable;
		this.modelNo = modelNo;
		this.headerRow = headerRow;
		this.Mask = Mask;
		this.pscol = pscol;
		this.ContinuousSheet=ContinuousSheet;

	}

	public void process() throws IOException {
		MissingRecordAwareHSSFListener listener = new MissingRecordAwareHSSFListener(
				this);
		formatListener = new FormatTrackingHSSFListener(listener);

		HSSFEventFactory factory = new HSSFEventFactory();
		HSSFRequest request = new HSSFRequest();

		if (outputFormulaValues) {
			request.addListenerForAllRecords(formatListener);
		} else {
			workbookBuildingListener = new SheetRecordCollectingListener(
					formatListener);
			request.addListenerForAllRecords(workbookBuildingListener);
		}

		factory.processWorkbookEvents(request, fs);

		if (tempValue != "") {
			try {
				Rev.SnpRevColXLS(pscol, sheetName, modelNo, tempValue,
						maxcolumncount);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

	}

	public void processRecord(Record record) {

		int thisRow = -1;
		int thisColumn = -1;
		String thisStr = null;

		switch (record.getSid()) {
		case BoundSheetRecord.sid:
			boundSheetRecords.add(record);
			break;
		case BOFRecord.sid:
			processSheet = false;
			BOFRecord br = (BOFRecord) record;
			if (br.getType() == BOFRecord.TYPE_WORKSHEET) {

				if (workbookBuildingListener != null && stubWorkbook == null) {
					stubWorkbook = workbookBuildingListener
							.getStubHSSFWorkbook();

				}

				// Create sub workbook if required
				// Output the worksheet name
				// Works by ordering the BSRs by the location of
				// their BOFRecords, and then knowing that we
				// process BOFRecords in byte offset order
				sheetIndex++;
				if (orderedBSRs == null) {
					orderedBSRs = BoundSheetRecord
							.orderByBofPosition(boundSheetRecords);
				}

				if (sheetNo.equals("%")) {

					//output.println("temp" + tempValue);
					// snp_rev_col
					if (psloop > 0) {
						try {
							Rev.SnpRevColXLS(pscol, sheetName, modelNo,
									tempValue, maxcolumncount);
						} catch (SQLException e) {
							e.printStackTrace();
						}
					}
					psloop++;

					processSheet = true;
					// output.println(sheetIndex);
					sheetName = orderedBSRs[sheetIndex - 1].getSheetname();
					output.println("Reversing Sheet " + sheetName);

					// SNP_REV_TABLE
					try {
						Rev.SnpRevTable(pstable, sheetName, modelNo, headerRow,
								Mask);
					} catch (SQLException e) {
						e.printStackTrace();
					}

					nullcol = 0;
					maxcolumncount = 0;
					col = "";
					colNo = "";
					maxValue = "";
					tempValue = "";
					checkHeader = "";
					scaleValues = "";
				}
				// - based values
				else if (sheetNo.indexOf("-") > 0) {
					String[] sheets = sheetNo.split("-");
					for (int i = Integer.parseInt(sheets[0]); i < Integer
							.parseInt(sheets[1]) + 1; i++) {
						if (sheetIndex == i) {

							// snp_rev_col
							if (psloop > 0) {
								try {
									Rev.SnpRevColXLS(pscol, sheetName, modelNo,
											tempValue, maxcolumncount);
								} catch (SQLException e) {
									e.printStackTrace();
								}
							}
							psloop++;

							processSheet = true;
							sheetName = orderedBSRs[sheetIndex - 1]
									.getSheetname();
							output.println("Reversing Sheet " + sheetName);

							// SNP_REV_TABLE
							try {
								Rev.SnpRevTable(pstable, sheetName, modelNo,
										headerRow, Mask);
							} catch (SQLException e) {
								e.printStackTrace();
							}
							nullcol = 0;
							maxcolumncount = 0;
							col = "";
							colNo = "";
							maxValue = "";
							tempValue = "";
							checkHeader = "";
							scaleValues = "";

						}
					}
				} // , based Values
				else {
					String[] sheets = sheetNo.split(",");
					for (int i = 0; i < sheets.length; i++) {
						if (sheetIndex == Integer.parseInt(sheets[i])) {

							// snp_rev_col
							if (psloop > 0) {
								try {
									Rev.SnpRevColXLS(pscol, sheetName, modelNo,
											tempValue, maxcolumncount);
								} catch (SQLException e) {
									e.printStackTrace();
								}
							}
							psloop++;

							processSheet = true;
							sheetName = orderedBSRs[sheetIndex - 1]
									.getSheetname();
							output.println("Reversing Sheet " + sheetName);
							if (ContinuousSheet.equals("1")) {
								sheetName=sheetName.substring(0,3)+"**";
							} 
							// SNP_REV_TABLE
							try {
								Rev.SnpRevTable(pstable, sheetName, modelNo,
										headerRow, Mask);
							} catch (SQLException e) {
								e.printStackTrace();
							}
							nullcol = 0;
							maxcolumncount = 0;
							col = "";
							colNo = "";
							maxValue = "";
							tempValue = "";
							checkHeader = "";
							scaleValues = "";

						}
					}
				}

			}
			break;

		case SSTRecord.sid:
			sstRecord = (SSTRecord) record;
			break;

		case BlankRecord.sid:
			BlankRecord brec = (BlankRecord) record;

			thisRow = brec.getRow();
			thisColumn = brec.getColumn();
			thisStr = "";
			datatype = "VARCHAR";
			break;
		case BoolErrRecord.sid:
			BoolErrRecord berec = (BoolErrRecord) record;

			thisRow = berec.getRow();
			thisColumn = berec.getColumn();
			thisStr = "";
			datatype = "VARCHAR";
			break;

		case FormulaRecord.sid:
			FormulaRecord frec = (FormulaRecord) record;

			thisRow = frec.getRow();
			thisColumn = frec.getColumn();

			if (outputFormulaValues) {
				if (Double.isNaN(frec.getValue())) {
					// Formula result is a string
					// This is stored in the next record
					outputNextStringRecord = true;
					nextRow = frec.getRow();
					nextColumn = frec.getColumn();
				} else {
					thisStr = formatListener.formatNumberDateCell(frec);
				}
			} else {
				thisStr = '"' + HSSFFormulaParser.toFormulaString(stubWorkbook,
						frec.getParsedExpression()) + '"';
			}
			datatype = "VARCHAR";
			break;
		case StringRecord.sid:
			if (outputNextStringRecord) {
				// String for formula
				StringRecord srec = (StringRecord) record;
				thisStr = srec.getString();
				thisRow = nextRow;
				thisColumn = nextColumn;
				outputNextStringRecord = false;
			}
			datatype = "VARCHAR";
			break;

		case LabelRecord.sid:
			LabelRecord lrec = (LabelRecord) record;

			thisRow = lrec.getRow();
			thisColumn = lrec.getColumn();
			thisStr = '"' + lrec.getValue() + '"';
			datatype = "VARCHAR";
			break;
		case LabelSSTRecord.sid:
			LabelSSTRecord lsrec = (LabelSSTRecord) record;

			thisRow = lsrec.getRow();
			thisColumn = lsrec.getColumn();
			if (sstRecord == null) {
				thisStr = '"' + "(No SST Record, can't identify string)" + '"';
			} else {
				thisStr = '"' + sstRecord.getString(lsrec.getSSTIndex())
						.toString() + '"';
			}
			datatype = "VARCHAR";
			break;
		case NoteRecord.sid:
			NoteRecord nrec = (NoteRecord) record;

			thisRow = nrec.getRow();
			thisColumn = nrec.getColumn();
			thisStr = '"' + "(TODO)" + '"';
			datatype = "VARCHAR";
			break;
		case NumberRecord.sid:
			NumberRecord numrec = (NumberRecord) record;
			//output.println(thisRow);
			thisRow = numrec.getRow();
			thisColumn = numrec.getColumn();
			// numrec.getValue())
			// Format
			thisStr = formatListener.formatNumberDateCell(numrec);
			// Number
			scaleValue = String.valueOf(thisStr).replace(".0", "");
/*			output.println(scaleValue);
			output.println(thisStr);*/
			if (scaleValue.indexOf(".") > 0) {
				scale = scaleValue.substring(scaleValue.indexOf("."),
						scaleValue.length());
			} else {
				scale = "";
			}

			// Date and DateTime
			if (thisStr.indexOf("/") > 0 || thisStr.indexOf("-") > 0
					|| thisStr.indexOf(":") > 0) {
				// output.print("index"+formatListener.getFormatIndex(numrec));
				if (formatListener.getFormatIndex(numrec) >= 10
						&& formatListener.getFormatIndex(numrec) <= 20) {
					datatype = "DATE";
				} else {
					datatype = "DATETIME";
				}
			} else {
				datatype = "NUMERIC";
			}
			break;
		case RKRecord.sid:
			RKRecord rkrec = (RKRecord) record;

			thisRow = rkrec.getRow();
			thisColumn = rkrec.getColumn();
			thisStr = '"' + "(TODO)" + '"';
			break;
		default:
			break;
		}

		if (processSheet == true) {
			// output.println(thisRow);
			// Handle new row
			if (thisRow != -1 && thisRow != lastRowNumber) {
				lastColumnNumber = -1;
			}

			// Handle missing column
			if (record instanceof MissingCellDummyRecord) {
				MissingCellDummyRecord mc = (MissingCellDummyRecord) record;
				thisRow = mc.getRow();
				thisColumn = mc.getColumn();
				thisStr = "";
			}

			// If we got something to print out, do so

			if (thisRow == headerRow - 1) {
				nullcol++;
				checkHeader += thisStr.trim();
				
				if (thisColumn >= 0) {

					col += ProcessColumn.getCol(thisStr, nullcol) + "-";
					colNo += (thisColumn + 1) + "-";
					// scaleValues+=scale+"-";
				}
				
				// maxcolumncount = thisColumn;
				maxlength = new int[256];
				maxScale = new int[256];
				dt = new String[256];

				for (int i = 0; i < dt.length; i++) {
					dt[i] = "";
					maxlength[i]=0;
					maxScale[i]=0;
				}
				zz = 0;

			} else if (thisRow >= headerRow && thisRow < 500) {
				//output.println("Row"+thisRow);
				// remove extra character in col & colNo
				if (zz == 0) {
					// output.print(col);
					if (checkHeader.length() == 0) {
						try {
							throw new Exception(
									"\n\n The Header Row ("
											+ headerRow
											+ ") for the Sheet - "
											+ sheetName
											+ " is blank.\n Please provide another Header row or check if its valid Sheet");
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					col = col.substring(0, col.length() - 1);
					//output.print(col);
					colNo = colNo.substring(0, colNo.length() - 1);
					//output.println(colNo);
				}
				zz++;
				if (thisColumn >= 0) {

					if (thisStr.length() > maxlength[thisColumn]) {
						maxlength[thisColumn] = thisStr.length();
					}

					if ((scale.length()) > maxScale[thisColumn]) {
						maxScale[thisColumn] = (scale.length() - 1);
					}
					

					if (thisRow == (headerRow)) {
						dt[thisColumn] = datatype;
					}
					if (thisRow >= (headerRow + 1)) {
						if (dt[thisColumn].equals(datatype)) {

							dt[thisColumn] = datatype;
							

						} else {
							dt[thisColumn] = "VARCHAR";
						}
					}

					if (thisColumn > maxcolumncount) {
						maxcolumncount = thisColumn;
					}
				}
				maxValue = "";
				scaleValues = "";
				datatypes="";
				for (int i = 0; i < maxlength.length; i++) {
					maxValue += maxlength[i] + "-";
					scaleValues += maxScale[i] + "-";
					datatypes += dt[i] + "-";
				}
				maxValue = maxValue.substring(0, maxValue.length() - 1);
				scaleValues = scaleValues
						.substring(0, scaleValues.length() - 1);
				datatypes = datatypes.substring(0, datatypes.length() - 1);
				
				tempValue = col + ";" + colNo + ";" + maxValue + ";"
						+ scaleValues + ";" + datatypes;
				// output.println(thisRow+"\t"+thisColumn+"\t"+thisStr+"\t"+thisStr.length());
			}

			// Update column and row count
			if (thisRow > -1)
				lastRowNumber = thisRow;
			if (thisColumn > -1)
				lastColumnNumber = thisColumn;

			// Handle end of row
			if (record instanceof LastCellOfRowDummyRecord) {
				// Print out any missing commas if needed
				if (minColumns > 0) {
					// Columns are 0 based
					if (lastColumnNumber == -1) {
						lastColumnNumber = 0;
					}
					for (int i = lastColumnNumber; i < (minColumns); i++) {
						// output.print(',');
					}
				}

				// We're onto a new row
				lastColumnNumber = -1;

				// End the row
				// output.println();
			}
		}

	}

}
