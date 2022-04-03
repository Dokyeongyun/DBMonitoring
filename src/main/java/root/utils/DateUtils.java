package root.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

public class DateUtils {

	public static List<DateTimeFormatter> DATE_TIME_FORMATTER_LIST = new ArrayList<>();
	static {
		DATE_TIME_FORMATTER_LIST.add(DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss yyyy", Locale.ENGLISH));
		DATE_TIME_FORMATTER_LIST.add(DateTimeFormatter.ofPattern("yyyy/MM/dd", Locale.ENGLISH));
		DATE_TIME_FORMATTER_LIST.add(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss", Locale.ENGLISH));
		DATE_TIME_FORMATTER_LIST.add(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH));
		DATE_TIME_FORMATTER_LIST.add(DateTimeFormatter.BASIC_ISO_DATE);
		DATE_TIME_FORMATTER_LIST.add(DateTimeFormatter.ISO_DATE);
		DATE_TIME_FORMATTER_LIST.add(DateTimeFormatter.ISO_DATE_TIME);
		DATE_TIME_FORMATTER_LIST.add(DateTimeFormatter.ISO_INSTANT);
		DATE_TIME_FORMATTER_LIST.add(DateTimeFormatter.ISO_LOCAL_DATE);
		DATE_TIME_FORMATTER_LIST.add(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
		DATE_TIME_FORMATTER_LIST.add(DateTimeFormatter.ISO_LOCAL_TIME);
		DATE_TIME_FORMATTER_LIST.add(DateTimeFormatter.ISO_OFFSET_DATE);
		DATE_TIME_FORMATTER_LIST.add(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
		DATE_TIME_FORMATTER_LIST.add(DateTimeFormatter.ISO_OFFSET_TIME);
		DATE_TIME_FORMATTER_LIST.add(DateTimeFormatter.ISO_ORDINAL_DATE);
		DATE_TIME_FORMATTER_LIST.add(DateTimeFormatter.ISO_TIME);
		DATE_TIME_FORMATTER_LIST.add(DateTimeFormatter.ISO_WEEK_DATE);
		DATE_TIME_FORMATTER_LIST.add(DateTimeFormatter.ISO_ZONED_DATE_TIME);
		DATE_TIME_FORMATTER_LIST.add(DateTimeFormatter.RFC_1123_DATE_TIME);
	}

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
	 * �������ڿ��� ��, ��, ���� ���� ��¥�� ��ȯ�Ѵ�. ���⵵ �����ϴ�.
	 * 
	 * @param curDate      ��������
	 * @param returnFormat ��ȯ���� ��¥����
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
	 * �������ڿ��� ��, ��, ���� ���� ��¥�� ��ȯ�Ѵ�. ���⵵ �����ϴ�.
	 * 
	 * @param dateStringYMD yyyy-MM-dd ������ ��¥ ���ڿ�
	 * @param year
	 * @param month
	 * @param day
	 * @return
	 */
	public static String addDate(String dateStringYMD, int year, int month, int day) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar cal = Calendar.getInstance();
		try {
			cal.setTime(sdf.parse(dateStringYMD));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		cal.add(Calendar.YEAR, year);
		cal.add(Calendar.MONTH, month);
		cal.add(Calendar.DATE, day);
		return sdf.format(cal.getTime());
	}

	/**
	 * �� ��¥�� ���̸� ��ȯ�Ѵ�. (ù��°��¥ - �ι�°��¥) �� ��, �� ��¥�� ������ ù��° ���ڷ� ������ dateFormat��
	 * �����ؾ��Ѵ�.
	 * 
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

	/**
	 * ��¥ ǥ�� ������ ��ȯ�Ѵ�.
	 * 
	 * @param fromFormat
	 * @param toFormat
	 * @param dateString
	 * @param locale
	 * @return
	 */
	public static String convertDateFormat(String fromFormat, String toFormat, String dateString, Locale locale) {
		SimpleDateFormat from = new SimpleDateFormat(fromFormat, locale);
		SimpleDateFormat to = new SimpleDateFormat(toFormat, locale);
		String convertedDateString = "";
		try {
			Date date = from.parse(dateString);
			convertedDateString = to.format(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return convertedDateString;
	}

	/**
	 * Date ��ü�� �������Ѵ�.
	 * 
	 * @param date
	 * @param toFormat
	 * @return
	 */
	public static String format(Date date, String toFormat) {
		return new SimpleDateFormat(toFormat).format(date);
	}

	/**
	 * ��¥�� ��Ұ��踦 ���Ѵ�.
	 * 
	 * @param format  ��¥ ����
	 * @param base    ���� ��¥
	 * @param compare �� ��¥
	 * @return
	 */
	public static int compareTo(String format, String base, String compare) {
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat(format);
			Date date1 = dateFormat.parse(base);
			Date date2 = dateFormat.parse(compare);

			return date1.compareTo(date2);
		} catch (ParseException e) {

		}

		return -1;
	}

	/**
	 * DateFormat�� ���� ����ǥ������ �� ��, ���� ����� DateTimeFormatter ����� ��ȸ�Ͽ� ��¥���ڿ��� �Ľ��Ѵ�.
	 * 
	 * @param dateString ��¥ ���ڿ�
	 * @return
	 */
	public static LocalDate parse(String dateString) {
		LocalDate result = null;

		for (DateTimeFormatter formatter : DATE_TIME_FORMATTER_LIST) {
			try {
				result = LocalDate.parse(dateString, formatter);
			} catch (Exception e) {
			}

			if (result != null) {
				break;
			}
		}

		return result;
	}
}
