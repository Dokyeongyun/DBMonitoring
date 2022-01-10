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
public class OSDiskUsage {

	@CsvBindByName(column = "Filesystem")
	private String fileSystem;

	@CsvBindByName(column = "Mounted on")
	private String mountedOn;

	@CsvCustomBindByName(column = "Size", converter = UnitStringConverter.class)
	private UnitString totalSpace;

	@CsvCustomBindByName(column = "Avail", converter = UnitStringConverter.class)
	private UnitString freeSpace;

	@CsvCustomBindByName(column = "Used", converter = UnitStringConverter.class)
	private UnitString usedSpace;

	@CsvCustomBindByName(column = "Use%", converter = UnitStringConverter.class)
	private UnitString usedPercent;

	public static String toCsvString(List<OSDiskUsage> list) {
		StringBuffer toCsv = new StringBuffer();
		toCsv.append("Filesystem,Size,Used,Avail,Use%,Mounted on").append("\n");

		for (OSDiskUsage data : list) {
			toCsv.append(data.getFileSystem()).append(",");
			toCsv.append(data.getTotalSpace().getValue()).append(data.getTotalSpace().getUnit()).append(",");
			toCsv.append(data.getUsedSpace().getValue()).append(data.getUsedSpace().getUnit()).append(",");
			toCsv.append(data.getFreeSpace().getValue()).append(data.getFreeSpace().getUnit()).append(",");
			toCsv.append(data.getUsedPercent().getValue()).append(data.getUsedPercent().getUnit()).append(",");
			toCsv.append(data.getMountedOn()).append("\n");
		}

		return toCsv.toString();
	}
}
