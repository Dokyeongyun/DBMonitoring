package JavaFx.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.configuration2.CombinedConfiguration;
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
	 * �������� ������Ƽ ������ Load�Ѵ�.
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
	 * �ֱ� ����� �������� ���ϸ��� ��ȯ�Ѵ�.
	 */
	@Override
	public String getLastUseConnInfoFileName() {
		return PropertiesUtils.combinedConfig.getString("filepath.config.lastuse");
	}

	/**
	 * ConnectionInfo Property ���Ͽ� �ۼ��� Monitoring Preset����Ʈ�� ��ȯ�Ѵ�.
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
}
