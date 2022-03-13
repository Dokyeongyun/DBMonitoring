package root.javafx.CustomView;

import root.core.domain.ASMDiskUsage;
import root.core.domain.ArchiveUsage;
import root.core.domain.MonitoringResult;
import root.core.domain.OSDiskUsage;
import root.core.domain.TableSpaceUsage;

public class MonitoringTableViewFactory {

	private MonitoringTableViewFactory() {
	}

	public static <T extends MonitoringResult> MonitoringTableView<T> create(Class<T> clazz) {
		MonitoringTableView<T> tableView = new MonitoringTableView<>();

		if (clazz == ArchiveUsage.class) {
			tableView.addColumn("Archive", "archiveName");
			tableView.addColumn("사용량(%)", "usedPercent");
		} else if (clazz == TableSpaceUsage.class) {
			tableView.addColumn("테이블스페이스", "tableSpaceName");
			tableView.addColumn("사용량(%)", "usedPercent");
		} else if (clazz == ASMDiskUsage.class) {
			tableView.addColumn("디스크 그룹", "asmDiskGroupName");
			tableView.addColumn("디스크 타입", "asmDiskGroupType");
			tableView.addColumn("사용량(%)", "usedPercent");
		} else if (clazz == OSDiskUsage.class) {
			tableView.addColumn("파일 시스템", "fileSystem");
			tableView.addColumn("마운트 위치", "mountedOn");
			tableView.addColumn("사용량(%)", "usedPercent");
		}
		return tableView;
	}
}
