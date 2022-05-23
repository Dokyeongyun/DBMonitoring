package root.repository.implement;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
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
	 * 모니터링 결과를 파일에 기록한다.
	 */
	@Override
	public <T> void writeReportFile(String fileName, String fileExtension, List<T> monitoringResult, Class<T> clazz) {
		if (monitoringResult == null || monitoringResult.size() == 0) {
			log.info("there is no monitoring result to write report");
			return;
		}
		
		File file = new File(monitoringFileDirMap.get(clazz) + "/" + fileName + fileExtension);
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

			for (Object t : monitoringResult) {
				String row = CsvUtils.createCsvRow(t, t.getClass());
				content = StringUtils.joinWith(System.lineSeparator(), content, row);
			}
		} catch (Exception e) {
			log.error(e.getMessage());
		}

		if (content == null) {
			log.info(String.format("파일에 작성할 내용이 없습니다. 파일경로: %s", file.getPath()));
			return;
		}

		try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, true))) {
			bw.append(content);
			bw.flush();
			bw.close();
		} catch (IOException e) {
			log.error(e.getMessage());
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

		} catch (IOException e) {
			log.error(e.getMessage());
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

		} catch (IOException e) {
			log.error(e.getMessage());
		}

		return result.toString();
	}
}
