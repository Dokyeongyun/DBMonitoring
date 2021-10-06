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
	 * ���� ��¥ �� �ð��� ������ format�� ���� ��ȯ�Ѵ�.
	 * 
	 * @param format
	 * @return
	 */
	public static String getToday(String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(new Date());
	}

	/**
	 * �Ű������� �־��� �⵵, ���� �� �ϼ��� ��ȯ�Ѵ�.
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
	 * ��, ���� ���� �� �ڸ��� ��ȯ�Ͽ� ��ȯ�Ѵ�.
	 * 
	 * @param value
	 * @return
	 */
	public static String getTwoDigitDate(int value) {
		return value < 10 ? "0" + value : String.valueOf(value);
	}

	/**
	 * �־��� ������ ������ ��ȯ�Ѵ�.
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
	 * �־��� ���ڰ� �ָ����� Ȯ���Ѵ�.
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
	 * �������ڿ��� ��, ��, ���� ���� ��¥�� ��ȯ�Ѵ�.
	 * ���⵵ �����ϴ�.
	 * @param curDate		��������
	 * @param returnFormat 	��ȯ���� ��¥����
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
	 * �� ��¥�� ���̸� ��ȯ�Ѵ�. (ù��°��¥ - �ι�°��¥)
	 * �� ��, �� ��¥�� ������ ù��° ���ڷ� ������ dateFormat�� �����ؾ��Ѵ�.
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
