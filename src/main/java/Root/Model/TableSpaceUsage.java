package Root.Model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TableSpaceUsage {
	private String tableSpaceName;
	private UnitString totalSpace;
	private UnitString freeSpace;
	private UnitString usedSpace;
	private UnitString usedPercent;

	public static String toCsvString(List<TableSpaceUsage> list) {
		StringBuffer toCsv = new StringBuffer();
		toCsv.append("TableSpace,Total(G),UsedSpace(G),Used(%),FreeSpace(G)").append("\n");
		
		for(TableSpaceUsage data : list) {
			toCsv.append(data.getTableSpaceName()).append(",");
			toCsv.append(data.getTotalSpace().getValue()).append(data.getTotalSpace().getUnit()).append(",");
			toCsv.append(data.getUsedSpace().getValue()).append(data.getUsedSpace().getUnit()).append(",");
			toCsv.append(data.getUsedPercent().getValue()).append(data.getUsedPercent().getUnit()).append(",");
			toCsv.append(data.getFreeSpace().getValue()).append(data.getFreeSpace().getUnit()).append("\n");
		}
		
		return toCsv.toString();
	}
}
