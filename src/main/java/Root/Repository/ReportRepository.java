package Root.Repository;

public interface ReportRepository {
	
	void writeReportFile(String filePath, String fileName, String fileExtension, String fileContent);
}
