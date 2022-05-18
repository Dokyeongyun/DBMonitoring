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
import root.core.domain.exceptions.PropertyNotFoundException;
import root.core.domain.exceptions.PropertyNotLoadedException;
import root.utils.UnitUtils.FileSize;

public interface PropertyService {

	/**
	 * 접속정보 설정파일을 Load 한다.
	 * 
	 * @param filePath
	 */
	void loadConnectionInfoConfig(String filePath);

	/**
	 * 모니터링 접속정보 설정파일의 경로를 반환한다.
	 * 
	 * @return
	 * @throws PropertyNotFoundException 
	 */
	List<String> getConnectionInfoList() throws PropertyNotFoundException;

	/**
	 * 최근 사용된 접속정보 설정파일의 경로를 반환한다.
	 * 
	 * @return
	 */
	String getLastUseConnectionInfoFilePath();

	/**
	 * 모니터링 여부 설정파일을 Load 한다.
	 * 
	 * @param filePath
	 */
	void loadMonitoringInfoConfig(String filePath);

	/**
	 * 최근 사용된 모니터링 여부 Preset 설정파일의 Preset명을 반환한다.
	 * 
	 * @return
	 */
	String getLastUsePresetFileName(String filePath);

	/**
	 * 모니터링여부 Preset 설정파일을 읽어 DB 모니터링 여부 리스트를 반환한다.
	 * 
	 * @param presetConfigFileName
	 * @return
	 */
	List<MonitoringYN> getDBMonitoringYnList(String presetConfigFileName);

	/**
	 * 모니터링여부 Preset 설정파일을 읽어 Server 모니터링 여부 리스트를 반환한다.
	 * 
	 * @param presetConfigFileName
	 * @return
	 */
	List<MonitoringYN> getServerMonitoringYnList(String presetConfigFileName);

	Map<String, String> getMonitoringPresetMap() throws PropertyNotLoadedException;

	List<String> getMonitoringPresetFilePathList() throws PropertyNotLoadedException;

	List<String> getMonitoringPresetNameList() throws PropertyNotLoadedException;

	String getMonitoringPresetFilePath(String presetName) throws PropertyNotLoadedException;

	List<String> getMonitoringDBNameList();

	List<String> getMonitoringServerNameList();

	List<JdbcConnectionInfo> getJdbcConnInfoList(List<String> dbNames);

	/**
	 * 서버의 접속정보를 가져온다.
	 * 
	 * @param serverName 서버 접속정보 별칭
	 * @return
	 */
	JschConnectionInfo getJschConnInfo(String serverName);

	/**
	 * 서버들의 접속정보를 가져온다.
	 * 
	 * @param serverNames
	 * @return
	 */
	List<JschConnectionInfo> getJschConnInfoList(List<String> serverNames);

	/**
	 * 기본값으로 설정된 FileSize 단위를 반환한다.
	 * 
	 * @return
	 */
	FileSize getDefaultFileSizeUnit();

	/**
	 * 기본값으로 설정된 반올림 자릿수를 반환한다.
	 * 
	 * @return
	 */
	RoundingDigits getDefaultRoundingDigits();

	/**
	 * 기본값으로 설정된 사용량 컬럼 UI 타입을 반환한다.
	 * 
	 * @return
	 */
	UsageUIType getDefaultUsageUIType();

	/**
	 * 공통 설정정보를 저장한다.
	 * 
	 * @param key   설정정보 키
	 * @param value 설정정보 값
	 */
	void saveCommonConfig(String key, String value) throws PropertyNotLoadedException;

	/**
	 * 최근 사용한 접속정보 설정정보를 저장한다.
	 * 
	 * @param filePath
	 * @throws PropertyNotLoadedException 
	 */
	void saveLastUseConnectionInfoSetting(String filePath) throws PropertyNotLoadedException;

	/**
	 * 접속정보 설정을 추가한다.
	 * 
	 * @param filePath
	 * @return
	 * @throws PropertyNotLoadedException 
	 */
	String addConnectionInfoSetting(String filePath) throws PropertyNotLoadedException;

	/**
	 * 모니터링여부 Preset 설정을 추가한다.
	 * 
	 * @param connInfoSetting
	 * @param presetName
	 */
	void addMonitoringPreset(String connInfoSetting, String presetName) throws PropertyNotLoadedException;

	/**
	 * 모니터링여부 Preset 설정을 저장한다.
	 * 
	 * @param presetName
	 * @param settingedMonitoringYN
	 */
	void saveMonitoringPresetSetting(String presetName,
			Map<MonitoringType, Map<String, Boolean>> settingedMonitoringYN);

	/**
	 * 설정된 AlertLog 모니터링 커맨드 정보를 가져온다.
	 * 
	 * @param connInfoSetting
	 * @param serverName
	 * @return
	 */
	AlertLogCommand getAlertLogCommand(String connInfoSetting, String serverName);
}
