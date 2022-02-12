package root.core.repository.implement;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import lombok.extern.slf4j.Slf4j;
import root.core.domain.ASMDiskUsage;
import root.core.domain.ArchiveUsage;
import root.core.domain.OSDiskUsage;
import root.core.domain.TableSpaceUsage;
import root.core.repository.constracts.ReportRepository;
import root.utils.CsvUtils;

@Slf4j
public class ReportFileRepo implements ReportRepository {

	private static ReportRepository reportRepo = new ReportFileRepo();

	private String rootDirectory;
	private Map<Class<?>, String> monitoringFileDirMap = new HashMap<>();

	private ReportFileRepo() {
		rootDirectory = "./report";

		monitoringFileDirMap.put(ArchiveUsage.class, rootDirectory + "/ArchiveUsage");
		monitoringFileDirMap.put(TableSpaceUsage.class, rootDirectory + "/TableSpaceUsage");
		monitoringFileDirMap.put(ASMDiskUsage.class, rootDirectory + "/ASMDiskUsage");
		monitoringFileDirMap.put(OSDiskUsage.class, rootDirectory + "/OSDiskUsage");
	}

	public static ReportRepository getInstance() {
		return reportRepo;
	}

	/**
	 * ����͸� ����� ���Ͽ� ����Ѵ�.
	 */
	@Override
	public <T> void writeReportFile(String filePath, String fileName, String fileExtension, List<T> monitoringResult,
			Class<T> clazz) {

		File file = new File(rootDirectory + "/" + filePath + "/" + fileName + fileExtension);
		String content = null;
		try {

			boolean isNewFile = false;
			if (!file.exists()) {
				file.getParentFile().mkdirs();
				isNewFile = file.createNewFile();
			}

			if(isNewFile) {
				content = CsvUtils.createCsvHeader(clazz);	
			}

			for (Object t : monitoringResult) {
				String row = CsvUtils.createCsvRow(t, t.getClass());
				content = StringUtils.joinWith(System.lineSeparator(), content, row);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (content == null) {
			log.info(String.format("���Ͽ� �ۼ��� ������ �����ϴ�. ���ϰ��: %s", file.getPath()));
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

	@Override
	public List<String> getReportHeaders(Class<?> monitoringType, String alias) {
		List<String> result = new ArrayList<>();

		File reportFile = new File(this.monitoringFileDirMap.get(monitoringType) + "/" + alias + ".txt");

		try (BufferedReader br = new BufferedReader(new FileReader(reportFile))) {

			String firstLine = br.readLine();
			Map<Integer, String> headerMap = CsvUtils.parseCsvLine(firstLine);
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

	@Override
	public String getReportContentsInCsv(Class<?> monitoringType, String alias) {
		StringBuilder result = new StringBuilder();
		
		File reportFile = new File(this.monitoringFileDirMap.get(monitoringType) + "/" + alias + ".txt");
		
		try (BufferedReader br = new BufferedReader(new FileReader(reportFile))) {

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