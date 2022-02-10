package Utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
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
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.opencsv.bean.CsvToBeanBuilder;

import root.core.domain.ArchiveUsage;

public class CsvUtilsTest {

	// @Test
	public void csvToBeanTest() {
		List<ArchiveUsage> result = null;
		File file = new File("./report/ArchiveUsage/ERP.txt");

		try {

			result = new CsvToBeanBuilder<ArchiveUsage>(new FileReader(file)).withSeparator(',')
					.withIgnoreEmptyLine(true).withType(ArchiveUsage.class).build().parse();

			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static List<String> headers;
	public static String csvString;

	@BeforeAll
	public static void setUp() {
//		headers = getHeadersFromText();
//		csvString = getCsvStringFromText();
		
		File file = new File("./report/ArchiveUsage/ERP.txt");
		headers = getHeadersFromFile(file);
		csvString = getCsvStringFromFile(file);
		System.out.println("Before parsing: \n" + csvString);
	}

	// @Test
	public void testFile() {
		System.out.println(getHeadersFromFile(new File("./report/ArchiveUsage/ERP.txt")));
	}

	private static List<String> getHeadersFromFile(File file) {
		List<String> result = new ArrayList<>();

		try (BufferedReader br = new BufferedReader(new FileReader(file))) {

			String firstLine = br.readLine();
			Map<Integer, String> headerMap = parseCsvLine(firstLine);
			List<Integer> sortedKeyList = headerMap.keySet().stream().sorted().collect(Collectors.toList());

			for (int i : sortedKeyList) {
				result.add(headerMap.get(i));
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return result;
	}

	private static String getCsvStringFromFile(File file) {
		StringBuilder result = new StringBuilder();

		try (BufferedReader br = new BufferedReader(new FileReader(file))) {

			String line = br.readLine();
			while ((line = br.readLine()) != null) {
				result.append(line).append(System.lineSeparator());
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return result.toString();
	}

	private static List<String> getHeadersFromText() {
		List<String> headers = List.of("FIRST", "SECOND", "THIRD");
		return headers;
	}

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

	//@Test
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
		
		List<ArchiveUsage> beanList = new ArrayList<>();
		
		for(Map<String, String> row : result) {
			ArchiveUsage instance = new ArchiveUsage();
			Class<?> archiveUsageClass = instance.getClass();
			
			for (String header : row.keySet()) {
				String setterName = "set" + header.substring(0, 1).toUpperCase() + header.substring(1);
				try {
					
					List<Field> allFields = getAllFields(instance.getClass());
					Class<?> fieldType = allFields
							.stream()
							.filter(f -> f.getName().equals(header))
							.findFirst()
							.get()
							.getType();
					Object fieldValue = null;
					if (fieldType == int.class) {
						fieldValue = Integer.valueOf(row.get(header));
					} else if (fieldType == double.class) {
						fieldValue = Double.valueOf(row.get(header));
					} else {
						fieldValue = row.get(header);
					}

					Method method = archiveUsageClass.getMethod(setterName, fieldType);
					method.invoke(instance, fieldValue);
				} catch (Exception e) {
					e.printStackTrace();
					break;
				}
			}
			beanList.add(instance);
		}
		
		beanList.stream().forEach(bean -> System.out.println(bean));
	}

	private static Map<Integer, String> parseCsvLine(String csvLine) {
		
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
					//System.out.println("PUT!: " + index + " " + StringEscapeUtils.unescapeHtml4(element.toString()));
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
