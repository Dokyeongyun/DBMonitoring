package Root.Usecases;

public interface DBCheckUsecase {
	void printArchiveUsageCheck();
	void printTableSpaceCheck();
	void printASMDiskCheck();
	
	void writeExcelArchiveUsageCheck() throws Exception;
	void writeCsvArchiveUsage() throws Exception;
	void writeCsvTableSpaceUsage() throws Exception;
	void writeCsvASMDiskUsage() throws Exception;
}
