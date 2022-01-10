package root.core.domain;

import java.util.List;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvCustomBindByName;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import root.javafx.CustomView.UnitStringConverter;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TableSpaceUsage {

	@CsvBindByName(column = "TableSpace")
	private String tableSpaceName;

	@CsvCustomBindByName(column = "Total(G)", converter = UnitStringConverter.class)
	private UnitString totalSpace;

	@CsvCustomBindByName(column = "FreeSpace(G)", converter = UnitStringConverter.class)
	private UnitString freeSpace;

	@CsvCustomBindByName(column = "UsedSpace(G)", converter = UnitStringConverter.class)
	private UnitString usedSpace;

	@CsvCustomBindByName(column = "Used(%)", converter = UnitStringConverter.class)
	private UnitString usedPercent;

	public static String toCsvString(List<TableSpaceUsage> list) {
		StringBuffer toCsv = new StringBuffer();
		toCsv.append("TableSpace,Total(G),UsedSpace(G),Used(%),FreeSpace(G)").append("\n");

		for (TableSpaceUsage data : list) {
			toCsv.append(data.getTableSpaceName()).append(",");
			toCsv.append(data.getTotalSpace().getValue()).append(data.getTotalSpace().getUnit()).append(",");
			toCsv.append(data.getUsedSpace().getValue()).append(data.getUsedSpace().getUnit()).append(",");
			toCsv.append(data.getUsedPercent().getValue()).append(data.getUsedPercent().getUnit()).append(",");
			toCsv.append(data.getFreeSpace().getValue()).append(data.getFreeSpace().getUnit()).append("\n");
		}

		return toCsv.toString();
	}
}
