package root.core.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
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

	public JschConnectionInfo(String serverName, String host, int port, String userName, String password,
			AlertLogCommand alc) {
		this.serverName = serverName;
		this.host = host;
		this.port = port;
		this.userName = userName;
		this.password = password;
		this.alc = alc;
	}
}
