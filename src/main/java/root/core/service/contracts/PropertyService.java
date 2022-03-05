package root.core.service.contracts;

import java.util.List;
import java.util.Map;

import root.core.domain.JdbcConnectionInfo;
import root.core.domain.JschConnectionInfo;
import root.core.domain.MonitoringYN;
import root.core.domain.enums.RoundingDigits;
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
	 */
	List<String> getConnectionInfoList();

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

	Map<String, String> getMonitoringPresetMap();

	List<String> getMonitoringPresetFilePathList();

	List<String> getMonitoringPresetNameList();

	String getMonitoringPresetFilePath(String presetName);

	List<String> getMonitoringDBNameList();

	List<String> getMonitoringServerNameList();

	List<JdbcConnectionInfo> getJdbcConnInfoList(List<String> dbNames);

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
}
