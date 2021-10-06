package Root.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class DateUtils {

	/**
	 * 오늘 날짜 및 시간을 지정한 format에 따라 반환한다.
	 * 
	 * @param format
	 * @return
	 */
	public static String getToday(String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(new Date());
	}

	/**
	 * 매개변수로 주어진 년도, 달의 총 일수를 반환한다.
	 * 
	 * @param year
	 * @param month
	 * @return
	 */
	public static int getMonthlyDayCount(int year, int month) {
		Calendar cal = new GregorianCalendar(year, --month, 1);
		return cal.getActualMaximum(Calendar.DAY_OF_MONTH);
	}

	/**
	 * 월, 일의 값을 두 자리로 변환하여 반환한다.
	 * 
	 * @param value
	 * @return
	 */
	public static String getTwoDigitDate(int value) {
		return value < 10 ? "0" + value : String.valueOf(value);
	}

	/**
	 * 주어진 일자의 요일을 반환한다.
	 * 
	 * @param year
	 * @param month
	 * @param day
	 * @param style
	 * @param locale
	 * @return
	 */
	public static String getDayOfWeek(int year, int month, int day, TextStyle style, Locale locale) {
		LocalDate date = LocalDate.of(year, month, day);
		DayOfWeek dayOfWeek = date.getDayOfWeek();
		return dayOfWeek.getDisplayName(style, locale);
	}

	/**
	 * 주어진 일자가 주말인지 확인한다.
	 * 
	 * @param year
	 * @param month
	 * @param day
	 * @return
	 */
	public static boolean isWeekEnd(int year, int month, int day) {
		LocalDate date = LocalDate.of(year, month, day);
		DayOfWeek dayOfWeek = date.getDayOfWeek();
		return dayOfWeek.getValue() == 6 ? true : dayOfWeek.getValue() == 7 ? true : false;
	}

	/**
	 * 기준일자에서 년, 월, 일을 더한 날짜를 반환한다.
	 * 빼기도 가능하다.
	 * @param curDate		기준일자
	 * @param returnFormat 	반환받을 날짜포맷
	 * @param year			
	 * @param month
	 * @param day
	 * @return
	 */
	public static String addDate(Date curDate, String returnFormat, int year, int month, int day) {
		SimpleDateFormat sdf = new SimpleDateFormat(returnFormat);
		Calendar cal = Calendar.getInstance();
		cal.setTime(curDate);
		cal.add(Calendar.YEAR, year);
		cal.add(Calendar.MONTH, month);
		cal.add(Calendar.DATE, day);
		return sdf.format(cal.getTime());
	}
	
	/**
	 * 두 날짜간 차이를 반환한다. (첫번째날짜 - 두번째날짜)
	 * 이 때, 각 날짜의 포맷은 첫번째 인자로 전달한 dateFormat과 동일해야한다.
	 * @param dateFormat
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static long getDateDiffTime(String dateFormat, String date1, String date2) {
		long diffTime = 0;
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		try {
	        Date FirstDate = sdf.parse(date1);
	        Date SecondDate = sdf.parse(date2);
	        diffTime = FirstDate.getTime() - SecondDate.getTime(); 
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return diffTime / 1000;
	}
}
