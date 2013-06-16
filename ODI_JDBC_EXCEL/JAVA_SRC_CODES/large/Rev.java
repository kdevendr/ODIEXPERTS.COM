package large;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public class Rev {

	public static void SnpRevTable(PreparedStatement ps, String SheetName,
			int modelNo, int headerRow, String Mask) throws SQLException {
		// SNP_REV_TABLE

		ps.clearParameters();
		ps.setInt(1, modelNo);
		ps.setString(2, SheetName.toUpperCase().trim());
		ps.setString(3, Mask.trim());
		ps.setString(4, SheetName.toUpperCase().trim());
		ps.setInt(5, headerRow);
		ps.setString(6, "1");
		ps.setString(7, "T");
		ps.executeUpdate();

	}

	public static void SnpRevColXLS(PreparedStatement pscol, String SheetName,
			int modelNo, String temp,int maxcolumncount) throws SQLException {
		// SNP_REV_TABLE
		//System.out.println(temp);
		pscol.clearParameters();
		String[] col = temp.split(";");
		String[] columns = col[0].split("-");
		String[] colno = col[1].toString().split("-");
		String[] collength = col[2].split("-");
		String[] colscale = col[3].split("-");
		String[] coldt=col[4].split("-");
		/*System.out.println( temp);
		System.out.println( maxcolumncount+1);*/
		//System.out.println("length" + colno.length + "\t" + colno[0]);
		//System.out.println( col[0]+"\t"+ col[1]+"\t"+ col[2]);
		for (int j = 0; j < maxcolumncount+1; j++) {
			//System.out.println(columns[0] + "\t" + colno[j] + "\t"+ collength[j]);
			pscol.setInt(1, modelNo);
			pscol.setString(2, SheetName.toUpperCase().trim());
			if (j >= colno.length) { pscol.setString(3, "C"+(j+1));}
			else {pscol.setString(3, columns[j]);}
			
			pscol.setString(4, coldt[j]);
			pscol.setInt(5, j+1);
			if (Integer.parseInt(collength[j]) ==0) {
				pscol.setInt(6,50);
			} else {
			pscol.setInt(6, Integer.parseInt(collength[j]));
			}
			if (coldt[j].equals("VARCHAR")) {
				pscol.setInt(7, 0);
			}else {
			pscol.setInt(7, Integer.parseInt(colscale[j]));
			}
			pscol.setInt(8, 0);
			pscol.setInt(9, 0);
			pscol.addBatch();
		}
		pscol.executeBatch();
	}
	
	
	public static void SnpRevColXLSX(PreparedStatement pscol, String SheetName,
			int modelNo, String temp,int maxcolumncount) throws SQLException {
		// SNP_REV_TABLE
		
		pscol.clearParameters();
		String[] col = temp.split(";");
		/*System.out.println(temp);
		System.out.println(col[0].trim()+"\t"+ col[1].trim()+"\t"+ col[2].trim());
		System.out.println("max"+maxcolumncount);*/
		String[] columns = col[0].split("-");
		String[] colno = col[1].toString().split("-");
		List<String> colnumber=Arrays.asList(colno);
		String[] collength = col[2].split("-");
		String[] colscale = col[3].split("-");
		String[] coldt=col[4].split("-");
		int zz=0;
		for (int j = 0; j < maxcolumncount+1; j++) {
			
			pscol.setInt(1, modelNo);
			pscol.setString(2, SheetName.toUpperCase().trim());
			if (colnumber.contains(String.valueOf(j+1)) ) { pscol.setString(3, columns[zz]);zz++;}
			else {pscol.setString(3, "C"+(j+1));}
			
			pscol.setString(4, coldt[j]);
			pscol.setInt(5, j+1);
			if (Integer.parseInt(collength[j]) ==0) {
				pscol.setInt(6,50);
			} else {
			pscol.setInt(6, Integer.parseInt(collength[j]));
			}
			if (coldt[j].equals("VARCHAR")) {
				pscol.setInt(7, 0);
			}else {
			pscol.setInt(7, Integer.parseInt(colscale[j]));
			}
			pscol.setInt(8, 0);
			pscol.setInt(9, 0);
			pscol.addBatch();
		}
		pscol.executeBatch();
	}

}
