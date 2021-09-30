package Root.Model;

public class JdbcConnectionInfo {
	private String jdbcDriver;
	private String jdbcUrl;
	private String jdbcId;
	private String jdbcPw;
	private String jdbcValidation;
	private int jdbcConnections;

	public JdbcConnectionInfo() { }
	
	public JdbcConnectionInfo(String jdbcDriver, String jdbcUrl, String jdbcId, String jdbcPw, String jdbcValidation,
			int jdbcConnections) {
		super();
		this.jdbcDriver = jdbcDriver;
		this.jdbcUrl = jdbcUrl;
		this.jdbcId = jdbcId;
		this.jdbcPw = jdbcPw;
		this.jdbcValidation = jdbcValidation;
		this.jdbcConnections = jdbcConnections;
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

	@Override
	public String toString() {
		return "JdbcConnectionInfo [jdbcDriver=" + jdbcDriver + ", jdbcUrl=" + jdbcUrl + ", jdbcId=" + jdbcId
				+ ", jdbcPw=" + jdbcPw + ", jdbcValidation=" + jdbcValidation + ", jdbcConnections=" + jdbcConnections
				+ "]";
	}
}
