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
	
	// Private 필드로 선언 후 Singletone으로 관리
	private static PropertyRepository propertyService = new PropertyRepositoryImpl();
	
	// 생성자를 Private으로 선언함으로써 해당 객체를 생성할 수 있는 방법을 업애버림 => 안정적인 Singletone 관리방법
	private PropertyRepositoryImpl() {}
	
	// propertyService Field에 접근할 수 있는 유일한 방법 (Static Factory Pattern)
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
	 * Configuration 객체를 반환한다.
	 * TODO 굳이 메서드를 Wrapping 해서 호출할 필요가 있을까..? Controller와 의존성 제거목적으로 일단 이렇게 함..
	 */
	@Override
	public PropertiesConfiguration getConfiguration(String name) {
		return (PropertiesConfiguration) PropertiesUtils.getConfig(name);
	}
	
	/**
	 * 주어진 경로에 PropertyConfiguration에 설정된 Key-Value를 저장한다.
	 * TODO PropertiesUtils 클래스쪽의 메서드 제거 후 여기에서 구현하기 (일원화)
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

			log.info("[" + filePath + "] 파일 저장이 성공적으로 완료되었습니다.");
		} catch (Exception e) {
			e.printStackTrace();
			log.error("[" + filePath + "] 파일 저장에 실패했습니다.");
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

			log.info("[" + filePath + "] 파일 저장이 성공적으로 완료되었습니다.");
		} catch (Exception e) {
			e.printStackTrace();
			log.error("[" + filePath + "] 파일 저장에 실패했습니다.");
		}
	}
	
	/**
	 * 접속정보 프로퍼티 파일을 Load한다.
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
	 * 모니터링여부 프로퍼티 파일을 Load한다.
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
	 * DB에 연결하여 모니터링할 내용을 반환한다.
	 */
	@Override
	public String[] getCommonResources(String key) {
		return PropertiesUtils.combinedConfig.getStringArray(key);
	}
	
	/**
	 * DB에 연결하여 모니터링할 내용을 반환한다.
	 */
	@Override
	public String[] getDBMonitoringContents() {
		return PropertiesUtils.combinedConfig.getStringArray("db.monitoring.contents");
	}
	
	/**
	 * Server에 연결하여 모니터링할 내용을 반환한다.
	 */
	@Override
	public String[] getServerMonitoringContents() {
		return PropertiesUtils.combinedConfig.getStringArray("server.monitoring.contents");
	}
	
	/**
	 * Oracle Driver ComboBox의 값을 반환한다.
	 */
	@Override
	public String[] getOracleDrivers() {
		return PropertiesUtils.combinedConfig.getStringArray("db.setting.oracle.driver.combo");
	}
	
	/**
	 * 최근 사용한 접속정보 파일명을 반환한다.
	 */
	@Override
	public String getLastUseConnInfoFilePath() {
		return PropertiesUtils.combinedConfig.getString("filepath.config.lastuse");
	}

	/**
	 * ConnectionInfo Property 파일에 작성된 Monitoring Preset리스트를 반환한다.
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
	 * ConnectionInfo Property 파일에 작성된 Monitoring Preset리스트를 반환한다.
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
	 * 최근 사용한 Monitoring Preset 이름을 반환한다.
	 * 단, 최근 사용한 Preset이 없을 때, NULL을 반환한다.
	 * @return
	 */
	@Override
	public String getLastUseMonitoringPresetName() {
		return PropertiesUtils.connInfoConfig.subset("monitoring.setting.preset.lastuse").getString("");
	}
	
	/**
	 * 모니터링할 DB명 배열을 반환한다.
	 */
	@Override
	public String[] getMonitoringDBNames() {
		return PropertiesUtils.connInfoConfig.getStringArray("dbnames");
	}
	
	/**
	 * 모니터링할 Server명 배열을 반환한다.
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
