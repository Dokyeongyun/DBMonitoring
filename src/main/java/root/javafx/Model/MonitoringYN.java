package root.javafx.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class MonitoringYN {
	private String alias;
	private String archiveUsageYN;
	private String tableSpaceUsageYN;
	private String asmDiskUsageYN;
	private String osDiskUsageYN;

	public MonitoringYN(String alias) {
		this.alias = alias;
	}
}
