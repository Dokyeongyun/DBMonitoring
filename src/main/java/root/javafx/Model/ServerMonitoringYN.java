package root.javafx.Model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Data
public class ServerMonitoringYN extends MonitoringYN {
	private String osDiskUsageYN;

	public ServerMonitoringYN(String alias) {
		super(alias);
	}
	
	public ServerMonitoringYN(String alias, String osDiskUsageYN) {
		super(alias);
		this.osDiskUsageYN = osDiskUsageYN;
	}
}
