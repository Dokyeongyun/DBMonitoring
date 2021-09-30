package Root.RemoteServer;

import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.io.IOUtils;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import Root.Model.JschConnectionInfo;

public class JschUtil {
	private JSch jsch;
	private Session session;
	private JschConnectionInfo jschConnectionInfo;

	public JschUtil(JschConnectionInfo jschConnectionInfo) {
		this.jschConnectionInfo = jschConnectionInfo;
	}

	public void init() {
		jsch = new JSch();
		session = null;
		
		try {
			session = jsch.getSession(jschConnectionInfo.getUserName(), jschConnectionInfo.getHost(), jschConnectionInfo.getPort());
			session.setPassword(jschConnectionInfo.getPassword());
			
			Properties config = new Properties();
			config.put("StrictHostKeyChecking", "no"); // 호스트 정보를 검사하지 않는다.
			session.setConfig(config);
			
		} catch (JSchException e) {
			System.out.println("JSch Session Creation Faild!");
			e.printStackTrace();
		}
	}
	
	public Session getSession() {
		if(session == null) {
			return null;
		}
		return session;
	}
	
	public Session connect(Session session) {
		try {
			session.connect();
		} catch (JSchException e) {
			System.out.println("JSch Connection Faild!");
			e.printStackTrace();
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
			//채널접속
			ChannelExec channelExec = (ChannelExec) channel; //명령 전송 채널사용
			channelExec.setPty(true);
			channelExec.setCommand(command); 
		} catch (JSchException e) {
			System.out.println("Channel Open Faild!");
			// e.printStackTrace();
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
			System.out.println("Channel Connect Failed!");
		}
		return in;
	}
	
	public void disConnectChannel(Channel channel) {
	    channel.disconnect();
	}
}
