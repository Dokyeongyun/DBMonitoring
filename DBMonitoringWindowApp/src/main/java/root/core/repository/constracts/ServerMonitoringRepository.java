package root.core.repository.constracts;

import java.util.List;

import root.core.domain.AlertLogCommand;
import root.core.domain.OSDiskUsage;

public interface ServerMonitoringRepository {
	String getServerName();

	int getAlertLogFileLineCount(AlertLogCommand alc);

	String checkAlertLog(AlertLogCommand alc);

	List<OSDiskUsage> checkOSDiskUsage();
}
