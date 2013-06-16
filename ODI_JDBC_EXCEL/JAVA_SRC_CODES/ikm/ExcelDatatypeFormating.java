package ikm;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExcelDatatypeFormating {
	
	static String num_format(String datatype) {
		String format = "";

		if (datatype.equals("NUMERIC()") || datatype.equals("NUMERIC(0)")) {
			format = "0";
		} else {
			int no = Integer.parseInt(datatype.substring(8,
					datatype.length() - 1));
			format = "0.";
			for (int i = 0; i < no; i++) {
				format += "0";
			}
		}
		return format;
	}

	static String perc_format(String datatype) {
		String format = "";
		if (datatype.equals("PERCENTAGE() ") || datatype.equals("PERCENTAGE(0)")) {
			format = "0%";
		} else {
			int no = Integer.parseInt(datatype.substring(11,
					datatype.length() - 1));
			format = "0.";
			for (int i = 0; i < no; i++) {
				format += "0";
			}
			format += "%";
		}
		return format;

	}

	static String scientific_format(String datatype) {
		String format = "";
		if (datatype.equals("SCIENTIFIC()")  || datatype.equals("SCIENTIFIC(0)") ) {
			format = "0";
		} else {
			int no = Integer.parseInt(datatype.substring(11,
					datatype.length() - 1));
			format = "0.";
			for (int i = 0; i < no; i++) {
				format += "0";
			}

		}
		format += "E+00";
		return format;

	}

	static String currency_format(String datatype, String symbol) {
		String format = "";
		if (datatype.equals("CURRENCY()")  || datatype.equals("CURRENCY(0)") ) {
			format = "0";
		} else {
			int no = Integer.parseInt(datatype.substring(9,
					datatype.length() - 1));
			format = "0.";
			for (int i = 0; i < no; i++) {
				format += "0";
			}
		}
		return symbol + "#,##" + format;
	}
	
	public static String isNumeric(String number)
    {
		String value="";
        boolean isValid = false;
        String expression = "[-+]?[0-9]*\\.?[0-9]+$";
        CharSequence inputStr = number;
        Pattern pattern = Pattern.compile(expression);
        Matcher matcher = pattern.matcher(inputStr);
        if(matcher.matches())
        {
            isValid = true;
        }
        
        if (isValid == true) {value =String.valueOf(Double.parseDouble(number));}
        else {value=number;}
        return  value;
    }

}
