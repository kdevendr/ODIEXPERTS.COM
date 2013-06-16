package large;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import org.apache.poi.hssf.eventusermodel.AbortableHSSFListener;
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
import org.apache.poi.ss.usermodel.DateUtil;
import org.xml.sax.SAXException;

import excel.CatchException;

public class LKMXLS implements HSSFListener {

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
	private int headerRow;
	private boolean processSheet = false;
	private String fileSheetName;
	private PreparedStatement ps;
	private String[] columnlist;
	private String[] dt;
	private int columnno;
	private NumberRecord numrec;
	private int index = 0;
	private int rowNo = 0;
	private int processExtra=0;
	private String CountSheet;
	private StringWriter errors;
	private boolean processloop=true;

	public LKMXLS(POIFSFileSystem fs, PrintStream output, int minColumns,
			PreparedStatement ps, String fileSheetName, String collist,
			boolean trim, int HeaderRow, String datatype,String CountSheet) {
		this.fs = fs;
		this.output = output;
		this.minColumns = minColumns;
		this.fileSheetName = fileSheetName;
		this.ps = ps;
		this.CountSheet=CountSheet;
		columnlist = collist.split(",");
		dt = datatype.split(",");

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
		
	}

	public void abortableProcessRecord(Record record) {
		
		record.cloneViaReserialise();
		
		
		
	}
	public void processRecord(Record record){
		
		if (CatchException.Result().length() >0) {
			processloop  = false;
			abortableProcessRecord(record);
		}

  if (processloop == true) {

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

				sheetIndex++;
				if (orderedBSRs == null) {
					orderedBSRs = BoundSheetRecord
							.orderByBofPosition(boundSheetRecords);
				}

				// output.println(sheetIndex);
				sheetName = orderedBSRs[sheetIndex - 1].getSheetname();
				//Process All Sheets
				if (CountSheet.equals("1")) {
					this.output.println("Loading Excel Sheet - " + sheetName);
					processSheet = true;
				} else {
				if (sheetName.toUpperCase().equals(fileSheetName.toUpperCase())) {
					this.output.println("Loading Excel Sheet - " + sheetName);
					processSheet = true;
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
			break;
		case BoolErrRecord.sid:
			BoolErrRecord berec = (BoolErrRecord) record;

			thisRow = berec.getRow();
			thisColumn = berec.getColumn();
			thisStr = "";
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
			break;

		case LabelRecord.sid:
			LabelRecord lrec = (LabelRecord) record;

			thisRow = lrec.getRow();
			thisColumn = lrec.getColumn();
			thisStr = '"' + lrec.getValue() + '"';
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
			break;
		case NoteRecord.sid:
			NoteRecord nrec = (NoteRecord) record;

			thisRow = nrec.getRow();
			thisColumn = nrec.getColumn();
			thisStr = '"' + "(TODO)" + '"';
			break;
		case NumberRecord.sid:
			numrec = (NumberRecord) record;

			thisRow = numrec.getRow();
			thisColumn = numrec.getColumn();

			// Format
			thisStr = formatListener.formatNumberDateCell(numrec);
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

			if (thisRow > headerRow) {
				processExtra=1;
				// output.println(rowNo);
				try {
					// Remove " " around the data.
					if (thisStr.startsWith("\"")) {
						thisStr = thisStr.substring(1, thisStr.length() - 1);
					}
					if (thisColumn >= 0) {
						//output.println(lastColumnNumber);
						for (int i = 0; i < columnlist.length; i++) {
							if ((thisColumn + 1) == Integer
									.parseInt(columnlist[i])) {
								index++;

								//output.println("index" + index);
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
									ps.setDouble(index,
											Double.parseDouble(thisStr) * 100);
								}
								// DATE
								else if (dt[i].toUpperCase().equals("DATE")) {
									Date date = DateUtil.getJavaDate(numrec
											.getValue());
									java.sql.Date sqlDate = new java.sql.Date(
											date.getTime());
									ps.setDate(index, sqlDate);
								}
								// DATETIME
								else if (dt[i].toUpperCase().equals("DATETIME")) {
									Date date = DateUtil.getJavaDate(numrec
											.getValue());
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
					}

				} catch (SQLException e) {
					e.printStackTrace(new PrintWriter(errors));
					CatchException.StoreExcept(errors.toString());
					try {
						throw new SQLException(e);
					} catch (SQLException e1) {e1.printStackTrace();
					}
				}

			}

			// Update column and row count
			if (thisRow > -1)
				lastRowNumber = thisRow;
			if (thisColumn > -1)
				lastColumnNumber = thisColumn;

			// Handle end of row
			if (record instanceof LastCellOfRowDummyRecord ) {
				// Print out any missing commas if needed

				if (processExtra == 1) {
				try {
					for (int i = (lastColumnNumber+1); i < columnlist.length; i++) {
						index++;
						ps.setObject(index, "");
						//output.println(index + "\t" + thisColumn);

					}
					ps.addBatch();
					//ps.executeUpdate();
				} catch (SQLException e) {
					errors= new StringWriter();
					e.printStackTrace(new PrintWriter(errors));
					CatchException.StoreExcept(errors.toString());
				}
				rowNo++;
				index = 0;
				}

				if (minColumns > 0) {
					// Columns are 0 based
					if (lastColumnNumber == -1) {
						lastColumnNumber = 0;
					}
					for (int i = lastColumnNumber; i < (minColumns); i++) {
						// output.print(',');
					}
				}
				// End the row
				// We're onto a new row
				lastColumnNumber = -1;

				if (rowNo == 100) {
					try {
						ps.executeBatch();
					} catch (SQLException e) {
						errors= new StringWriter();
						e.printStackTrace(new PrintWriter(errors));
						CatchException.StoreExcept(errors.toString());
						try {
							throw new SQLException(e);
						} catch (SQLException e1) {e1.printStackTrace();
						}
					}
					rowNo = 0;
				}

				
			}

		}
	}
  }
}
