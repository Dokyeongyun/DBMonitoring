package Root.Model;

public class JschConnectionInfo {
	private String serverName;
	private String host;
	private int port;
	private String userName;
	private String password;
	private AlertLogCommand alc;
	
	public JschConnectionInfo() { }
	public JschConnectionInfo(String serverName, String host, int port, String userName, String password) {
		this.serverName = serverName;
		this.host = host;
		this.port = port;
		this.userName = userName;
		this.password = password;
		this.alc = new AlertLogCommand();
	}

	public String getServerName() {
		return serverName;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}
	
	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public AlertLogCommand getAlc() {
		return alc;
	}

	public void setAlc(AlertLogCommand alc) {
		this.alc = alc;
	}
	
	@Override
	public String toString() {
		return "JschConnectionInfo [serverName=" + serverName + ", host=" + host + ", port=" + port + ", userName="
				+ userName + ", password=" + password + ", alc=" + alc + "]";
	}
}
