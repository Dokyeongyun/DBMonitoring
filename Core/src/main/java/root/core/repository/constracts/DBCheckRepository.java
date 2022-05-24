package root.core.repository.constracts;

import java.sql.Connection;
import java.util.List;

import root.core.domain.ASMDiskUsage;
import root.core.domain.ArchiveUsage;
import root.core.domain.TableSpaceUsage;

public interface DBCheckRepository {
	String getDBName();

	Connection getTran();

	void endTran(Connection conn);

	List<ArchiveUsage> checkArchiveUsage();

	List<TableSpaceUsage> checkTableSpaceUsage();

	List<ASMDiskUsage> checkASMDiskUsage();
}
