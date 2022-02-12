package root.core.repository.implement;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import Utils.CsvUtilsTest;
import root.core.domain.ASMDiskUsage;
import root.core.domain.ArchiveUsage;
import root.core.domain.OSDiskUsage;
import root.core.domain.TableSpaceUsage;
import root.utils.CsvUtils;
import root.utils.DateUtils;

public class ReportRepositoryImplTest {

	public static String rootDirectory = "./report";
	public static String fileName;
	public static String fileExtension;

	@BeforeAll
	public static void setUp() {
		fileName = "ERP";
		fileExtension = ".txt";
	}

	@Test
	public void writeReportFile_ArchiveUsageObj() {
		String filePath = "ArchiveUsage";
		String fileExtension = ".txt";

		Date now = new Date();
		String monitoringDate = DateUtils.format(now, "yyyyMMdd");
		String monitoringTime = DateUtils.format(now, "HHmmss");
		List<ArchiveUsage> list = List.of(new ArchiveUsage(monitoringDate, monitoringTime, "+RECO", 110, 1.073741824E12,
				3.30987208704E11, 5.67697997824E11, 53.0, "2022-02-09, 01:00:28"));

		File file = new File(rootDirectory + "/" + filePath + "/" + fileName + fileExtension);
		writeReportFile(file, ArchiveUsage.class, list);
	}

	@Test
	public void writeReportFile_TableSpaceObj() {
		String filePath = "TableSpaceUsage";

		Date now = new Date();
		String monitoringDate = DateUtils.format(now, "yyyyMMdd");
		String monitoringTime = DateUtils.format(now, "HHmmss");
		List<TableSpaceUsage> list = List.of(
				new TableSpaceUsage(monitoringDate, monitoringTime, "GGS_DATA", 17.67, 16.83, 95, .84),
				new TableSpaceUsage(monitoringDate, monitoringTime, "SYSTEM", 3.2, 2.9, 91, .3),
				new TableSpaceUsage(monitoringDate, monitoringTime, "DAISO_INDX", 1080, 960.09, 89, 119.91),
				new TableSpaceUsage(monitoringDate, monitoringTime, "DAISO_TBS", 2130, 1719.55, 81, 410.45));

		File file = new File(rootDirectory + "/" + filePath + "/" + fileName + fileExtension);
		writeReportFile(file, TableSpaceUsage.class, list);
	}

	@Test
	public void writeReportFile_OSDiskUsageObj() {
		String filePath = "OSDiskUsage";

		Date now = new Date();
		String monitoringDate = DateUtils.format(now, "yyyyMMdd");
		String monitoringTime = DateUtils.format(now, "HHmmss");
		List<OSDiskUsage> list = List.of(
				new OSDiskUsage(monitoringDate, monitoringTime, "/fileSystem", "/mounted", 16.83, 95, .84, 95),
				new OSDiskUsage(monitoringDate, monitoringTime, "/fileSystem2", "/mounted2", 3.2, 2.9, 91, .3),
				new OSDiskUsage(monitoringDate, monitoringTime, "/fileSystem3", "/mounted3", 1080, 960.09, 89, 119.91),
				new OSDiskUsage(monitoringDate, monitoringTime, "/fileSystem4", "/mounted4", 2130, 171.55, 81, 410.45));

		File file = new File(rootDirectory + "/" + filePath + "/" + fileName + fileExtension);
		writeReportFile(file, OSDiskUsage.class, list);
	}

	@Test
	public void writeReportFile_ASMDiskUsageObj() {
		String filePath = "ASMDiskUsage";

		Date now = new Date();
		String monitoringDate = DateUtils.format(now, "yyyyMMdd");
		String monitoringTime = DateUtils.format(now, "HHmmss");
		List<ASMDiskUsage> list = List.of(
				new ASMDiskUsage(monitoringDate, monitoringTime, "DATA", "NORMAL", 1.280302907392E13, 4883968.0,
						3.54643083264E11, 4.766568546304E12, 93.08, "WARNING"),
				new ASMDiskUsage(monitoringDate, monitoringTime, "RECO", "NORMAL", 3.19975063552E12, 1220608.0,
						7.1829553152E11, 5.61604722688E11, 43.88, "GOOD"));

		File file = new File(rootDirectory + "/" + filePath + "/" + fileName + fileExtension);
		writeReportFile(file, ASMDiskUsage.class, list);
	}

	private <T> void writeReportFile(File file, Class<T> clazz, List<T> list) {
		String content = null;
		try {

			boolean isNewFile = false;
			if (!file.exists()) {
				file.getParentFile().mkdirs();
				isNewFile = file.createNewFile();
			}

			if (isNewFile) {
				content = CsvUtils.createCsvHeader(clazz);
			} 

			for (Object t : list) {
				String row = CsvUtils.createCsvRow(t, t.getClass());
				content = StringUtils.joinWith(System.lineSeparator(), content, row);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (content == null) {
			System.out.println(String.format("파일에 작성할 내용이 없습니다. 파일경로: %s", file.getPath()));
			return;
		}

		try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, true))) {
			bw.append(content);
			bw.flush();
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static List<String> getHeadersFromFile(File file) {
		List<String> result = new ArrayList<>();

		try (BufferedReader br = new BufferedReader(new FileReader(file))) {

			String firstLine = br.readLine();
			Map<Integer, String> headerMap = CsvUtilsTest.parseCsvLine(firstLine);
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

	public static String getCsvStringFromFile(File file) {
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
}
