package root.core.repository.implement;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.lang3.StringUtils;

import lombok.extern.slf4j.Slf4j;
import root.core.domain.MonitoringResult;
import root.core.domain.TableSpaceUsage;
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
	public void writeReportFile(String filePath, String fileName, String fileExtension,
			MonitoringResult<?> monitoringResult) {

		File file = new File(rootDirectory + "/" + filePath + "/" + fileName + fileExtension);
		File parentDir = file.getParentFile();

		String content = null;

		try {

			boolean isNewFile = false;
			if (!file.exists()) {
				parentDir.mkdirs();
				isNewFile = file.createNewFile();
			}

			// 첫 파일작성인 경우 헤더 추가
			if (isNewFile) {
				content = StringUtils.joinWith(",", "MONITORING_DATE", "MONITORING_TIME",
						CsvUtils.createCsvHeader(TableSpaceUsage.class));
			}

			// 모니터링결과 Row 추가
			String monitoringDay = monitoringResult.getMonitoringDay();
			String monitoringTime = monitoringResult.getMonitoringTime();
			for (Object obj : monitoringResult.getMonitoringResults()) {
				String rowString = StringUtils.joinWith(",", monitoringDay, monitoringTime,
						CsvUtils.createCsvRow(obj, obj.getClass()));
				content = StringUtils.joinWith(System.lineSeparator(), content, rowString);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (content == null) {
			log.error(String.format("파일에 작성할 내용이 없습니다. 파일경로: %s", file.getPath()));
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

}
