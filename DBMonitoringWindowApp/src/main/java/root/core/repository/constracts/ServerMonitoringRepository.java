package root.core.repository.constracts;

import java.util.List;

import root.common.server.implement.AlertLogCommand;
import root.core.domain.OSDiskUsage;

public interface ServerMonitoringRepository {
	String getServerName();

	int getAlertLogFileLineCount(AlertLogCommand alc);

	String checkAlertLog(AlertLogCommand alc);

	List<OSDiskUsage> checkOSDiskUsage();
}
