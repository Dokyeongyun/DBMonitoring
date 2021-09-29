package Root.Repository;

import java.util.List;
import java.util.Map;

public interface DBCheckRepository {
	Object getTran();
	void endTran(Object conn);
	
	List<Map<String, Object>> checkArchiveUsage();
	List<Map<String, Object>> checkTableSpaceUsage();
	List<Map<String, Object>> checkASMDiskUsage();
}
