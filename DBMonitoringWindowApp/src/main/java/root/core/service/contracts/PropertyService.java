package root.core.service.contracts;

import java.util.List;
import java.util.Map;

import root.common.database.implement.JdbcConnectionInfo;
import root.common.server.implement.AlertLogCommand;
import root.common.server.implement.JschConnectionInfo;
import root.core.domain.MonitoringYN;
import root.core.domain.enums.MonitoringType;
import root.core.domain.enums.RoundingDigits;
import root.core.domain.enums.UsageUIType;
import root.utils.UnitUtils.FileSize;

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

	/**
	 * ������ ���������� �����´�.
	 * 
	 * @param serverName ���� �������� ��Ī
	 * @return
	 */
	JschConnectionInfo getJschConnInfo(String serverName);

	/**
	 * �������� ���������� �����´�.
	 * 
	 * @param serverNames
	 * @return
	 */
	List<JschConnectionInfo> getJschConnInfoList(List<String> serverNames);

	/**
	 * �⺻������ ������ FileSize ������ ��ȯ�Ѵ�.
	 * 
	 * @return
	 */
	FileSize getDefaultFileSizeUnit();

	/**
	 * �⺻������ ������ �ݿø� �ڸ����� ��ȯ�Ѵ�.
	 * 
	 * @return
	 */
	RoundingDigits getDefaultRoundingDigits();

	/**
	 * �⺻������ ������ ��뷮 �÷� UI Ÿ���� ��ȯ�Ѵ�.
	 * 
	 * @return
	 */
	UsageUIType getDefaultUsageUIType();

	/**
	 * ���� ���������� �����Ѵ�.
	 * 
	 * @param key   �������� Ű
	 * @param value �������� ��
	 */
	void saveCommonConfig(String key, String value);

	/**
	 * �ֱ� ����� �������� ���������� �����Ѵ�.
	 * 
	 * @param filePath
	 */
	void saveLastUseConnectionInfoSetting(String filePath);

	/**
	 * �������� ������ �߰��Ѵ�.
	 * 
	 * @param filePath
	 * @return
	 */
	String addConnectionInfoSetting(String filePath);

	/**
	 * ����͸����� Preset ������ �߰��Ѵ�.
	 * 
	 * @param connInfoSetting
	 * @param presetName
	 */
	void addMonitoringPreset(String connInfoSetting, String presetName);

	/**
	 * ����͸����� Preset ������ �����Ѵ�.
	 * 
	 * @param presetName
	 * @param settingedMonitoringYN
	 */
	void saveMonitoringPresetSetting(String presetName,
			Map<MonitoringType, Map<String, Boolean>> settingedMonitoringYN);

	/**
	 * ������ AlertLog ����͸� Ŀ�ǵ� ������ �����´�.
	 * 
	 * @param connInfoSetting
	 * @param serverName
	 * @return
	 */
	AlertLogCommand getAlertLogCommand(String connInfoSetting, String serverName);
}
