package root.core.repository.constracts;

public interface ReportRepository {
	
	void writeReportFile(String filePath, String fileName, String fileExtension, String fileContent);
}
