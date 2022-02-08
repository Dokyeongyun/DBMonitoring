package root.utils;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.opencsv.CSVWriter;

public class CsvUtils {

	/**
	 * 
	 * @param list
	 * @param clazz
	 * @return
	 */
	public static String toCsvString(List<?> list, Class<?> clazz) {
		StringBuffer sb = new StringBuffer();
		
		try {
			Field[] fields = clazz.getDeclaredFields();
			List<String> fieldName = Arrays.asList(fields).stream().map(f -> (f.getName()))
					.collect(Collectors.toList());

			sb.append(createCsvHeader(fieldName));

			for (Object item : list) {
				sb.append(CSVWriter.RFC4180_LINE_END).append(createCsvRow(item, clazz));
			}
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}

		return sb.toString();
	}

	/**
	 * 
	 * @param fieldNames
	 * @return
	 */
	public static String createCsvHeader(List<String> fieldNames) {
		StringBuffer sb = new StringBuffer();

		for (String f : fieldNames) {
			sb.append(sb.isEmpty() ? f : CSVWriter.DEFAULT_SEPARATOR + f);
		}

		return sb.toString();
	}
	
	/**
	 * 
	 * @param fieldNames
	 * @return
	 */
	public static String createCsvHeader(Class<?> clazz) {
		StringBuffer sb = new StringBuffer();
		
		try {
			Field[] fields = clazz.getDeclaredFields();
			List<String> fieldName = Arrays.asList(fields).stream().map(f -> (f.getName()))
					.collect(Collectors.toList());

			sb.append(createCsvHeader(fieldName));
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}

		return sb.toString();
	}
	
	/**
	 * 
	 * @param object
	 * @param clazz
	 * @return
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public static String createCsvRow(Object object, Class<?> clazz)
			throws IllegalArgumentException, IllegalAccessException {
		StringBuffer sb = new StringBuffer();

		if (StringUtils.equals(object.getClass().getName(), clazz.getName())) {
			for (Field f : clazz.getDeclaredFields()) {
				@SuppressWarnings("deprecation")
				boolean accessible = f.isAccessible();
				
				f.setAccessible(true);

				String appender = sb.isEmpty() ? StringUtils.getIfEmpty(f.get(object).toString(), () -> "-")
						: CSVWriter.DEFAULT_SEPARATOR + StringUtils.getIfEmpty(f.get(object).toString(), () -> "-");
				sb.append(appender);

				f.setAccessible(accessible);
			}
		}

		return sb.toString();
	}
}