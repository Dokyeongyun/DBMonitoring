package Root.Model;

import java.util.List;

import lombok.Data;

@Data
public class OSDiskUsage {
	private double availableSpace;
	private String availableSpaceString;
	private double usedSpace;
	private String usedSpaceString;
	private double totalSpace;
	private String totalSpaceString;
	private double usedPercent;
	private String usedPercentString;
	private String fileSystem;
	private String mountedOn;
	
	public OSDiskUsage(String fileSystem, String mountedOn, String totalSpaceString, 
			String availableSpaceString, String usedSpaceString, String usedPercentString) {
		super();
		this.availableSpaceString = availableSpaceString;
		this.usedSpaceString = usedSpaceString;
		this.totalSpaceString = totalSpaceString;
		this.usedPercentString = usedPercentString;
		this.fileSystem = fileSystem;
		this.mountedOn = mountedOn;
	}


	public static String toCsvString(List<OSDiskUsage> list) {
		StringBuffer toCsv = new StringBuffer();
		toCsv.append("Filesystem,Size,Used,Avail,Use%,Mounted on").append("\n");
		
		for(OSDiskUsage data : list) {
			toCsv.append(data.getFileSystem()).append(",");
			toCsv.append(data.getTotalSpaceString()).append(",");
			toCsv.append(data.getUsedSpaceString()).append(",");
			toCsv.append(data.getAvailableSpaceString()).append(",");
			toCsv.append(data.getUsedPercentString()).append(",");
			toCsv.append(data.getMountedOn()).append("\n");
		}
		
		return toCsv.toString();
	}
}
