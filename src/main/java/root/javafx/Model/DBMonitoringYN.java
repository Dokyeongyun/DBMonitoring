package root.javafx.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Data
public class DBMonitoringYN extends MonitoringYN {
	private String archiveUsageYN;
	private String tableSpaceUsageYN;
	private String asmDiskUsageYN;

	public DBMonitoringYN(String alias) {
		super(alias);
	}

	public DBMonitoringYN(String alias, String archiveUsageYN, String tableSpaceUsageYN, String asmDiskUsageYN) {
		super(alias);
		this.archiveUsageYN = archiveUsageYN;
		this.tableSpaceUsageYN = tableSpaceUsageYN;
		this.asmDiskUsageYN = asmDiskUsageYN;
	}
}
