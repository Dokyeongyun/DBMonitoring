package root.core.domain;

import java.util.Date;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import root.utils.UnitUtils;
import root.utils.UnitUtils.FileSize;

@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Data
public class TableSpaceUsage extends MonitoringResult {

	public TableSpaceUsage(Date monitoringDate, String tableSpaceName, double totalSpace, double freeSpace,
			double usedSpace, double usedPercent) {
		super(monitoringDate);
		this.tableSpaceName = tableSpaceName;
		this.totalSpace = totalSpace;
		this.freeSpace = freeSpace;
		this.usedSpace = usedSpace;
		this.usedPercent = usedPercent;
	}

	public TableSpaceUsage(String monitoringDate, String monitoringTime, String tableSpaceName, double totalSpace,
			double freeSpace, double usedSpace, double usedPercent) {
		super(monitoringDate, monitoringTime);
		this.tableSpaceName = tableSpaceName;
		this.totalSpace = totalSpace;
		this.freeSpace = freeSpace;
		this.usedSpace = usedSpace;
		this.usedPercent = usedPercent;
	}

	private String tableSpaceName;

	private double totalSpace;

	private double freeSpace;

	private double usedSpace;

	private double usedPercent;

	@Override
	public void convertUnit(FileSize fromUnit, FileSize toUnit, int round) {
		this.totalSpace = UnitUtils.convertFileUnit(fromUnit, toUnit, totalSpace, round);
		this.freeSpace = UnitUtils.convertFileUnit(fromUnit, toUnit, freeSpace, round);
		this.usedSpace = UnitUtils.convertFileUnit(fromUnit, toUnit, usedSpace, round);
	}
}
