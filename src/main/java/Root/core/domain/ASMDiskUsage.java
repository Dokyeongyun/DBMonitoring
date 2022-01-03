package root.core.domain;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ASMDiskUsage {
	private String asmDiskGroupName;
	private String asmDiskGroupType;
	private UnitString totalRawSpace;
	private UnitString totalFreeSpace;
	private UnitString freeSpace;
	private UnitString usedSpace;
	private UnitString usedPercent;
	private String resultMsg;

	public static String toCsvString(List<ASMDiskUsage> list) {
		StringBuffer toCsv = new StringBuffer();
		toCsv.append("NAME,TYPE,TOTAL_RAW(MB),TOTAL_USABLE(MB),USED(MB),USED(%),FREE(MB),RESULT").append("\n");
		
		for(ASMDiskUsage data : list) {
			toCsv.append(data.getAsmDiskGroupName()).append(",");
			toCsv.append(data.getAsmDiskGroupType()).append(",");
			toCsv.append(data.getTotalRawSpace().getValue()).append(data.getTotalRawSpace().getUnit()).append(",");
			toCsv.append(data.getTotalFreeSpace().getValue()).append(data.getTotalFreeSpace().getUnit()).append(",");
			toCsv.append(data.getFreeSpace().getValue()).append(data.getFreeSpace().getUnit()).append(",");
			toCsv.append(data.getUsedSpace().getValue()).append(data.getUsedSpace().getUnit()).append(",");
			toCsv.append(data.getUsedPercent().getValue()).append(data.getUsedPercent().getUnit()).append(",");
			toCsv.append(data.getResultMsg()).append("\n");
		}
		
		return toCsv.toString();
	}
}
