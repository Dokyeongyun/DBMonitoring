package Root.Repository;

import java.io.InputStream;

import org.apache.commons.io.IOUtils;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import Root.RemoteServer.JschUtil;

public class ServerCheckRepositoryImpl implements ServerCheckRepository {
	private JschUtil jsch;
	
	public ServerCheckRepositoryImpl(JschUtil jsch) {
		this.jsch = jsch;
	}
	
	@Override
	public Session getSession() {
		return jsch.getSession();
	}
	
	@Override
	public Session connectSession(Session session) {
		try {
			session.connect();
		} catch (JSchException e) {
			e.printStackTrace();
		}
		return session;
	}
	
	@Override
	public void disConnectSession(Session session) {
		if(session.isConnected() == true && session != null) {
			session.disconnect();
		}
	}
	
	@Override
	public String checkAlertLog() {
		String result = "";
		try {
			Session session = this.getSession();
			session = this.connectSession(session);
			
			Channel channel = jsch.openExecChannel(session, "tail -10000 /u01/app/oracle/diag/rdbms/dberp/DBERP1/trace/alert_DBERP1.log | grep 'ORA'");
			InputStream in = jsch.connectChannel(channel);
			result = IOUtils.toString(in, "UTF-8");
			jsch.disConnectChannel(channel);
		}		
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
}
