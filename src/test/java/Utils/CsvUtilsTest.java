package Utils;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import root.core.domain.ArchiveUsage;
import root.core.repository.implement.ReportRepositoryImplTest;

public class CsvUtilsTest {

	public static List<String> headers;
	public static String csvString;

	@BeforeAll
	public static void setUp() {
//		headers = getHeadersFromText();
//		csvString = getCsvStringFromText();

		File file = new File("./report/ArchiveUsage/ERP.txt");
		headers = ReportRepositoryImplTest.getHeadersFromFile(file);
		csvString = ReportRepositoryImplTest.getCsvStringFromFile(file);
		System.out.println("Before parsing: \n" + csvString);
	}

	// @Test
	public void testFile() {
		System.out.println(ReportRepositoryImplTest.getHeadersFromFile(new File("./report/ArchiveUsage/ERP.txt")));
	}

	@SuppressWarnings("unused")
	private static List<String> getHeadersFromText() {
		List<String> headers = List.of("FIRST", "SECOND", "THIRD");
		return headers;
	}

	@SuppressWarnings("unused")
	private static String getCsvStringFromText() {
		String first = wrapInDoubleQuotation("1");
		String second = wrapInDoubleQuotation("\\¿ª½½·¡½¬");
		String third = wrapInDoubleQuotation("\"½Öµû¿È,Ç¥·Î ¹­À½\"");
		String csvString1 = StringUtils.joinWith(",", first, second, third);

		String first2 = wrapInDoubleQuotation("2");
		String second2 = wrapInDoubleQuotation("\\¿ª½½·¡½¬2");
		String third2 = wrapInDoubleQuotation("\"½Öµû¿È,Ç¥·Î ¹­À½2\"");
		String csvString2 = StringUtils.joinWith(",", first2, second2, third2);

		String first3 = wrapInDoubleQuotation("3");
		String second3 = wrapInDoubleQuotation("\\¿ª½½·¡½¬3");
		String third3 = wrapInDoubleQuotation("\"½Öµû¿È,Ç¥·Î ¹­À½3\"");
		String csvString3 = StringUtils.joinWith(",", first3, second3, third3);

		return StringUtils.joinWith("\n", csvString1, csvString2, csvString3);
	}

	// @Test
	public void parseCsvString() {
		String[] csvLines = csvString.split("\n");

		List<Map<String, String>> result = new ArrayList<>();

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

			System.out.println(result.size() + " " + map);
			result.add(map);
		}

		System.out.println(result);
	}

	@Test
	public void parseCsvStringAndMappingToBean() {
		String[] csvLines = csvString.split("\n");

		List<Map<String, String>> result = new ArrayList<>();

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

		csvStringToBean(result, ArchiveUsage.class);
	}

	private static <T> List<T> csvStringToBean(List<Map<String, String>> result, Class<T> clazz) {
		List<T> beanList = new ArrayList<>();

		for (Map<String, String> row : result) {
			
			Constructor<T> constructor;
			Object instance;
			Class<?> instanceClass;
			
			try {
				constructor = clazz.getConstructor();
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
						fieldValue = Double.valueOf(row.get(header));
					} else {
						fieldValue = row.get(header);
					}

					Method method = instanceClass.getMethod(setterName, fieldType);
					method.invoke(instance, fieldValue);
				} catch (Exception e) {
					e.printStackTrace();
					break;
				}
			}
			beanList.add(clazz.cast(instance));
		}

		beanList.stream().forEach(bean -> System.out.println(bean));

		return beanList;
	}

	public static Map<Integer, String> parseCsvLine(String csvLine) {

		System.out.println("parseLine: " + csvLine);
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
					// System.out.println("PUT!: " + index + " " +
					// StringEscapeUtils.unescapeHtml4(element.toString()));
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

	private static String wrapInDoubleQuotation(String string) {
		return StringUtils.join("\"", StringEscapeUtils.escapeHtml4(string), "\"");
	}

	/**
	 * ºÎ¸ðÅ¬·¡½ºÀÇ Field ReflectionÇÏ¿© ¹ÝÈ¯ÇÑ´Ù.
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
