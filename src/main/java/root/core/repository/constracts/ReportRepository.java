package root.core.repository.constracts;

import java.io.File;
import java.util.List;

public interface ReportRepository {

	<T> void writeReportFile(String filePath, String fileName, String fileExtension, List<T> monitoringResult,
			Class<T> clazz);

	List<String> getReportHeaders(File reportFile);
	
	String getReportContentsInCsv(File reportFile);
}
