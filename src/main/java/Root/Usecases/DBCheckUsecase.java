package Root.Usecases;

import java.util.List;

import Root.Model.ASMDiskUsage;
import Root.Model.ArchiveUsage;
import Root.Model.TableSpaceUsage;

public interface DBCheckUsecase {
	void printArchiveUsageCheck();
	void printTableSpaceCheck();
	void printASMDiskCheck();
	
	void writeExcelArchiveUsageCheck() throws Exception;
	void writeCsvArchiveUsage() throws Exception;
	void writeCsvTableSpaceUsage() throws Exception;
	void writeCsvASMDiskUsage() throws Exception;
	
	List<ArchiveUsage> getCurrentArchiveUsage();
	List<TableSpaceUsage> getCurrentTableSpaceUsage();
	List<ASMDiskUsage> getCurrentASMDiskUsage();
	
}
