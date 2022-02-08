package root.core.repository.constracts;

import root.core.domain.MonitoringResult;

public interface ReportRepository {

	void writeReportFile(String filePath, String fileName, String fileExtension, MonitoringResult<?> monitoringResult);
}
