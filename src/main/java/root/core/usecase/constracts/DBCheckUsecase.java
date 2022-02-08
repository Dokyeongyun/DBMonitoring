package root.core.usecase.constracts;

import root.core.domain.ASMDiskUsage;
import root.core.domain.ArchiveUsage;
import root.core.domain.MonitoringResult;
import root.core.domain.TableSpaceUsage;

public interface DBCheckUsecase {
	void printArchiveUsageCheck();
	void printTableSpaceCheck();
	void printASMDiskCheck();
	
	void writeExcelArchiveUsageCheck() throws Exception;
	void writeCsvArchiveUsage();
	void writeCsvTableSpaceUsage();
	void writeCsvASMDiskUsage();
	
	MonitoringResult<ArchiveUsage> getCurrentArchiveUsage();
	MonitoringResult<TableSpaceUsage> getCurrentTableSpaceUsage();
	MonitoringResult<ASMDiskUsage> getCurrentASMDiskUsage();
	
}
