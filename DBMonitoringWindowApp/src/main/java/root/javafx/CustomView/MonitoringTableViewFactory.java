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
		archiveColumns.add(new Column("파일 개수", "numberOfFiles", false));
		archiveColumns.add(new Column("전체 공간", "totalSpace", false));
		archiveColumns.add(new Column("가용 공간", "reclaimableSpace", false));
		archiveColumns.add(new Column("사용중인 공간", "usedSpace", false));
		archiveColumns.add(new Column("사용량(%)", "usedPercent", true));
		archiveColumns.add(new Column("모니터링일시", "monitoringDateTime", false));
		tableColumnMap.put(ArchiveUsage.class, archiveColumns);

		List<Column> tableSpaceColumns = new ArrayList<>();
		tableSpaceColumns.add(new Column("테이블스페이스명", "tableSpaceName", true));
		tableSpaceColumns.add(new Column("전체 공간", "totalSpace", false));
		tableSpaceColumns.add(new Column("가용 공간", "freeSpace", false));
		tableSpaceColumns.add(new Column("사용중인 공간", "usedSpace", false));
		tableSpaceColumns.add(new Column("사용량(%)", "usedPercent", true));
		tableSpaceColumns.add(new Column("모니터링일시", "monitoringDateTime", false));
		tableColumnMap.put(TableSpaceUsage.class, tableSpaceColumns);

		List<Column> asmDiskColumns = new ArrayList<>();
		asmDiskColumns.add(new Column("디스크 그룹", "asmDiskGroupName", true));
		asmDiskColumns.add(new Column("디스크 타입", "asmDiskGroupType", true));
		asmDiskColumns.add(new Column("전체 공간(Raw)", "totalRawSpace", false));
		asmDiskColumns.add(new Column("전체 공간(Actual)", "totalFreeSpace", false));
		asmDiskColumns.add(new Column("가용 공간", "freeSpace", false));
		asmDiskColumns.add(new Column("사용중인 공간", "usedSpace", false));
		asmDiskColumns.add(new Column("사용량(%)", "usedPercent", true));
		asmDiskColumns.add(new Column("모니터링일시", "monitoringDateTime", false));
		tableColumnMap.put(ASMDiskUsage.class, asmDiskColumns);

		List<Column> osDiskColumns = new ArrayList<>();
		osDiskColumns.add(new Column("파일 시스템", "fileSystem", true));
		osDiskColumns.add(new Column("마운트 위치", "mountedOn", true));
		osDiskColumns.add(new Column("전체 공간", "totalSpace", false));
		osDiskColumns.add(new Column("가용 공간", "freeSpace", false));
		osDiskColumns.add(new Column("사용중인 공간", "usedSpace", false));
		osDiskColumns.add(new Column("사용량(%)", "usedPercent", true));
		osDiskColumns.add(new Column("모니터링일시", "monitoringDateTime", false));
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
