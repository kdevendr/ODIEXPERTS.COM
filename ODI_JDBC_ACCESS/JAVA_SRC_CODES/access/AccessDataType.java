package access;


public class AccessDataType {
	
	public static int getSQLType(String datatype) {
		int dt = 0 ;
		if (datatype.equals("BOOLEAN")) {
			dt=16;
		}
		else if (datatype.equals("BYTE")) {
			dt=-6;
		}
		else if (datatype.equals("INT")) {
			dt=5;
		}
		else if (datatype.equals("LONG")) {
			dt=4;
		}
		else if (datatype.equals("CURRENCY")) {
			dt=3;
		}
		else if (datatype.equals("FLOAT")) {
			dt=6;
		}
		else if (datatype.equals("DOUBLE")) {
			dt=8;
		}
		else if (datatype.equals("DATETIME")) {
			dt=93;
		}
		else if (datatype.equals("GUID")) {
			//dt=-2;
		}
		else if (datatype.equals("DECIMAL")) {
			dt=2;
		}
		else if (datatype.equals("TEXT")) {
			dt=12;
		}
		else if (datatype.equals("OLE")) {
			dt=-4;
		}
		else if (datatype.equals("MEMO")) {
			dt=-1;
		}
		else {dt=12;}
		return dt;
		
	}

	public static String getDatatypefromSQLType(int dt){

		String datatype="";	
		if (dt == 12){
			datatype= "TEXT";
		}
		else if (dt == -1){
			datatype= "MEMO";
		}
		else if (dt == -6){
			datatype= "BYTE";
		}
		else if (dt == 5){
			datatype= "INT";
		}
		else if (dt == 4){
			datatype= "LONG";
		}
		else if (dt == 6){
			datatype= "FLOAT";
		}
		else if (dt == 8){
			datatype= "DOUBLE";
		}
		else if (dt == 2){
			datatype= "DECIMAL";
		}
		else if (dt == 93){
			datatype= "DATETIME";
		}
		else if (dt == 3){
			datatype= "CURRENCY";
		}
		else if (dt == 16){
			datatype= "BOOLEAN";
		}	
		else if (dt == -4){
			datatype= "OLE";
		}
	    else {datatype= "TEXT";}
		return datatype;
	}		
	
	public static Object getConvertedDATA (String data,int dt) {
        Object output = null;
         if (dt == 12){
              output=data;
        }
         else if (dt == -1){
              output=data;
        }
         else if (dt == -6){
              output=Byte. parseByte(data);
        }
         else if (dt == 5){
              output=Integer. parseInt(data);
        }
         else if (dt == 4){
              output=Long. parseLong(data);
        }
         else if (dt == 6){
              output=Float. parseFloat(data);
        }
         else if (dt == 8){
              output=Double. parseDouble(data);
        }
         else if (dt == 2){
              output=Integer. parseInt(data);
        }
         else if (dt == 93){
              output=java.sql.Date. valueOf(data);
        }
         else if (dt == 3){
              output=Integer. parseInt(data);
        }
         else if (dt == 16){
              output=Boolean. parseBoolean(data);
        }     
         else if (dt == -4){
              output=data;
        }
      else {output=data;}
         return output;
        
  }

	
	
}
