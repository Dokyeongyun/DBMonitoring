package root.core.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ASMDiskUsage {

	private String asmDiskGroupName;

	private String asmDiskGroupType;

	private double totalRawSpace;

	private double totalFreeSpace;

	private double freeSpace;

	private double usedSpace;

	private double usedPercent;

	private String resultMsg;

}
