package root.core.service.contracts;

import java.util.List;
import java.util.Map;

import root.core.domain.JdbcConnectionInfo;
import root.core.domain.JschConnectionInfo;

public interface PropertyService {

	/**
	 * ����͸� �������� ���������� ��θ� ��ȯ�Ѵ�.
	 * 
	 * @return
	 */
	List<String> getConnectionInfoList();
	
	/**
	 * �ֱ� ���� �������� ���������� ��θ� ��ȯ�Ѵ�.
	 * 
	 * @return
	 */
	String getLastUseConnectionInfo();
	
	void loadConnectionInfoConfig(String filePath);

	Map<String, String> getMonitoringPresetMap();

	List<String> getMonitoringPresetFilePathList();

	List<String> getMonitoringPresetNameList();

	String getMonitoringPresetFilePath(String presetName);
	
	List<String> getMonitoringDBNameList();

	List<String> getMonitoringServerNameList();
	
	List<JdbcConnectionInfo> getJdbcConnInfoList(List<String> dbNames);

	List<JschConnectionInfo> getJschConnInfoList(List<String> serverNames);
}
