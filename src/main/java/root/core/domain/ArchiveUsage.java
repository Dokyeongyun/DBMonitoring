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
public class ArchiveUsage extends MonitoringResult {

	public ArchiveUsage(Date monitoringDate, String archiveName, int numberOfFiles, double totalSpace,
			double reclaimableSpace, double usedSpace, double usedPercent, String dnt) {
		super(monitoringDate);
		this.archiveName = archiveName;
		this.numberOfFiles = numberOfFiles;
		this.totalSpace = totalSpace;
		this.reclaimableSpace = reclaimableSpace;
		this.usedSpace = usedSpace;
		this.usedPercent = usedPercent;
		this.dnt = dnt;
	}

	public ArchiveUsage(String monitoringDate, String monitoringTime, String archiveName, int numberOfFiles,
			double totalSpace, double reclaimableSpace, double usedSpace, double usedPercent, String dnt) {
		super(monitoringDate, monitoringTime);
		this.archiveName = archiveName;
		this.numberOfFiles = numberOfFiles;
		this.totalSpace = totalSpace;
		this.reclaimableSpace = reclaimableSpace;
		this.usedSpace = usedSpace;
		this.usedPercent = usedPercent;
		this.dnt = dnt;
	}

	private String archiveName;

	private int numberOfFiles;

	private double totalSpace;

	private double reclaimableSpace;

	private double usedSpace;

	private double usedPercent;

	private String dnt;

	@Override
	public void convertUnit(FileSize fromUnit, FileSize toUnit, int round) {
		this.totalSpace = UnitUtils.convertFileUnit(fromUnit, toUnit, totalSpace, round);
		this.reclaimableSpace = UnitUtils.convertFileUnit(fromUnit, toUnit, reclaimableSpace, round);
		this.usedSpace = UnitUtils.convertFileUnit(fromUnit, toUnit, usedSpace, round);
	}

}