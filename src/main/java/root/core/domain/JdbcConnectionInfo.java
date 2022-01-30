package root.core.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author DKY
 *
 */
@NoArgsConstructor
@Data
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

	public JdbcConnectionInfo(String jdbcDriver, String jdbcUrl, String jdbcId, String jdbcPw, String jdbcValidation,
			int jdbcConnections) {
		this.jdbcDriver = jdbcDriver;
		this.jdbcUrl = jdbcUrl;
		this.jdbcId = jdbcId;
		this.jdbcPw = jdbcPw;
		this.jdbcValidation = jdbcValidation;
		this.jdbcConnections = jdbcConnections;
		
		// TODO 선택된 Oracle Driver Type에 따라서, Driver 값 변경하기, 현재는 임시로 모두 동일한 값 입력
		this.jdbcOracleDriver = "oracle.jdbc.driver.OracleDriver";
	}

	public JdbcConnectionInfo(String jdbcDBName, String jdbcDriver, String jdbcUrl, String jdbcId, String jdbcPw,
			String jdbcValidation, int jdbcConnections) {
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
}
