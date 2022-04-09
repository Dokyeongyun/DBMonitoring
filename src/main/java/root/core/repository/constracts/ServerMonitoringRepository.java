package root.core.repository.constracts;

import java.util.List;

import root.core.domain.AlertLog;
import root.core.domain.AlertLogCommand;
import root.core.domain.OSDiskUsage;

public interface ServerMonitoringRepository {
	String getServerName();

	int getAlertLogFileLineCount(AlertLogCommand alc);

	String checkAlertLog(AlertLogCommand alc);

	AlertLog checkAlertLogDuringPeriod(AlertLogCommand alc, String startDate, String endDate);

	List<OSDiskUsage> checkOSDiskUsage();
}
