package large;

public class ProcessColumn {

	public static String getCol(String coll,int nullcol) {
		
		String column="";
		if (coll != "") {
			column=coll.toUpperCase().trim().replace("\"", "").replaceAll("[~`!@#%^&*():;/?.,]", "");
			column=column.replace(" ", "_").replace("-", "_");
		} else { 
			column="C"+nullcol;
			nullcol++;
		}
		return column;
	}
	
	public static int nameToColumn(String name) {
		int column = -1;
		for (int i = 0; i < name.length(); ++i) {
			int c = name.charAt(i);
			column = (column + 1) * 26 + c - 'A';
		}
		return column;
	}
	
}
