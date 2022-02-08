package root.core.repository.implement;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import root.core.domain.TableSpaceUsage;
import root.utils.CsvUtils;
import root.utils.DateUtils;

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
		List<TableSpaceUsage> monitoringResult = List.of(
				new TableSpaceUsage("GGS_DATA", 17.67, 16.83, 95, .84),
				new TableSpaceUsage("SYSTEM", 3.2, 2.9, 91, .3),
				new TableSpaceUsage("DAISO_INDX", 1080, 960.09, 89, 119.91),
				new TableSpaceUsage("DAISO_TBS", 2130, 1719.55, 81, 410.45)
				);

		try {
			File file = new File(rootDirectory + "/" + filePath + "/" + fileName + fileExtension);
			File parentDir = file.getParentFile();

			boolean isNewFile = false;
			if (!file.exists()) {
				parentDir.mkdirs();
				file.createNewFile();
				isNewFile = true;
			}

			String content = null;
			if (isNewFile) { // 첫 파일작성인 경우 헤더 추가
				content = StringUtils.joinWith(",", "MONITORING_DATE", "MONITORING_TIME",
						CsvUtils.createCsvHeader(TableSpaceUsage.class));
			}
			
			String now = DateUtils.getToday("yyyyMMddHHmmss");
			for (TableSpaceUsage t : monitoringResult) {
				String row = StringUtils.joinWith(",", now.substring(0, 8), now.substring(8),
						CsvUtils.createCsvRow(t, TableSpaceUsage.class));
				content = StringUtils.joinWith(System.lineSeparator(), content, row);
			}			
			
			BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));
			bw.append(content);
			bw.flush();
			bw.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
