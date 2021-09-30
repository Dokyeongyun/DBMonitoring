package Root.Repository;

import com.jcraft.jsch.Session;

public interface ServerCheckRepository {
	Session getSession();
	Session connectSession(Session session);
	void disConnectSession(Session session);
	
	String checkAlertLog();
}
