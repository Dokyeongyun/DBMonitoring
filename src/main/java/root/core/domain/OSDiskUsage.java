package root.core.domain;

import java.util.Date;

import lombok.Data;
import lombok.EqualsAndHashCode;
import root.utils.UnitUtils;
import root.utils.UnitUtils.FileSize;

@EqualsAndHashCode(callSuper = false)
@Data
public class OSDiskUsage extends MonitoringResult {

	public OSDiskUsage() {
		super(new Date());
	}

	public OSDiskUsage(String monitoringDate, String monitoringTime, String fileSystem, String mountedOn,
			double totalSpace, double freeSpace, double usedSpace, double usedPercent) {
		super(monitoringDate, monitoringTime);
		this.fileSystem = fileSystem;
		this.mountedOn = mountedOn;
		this.totalSpace = totalSpace;
		this.freeSpace = freeSpace;
		this.usedSpace = usedSpace;
		this.usedPercent = usedPercent;
	}

	private String fileSystem;

	private String mountedOn;

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

	@Override
	public String toString() {
		return "OSDiskUsage [fileSystem=" + fileSystem + ", mountedOn=" + mountedOn + ", totalSpace=" + totalSpace
				+ ", freeSpace=" + freeSpace + ", usedSpace=" + usedSpace + ", usedPercent=" + usedPercent + "]"
				+ super.getMonitoringDate() + " " + super.getMonitoringTime();
	}
}
