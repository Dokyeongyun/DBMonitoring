package root.core.repository.implement;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration.PropertiesWriter;
import org.apache.commons.configuration2.PropertiesConfigurationLayout;

import lombok.extern.slf4j.Slf4j;
import root.core.domain.JdbcConnectionInfo;
import root.core.domain.JschConnectionInfo;
import root.core.repository.constracts.PropertyRepository;
import root.utils.PropertiesUtils;

@Slf4j
public class PropertyRepositoryImpl implements PropertyRepository {
	
	// Private �ʵ�� ���� �� Singletone���� ����
	private static PropertyRepository propertyService = new PropertyRepositoryImpl();
	
	// �����ڸ� Private���� ���������ν� �ش� ��ü�� ������ �� �ִ� ����� ���ֹ��� => �������� Singletone �������
	private PropertyRepositoryImpl() {}
	
	// propertyService Field�� ������ �� �ִ� ������ ��� (Static Factory Pattern)
	public static PropertyRepository getInstance() {
		return propertyService;
	}
	
	private static Pattern dbPropPattern = Pattern.compile("(.*).jdbc.(.*)");
	private static Pattern serverPropPattern = Pattern.compile("(.*).server.(.*)");

	/****************************************************************************/
	
	@Override
	public boolean isFileExist(String filePath) {
		return new File(filePath).exists();
	}

	/**
	 * Configuration ��ü�� ��ȯ�Ѵ�.
	 * TODO ���� �޼��带 Wrapping �ؼ� ȣ���� �ʿ䰡 ������..? Controller�� ������ ���Ÿ������� �ϴ� �̷��� ��..
	 */
	@Override
	public PropertiesConfiguration getConfiguration(String name) {
		return (PropertiesConfiguration) PropertiesUtils.getConfig(name);
	}
	
	/**
	 * �־��� ��ο� PropertyConfiguration�� ������ Key-Value�� �����Ѵ�.
	 * TODO PropertiesUtils Ŭ�������� �޼��� ���� �� ���⿡�� �����ϱ� (�Ͽ�ȭ)
	 */
	@Override
	public void save(String filePath, PropertiesConfiguration config) {
		PropertiesUtils.save(filePath, config);
	}
	
	@Override
	public void saveDBConnectionInfo(String filePath, Map<String, JdbcConnectionInfo> dbConfig) {
		PropertiesConfiguration config = PropertiesUtils.connInfoConfig;

		// TODO dbnames property.. 
		String dbNames = "";
		for(String dbName : dbConfig.keySet()) {
			dbNames += dbName + ",";
			
			JdbcConnectionInfo jdbc = dbConfig.get(dbName);
			config.setProperty(dbName + ".jdbc.alias", jdbc.getJdbcDBName());
			config.setProperty(dbName + ".jdbc.id", jdbc.getJdbcId());
			config.setProperty(dbName + ".jdbc.pw", jdbc.getJdbcPw());
			config.setProperty(dbName + ".jdbc.url", jdbc.getJdbcUrl());
			config.setProperty(dbName + ".jdbc.driver", jdbc.getJdbcOracleDriver());
			config.setProperty(dbName + ".jdbc.validation", jdbc.getJdbcValidation());
			config.setProperty(dbName + ".jdbc.connections", jdbc.getJdbcConnections());
		}
		
		config.setProperty("dbnames", dbNames.substring(0, dbNames.length()-1));

		PropertiesConfigurationLayout layout = config.getLayout();
		try {
			PropertiesWriter writer = config.getIOFactory()
					.createPropertiesWriter(new FileWriter(filePath, false), config.getListDelimiterHandler());
			
			// Write Header Comment
			writer.writeln(layout.getHeaderComment());
			
			for (final String key : layout.getKeys()) {
				Matcher m = dbPropPattern.matcher(key);
				if (m.matches()) {
					String dbName = m.group(1);
					if(!dbConfig.containsKey(dbName)) {
						continue;
					} 
				}
				
				// Output blank lines before property
				for (int i = 0; i < layout.getBlancLinesBefore(key); i++) {
					writer.writeln(null);
				}

				// Output the property and its value
				boolean singleLine = layout.isForceSingleLine() || layout.isSingleLine(key);
				writer.setCurrentSeparator(layout.getSeparator(key));
				writer.writeProperty(key, config.getProperty(key), singleLine);
			}

			writer.writeln(layout.getCanonicalFooterCooment(true));
			writer.flush();

			log.info("[" + filePath + "] ���� ������ ���������� �Ϸ�Ǿ����ϴ�.");
		} catch (Exception e) {
			e.printStackTrace();
			log.error("[" + filePath + "] ���� ���忡 �����߽��ϴ�.");
		}
	}
	
