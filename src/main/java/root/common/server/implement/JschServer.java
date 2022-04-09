package root.common.server.implement;

import java.io.InputStream;
import java.util.Properties;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import root.core.domain.JschConnectionInfo;

@Slf4j
@ToString
public class JschServer {
	private JSch jsch;
	private Session session;
	private JschConnectionInfo jschConnectionInfo;

	public JschServer(JschConnectionInfo jschConnectionInfo) {
		this.jschConnectionInfo = jschConnectionInfo;
	}

	public void init() {
		jsch = new JSch();
		session = null;

		try {
			session = jsch.getSession(jschConnectionInfo.getUserName(), jschConnectionInfo.getHost(),
					Integer.valueOf(jschConnectionInfo.getPort()));
			session.setPassword(jschConnectionInfo.getPassword());

			Properties config = new Properties();
			config.put("StrictHostKeyChecking", "no"); // 호스트 정보를 검사하지 않는다.
			config.put("PreferredAuthentications", "password");
			session.setConfig(config);

		} catch (JSchException e) {
			log.error(e.getMessage());
		}
	}

	public Session getSession() {
		if (session == null) {
			return null;
		}
		return session;
	}

	public Session connect(Session session) throws JSchException {
		if(session == null) {
			throw new NullPointerException("Session is null");
		}
		
		try {
			session.connect();
		} catch (JSchException e) {
			log.error(e.getMessage());
			throw e;
		}
		
		return session;
	}

	public void disConnect(Session session) {
		session.disconnect();
	}

	public Channel openExecChannel(Session session, String command) {
		Channel channel = null;
		try {
			channel = session.openChannel("exec");
			// 채널접속
			ChannelExec channelExec = (ChannelExec) channel; // 명령 전송 채널사용
			channelExec.setPty(true);
			channelExec.setCommand(command);
		} catch (JSchException e) {
			log.error(e.getMessage());
		}
		return channel;
	}

	public InputStream connectChannel(Channel channel) {
		InputStream in = null;
		try {
			// CallBack
			in = channel.getInputStream();
			((ChannelExec) channel).setErrStream(System.err);

			channel.connect();
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return in;
	}

	public void disConnectChannel(Channel channel) {
		channel.disconnect();
	}

	public String getServerName() {
		return this.jschConnectionInfo.getServerName();
	}

	public static boolean validateConn(Session session) {
		if (session == null) {
			log.error("JSch session is null");
			return false;
		}

		try {
			session.connect(3000);
		} catch (JSchException e) {
			log.error(e.getMessage());
			return false;
		}

		return session.isConnected();
	}
}
