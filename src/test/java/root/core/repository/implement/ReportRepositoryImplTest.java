package root.core.repository.implement;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import root.core.domain.MonitoringResult;
import root.core.domain.TableSpaceUsage;
import root.utils.CsvUtils;

public class ReportRepositoryImplTest {

	public static String rootDirectory = "./report";

	@BeforeAll
	public static void setUp() {
	}

	@Test
	public void writeReportFile_TableSpaceObj() {
		String filePath = "TableSpaceUsage";
		String fileName = "DB1";
		String fileExtension = ".csv";
		List<TableSpaceUsage> list = List.of(new TableSpaceUsage("GGS_DATA", 17.67, 16.83, 95, .84),
				new TableSpaceUsage("SYSTEM", 3.2, 2.9, 91, .3),
				new TableSpaceUsage("DAISO_INDX", 1080, 960.09, 89, 119.91),
				new TableSpaceUsage("DAISO_TBS", 2130, 1719.55, 81, 410.45));

		MonitoringResult<TableSpaceUsage> monitoringResult = new MonitoringResult<>(list);

		File file = new File(rootDirectory + "/" + filePath + "/" + fileName + fileExtension);
		File parentDir = file.getParentFile();

		String content = null;

		try {

			boolean isNewFile = false;
			if (!file.exists()) {
				parentDir.mkdirs();
				file.createNewFile();
				isNewFile = true;
			}

			if (isNewFile) { // 첫 파일작성인 경우 헤더 추가
				content = StringUtils.joinWith(",", "MONITORING_DATE", "MONITORING_TIME",
						CsvUtils.createCsvHeader(TableSpaceUsage.class));
			}

			String monitoringDay = monitoringResult.getMonitoringDay();
			String monitoringTime = monitoringResult.getMonitoringTime();
			for (Object t : monitoringResult.getMonitoringResults()) {
				String row = StringUtils.joinWith(",", monitoringDay, monitoringTime,
						CsvUtils.createCsvRow(t, t.getClass()));
				content = StringUtils.joinWith(System.lineSeparator(), content, row);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if(content == null) {
			System.out.println(String.format("파일에 작성할 내용이 없습니다. 파일경로: %s", file.getPath()));
			return;
		}

		try(BufferedWriter bw = new BufferedWriter(new FileWriter(file, true))) {
			bw.append(content);
			bw.flush();
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
}
