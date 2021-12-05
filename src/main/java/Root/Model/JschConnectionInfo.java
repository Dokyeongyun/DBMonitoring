package Root.Model;

import lombok.Data;

@Data
public class JschConnectionInfo {
	private String serverName;
	private String host;
	private int port;
	private String userName;
	private String password;
	private AlertLogCommand alc;
	
	public JschConnectionInfo(String serverName, String host, int port, String userName, String password) {
		this.serverName = serverName;
		this.host = host;
		this.port = port;
		this.userName = userName;
		this.password = password;
		this.alc = new AlertLogCommand();
	}
}
