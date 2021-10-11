package Root.Model;

import java.util.List;

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
	
	public double getAvailableSpace() {
		return availableSpace;
	}

	public void setAvailableSpace(double availableSpace) {
		this.availableSpace = availableSpace;
	}

	public String getAvailableSpaceString() {
		return availableSpaceString;
	}

	public void setAvailableSpaceString(String availableSpaceString) {
		this.availableSpaceString = availableSpaceString;
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

	public String getFileSystem() {
		return fileSystem;
	}

	public void setFileSystem(String fileSystem) {
		this.fileSystem = fileSystem;
	}

	public String getMountedOn() {
		return mountedOn;
	}

	public void setMountedOn(String mountedOn) {
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

	@Override
	public String toString() {
		return "OSDiskUsage [availableSpace=" + availableSpace + ", availableSpaceString=" + availableSpaceString
				+ ", usedSpace=" + usedSpace + ", usedSpaceString=" + usedSpaceString + ", totalSpace=" + totalSpace
				+ ", totalSpaceString=" + totalSpaceString + ", usedPercent=" + usedPercent + ", usedPercentString="
				+ usedPercentString + ", fileSystem=" + fileSystem + ", mountedOn=" + mountedOn + "]";
	}
}
