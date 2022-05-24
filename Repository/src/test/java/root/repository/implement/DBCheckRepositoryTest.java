package root.repository.implement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import root.common.database.implement.JdbcConnectionInfo;
import root.common.database.implement.JdbcDatabase;
import root.core.domain.ASMDiskUsage;
import root.core.domain.ArchiveUsage;
import root.core.domain.TableSpaceUsage;
import root.core.repository.constracts.DBCheckRepository;

public class DBCheckRepositoryTest {
	public static DBCheckRepository repo;

	@BeforeAll
	public static void setup() {

		// Load db config
		try {
			String str = Files.readString(Paths.get("src/test/resources/dbconfig.txt"));
			String[] split = str.split(",");
			Map<String, String> configMap = new HashMap<>();
			for (String s : split) {
				String[] split2 = s.split("=");
				configMap.put(split2[0], split2[1]);
			}

			String jdbcDriver = configMap.get("jdbcDriver");
			String jdbcUrl = configMap.get("jdbcUrl");
			String jdbcId = configMap.get("jdbcId");
			String jdbcPw = configMap.get("jdbcPw");
			String jdbcValidation = configMap.get("jdbcValidation");
			int jdbcConnections = Integer.parseInt(configMap.get("jdbcConnections"));

			JdbcConnectionInfo jdbc = new JdbcConnectionInfo(jdbcDriver, jdbcUrl, jdbcId, jdbcPw, jdbcValidation,
					jdbcConnections);
			jdbc.setJdbcDBName("testDB");
			JdbcDatabase db = new JdbcDatabase(jdbc);
			db.init();

			repo = new DBCheckRepositoryImpl(db);

		} catch (IOException e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testGetDBName() {
		String result = repo.getDBName();
		assertEquals("testDB", result);
	}

	@Test
	public void testGetTran() {
		Connection result = repo.getTran();
		assertNotNull(result);
	}

	@Test
	public void testEndTran() throws SQLException {
		Connection conn = repo.getTran();
		repo.endTran(conn);
		assertFalse(conn.isClosed());

		repo.endTran(null);
	}

	@Test
	public void testCheckArchiveUsage() {
		// Act
		List<ArchiveUsage> result = repo.checkArchiveUsage();

		// Assert
		assertNotNull(result);
	}
	
	@Test
	public void testCheckTableSpaceUsage() {
		// Act
		List<TableSpaceUsage> result = repo.checkTableSpaceUsage();

		// Assert
		assertNotNull(result);
	}
	
	@Test
	public void testCheckASMDiskUsage() {
		// Act
		List<ASMDiskUsage> result = repo.checkASMDiskUsage();

		// Assert
		assertNotNull(result);
	}
}
