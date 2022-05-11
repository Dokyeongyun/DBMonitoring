package root.core.usecase.constracts;

import java.util.List;

import root.core.domain.ASMDiskUsage;
import root.core.domain.ArchiveUsage;
import root.core.domain.TableSpaceUsage;

public interface DBCheckUsecase {
	void printArchiveUsageCheck();

	void printTableSpaceCheck();

	void printASMDiskCheck();

	void writeExcelArchiveUsageCheck() throws Exception;

	void writeCsvArchiveUsage();

	void writeCsvTableSpaceUsage();

	void writeCsvASMDiskUsage();

	List<ArchiveUsage> getCurrentArchiveUsage();

	List<TableSpaceUsage> getCurrentTableSpaceUsage();

	List<ASMDiskUsage> getCurrentASMDiskUsage();
}
