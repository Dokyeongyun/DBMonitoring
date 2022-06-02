package root.common.server.implement;

import lombok.Data;

@Data
public class JschConnectionInfo {
	private String serverName;
	private ServerOS serverOS;
	private String host;
	private int port;
	private String userName;
	private String password;
	private AlertLogCommand alc;

	public JschConnectionInfo() {
		this.alc = new AlertLogCommand();
	}

	public JschConnectionInfo(String serverName, ServerOS serverOS, String host, int port, String userName,
			String password) {
		this.serverName = serverName;
		this.serverOS = serverOS;
		this.host = host;
		this.port = port;
		this.userName = userName;
		this.password = password;
		this.alc = new AlertLogCommand();
	}

	public JschConnectionInfo(String serverName, ServerOS serverOS, String host, String port, String userName,
			String password, AlertLogCommand alc) {
		this(serverName, serverOS, host, 22, userName, password);
		this.setAlc(alc);
		this.setPort(port);
	}

	public JschConnectionInfo(String host, String port, String userName, String password) {
		this("", null, host, 22, userName, password);
		this.setPort(port);
	}

	public int getPort() {
		return this.port == 0 ? 22 : this.port;
	}

	public void setPort(String portString) {
		try {
			this.port = Integer.parseInt(portString);
		} catch (NumberFormatException e) {
			this.port = 22;
		}
	}

	public void setPort(int port) {
		this.port = port;
	}
}
