package FileLoad;

import io.FileUtils;
import io.LineIterator;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import api.getInfo;

public class rkmFile {

	public static void process(String Directory ,String FileName, String encoding,
			String delimiter, String ReplaceCharacter,
			Connection conn, int ModelId,int Hdr) 
			throws Exception {		
		
		File[] fileName = getInfo.getFiles(Directory , FileName);
		for (int z = 0; z < fileName.length; z++) {
			
			String file = fileName[z].getName();
			//System.out.println(file);
			LineIterator it = FileUtils.lineIterator(new File(fileName[z].getAbsolutePath()), encoding);
			int rowNo = 0;
			String[] firstLine = null;
			int[] colLength = null;
			String[] eachcol = null;
			try {
				while (it.hasNext()) {
					String line = it.nextLine();
					rowNo++;
					
					if (   (rowNo == 1) && (delimiter.equals("CALCULATE"))   ) {
						 ArrayList<String> delim = getDelimiter(line);
						 if (delim.size() > 1) { throw new Exception ("\n\n two or more Delimiters Found.\nPlease reverse manually by providing the correct Delimiter ");}
						 else { delimiter = delim.get(0); }
					}
					
					line = line.replace(ReplaceCharacter, "");
					eachcol = line.split(delimiter, -1);
					
					if (rowNo == 1) {
						firstLine = eachcol;
						colLength = new int[eachcol.length];
					} else {
						for (int i = 0; i < eachcol.length; i++) {
							if (eachcol[i].length() > colLength[i]) {
								colLength[i] = eachcol[i].length();
							}
						}
					}
				} // While Loop Ends
				// First Line
				for (int i = 0; i < firstLine.length; i++) {
					System.out.println(firstLine[i] + "\t Max Lentgth ="
							+ colLength[i]);
				}
				
				//Loading Values into the Repository 
				LoadRKM(conn,ModelId,file,Hdr,delimiter,firstLine,colLength,rowNo);

			} finally {
				LineIterator.closeQuietly(it);
			}
			
		}
	}
	
	public static void LoadRKM(Connection conn,int modelId,String FileName,int hdr,String delimiter, String[] firstLine, int[] colLength,int totalRows) throws SQLException {
		
		String rec_sep="";
		PreparedStatement ps;	
		String sqltxt="";
		
		String OS = System.getProperty("os.name").toLowerCase();
		if (OS.indexOf("win") >= 0) { 
			rec_sep = "\\u00"+"0D"+"\\u00"+"0A";
		} else {
			rec_sep="\\u00"+"0A";
		}
		
		//Table Names 
		sqltxt = "insert into SNP_REV_TABLE (I_MOD,TABLE_NAME,RES_NAME,TABLE_ALIAS,TABLE_DESC,IND_SHOW,TABLE_TYPE,FILE_FORMAT,FILE_FIRST_ROW,FILE_SEP_ROW,FILE_SEP_FIELD,R_COUNT) values (?,?,?,?,?,?,?,?,?,?,?,?)";
	    ps = conn.prepareStatement(sqltxt);
	    ps.clearParameters();
	    ps.setInt(1, modelId);
	    ps.setString(2, FileName.substring(0,3));
	    ps.setString(3,FileName);
	    ps.setString(4, FileName.substring(0,3));
	    ps.setString(5,"");
	    ps.setString(6, "1");
	    ps.setString(7, "T");
	    ps.setString(8, "D");
	    ps.setInt(9, hdr);
	    ps.setString(10,rec_sep);
	    char[] c = delimiter.toCharArray();
	    //System.out.println("\\u00"+String.valueOf(Integer.toHexString((int) c[0] )));
	    //System.out.println("\\u00"+String.valueOf(Integer.toHexString( (int) delimiter ))));
	    ps.setString(11,"\\u00"+String.valueOf(Integer.toHexString((int) c[0] )));
	    ps.setInt(12,totalRows-hdr);
	    ps.executeUpdate();
	    
	    ps.close();
	    

// Columns 
	    sqltxt= "insert into SNP_REV_COL (I_MOD,TABLE_NAME,COL_NAME,DT_DRIVER,POS,LONGC,BYTES,SCALEC,CHECK_FLOW,CHECK_STAT) values (?,?,?,?,?,?,?,?,?,?)";
	    ps = conn.prepareStatement(sqltxt);
	    
	    for (int i = 0; i < firstLine.length; i++) {
	    	ps.clearParameters();
	        ps.setInt(1, modelId);
	        ps.setString(2, FileName.substring(0,3));
	        ps.setString(3, firstLine[i].toUpperCase());
	        ps.setString(4, "STRING");
	        ps.setInt(5,(i+1));
	        ps.setInt(6, colLength[i]);
	        ps.setInt(7, colLength[i]);
	        ps.setInt(8, 0);
	        ps.setInt(9, 0);
	        ps.setInt(10, 0);
	        ps.executeUpdate();
		}

	}

	public static ArrayList<String> getDelimiter(String FirstLine) {

		ArrayList<String> specialChar = new ArrayList<String>();

		Pattern p = Pattern.compile("[^a-z0-9\"_]", Pattern.CASE_INSENSITIVE);
		char[] eachchar = FirstLine.toCharArray();
		for (int i = 0; i < eachchar.length; i++) {
			Matcher m = p.matcher(String.valueOf(eachchar[i]));
			boolean b = m.find();

			if (b) {
				// If special char exists and not already present
				if (!specialChar.contains(String.valueOf(eachchar[i]))) {
					specialChar.add(String.valueOf(eachchar[i]));
				}

			}
		}

		return specialChar;
	}
}
