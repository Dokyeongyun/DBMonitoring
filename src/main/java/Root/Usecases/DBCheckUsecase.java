package Root.Usecases;

public interface DBCheckUsecase {
	void printArchiveUsageCheck();
	void printTableSpaceCheck();
	void printASMDiskCheck();
	
	void writeExcelArchiveUsageCheck() throws Exception;
}
