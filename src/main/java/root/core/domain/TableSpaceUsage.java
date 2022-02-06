package root.core.domain;

import java.util.List;

import com.opencsv.bean.CsvBindByName;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TableSpaceUsage {

	@CsvBindByName(column = "TableSpace")
	private String tableSpaceName;

	@CsvBindByName(column = "Total(G)")
	private double totalSpace;

	@CsvBindByName(column = "FreeSpace(G)")
	private double freeSpace;

	@CsvBindByName(column = "UsedSpace(G)")
	private double usedSpace;

	@CsvBindByName(column = "Used(%)")
	private double usedPercent;

	public static String toCsvString(List<TableSpaceUsage> list) {
		StringBuffer toCsv = new StringBuffer();
		toCsv.append("TableSpace,Total(G),UsedSpace(G),Used(%),FreeSpace(G)").append("\n");

		for (TableSpaceUsage data : list) {
			toCsv.append(data.getTableSpaceName()).append(",");
			toCsv.append(data.getTotalSpace()).append(",");
			toCsv.append(data.getUsedSpace()).append(",");
			toCsv.append(data.getUsedPercent()).append(",");
			toCsv.append(data.getFreeSpace()).append(",");
		}

		return toCsv.toString();
	}
}