	@Override
	public void saveServerConnectionInfo(String filePath, Map<String, JschConnectionInfo> serverConfig) {
		PropertiesConfiguration config = PropertiesUtils.connInfoConfig;

		// TODO servernames property.. 
		String serverNames = "";
		for(String serverName : serverConfig.keySet()) {
			serverNames += serverName + ",";
			
			JschConnectionInfo jsch = serverConfig.get(serverName);
			config.setProperty(serverName + ".server.servername", jsch.getServerName());
			config.setProperty(serverName + ".server.host", jsch.getHost());
			config.setProperty(serverName + ".server.port", jsch.getPort());
			config.setProperty(serverName + ".server.username", jsch.getUserName());
			config.setProperty(serverName + ".server.password", jsch.getPassword());
			
			String dateFormat = jsch.getAlc().getDateFormat();
			String dateFormatRegex = "";

			if (dateFormat.equals("EEE MMM dd HH:mm:ss yyyy")) {
				dateFormatRegex = "...\\s...\\s([0-2][0-9]|1[012])\\s\\d\\d:\\d\\d:\\d\\d\\s\\d{4}";
			} else if (dateFormat.equals("yyyy-MM-dd")) {
				dateFormatRegex = "\\d{4}-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[01])T";
			}

			config.setProperty(serverName + ".server.alertlog.dateformat", dateFormat);
			config.setProperty(serverName + ".server.alertlog.dateformatregex", dateFormatRegex);
			config.setProperty(serverName + ".server.alertlog.filepath", jsch.getAlc().getReadFilePath());
			config.setProperty(serverName + ".server.alertlog.readLine", 500);
		}
		
		config.setProperty("servernames", serverNames.substring(0, serverNames.length()-1));

		PropertiesConfigurationLayout layout = config.getLayout();
		try {
			PropertiesWriter writer = config.getIOFactory()
					.createPropertiesWriter(new FileWriter(filePath, false), config.getListDelimiterHandler());
			
			// Write Header Comment
			writer.writeln(layout.getHeaderComment());
			
			for (final String key : layout.getKeys()) {
				Matcher m = serverPropPattern.matcher(key);
				if (m.matches()) {
					String serverName = m.group(1);
					if(!serverConfig.containsKey(serverName)) {
						continue;
					} 
				}
				
				// Output blank lines before property
				for (int i = 0; i < layout.getBlancLinesBefore(key); i++) {
					writer.writeln(null);
				}

				// Output the property and its value
				boolean singleLine = layout.isForceSingleLine() || layout.isSingleLine(key);
				writer.setCurrentSeparator(layout.getSeparator(key));
				writer.writeProperty(key, config.getProperty(key), singleLine);
			}

			writer.writeln(layout.getCanonicalFooterCooment(true));
			writer.flush();

			log.info("[" + filePath + "] ���� ������ ���������� �Ϸ�Ǿ����ϴ�.");
		} catch (Exception e) {
			e.printStackTrace();
			log.error("[" + filePath + "] ���� ���忡 �����߽��ϴ�.");
		}
	}
	
	/**
	 * �������� ������Ƽ ������ Load�Ѵ�.
	 */
	@Override
	public boolean loadConnectionInfoConfig(String filePath) {
		boolean isSuccess = true;
		try {
			PropertiesUtils.loadAppConfiguration(filePath, "connInfoConfig");
		} catch (Exception e) {
			isSuccess = false;
			e.printStackTrace();
		}
		return isSuccess;
	}
	
