package root.core.service.contracts;

import java.util.List;
import java.util.Map;

import root.core.domain.JdbcConnectionInfo;
import root.core.domain.JschConnectionInfo;
import root.core.domain.MonitoringYN;

public interface PropertyService {

	/**
	 * �������� ���������� Load �Ѵ�.
	 * 
	 * @param filePath
	 */
	void loadConnectionInfoConfig(String filePath);
	
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
	String getLastUseConnectionInfoFilePath();

	/**
	 * ����͸� ���� ���������� Load �Ѵ�.
	 * 
	 * @param filePath
	 */
	void loadMonitoringInfoConfig(String filePath);
	
	/**
	 * �ֱ� ���� ����͸� ���� Preset ���������� Preset���� ��ȯ�Ѵ�.
	 * 
	 * @return
	 */
	String getLastUsePresetFileName(String filePath);

	/**
	 * ����͸����� Preset ���������� �о� DB ����͸� ���� ����Ʈ�� ��ȯ�Ѵ�.
	 * 
	 * @param presetConfigFileName
	 * @return
	 */
	List<MonitoringYN> getDBMonitoringYnList(String presetConfigFileName);
	
	/**
	 * ����͸����� Preset ���������� �о� Server ����͸� ���� ����Ʈ�� ��ȯ�Ѵ�.
	 * 
	 * @param presetConfigFileName
	 * @return
	 */
	List<MonitoringYN> getServerMonitoringYnList(String presetConfigFileName);

	Map<String, String> getMonitoringPresetMap();

	List<String> getMonitoringPresetFilePathList();

	List<String> getMonitoringPresetNameList();

	String getMonitoringPresetFilePath(String presetName);

	List<String> getMonitoringDBNameList();

	List<String> getMonitoringServerNameList();

	List<JdbcConnectionInfo> getJdbcConnInfoList(List<String> dbNames);

	List<JschConnectionInfo> getJschConnInfoList(List<String> serverNames);
}
