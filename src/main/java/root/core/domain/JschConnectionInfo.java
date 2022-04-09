package root.core.domain;

import lombok.Data;

@Data
public class JschConnectionInfo {
	private String serverName;
	private String host;
	private int port;
	private String userName;
	private String password;
	private AlertLogCommand alc;

	public JschConnectionInfo() {
		this.alc = new AlertLogCommand();
	}

	public JschConnectionInfo(String serverName, String host, int port, String userName, String password) {
		this.serverName = serverName;
		this.host = host;
		this.port = port;
		this.userName = userName;
		this.password = password;
		this.alc = new AlertLogCommand();
	}

	public JschConnectionInfo(String serverName, String host, String port, String userName, String password) {
		this(serverName, host, 22, userName, password);
		this.setPort(port);
	}

	public JschConnectionInfo(String serverName, String host, int port, String userName, String password,
			AlertLogCommand alc) {
		this(serverName, host, port, userName, password);
		this.alc = alc;
	}

	public JschConnectionInfo(String serverName, String host, String port, String userName, String password,
			AlertLogCommand alc) {
		this(serverName, host, 22, userName, password, alc);
		this.setPort(port);
	}

	public int getPort() {
		return this.port == 0 ? 22 : this.port;
	}

	public void setPort(String portString) {
		try {
			this.port = Integer.parseInt(portString);
		} catch (NumberFormatException e) {
			this.port = 22; // ±âº»°ª
		}
	}
}
