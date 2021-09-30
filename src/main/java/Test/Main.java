package Test;

import java.io.InputStream;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

public class Main {
	public static void main(String[] args) throws Exception {
		JSch jsch = new JSch();
		Session session = jsch.getSession("root", "172.16.10.32", 22);
		session.setPassword("daisoasung");
		java.util.Properties config = new java.util.Properties();
		config.put("StrictHostKeyChecking", "no");
		session.setConfig(config);
		session.connect(); // 연결

		Channel channel = session.openChannel("exec"); // 채널접속
		ChannelExec channelExec = (ChannelExec) channel; // 명령 전송 채널사용
		channelExec.setPty(true);
		channelExec.setCommand("netstat -tnlp"); // 내가 실행시킬 명령어를 입력

		// 콜백을 받을 준비.
		StringBuilder outputBuffer = new StringBuilder();
		InputStream in = channel.getInputStream();
		((ChannelExec) channel).setErrStream(System.err);

		channel.connect(); // 실행

		byte[] tmp = new byte[1024];
		while (true) {
			while (in.available() > 0) {
				int i = in.read(tmp, 0, 1024);
				outputBuffer.append(new String(tmp, 0, i));
				if (i < 0)
					break;
			}
			if (channel.isClosed()) {
				System.out.println("결과");
				System.out.println(outputBuffer.toString());
				channel.disconnect();
			}
		}
	}
}
