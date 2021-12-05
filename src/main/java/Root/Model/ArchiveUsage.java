package Root.Model;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class ArchiveUsage {
	private String archiveName;
	private int numberOfFiles;
	private double reclaimableSpace;
	private String reclaimableSpaceString;
	private double usedSpace;
	private String usedSpaceString;
	private double usedPercent;
	private String usedPercentString;
	private double totalSpace;
	private String totalSpaceString;
	private String dnt;
	
	public ArchiveUsage(String archiveName, int numberOfFiles, String reclaimableSpaceString, String usedSpaceString, String usedPercentString, String totalSpaceString, String dnt) {
		this.archiveName = archiveName;
		this.numberOfFiles = numberOfFiles;
		this.reclaimableSpaceString = reclaimableSpaceString;
		this.usedSpaceString = usedSpaceString;
		this.usedPercentString = usedPercentString;
		this.totalSpaceString = totalSpaceString;
		this.dnt = dnt;
	}
	
	public static String toCsvString(List<ArchiveUsage> list) {
		StringBuffer toCsv = new StringBuffer();
		toCsv.append("NAME,NumberOfFiles,Used(%),UsedSpace(G),ReclaimableSpace(G),LimitSpace(G),DNT").append("\n");
		
		for(ArchiveUsage data : list) {
			toCsv.append(data.getArchiveName()).append(",");
			toCsv.append(data.getNumberOfFiles()).append(",");
			toCsv.append(data.getUsedPercentString()).append(",");
			toCsv.append(data.getUsedSpaceString()).append(",");
			toCsv.append(data.getReclaimableSpaceString()).append(",");
			toCsv.append(data.getTotalSpaceString()).append(",");
			toCsv.append(data.getDnt()).append("\n");
		}
		
		return toCsv.toString();
	}
}