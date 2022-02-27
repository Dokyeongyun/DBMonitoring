package root.core.service.contracts;

import java.util.List;
import java.util.Map;

import root.core.domain.JdbcConnectionInfo;
import root.core.domain.JschConnectionInfo;

public interface PropertyService {

	/**
	 * 모니터링 접속정보 설정파일의 경로를 반환한다.
	 * 
	 * @return
	 */
	List<String> getConnectionInfoList();
	
	/**
	 * 최근 사용된 접속정보 설정파일을 경로를 반환한다.
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
