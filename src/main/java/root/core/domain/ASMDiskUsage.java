package root.core.domain;

import java.util.Date;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Data
public class ASMDiskUsage extends MonitoringResult {

	public ASMDiskUsage(Date monitoringDate, String asmDiskGroupName, String asmDiskGroupType, double totalRawSpace,
			double totalFreeSpace, double freeSpace, double usedSpace, double usedPercent, String resultMsg) {
		super(monitoringDate);
		this.asmDiskGroupName = asmDiskGroupName;
		this.asmDiskGroupType = asmDiskGroupType;
		this.totalRawSpace = totalRawSpace;
		this.totalFreeSpace = totalFreeSpace;
		this.freeSpace = freeSpace;
		this.usedSpace = usedSpace;
		this.usedPercent = usedPercent;
		this.resultMsg = resultMsg;
	}

	public ASMDiskUsage(String monitoringDate, String monitoringTime, String asmDiskGroupName, String asmDiskGroupType,
			double totalRawSpace, double totalFreeSpace, double freeSpace, double usedSpace, double usedPercent,
			String resultMsg) {
		super(monitoringDate, monitoringTime);
		this.asmDiskGroupName = asmDiskGroupName;
		this.asmDiskGroupType = asmDiskGroupType;
		this.totalRawSpace = totalRawSpace;
		this.totalFreeSpace = totalFreeSpace;
		this.freeSpace = freeSpace;
		this.usedSpace = usedSpace;
		this.usedPercent = usedPercent;
		this.resultMsg = resultMsg;
	}

	private String asmDiskGroupName;

	private String asmDiskGroupType;

	private double totalRawSpace;

	private double totalFreeSpace;

	private double freeSpace;

	private double usedSpace;

	private double usedPercent;

	private String resultMsg;

}
