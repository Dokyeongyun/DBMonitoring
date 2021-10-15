package Root.Model;

import java.util.List;

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
	
	public ArchiveUsage() {	}
	
	public ArchiveUsage(String archiveName, int numberOfFiles, String reclaimableSpaceString, String usedSpaceString, String usedPercentString, String totalSpaceString, String dnt) {
		this.archiveName = archiveName;
		this.numberOfFiles = numberOfFiles;
		this.reclaimableSpaceString = reclaimableSpaceString;
		this.usedSpaceString = usedSpaceString;
		this.usedPercentString = usedPercentString;
		this.totalSpaceString = totalSpaceString;
		this.dnt = dnt;
	}

	public String getArchiveName() {
		return archiveName;
	}

	public void setArchiveName(String archiveName) {
		this.archiveName = archiveName;
	}

	public int getNumberOfFiles() {
		return numberOfFiles;
	}

	public void setNumberOfFiles(int numberOfFiles) {
		this.numberOfFiles = numberOfFiles;
	}

	public double getReclaimableSpace() {
		return reclaimableSpace;
	}

	public void setReclaimableSpace(double reclaimableSpace) {
		this.reclaimableSpace = reclaimableSpace;
	}

	public String getReclaimableSpaceString() {
		return reclaimableSpaceString;
	}

	public void setReclaimableSpaceString(String reclaimableSpaceString) {
		this.reclaimableSpaceString = reclaimableSpaceString;
	}

	public double getUsedSpace() {
		return usedSpace;
	}

	public void setUsedSpace(double usedSpace) {
		this.usedSpace = usedSpace;
	}

	public String getUsedSpaceString() {
		return usedSpaceString;
	}

	public void setUsedSpaceString(String usedSpaceString) {
		this.usedSpaceString = usedSpaceString;
	}

	public double getUsedPercent() {
		return usedPercent;
	}

	public void setUsedPercent(double usedPercent) {
		this.usedPercent = usedPercent;
	}

	public String getUsedPercentString() {
		return usedPercentString;
	}

	public void setUsedPercentString(String usedPercentString) {
		this.usedPercentString = usedPercentString;
	}

	public double getTotalSpace() {
		return totalSpace;
	}

	public void setTotalSpace(double totalSpace) {
		this.totalSpace = totalSpace;
	}

	public String getTotalSpaceString() {
		return totalSpaceString;
	}

	public void setTotalSpaceString(String totalSpaceString) {
		this.totalSpaceString = totalSpaceString;
	}

	public String getDnt() {
		return dnt;
	}

	public void setDnt(String dnt) {
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
	
	@Override
	public String toString() {
		return "ArchiveUsage [archiveName=" + archiveName + ", numberOfFiles=" + numberOfFiles + ", reclaimableSpace="
				+ reclaimableSpace + ", reclaimableSpaceString=" + reclaimableSpaceString + ", usedSpace=" + usedSpace
				+ ", usedSpaceString=" + usedSpaceString + ", usedPercent=" + usedPercent + ", usedPercentString="
				+ usedPercentString + ", totalSpace=" + totalSpace + ", totalSpaceString=" + totalSpaceString + "]";
	}
}