	/**
	 * ����͸����� ������Ƽ ������ Load�Ѵ�.
	 */
	@Override
	public boolean loadMonitoringInfoConfig(String filePath) {
		boolean isSuccess = true;
		try {
			PropertiesUtils.loadAppConfiguration(filePath, "monitoringConfig");
		} catch (Exception e) {
			isSuccess = false;
			e.printStackTrace();
		}
		return isSuccess;
	}
	
	
	@Override
	public String[] getConnectionInfoFileNames() {
		String connInfoDirPath = "./config/connectioninfo";
		String[] connInfoFileList = new File(connInfoDirPath).list();
		for(int i=0; i<connInfoFileList.length; i++) {
			connInfoFileList[i] = connInfoDirPath + "/"+connInfoFileList[i];
		}
		return connInfoFileList;
	}
	
	/**
	 * DB�� �����Ͽ� ����͸��� ������ ��ȯ�Ѵ�.
	 */
	@Override
	public String[] getCommonResources(String key) {
		return PropertiesUtils.combinedConfig.getStringArray(key);
	}
	
	/**
	 * DB�� �����Ͽ� ����͸��� ������ ��ȯ�Ѵ�.
	 */
	@Override
	public String[] getDBMonitoringContents() {
		return PropertiesUtils.combinedConfig.getStringArray("db.monitoring.contents");
	}
	
	/**
	 * Server�� �����Ͽ� ����͸��� ������ ��ȯ�Ѵ�.
	 */
	@Override
	public String[] getServerMonitoringContents() {
		return PropertiesUtils.combinedConfig.getStringArray("server.monitoring.contents");
	}
	
	/**
	 * Oracle Driver ComboBox�� ���� ��ȯ�Ѵ�.
	 */
	@Override
	public String[] getOracleDrivers() {
		return PropertiesUtils.combinedConfig.getStringArray("db.setting.oracle.driver.combo");
	}
	
	/**
	 * �ֱ� ����� �������� ���ϸ��� ��ȯ�Ѵ�.
	 */
	@Override
	public String getLastUseConnInfoFilePath() {
		return PropertiesUtils.combinedConfig.getString("filepath.config.lastuse");
	}

	/**
	 * ConnectionInfo Property ���Ͽ� �ۼ��� Monitoring Preset����Ʈ�� ��ȯ�Ѵ�.
	 */
	@Override
	public List<String> getMonitoringPresetNameList() {
		List<String> presetList = new ArrayList<>();
		Configuration monitoringConfig = PropertiesUtils.connInfoConfig.subset("monitoring.setting.preset");
		monitoringConfig.getKeys().forEachRemaining(s -> {
			if(!s.startsWith("lastuse")) {
				presetList.add(s.substring(0, s.indexOf(".")));
			}
		});
		return presetList;
	}
	
	/**
	 * ConnectionInfo Property ���Ͽ� �ۼ��� Monitoring Preset����Ʈ�� ��ȯ�Ѵ�.
	 */
	@Override
	public Map<String, String> getMonitoringPresetMap() {
		Map<String, String> presetMap = new LinkedHashMap<>();
		Configuration monitoringConfig = PropertiesUtils.connInfoConfig.subset("monitoring.setting.preset");
		monitoringConfig.getKeys().forEachRemaining(s -> {
			if(!s.startsWith("lastuse")) {
				presetMap.put(s.substring(0, s.indexOf(".")), monitoringConfig.getString(s));	
			}
		});
		return presetMap;
	}
	
	/**
	 * �ֱ� ����� Monitoring Preset �̸��� ��ȯ�Ѵ�.
	 * ��, �ֱ� ����� Preset�� ���� ��, NULL�� ��ȯ�Ѵ�.
	 * @return
	 */
	@Override
	public String getLastUseMonitoringPresetName() {
		return PropertiesUtils.connInfoConfig.subset("monitoring.setting.preset.lastuse").getString("");
	}
	
	/**
	 * ����͸��� DB�� �迭�� ��ȯ�Ѵ�.
	 */
	@Override
	public String[] getMonitoringDBNames() {
		return PropertiesUtils.connInfoConfig.getStringArray("dbnames");
	}
	
	/**
	 * ����͸��� Server�� �迭�� ��ȯ�Ѵ�.
	 */
	@Override
	public String[] getMonitoringServerNames() {
		return PropertiesUtils.connInfoConfig.getStringArray("servernames");
	}
	
	@Override
	public boolean isMonitoringContent(String toggleId) {
		return PropertiesUtils.monitoringConfig.containsKey(toggleId) == false ? true 
				: PropertiesUtils.monitoringConfig.getBoolean(toggleId);
	}
}
