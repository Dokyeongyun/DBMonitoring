package root.core.domain;

import java.util.List;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Data;
import root.core.domain.enums.MonitoringType;

@AllArgsConstructor
@Data
public class MonitoringYN {

	private String monitoringAlias;
	private List<MonitoringTypeAndYN> monitoringTypeList;

	public MonitoringYN(String monitoringAlias) {
		this.monitoringAlias = monitoringAlias;
	}

	public List<MonitoringType> getDistinctMonitoringTypes() {
		return monitoringTypeList.stream().map(type -> type.getMonitoringType()).distinct()
				.collect(Collectors.toList());
	}

	@AllArgsConstructor
	@Data
	public static class MonitoringTypeAndYN {
		private MonitoringType monitoringType;
		private boolean isMonitoring;
	}
}
