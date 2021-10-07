package Root.Repository;

import com.jcraft.jsch.Session;

import Root.Model.AlertLogCommand;
import Root.Model.AlertLogCommandPeriod;

public interface ServerCheckRepository {
	String getServerName();
	
	Session getSession();
	Session connectSession(Session session);
	void disConnectSession(Session session);
	
	String checkAlertLog(AlertLogCommand alc);
	String checkAlertLogDuringPeriod(AlertLogCommandPeriod alc);
	String checkOSDiskUsage(String command);
}
