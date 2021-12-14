package Root.Model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ArchiveUsage {

	private String archiveName;
	private int numberOfFiles;
	private UnitString totalSpace;
	private UnitString reclaimableSpace;
	private UnitString usedSpace;
	private UnitString usedPercent;
	private String dnt;
	
	public static String toCsvString(List<ArchiveUsage> list) {
		StringBuffer toCsv = new StringBuffer();
		toCsv.append("NAME,NumberOfFiles,Used(%),UsedSpace(G),ReclaimableSpace(G),LimitSpace(G),DNT").append("\n");
		
		for(ArchiveUsage data : list) {
			toCsv.append(data.getArchiveName()).append(",");
			toCsv.append(data.getNumberOfFiles()).append(",");
			toCsv.append(data.getUsedPercent().getValue()).append(data.getUsedPercent().getUnit()).append(",");
			toCsv.append(data.getUsedSpace().getValue()).append(data.getUsedSpace().getUnit()).append(",");
			toCsv.append(data.getReclaimableSpace().getValue()).append(data.getReclaimableSpace().getUnit()).append(",");
			toCsv.append(data.getTotalSpace().getValue()).append(data.getTotalSpace().getUnit()).append(",");
			toCsv.append(data.getDnt()).append("\n");
		}
		
		return toCsv.toString();
	}
}