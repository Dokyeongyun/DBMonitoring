package Root.Model;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
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
	
	public TableSpaceUsage(String tableSpaceName, String availableSpaceString, String usedSpaceString, String usedPercentString, String totalSpaceString) {
		this.tableSpaceName = tableSpaceName;
		this.availableSpaceString = availableSpaceString;
		this.usedSpaceString = usedSpaceString;
		this.usedPercentString = usedPercentString;
		this.totalSpaceString = totalSpaceString;
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
}
