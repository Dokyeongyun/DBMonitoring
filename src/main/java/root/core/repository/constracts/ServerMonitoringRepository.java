package root.core.repository.constracts;

import java.util.List;

import com.jcraft.jsch.Session;

import root.core.domain.AlertLog;
import root.core.domain.AlertLogCommand;
import root.core.domain.OSDiskUsage;

public interface ServerMonitoringRepository {
	String getServerName();

	Session getSession();

	Session connectSession(Session session);

	void disConnectSession(Session session);

	int getAlertLogFileLineCount(AlertLogCommand alc);

	String checkAlertLog(AlertLogCommand alc);

	AlertLog checkAlertLogDuringPeriod(AlertLogCommand alc, String startDate, String endDate);

	List<OSDiskUsage> checkOSDiskUsage();
}
