package root.core.repository.implement;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import lombok.extern.slf4j.Slf4j;
import root.core.repository.constracts.ReportRepository;
import root.utils.CsvUtils;

@Slf4j
public class ReportRepositoryImpl implements ReportRepository {

	private static ReportRepository reportRepository = new ReportRepositoryImpl();

	private String rootDirectory = "./report";

	private ReportRepositoryImpl() {
	}

	public static ReportRepository getInstance() {
		return reportRepository;
	}

	/**
	 * 모니터링 결과를 파일에 기록한다.
	 */
	@Override
	public <T> void writeReportFile(String filePath, String fileName, String fileExtension, List<T> monitoringResult,
			Class<T> clazz) {

		File file = new File(rootDirectory + "/" + filePath + "/" + fileName + fileExtension);
		String content = null;
		try {

			if (!file.exists()) {
				file.getParentFile().mkdirs();
				file.createNewFile();
			}

			content = CsvUtils.createCsvHeader(clazz);

			for (Object t : monitoringResult) {
				String row = CsvUtils.createCsvRow(t, t.getClass());
				content = StringUtils.joinWith(System.lineSeparator(), content, row);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (content == null) {
			log.info(String.format("파일에 작성할 내용이 없습니다. 파일경로: %s", file.getPath()));
			return;
		}

		try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, false))) {
			bw.append(content);
			bw.flush();
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public List<String> getReportHeaders(File file) {
		List<String> result = new ArrayList<>();

		try (BufferedReader br = new BufferedReader(new FileReader(file))) {

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
	public String getReportContentsInCsv(File reportFile) {
		StringBuilder result = new StringBuilder();

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
