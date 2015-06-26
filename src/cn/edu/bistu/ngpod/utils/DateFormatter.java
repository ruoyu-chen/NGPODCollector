/**
 * 
 */
package cn.edu.bistu.ngpod.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;

/**
 * 日期类型的格式转换函数
 * 
 * @author chenruoyu
 *
 */
public class DateFormatter {
	public static final String DEFAULT_PATTERN="yyyyMMdd";

	private static SimpleDateFormat fmt = new SimpleDateFormat(DEFAULT_PATTERN);
		
	private static Hashtable<String, String> monthTable = new Hashtable<String, String>();
	static {
		monthTable.put("DECEMBER", "12");
		monthTable.put("NOVEMBER", "11");
		monthTable.put("OCTOBER", "10");
		monthTable.put("SEPTEMBER", "09");
		monthTable.put("AUGUST", "08");
		monthTable.put("JULY", "07");
		monthTable.put("JUNE", "06");
		monthTable.put("MAY", "05");
		monthTable.put("APRIL", "04");
		monthTable.put("MARCH", "03");
		monthTable.put("FEBRUARY", "02");
		monthTable.put("JANUARY", "01");
	}
	
	/**
	 * 输入的日期格式为MAY 6, 2009 输出的日期格式为YYYYMMDD，如果输入格式不正确，返回值为null
	 * @param raw
	 * @return
	 * @throws Exception
	 */
	public static String format(String raw) throws Exception {
		if (raw == null || "".equals(raw.trim())) {
			return null;
		}
		String str[] = raw.trim().split(",");
		String year = str[1].trim();
		str = str[0].split(" ");
		String month = monthTable.get(str[0].toUpperCase());
		String day = Integer.parseInt(str[1]) < 10 ? "0" + str[1] : str[1];
		return year + month + day;
	}
	/**
	  * 将原始的输入日期（MAY 6, 2009形式）转换为java.util.Date类型，如果输入格式不正确，返回值为null
	 * @param raw
	 * @return
	 * @throws Exception
	 */
	public static Date format2Date(String raw) throws Exception {
		if(raw==null||"".equals(raw.trim())){
			return null;
		}else{
			return fmt.parse(format(raw));
		}
	}
	
	public static int format2Int(Date date){
		if(date == null){
			date = new Date();
		}
		return Integer.parseInt(fmt.format(date));
	}
	
	/**
	  * 将原始的输入日期（MAY 6, 2009形式）转换为该日期对应的数字形式（20090506形式），如果输入格式不正确，返回值为-1
	 * @param raw
	 * @return
	 * @throws Exception
	 */
	public static int format2Num(String raw) throws Exception {
		if (raw == null || "".equals(raw.trim())) {
			return -1;
		}
		String str[] = raw.trim().split(",");
		String year = str[1].trim();
		str = str[0].split(" ");
		String month = monthTable.get(str[0].toUpperCase());
		String day = Integer.parseInt(str[1]) < 10 ? "0" + str[1] : str[1];
		return Integer.parseInt(year+month+day);
	}
}
