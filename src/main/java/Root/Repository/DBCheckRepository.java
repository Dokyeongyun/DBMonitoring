package Root.Repository;

import java.util.List;
import java.util.Map;

@SuppressWarnings("rawtypes")
public interface DBCheckRepository {
	String getDBName();
	
	Object getTran();
	void endTran(Object conn);
	
	List<Map> checkArchiveUsage();
	List<Map> checkTableSpaceUsage();
	List<Map> checkASMDiskUsage();
}
