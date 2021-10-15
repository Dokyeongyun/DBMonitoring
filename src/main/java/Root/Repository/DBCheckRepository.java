package Root.Repository;

import java.util.List;

import Root.Model.ASMDiskUsage;
import Root.Model.ArchiveUsage;
import Root.Model.TableSpaceUsage;

public interface DBCheckRepository {
	String getDBName();
	
	Object getTran();
	void endTran(Object conn);
	
	List<ArchiveUsage> checkArchiveUsage();
	List<TableSpaceUsage> checkTableSpaceUsage();
	List<ASMDiskUsage> checkASMDiskUsage();
}
