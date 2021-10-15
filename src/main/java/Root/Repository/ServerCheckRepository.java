package Root.Repository;

import java.util.List;

import com.jcraft.jsch.Session;

import Root.Model.AlertLog;
import Root.Model.AlertLogCommand;
import Root.Model.AlertLogCommandPeriod;
import Root.Model.OSDiskUsage;

public interface ServerCheckRepository {
	String getServerName();
	
	Session getSession();
	Session connectSession(Session session);
	void disConnectSession(Session session);
	
	String checkAlertLog(AlertLogCommand alc);
	AlertLog checkAlertLogDuringPeriod(AlertLogCommandPeriod alc);
	List<OSDiskUsage> checkOSDiskUsage(String command);
}
