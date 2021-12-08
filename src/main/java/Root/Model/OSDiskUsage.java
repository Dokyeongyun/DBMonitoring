package Root.Model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class OSDiskUsage {
	private String fileSystem;
	private String mountedOn;
	private UnitString totalSpace;
	private UnitString freeSpace;
	private UnitString usedSpace;
	private UnitString usedPercent;

	public static String toCsvString(List<OSDiskUsage> list) {
		StringBuffer toCsv = new StringBuffer();
		toCsv.append("Filesystem,Size,Used,Avail,Use%,Mounted on").append("\n");
		
		for(OSDiskUsage data : list) {
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
