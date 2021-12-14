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
	
	// Private 필드로 선언 후 Singletone으로 관리
	private static PropertyService propertyService = new PropertyServiceImpl();
	
	// 생성자를 Private으로 선언함으로써 해당 객체를 생성할 수 있는 방법을 업애버림 => 안정적인 Singletone 관리방법
	private PropertyServiceImpl() {}
	
	// propertyService Field에 접근할 수 있는 유일한 방법 (Static Factory Pattern)
	public static PropertyService getInstance() {
		return propertyService;
	}
	
	/****************************************************************************/
	
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
