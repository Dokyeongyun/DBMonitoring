package JavaFx.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.PropertiesConfiguration;

import Root.Utils.PropertiesUtils;

public class PropertyServiceImpl implements PropertyService {
	
	// Private �ʵ�� ���� �� Singletone���� ����
	private static PropertyService propertyService = new PropertyServiceImpl();
	
	// �����ڸ� Private���� ���������ν� �ش� ��ü�� ������ �� �ִ� ����� ���ֹ��� => �������� Singletone �������
	private PropertyServiceImpl() {}
	
	// propertyService Field�� ������ �� �ִ� ������ ��� (Static Factory Pattern)
	public static PropertyService getInstance() {
		return propertyService;
	}
	
	/****************************************************************************/
	
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
