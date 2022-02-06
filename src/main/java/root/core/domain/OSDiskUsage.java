package root.core.domain;

import java.util.List;

import com.opencsv.bean.CsvBindByName;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class OSDiskUsage {

	@CsvBindByName(column = "Filesystem")
	private String fileSystem;

	@CsvBindByName(column = "Mounted on")
	private String mountedOn;

	@CsvBindByName(column = "Size")
	private double totalSpace;

	@CsvBindByName(column = "Avail")
	private double freeSpace;

	@CsvBindByName(column = "Used")
	private double usedSpace;

	@CsvBindByName(column = "Use%")
	private double usedPercent;

	public static String toCsvString(List<OSDiskUsage> list) {
		StringBuffer toCsv = new StringBuffer();
		toCsv.append("Filesystem,Size,Used,Avail,Use%,Mounted on").append("\n");

		for (OSDiskUsage data : list) {
			toCsv.append(data.getFileSystem()).append(",");
			toCsv.append(data.getTotalSpace()).append(",");
			toCsv.append(data.getUsedSpace()).append(",");
			toCsv.append(data.getFreeSpace()).append(",");
			toCsv.append(data.getUsedPercent()).append(",");
			toCsv.append(data.getMountedOn()).append("\n");
		}

		return toCsv.toString();
	}
}
