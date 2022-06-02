package root.repository.implement;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.configuration2.PropertiesConfiguration;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import root.common.database.implement.JdbcConnectionInfo;
import root.common.server.implement.AlertLogCommand;
import root.common.server.implement.JschConnectionInfo;
import root.common.server.implement.ServerOS;
import root.core.domain.exceptions.PropertyNotFoundException;
import root.core.domain.exceptions.PropertyNotLoadedException;
import root.core.repository.constracts.PropertyRepository;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PropertyRepositoryImplTest {

	public static PropertyRepository repo;

	@BeforeAll
	public static void setup() {
		repo = PropertyRepositoryImpl.getInstance();
	}

	@Order(1)
	@Test
	public void testGetConfiguration_ConnInfoConfigIsNotExist() {
		// Arrange
		String configName = "connInfoConfig";

		// Act & Assert
		assertThrows(PropertyNotLoadedException.class, () -> repo.getConfiguration(configName));
	}

	@Order(2)
	@Test
	public void testGetConfiguration_MonitoringConfigIsNotExist() {
		// Arrange
		String configName = "monitoringConfig";

		// Act & Assert
		assertThrows(PropertyNotLoadedException.class, () -> repo.getConfiguration(configName));
	}

	@Order(3)
	@Test
	public void testGetConfiguration_InvalidConfig() {
		// Arrange
		String configName = "error";

		// Act & Assert
		assertThrows(PropertyNotLoadedException.class, () -> repo.getConfiguration(configName));
	}

	@Order(4)
	@Test
	public void testGetConfiguration_ConfigIsExist() throws PropertyNotLoadedException {
		// Arrange
		String configName1 = "connInfoConfig";
		String configName2 = "monitoringConfig";
		String configName3 = "commonConfig";
		String configName4 = "rememberConfig";

		repo.loadConnectionInfoConfig("./config/connectioninfo/app.properties");
		repo.loadMonitoringInfoConfig("./config/monitoring/app/default.properties");

		// Act
		PropertiesConfiguration result1 = repo.getConfiguration(configName1);
		PropertiesConfiguration result2 = repo.getConfiguration(configName2);
		PropertiesConfiguration result3 = repo.getConfiguration(configName3);
		PropertiesConfiguration result4 = repo.getConfiguration(configName4);

		// Assert
		assertNotNull(result1);
		assertNotNull(result2);
		assertNotNull(result3);
		assertNotNull(result4);
	}

	@Test
	public void testConnectionInfoFileNames() throws PropertyNotFoundException {
		// Act
		String[] result = repo.getConnectionInfoFileNames();

		// Assert
		String[] expected = new String[] { "./config/connectioninfo/app.properties" };
		assertNotNull(result);
		assertArrayEquals(expected, result);
	}

	@Test
	public void testGetCommonResource() {
		// Arrange
		String key = "unit.filesize";

		// Act
		String result = repo.getCommonResource(key);

		// Assert
		assertNotNull(result);
	}

	@Test
	public void testGetCommonResources() {
		// Arrange
		String key = "db.monitoring.contents";

		// Act
		String[] result = repo.getCommonResources(key);

		// Assert
		assertNotNull(result);
	}

	@Test
	public void testGetDBMonitoringContents() {
		String[] result = repo.getDBMonitoringContents();
		assertNotNull(result);
	}

	@Test
	public void testGetServerMonitoringContents() {
		String[] result = repo.getServerMonitoringContents();
		assertNotNull(result);
	}

	@Test
	public void testGetOracleDrivers() {
		String[] result = repo.getOracleDrivers();
		assertNotNull(result);
	}

	@Test
	public void testGetLastUseConnInfoFilePath() {
		String result = repo.getLastUseConnInfoFilePath();
		assertNotNull(result);
	}

	@Test
	public void testGetMonitoringPresetNameList() {
		List<String> result = repo.getMonitoringPresetNameList();
		assertNotNull(result);
	}

	@Test
	public void testGetMonitoringPresetMap() {
		Map<String, String> result = repo.getMonitoringPresetMap();
		assertNotNull(result);
	}

	@Test
	public void testGetLastUseMonitoringPresetName() {
		String result = repo.getLastUseMonitoringPresetName("./config/connectioninfo/app.properties");
		assertNotNull(result);
	}

	@Test
	public void testGetMonitoringDBNames() {
		String[] result = repo.getMonitoringDBNames();
		assertNotNull(result);
	}

	@Test
	public void testGetMonitoringServerNames() {
		String[] result = repo.getMonitoringServerNames();
		assertNotNull(result);
	}

	@Test
	public void testCreateNewPropertiesFile() {
		// Arrange
		String filePath = "./config/connectioninfo/newApp.properties";
		String type = "ConnectionInfo";

		// Act
		repo.createNewPropertiesFile(filePath, type);

		// Assert
		File created = new File(filePath);
		assertNotNull(created);
		assertTrue(created.exists());

		repo.loadConnectionInfoConfig(filePath);
		assertEquals("default", repo.getLastUseMonitoringPresetName(filePath));
		assertArrayEquals(new String[] {}, repo.getMonitoringDBNames());
		assertArrayEquals(new String[] {}, repo.getMonitoringServerNames());

		// remove created file
		created.delete();
	}

	@Test
	public void testSaveDBConnectionInfo() {
		// Arrange
		String filePath = "./config/connectioninfo/newApp.properties";
		String type = "ConnectionInfo";

		JdbcConnectionInfo info = new JdbcConnectionInfo();
		info.setJdbcDBName("testDB");
		info.setJdbcId("testID");
		info.setJdbcPw("testPW");
		info.setJdbcUrl("testURL");
		info.setJdbcDriver("testDriver");
		info.setJdbcValidation("testValidation");
		info.setJdbcConnections(1);

		Map<String, JdbcConnectionInfo> dbConfig = new HashMap<>();
		dbConfig.put("testDB", info);

		repo.createNewPropertiesFile(filePath, type);

		// Act
		repo.saveDBConnectionInfo(filePath, dbConfig);

		// Assert
		File configFile = new File(filePath);
		assertNotNull(configFile);
		assertTrue(configFile.exists());

		assertArrayEquals(new String[] { "testDB" }, repo.getMonitoringDBNames());
		
		// remove created file
		configFile.delete();
	}
	
	@Test
	public void testSaveServerConnectionInfo() {
		// Arrange
		String filePath = "./config/connectioninfo/newApp.properties";
		String type = "ConnectionInfo";

		JschConnectionInfo info = new JschConnectionInfo();
		info.setServerName("testServer");
		info.setServerOS(ServerOS.LINUX);
		info.setHost("testHost");
		info.setPort("testPort");
		info.setUserName("testUserName");
		info.setPassword("testPassword");
		info.setAlc(new AlertLogCommand(500, "/testPath.log"));

		Map<String, JschConnectionInfo> serverConfig = new HashMap<>();
		serverConfig.put("testServer", info);

		repo.createNewPropertiesFile(filePath, type);

		// Act
		repo.saveServerConnectionInfo(filePath, serverConfig);

		// Assert
		File configFile = new File(filePath);
		assertNotNull(configFile);
		assertTrue(configFile.exists());

		assertArrayEquals(new String[] { "testServer" }, repo.getMonitoringServerNames());
		
		// remove created file
		configFile.delete();
	}
	
	@Test
	public void testSaveCommonConfig_WithMapParameter() throws PropertyNotLoadedException {
		// Arrange
		Map<String, Object> values = new HashMap<>();
		values.put("unit.filesize", "TB");
		
		// Act
		repo.saveCommonConfig(values);
		
		// Assert
		assertEquals("TB", repo.getCommonResource("unit.filesize"));
	}
	
	@Test
	public void testSaveCommonConfig_WithKeyValueParameter() throws PropertyNotLoadedException {
		// Arrange
		String key = "unit.filesize";
		String value = "KB";
		
		// Act
		repo.saveCommonConfig(key, value);
		
		// Assert
		assertEquals(value, repo.getCommonResource(key));
	}
	
	@Test
	public void testGetJdbcConnectionInfo() {
		// Arrange
		String filePath = "./config/connectioninfo/newApp.properties";
		String type = "ConnectionInfo";

		JdbcConnectionInfo info = new JdbcConnectionInfo();
		info.setJdbcDBName("testDB");
		info.setJdbcId("testID");
		info.setJdbcPw("testPW");
		info.setJdbcUrl("jdbc:oracle:thin:@111.11.111.11:1521/SID");
		info.setJdbcDriver("testDriver");
		info.setJdbcValidation("testValidation");
		info.setJdbcConnections(1);

		Map<String, JdbcConnectionInfo> dbConfig = new HashMap<>();
		dbConfig.put("testDB", info);

		repo.createNewPropertiesFile(filePath, type);
		repo.saveDBConnectionInfo(filePath, dbConfig);
		
		// Act
		JdbcConnectionInfo result = repo.getJdbcConnectionInfo("testDB");
		
		// Assert
		assertEquals("testDB", result.getJdbcDBName());
		assertEquals("testID", result.getJdbcId());
		assertEquals("testPW", result.getJdbcPw());
		assertEquals("jdbc:oracle:thin:@111.11.111.11:1521/SID", result.getJdbcUrl());
		assertEquals("testDriver", result.getJdbcDriver());
		assertEquals("testValidation", result.getJdbcValidation());
		assertEquals(1, result.getJdbcConnections());
		
		// remove created file
		File configFile = new File(filePath);
		configFile.delete();
	}
	
	@Test
	public void testGetJschConnectionInfo() {
		// Arrange
		String filePath = "./config/connectioninfo/newApp.properties";
		String type = "ConnectionInfo";

		JschConnectionInfo info = new JschConnectionInfo();
		info.setServerName("testServer");
		info.setServerOS(ServerOS.LINUX);
		info.setHost("testHost");
		info.setPort(22);
		info.setUserName("testUserName");
		info.setPassword("testPassword");
		info.setAlc(new AlertLogCommand(500, "/testPath.log"));

		Map<String, JschConnectionInfo> serverConfig = new HashMap<>();
		serverConfig.put("testServer", info);

		repo.createNewPropertiesFile(filePath, type);
		repo.saveServerConnectionInfo(filePath, serverConfig);
		
		// Act
		JschConnectionInfo result = repo.getJschConnectionInfo("testServer");
		
		// Assert
		assertEquals("testServer", result.getServerName());
		assertEquals(ServerOS.LINUX, result.getServerOS());
		assertEquals("testHost", result.getHost());
		assertEquals(22, result.getPort());
		assertEquals("testUserName", result.getUserName());
		assertEquals("testPassword", result.getPassword());
		assertEquals("/testPath.log", result.getAlc().getReadFilePath());
		assertEquals(500, result.getAlc().getReadLine());
		
		// remove created file
		File configFile = new File(filePath);
		configFile.delete();
	}
	
	@Test
	public void testGetMonitoringConfigResource() {
		repo.getMonitoringConfigResource("");
	}
}
