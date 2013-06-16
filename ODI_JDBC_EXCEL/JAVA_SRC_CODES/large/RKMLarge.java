package large;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;

import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackageAccess;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

public class RKMLarge {

	private static String sqltxt;
	private static PreparedStatement pstable;
	private static PreparedStatement pscol;

	public static void ProcessRKM(String fileName, String mask,
			Connection conn, int ModelId, int HdrRow, String Sheet_No,String ContinuousSheet)
			throws Exception {
		
		sqltxt = "insert into SNP_REV_TABLE (I_MOD,TABLE_NAME,RES_NAME,TABLE_ALIAS,R_COUNT,IND_SHOW,TABLE_TYPE) values (?,?,?,?,?,?,?)";
		pstable=conn.prepareStatement(sqltxt);
		sqltxt = "insert into SNP_REV_COL (I_MOD,TABLE_NAME,COL_NAME,DT_DRIVER,POS,LONGC,SCALEC,CHECK_FLOW,CHECK_STAT) values (?,?,?,?,?,?,?,?,?)";
		pscol=conn.prepareStatement(sqltxt);

		if (fileName.endsWith(".xls")) {
			POIFSFileSystem fs=new POIFSFileSystem(new FileInputStream(fileName));
			RKMXLS xls=new RKMXLS(fs, System.out, 2, Sheet_No, pstable,pscol, ModelId, HdrRow, mask,ContinuousSheet);
			xls.process();

		} else {
			File xlsxFile = new File(fileName);
			OPCPackage p = OPCPackage.open(xlsxFile.getPath(),PackageAccess.READ);
			RKMXLSX xlsx = new RKMXLSX(p, System.out, 2);
			xlsx.process(conn, Sheet_No, ModelId, HdrRow, mask,ContinuousSheet);

		}
		
		pstable.close();
		pscol.close();

	}

}
