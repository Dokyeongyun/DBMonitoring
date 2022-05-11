package root.common.server.implement;

import java.io.InputStream;

import org.apache.commons.io.IOUtils;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import lombok.extern.slf4j.Slf4j;
import root.core.domain.JschConnectionInfo;

@Slf4j
public class JschServer {
	private JschConnectionInfo jschConnectionInfo;
	private ServerSessionConnectionPoolFactory connPool = null;

	public JschServer(JschConnectionInfo jschConnectionInfo) {
		this.jschConnectionInfo = jschConnectionInfo;
	}

	public String getServerName() {
		return jschConnectionInfo.getServerName();
	}

	public void init() {
		connPool = new ServerSessionConnectionPoolFactory();
	}

	public Session getSession() throws Exception {
		try {
			return connPool.makeObject(jschConnectionInfo).getObject();
		} catch (Exception e) {
			log.error(e.getMessage());
			return null;
		}
	}

	public void disConnect(Session session) throws Exception {
		if (session != null && session.isConnected()) {
			connPool.destroyObject(jschConnectionInfo, connPool.wrap(session));
		}
	}

	public Channel openExecChannel(Session session, String command) throws JSchException {
		if (session == null || !session.isConnected()) {
			throw new JSchException("session is not valid");
		}

		Channel channel = session.openChannel("exec");
		ChannelExec channelExec = (ChannelExec) channel;
//		channelExec.setPty(true);
		channelExec.setCommand(command);
		return channel;
	}

	public InputStream connectChannel(Channel channel) throws Exception {
		InputStream in = channel.getInputStream();
		((ChannelExec) channel).setErrStream(System.err);
		channel.connect();
		return in;
	}

	public void disConnectChannel(Channel channel) {
		if (channel != null && channel.isConnected()) {
			channel.disconnect();
		}
	}

	public String executeCommand(Session session, String command) throws Exception {
		log.debug(command);
		Channel channel = openExecChannel(session, command);
		InputStream in = connectChannel(channel);
		String result = IOUtils.toString(in, "UTF-8");
		disConnectChannel(channel);
		disConnect(session);
		return result;
	}

	public String executeCommand(String command) throws Exception {
		Session session = getSession();
		return executeCommand(session, command);
	}

	public boolean validateConn(Session session) {
		try {
			return connPool.validateObject(jschConnectionInfo, connPool.wrap(session));
		} catch (Exception e) {
			log.error(e.getMessage());
			return false;
		}
	}
}
