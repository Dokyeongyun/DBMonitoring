package root.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;

public class CsvUtils {

	/**
	 * ��ü����Ʈ�� csv string ���·� ��ȯ�Ѵ�.
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
				sb.append(System.lineSeparator()).append(createCsvRow(item, clazz));
			}
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}

		return sb.toString();
	}

	/**
	 * ���ڿ�����Ʈ�� comma(,) ���ڷ� �̾� csv������ ����� ��ȯ�Ѵ�.
	 * 
	 * @param fieldNames
	 * @return
	 */
	public static String createCsvHeader(List<String> fieldNames) {
		StringBuffer sb = new StringBuffer();

		for (String f : fieldNames) {
			sb.append(sb.isEmpty() ? wrapInDoubleQuotation(f) : "," + wrapInDoubleQuotation(f));
		}

		return sb.toString();
	}

	/**
	 * �Ű������� ���޵� Class�� �ʵ��, �θ� Ŭ������ �ʵ带 comma(,)�� �̾� csv������ ����� ��ȯ�Ѵ�.
	 * 
	 * @param clazz
	 * @return
	 */
	public static String createCsvHeader(Class<?> clazz) {
		StringBuffer sb = new StringBuffer();

		try {
			List<Field> fields = getAllFields(clazz);
			List<String> fieldName = fields.stream().map(f -> (f.getName())).collect(Collectors.toList());

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
			for (Field f : getAllFields(clazz)) {
				@SuppressWarnings("deprecation")
				boolean accessible = f.isAccessible();

				f.setAccessible(true);

				String appender = sb.isEmpty()
						? StringUtils.getIfEmpty(wrapInDoubleQuotation(f.get(object).toString()),
								() -> wrapInDoubleQuotation("-"))
						: "," + StringUtils.getIfEmpty(wrapInDoubleQuotation(f.get(object).toString()),
								() -> wrapInDoubleQuotation("-"));
				sb.append(appender);

				f.setAccessible(accessible);
			}
		}

		return sb.toString();
	}

	/**
	 * 
	 * @param <T>
	 * @param headers
	 * @param csvString
	 * @param type
	 * @return
	 */
	public static <T> List<T> parseCsvToBeanList(List<String> headers, String csvString, Class<T> type) {
		List<T> beanList = new ArrayList<>();

		List<Map<String, String>> parsedList = parseCsvString(headers, csvString);

		for (Map<String, String> row : parsedList) {

			Constructor<T> constructor;
			Object instance;
			Class<?> instanceClass;
			try {
				constructor = type.getConstructor();
				instance = constructor.newInstance();
				instanceClass = instance.getClass();
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}

			for (String header : row.keySet()) {
				String setterName = "set" + header.substring(0, 1).toUpperCase() + header.substring(1);

				try {
					List<Field> allFields = getAllFields(instance.getClass());
					Class<?> fieldType = allFields.stream().filter(f -> f.getName().equals(header)).findFirst().get()
							.getType();

					Object fieldValue = null;

					if (fieldType == int.class) {
						fieldValue = Integer.valueOf(row.get(header));
					} else if (fieldType == double.class) {
						try {
							fieldValue = Double.valueOf(row.get(header));
						} catch (Exception e) {
							fieldValue = -1;
						}
					} else {
						fieldValue = row.get(header);
					}

					Method method = instanceClass.getMethod(setterName, fieldType);
					method.invoke(instance, fieldValue);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			beanList.add((type.cast(instance)));
		}
		return beanList;
	}

	/**
	 * 
	 * @param headers
	 * @param csvString
	 * @return
	 */
	public static List<Map<String, String>> parseCsvString(List<String> headers, String csvString) {
		List<Map<String, String>> result = new ArrayList<>();

		String[] csvLines = csvString.split("\n");
		for (String line : csvLines) {
			Map<String, String> map = new LinkedHashMap<>();

			Map<Integer, String> lineMap = parseCsvLine(line);
			for (int index : lineMap.keySet()) {
				if (index >= headers.size()) {
					continue;
				} else {
					map.put(headers.get(index), lineMap.get(index));
				}
			}

			result.add(map);
		}

		return result;
	}

	/**
	 * csv ������ ���ڿ��� �Ľ��Ͽ� ������� Map��ü�� ��� ��ȯ�Ѵ�.
	 * 
	 * @param csvLine
	 * @return
	 */
	public static Map<Integer, String> parseCsvLine(String csvLine) {
		Map<Integer, String> map = new LinkedHashMap<>();

		int index = 0;
		boolean isFirst = true;
		boolean isOpen = false;
		StringBuilder element = new StringBuilder();
		for (char c : csvLine.toCharArray()) {
			if (c == '"') {
				isOpen = !isOpen;
				if (isFirst) {
					isFirst = false;
					continue;
				}

				if (!isOpen) {
					map.put(index, StringEscapeUtils.unescapeHtml4(element.toString()));
					element = new StringBuilder();
				}
			} else if (c == ',') {
				if (isOpen) {
					element.append(c);
				} else {
					index++;
				}
			} else {
				element.append(c);
			}
		}
		return map;
	}

	/**
	 * ���ڿ��� �ֵ���ǥ�� ���� �� ��ȯ�Ѵ�. ��, ���ڿ� ���� �ֵ���ǥ�� �ִٸ� html escape�� ���ڷ� ��ȯ�Ѵ�.
	 * 
	 * @param string
	 * @return
	 */
	private static String wrapInDoubleQuotation(String string) {
		return StringUtils.join("\"", StringEscapeUtils.escapeHtml4(string), "\"");
	}

	/**
	 * �Ű������� ���޵� Ŭ������ Field��, �θ�Ŭ������ Field���� Reflection�Ͽ� ��ȯ�Ѵ�.
	 * 
	 * @param clazz
	 * @return
	 */
	private static List<Field> getAllFields(Class<?> clazz) {
		List<Field> fields = new ArrayList<Field>();

		fields.addAll(Arrays.asList(clazz.getDeclaredFields()));

		Class<?> superClazz = clazz.getSuperclass();
		if (superClazz != null) {
			fields.addAll(getAllFields(superClazz));
		}

		return fields;
	}
}