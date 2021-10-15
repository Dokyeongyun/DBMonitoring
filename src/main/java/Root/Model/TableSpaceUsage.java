package Root.Model;

import java.util.List;

public class TableSpaceUsage {
	private double availableSpace;
	private String availableSpaceString;
	private double usedSpace;
	private String usedSpaceString;
	private double usedPercent;
	private String usedPercentString;
	private double totalSpace;
	private String totalSpaceString;
	private String tableSpaceName;
	
	public TableSpaceUsage() { }
	
	public TableSpaceUsage(String tableSpaceName, String availableSpaceString, String usedSpaceString, String usedPercentString, String totalSpaceString) {
		this.tableSpaceName = tableSpaceName;
		this.availableSpaceString = availableSpaceString;
		this.usedSpaceString = usedSpaceString;
		this.usedPercentString = usedPercentString;
		this.totalSpaceString = totalSpaceString;
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

	public double getTotalSpace() {
		return totalSpace;
	}

	public void setTotalSpace(double totalSpace) {
		this.totalSpace = totalSpace;
	}

	public String getTotalSpaceString() {
		return totalSpaceString;
	}

	public void setTotalSpaceString(String totalSpaceString) {
		this.totalSpaceString = totalSpaceString;
	}

	public String getTableSpaceName() {
		return tableSpaceName;
	}

	public void setTableSpaceName(String tableSpaceName) {
		this.tableSpaceName = tableSpaceName;
	}

	public static String toCsvString(List<TableSpaceUsage> list) {
		StringBuffer toCsv = new StringBuffer();
		toCsv.append("TableSpace,Total(G),UsedSpace(G),Used(%),FreeSpace(G)").append("\n");
		
		for(TableSpaceUsage data : list) {
			toCsv.append(data.getTableSpaceName()).append(",");
			toCsv.append(data.getTotalSpaceString()).append(",");
			toCsv.append(data.getUsedSpaceString()).append(",");
			toCsv.append(data.getUsedPercentString()).append(",");
			toCsv.append(data.getAvailableSpaceString()).append("\n");
		}
		
		return toCsv.toString();
	}
	
	@Override
	public String toString() {
		return "TableSpaceUsage [availableSpace=" + availableSpace + ", availableSpaceString=" + availableSpaceString
				+ ", usedSpace=" + usedSpace + ", usedSpaceString=" + usedSpaceString + ", usedPercent=" + usedPercent
				+ ", usedPercentString=" + usedPercentString + ", totalSpace=" + totalSpace + ", totalSpaceString="
				+ totalSpaceString + "]";
	}
}
