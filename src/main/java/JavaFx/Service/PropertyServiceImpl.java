package JavaFx.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.configuration2.CombinedConfiguration;
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
	 * 접속정보 프로퍼티 파일을 Load한다.
	 */
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
	 * 최근 사용한 접속정보 파일명을 반환한다.
	 */
	@Override
	public String getLastUseConnInfoFileName() {
		return PropertiesUtils.combinedConfig.getString("filepath.config.lastuse");
	}

	/**
	 * ConnectionInfo Property 파일에 작성된 Monitoring Preset리스트를 반환한다.
	 */
	@Override
	public List<String> getMonitoringPresetList() {
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
}
