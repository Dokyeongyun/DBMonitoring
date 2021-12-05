package Root.Model;

import java.util.List;

import lombok.Data;

@Data
public class ASMDiskUsage {
	
	private String asmDiskGroupName;
	private String asmDiskGroupType;
	private double totalRawSpace;
	private String totalRawSpaceString;
	private double totalAvailableSpace;
	private String totalAvailableSpaceString;
	private double availableSpace;
	private String availableSpaceString;
	private double usedSpace;
	private String usedSpaceString;
	private double usedPercent;
	private String usedPercentString;
	private String resultMsg;
    
	public ASMDiskUsage(String asmDiskGroupName, String asmDiskGroupType, String totalRawSpaceString,
			String totalAvailableSpaceString, String availableSpaceString, String usedSpaceString,
			String usedPercentString, String resultMsg) {

		this.asmDiskGroupName = asmDiskGroupName;
		this.asmDiskGroupType = asmDiskGroupType;
		this.totalRawSpaceString = totalRawSpaceString;
		this.totalAvailableSpaceString = totalAvailableSpaceString;
		this.availableSpaceString = availableSpaceString;
		this.usedSpaceString = usedSpaceString;
		this.usedPercentString = usedPercentString;
		this.resultMsg = resultMsg;
	}

	public static String toCsvString(List<ASMDiskUsage> list) {
		StringBuffer toCsv = new StringBuffer();
		toCsv.append("NAME,TYPE,TOTAL_RAW(MB),TOTAL_USABLE(MB),USED(MB),USED(%),FREE(MB),RESULT").append("\n");
		
		for(ASMDiskUsage data : list) {
			toCsv.append(data.getAsmDiskGroupName()).append(",");
			toCsv.append(data.getAsmDiskGroupType()).append(",");
			toCsv.append(data.getTotalRawSpaceString()).append(",");
			toCsv.append(data.getTotalAvailableSpaceString()).append(",");
			toCsv.append(data.getUsedSpaceString()).append(",");
			toCsv.append(data.getUsedPercentString()).append(",");
			toCsv.append(data.getAvailableSpaceString()).append(",");
			toCsv.append(data.getResultMsg()).append("\n");
		}
		
		return toCsv.toString();
	}
}
