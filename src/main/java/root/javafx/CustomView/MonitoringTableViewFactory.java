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
			tableView.addColumn("��뷮(%)", "usedPercent");
		} else if (clazz == TableSpaceUsage.class) {
			tableView.addColumn("���̺����̽�", "tableSpaceName");
			tableView.addColumn("��뷮(%)", "usedPercent");
		} else if (clazz == ASMDiskUsage.class) {
			tableView.addColumn("��ũ �׷�", "asmDiskGroupName");
			tableView.addColumn("��ũ Ÿ��", "asmDiskGroupType");
			tableView.addColumn("��뷮(%)", "usedPercent");
		} else if (clazz == OSDiskUsage.class) {
			tableView.addColumn("���� �ý���", "fileSystem");
			tableView.addColumn("����Ʈ ��ġ", "mountedOn");
			tableView.addColumn("��뷮(%)", "usedPercent");
		}
		return tableView;
	}
}
