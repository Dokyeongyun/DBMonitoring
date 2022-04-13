package root.common.server.implement;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.io.IOUtils;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import lombok.extern.slf4j.Slf4j;
import root.core.domain.JschConnectionInfo;

@Slf4j
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
			config.put("StrictHostKeyChecking", "no"); // ȣ��Ʈ ������ �˻����� �ʴ´�.
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
		
		if(session.isConnected()) {
			return session;
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
		if(session == null) {
			throw new NullPointerException("Session is null");
		}
		
		session.disconnect();
	}

	public Channel openExecChannel(Session session, String command) {
		if(session == null) {
			init();
			try {
				session = this.connect(this.getSession());
			} catch (JSchException e) {
				log.error(e.getMessage());
			}
		}
		
		Channel channel = null;
		try {
			channel = session.openChannel("exec");
			// ä������
			ChannelExec channelExec = (ChannelExec) channel; // ��� ���� ä�λ��
//			channelExec.setPty(true);
			channelExec.setCommand(command);
		} catch (JSchException e) {
			log.error(e.getMessage());
		}
		return channel;
	}
	
	private Channel openExecChannel(String command) {
		return openExecChannel(session, command);
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

	public String executeCommand(String command) throws JSchException, IOException {
		log.debug(command);
		Channel channel = openExecChannel(command);
		InputStream in = connectChannel(channel);
		String result = IOUtils.toString(in, "UTF-8");
		disConnectChannel(channel);
		disConnect(session);
		return result.trim();
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
