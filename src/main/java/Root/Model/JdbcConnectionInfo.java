package Root.Model;

/**
 * @author DKY
 *
 */
public class JdbcConnectionInfo {
	private String jdbcDBName;
	private String jdbcDriver;
	private String jdbcUrl;
	private String jdbcId;
	private String jdbcPw;
	private String jdbcValidation;
	private int jdbcConnections;
	private String jdbcSID;
	private String jdbcHost;
	private String jdbcPort;
	private String jdbcOracleDriver;
	
	public JdbcConnectionInfo() { }
	
	public JdbcConnectionInfo(String jdbcDBName, String jdbcDriver, String jdbcUrl, String jdbcId, String jdbcPw, String jdbcValidation, int jdbcConnections) {
		this.jdbcDBName = jdbcDBName;
		this.jdbcDriver = jdbcDriver;
		this.jdbcUrl = jdbcUrl;
		this.jdbcId = jdbcId;
		this.jdbcPw = jdbcPw;
		this.jdbcValidation = jdbcValidation;
		this.jdbcConnections = jdbcConnections;
		this.jdbcSID = jdbcUrl.split("@")[1].split(":")[1].split("/")[1];
		this.jdbcHost = jdbcUrl.split("@")[1].split(":")[0];
		this.jdbcPort = jdbcUrl.split("@")[1].split(":")[1].split("/")[0];
		this.jdbcOracleDriver = jdbcUrl.split("@")[0].split(":")[2];
	}

	public String getJdbcDBName() {
		return jdbcDBName;
	}

	public void setJdbcDBName(String jdbcDBName) {
		this.jdbcDBName = jdbcDBName;
	}

	public String getJdbcDriver() {
		return jdbcDriver;
	}

	public void setJdbcDriver(String jdbcDriver) {
		this.jdbcDriver = jdbcDriver;
	}

	public String getJdbcUrl() {
		return jdbcUrl;
	}

	public void setJdbcUrl(String jdbcUrl) {
		this.jdbcUrl = jdbcUrl;
	}

	public String getJdbcId() {
		return jdbcId;
	}

	public void setJdbcId(String jdbcId) {
		this.jdbcId = jdbcId;
	}

	public String getJdbcPw() {
		return jdbcPw;
	}

	public void setJdbcPw(String jdbcPw) {
		this.jdbcPw = jdbcPw;
	}

	public String getJdbcValidation() {
		return jdbcValidation;
	}

	public void setJdbcValidation(String jdbcValidation) {
		this.jdbcValidation = jdbcValidation;
	}

	public int getJdbcConnections() {
		return jdbcConnections;
	}

	public void setJdbcConnections(int jdbcConnections) {
		this.jdbcConnections = jdbcConnections;
	}

	public String getJdbcSID() {
		return jdbcSID;
	}

	public void setJdbcSID(String jdbcSID) {
		this.jdbcSID = jdbcSID;
	}

	public String getJdbcHost() {
		return jdbcHost;
	}

	public void setJdbcHost(String jdbcHost) {
		this.jdbcHost = jdbcHost;
	}

	public String getJdbcPort() {
		return jdbcPort;
	}

	public void setJdbcPort(String jdbcPort) {
		this.jdbcPort = jdbcPort;
	}
	
	public String getJdbcOracleDriver() {
		return jdbcOracleDriver;
	}

	public void setJdbcOracleDriver(String jdbcOracleDriver) {
		this.jdbcOracleDriver = jdbcOracleDriver;
	}

	@Override
	public String toString() {
		return "JdbcConnectionInfo [jdbcDBName=" + jdbcDBName + ", jdbcDriver=" + jdbcDriver + ", jdbcUrl=" + jdbcUrl
				+ ", jdbcId=" + jdbcId + ", jdbcPw=" + jdbcPw + ", jdbcValidation=" + jdbcValidation
				+ ", jdbcConnections=" + jdbcConnections + ", jdbcSID=" + jdbcSID + ", jdbcHost=" + jdbcHost
				+ ", jdbcPort=" + jdbcPort + ", jdbcOracleDriver=" + jdbcOracleDriver + "]";
	}

}
