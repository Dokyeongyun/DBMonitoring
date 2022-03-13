package root.javafx.CustomView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Getter;
import root.core.domain.ASMDiskUsage;
import root.core.domain.ArchiveUsage;
import root.core.domain.MonitoringResult;
import root.core.domain.OSDiskUsage;
import root.core.domain.TableSpaceUsage;

public class MonitoringTableViewFactory {

	private MonitoringTableViewFactory() {
	}

	@AllArgsConstructor
	@Getter
	private static class Column {
		private String headerName;
		private String fieldName;
		private boolean isSimpleTableField;
	}

	private static Map<Class<? extends MonitoringResult>, List<Column>> tableColumnMap = new HashMap<>();
	static {
		List<Column> archiveColumns = new ArrayList<>();
		archiveColumns.add(new Column("Archive", "archiveName", true));
		archiveColumns.add(new Column("���� ����", "numberOfFiles", false));
		archiveColumns.add(new Column("��ü ����", "totalSpace", false));
		archiveColumns.add(new Column("���� ����", "reclaimableSpace", false));
		archiveColumns.add(new Column("������� ����", "usedSpace", false));
		archiveColumns.add(new Column("��뷮(%)", "usedPercent", true));
		archiveColumns.add(new Column("����͸��Ͻ�", "monitoringDateTime", false));
		tableColumnMap.put(ArchiveUsage.class, archiveColumns);

		List<Column> tableSpaceColumns = new ArrayList<>();
		tableSpaceColumns.add(new Column("���̺����̽���", "tableSpaceName", true));
		tableSpaceColumns.add(new Column("��ü ����", "totalSpace", false));
		tableSpaceColumns.add(new Column("���� ����", "freeSpace", false));
		tableSpaceColumns.add(new Column("������� ����", "usedSpace", false));
		tableSpaceColumns.add(new Column("��뷮(%)", "usedPercent", true));
		tableSpaceColumns.add(new Column("����͸��Ͻ�", "monitoringDateTime", false));
		tableColumnMap.put(TableSpaceUsage.class, tableSpaceColumns);

		List<Column> asmDiskColumns = new ArrayList<>();
		asmDiskColumns.add(new Column("��ũ �׷�", "asmDiskGroupName", true));
		asmDiskColumns.add(new Column("��ũ Ÿ��", "asmDiskGroupType", true));
		asmDiskColumns.add(new Column("��ü ����(Raw)", "totalRawSpace", false));
		asmDiskColumns.add(new Column("��ü ����(Actual)", "totalFreeSpace", false));
		asmDiskColumns.add(new Column("���� ����", "freeSpace", false));
		asmDiskColumns.add(new Column("������� ����", "usedSpace", false));
		asmDiskColumns.add(new Column("��뷮(%)", "usedPercent", true));
		asmDiskColumns.add(new Column("����͸��Ͻ�", "monitoringDateTime", false));
		tableColumnMap.put(ASMDiskUsage.class, asmDiskColumns);

		List<Column> osDiskColumns = new ArrayList<>();
		osDiskColumns.add(new Column("���� �ý���", "fileSystem", true));
		osDiskColumns.add(new Column("����Ʈ ��ġ", "mountedOn", true));
		osDiskColumns.add(new Column("��ü ����", "totalSpace", false));
		osDiskColumns.add(new Column("���� ����", "freeSpace", false));
		osDiskColumns.add(new Column("������� ����", "usedSpace", false));
		osDiskColumns.add(new Column("��뷮(%)", "usedPercent", true));
		osDiskColumns.add(new Column("����͸��Ͻ�", "monitoringDateTime", false));
		tableColumnMap.put(OSDiskUsage.class, osDiskColumns);
	}

	public static <T extends MonitoringResult> MonitoringTableView<T> create(Class<T> clazz, boolean isSimpleTable) {
		if (tableColumnMap.get(clazz) == null) {
			return null;
		}

		MonitoringTableView<T> tableView = new MonitoringTableView<>();
		List<Column> tableColumns = tableColumnMap.get(clazz)
				.stream()
				.filter(c -> isSimpleTable ? c.isSimpleTableField() : true)
				.collect(Collectors.toList());

		tableColumns.stream().forEach(c -> tableView.addColumn(c.getHeaderName(), c.getFieldName()));
		tableView.setMonitoringDateTimeFormat("yyyy/MM/dd HH:mm:ss");
		
		return tableView;
	}
}
