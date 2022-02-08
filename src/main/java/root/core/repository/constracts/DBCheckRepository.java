package root.core.repository.constracts;

import root.core.domain.ASMDiskUsage;
import root.core.domain.ArchiveUsage;
import root.core.domain.MonitoringResult;
import root.core.domain.TableSpaceUsage;

public interface DBCheckRepository {
	String getDBName();
	
	Object getTran();
	void endTran(Object conn);
	
	MonitoringResult<ArchiveUsage> checkArchiveUsage();
	MonitoringResult<TableSpaceUsage> checkTableSpaceUsage();
	MonitoringResult<ASMDiskUsage> checkASMDiskUsage();
}
