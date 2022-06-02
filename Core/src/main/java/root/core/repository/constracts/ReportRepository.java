package root.core.repository.constracts;

import java.util.List;

public interface ReportRepository {

	<T> void writeReportFile(String fileName, String fileExtension, List<T> monitoringResult, Class<T> clazz);

	List<String> getReportHeaders(Class<?> monitoringType, String alias);

	String getReportContentsInCsv(Class<?> monitoringType, String alias);
}
