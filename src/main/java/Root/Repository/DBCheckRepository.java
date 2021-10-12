package Root.Repository;

import java.util.List;
import java.util.Map;

import Root.Model.TableSpaceUsage;

@SuppressWarnings("rawtypes")
public interface DBCheckRepository {
	String getDBName();
	
	Object getTran();
	void endTran(Object conn);
	
	List<Map> checkArchiveUsage();
	List<TableSpaceUsage> checkTableSpaceUsage();
	List<Map> checkASMDiskUsage();
}
