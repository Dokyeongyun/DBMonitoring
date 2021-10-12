package Root.Repository;

import java.util.List;
import java.util.Map;

import Root.Model.ArchiveUsage;
import Root.Model.TableSpaceUsage;

@SuppressWarnings("rawtypes")
public interface DBCheckRepository {
	String getDBName();
	
	Object getTran();
	void endTran(Object conn);
	
	List<ArchiveUsage> checkArchiveUsage();
	List<TableSpaceUsage> checkTableSpaceUsage();
	List<Map> checkASMDiskUsage();
}
