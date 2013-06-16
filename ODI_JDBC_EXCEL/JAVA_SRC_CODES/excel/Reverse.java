package excel;

import java.sql.Connection;

import large.RKMLarge;

public class Reverse {
	
	public static void ReverseExcel (String fileName, String mask,
			Connection conn, int ModelId, int HdrRow, String Sheet_No,
			String LargeExcel , String ContinuousSheet)
			throws Exception {
		
		if (LargeExcel.equals("1") ) {
			RKMLarge.ProcessRKM(fileName, mask, conn, ModelId, HdrRow, Sheet_No,ContinuousSheet);
		} else if ( ContinuousSheet.equals("1") ) {
			RKMLarge.ProcessRKM(fileName, mask, conn, ModelId, HdrRow, "1",ContinuousSheet);
		} else {
			System.out.println("RKM");
			RKM.ProcessRKM(fileName, mask, conn, ModelId, HdrRow, Sheet_No);
		}
	}

}
