package root.core.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Data
public class OSDiskUsage extends MonitoringResult {

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

}
