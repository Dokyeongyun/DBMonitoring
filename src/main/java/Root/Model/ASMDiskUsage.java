package Root.Model;

import java.util.List;

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
	
	public ASMDiskUsage() { }
    
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

	public String getAsmDiskGroupName() {
		return asmDiskGroupName;
	}
	
	public void setAsmDiskGroupName(String asmDiskGroupName) {
		this.asmDiskGroupName = asmDiskGroupName;
	}
	
	public String getAsmDiskGroupType() {
		return asmDiskGroupType;
	}
	
	public void setAsmDiskGroupType(String asmDiskGroupType) {
		this.asmDiskGroupType = asmDiskGroupType;
	}
	
	public double getTotalRawSpace() {
		return totalRawSpace;
	}
	
	public void setTotalRawSpace(double totalRawSpace) {
		this.totalRawSpace = totalRawSpace;
	}
	
	public String getTotalRawSpaceString() {
		return totalRawSpaceString;
	}
	
	public void setTotalRawSpaceString(String totalRawSpaceString) {
		this.totalRawSpaceString = totalRawSpaceString;
	}
	
	public double getTotalAvailableSpace() {
		return totalAvailableSpace;
	}
	
	public void setTotalAvailableSpace(double totalAvailableSpace) {
		this.totalAvailableSpace = totalAvailableSpace;
	}
	
	public String getTotalAvailableSpaceString() {
		return totalAvailableSpaceString;
	}
	
	public void setTotalAvailableSpaceString(String totalAvailableSpaceString) {
		this.totalAvailableSpaceString = totalAvailableSpaceString;
	}
	
	public double getAvailableSpace() {
		return availableSpace;
	}
	
	public void setAvailableSpace(double availableSpace) {
		this.availableSpace = availableSpace;
	}
	
	public String getAvailableSpaceString() {
		return availableSpaceString;
	}
	
	public void setAvailableSpaceString(String availableSpaceString) {
		this.availableSpaceString = availableSpaceString;
	}
	
	public double getUsedSpace() {
		return usedSpace;
	}
	
	public void setUsedSpace(double usedSpace) {
		this.usedSpace = usedSpace;
	}
	
	public String getUsedSpaceString() {
		return usedSpaceString;
	}
	
	public void setUsedSpaceString(String usedSpaceString) {
		this.usedSpaceString = usedSpaceString;
	}
	
	public double getUsedPercent() {
		return usedPercent;
	}
	
	public void setUsedPercent(double usedPercent) {
		this.usedPercent = usedPercent;
	}
	
	public String getUsedPercentString() {
		return usedPercentString;
	}
	
	public void setUsedPercentString(String usedPercentString) {
		this.usedPercentString = usedPercentString;
	}
	
	public String getResultMsg() {
		return resultMsg;
	}
	
	public void setResultMsg(String resultMsg) {
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

	@Override
	public String toString() {
		return "ASMDiskUsage [asmDiskGroupName=" + asmDiskGroupName + ", asmDiskGroupType=" + asmDiskGroupType
				+ ", totalRawSpace=" + totalRawSpace + ", totalRawSpaceString=" + totalRawSpaceString
				+ ", totalAvailableSpace=" + totalAvailableSpace + ", totalAvailableSpaceString="
				+ totalAvailableSpaceString + ", availableSpace=" + availableSpace + ", availableSpaceString="
				+ availableSpaceString + ", usedSpace=" + usedSpace + ", usedSpaceString=" + usedSpaceString
				+ ", usedPercent=" + usedPercent + ", usedPercentString=" + usedPercentString + ", resultMsg="
				+ resultMsg + "]";
	}
}
