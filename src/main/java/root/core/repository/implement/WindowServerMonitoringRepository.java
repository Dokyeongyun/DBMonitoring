package root.core.repository.implement;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import lombok.extern.slf4j.Slf4j;
import root.common.server.implement.JschServer;
import root.core.domain.AlertLog;
import root.core.domain.AlertLogCommand;
import root.core.domain.Log;
import root.core.domain.OSDiskUsage;
import root.core.repository.constracts.ServerMonitoringRepository;
import root.utils.DateUtils;

@Slf4j
public class WindowServerMonitoringRepository implements ServerMonitoringRepository {
	private JschServer jsch;

	public WindowServerMonitoringRepository(JschServer jsch) {
		this.jsch = jsch;
	}

	@Override
	public String getServerName() {
		return jsch.getServerName();
	}

	@Override
	public int getAlertLogFileLineCount(AlertLogCommand alc) {
		int fileLineCnt = 0;

		try {
			String command = String.format("find /v /c \"\" %s", alc.getReadFilePath());
			String executeResult = jsch.executeCommand(command);
			StringTokenizer st = new StringTokenizer(executeResult);
			String lastToken = "0";
			while (st.hasMoreTokens()) {
				lastToken = st.nextToken();
			}

			fileLineCnt = Integer.parseInt(lastToken);
			log.debug(String.format("alert log file line count: %s, %d", alc.getReadFilePath(), fileLineCnt));
		} catch (Exception e) {
			log.error(e.getMessage());
		}

		return fileLineCnt;
	}

	@Override
	public String checkAlertLog(AlertLogCommand alc) {
		String result = "";
		try {
			String command = String.format("tail -%d %s", alc.getReadLine(), alc.getReadFilePath());
			result = jsch.executeCommand(command);
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		log.debug(alc.toString());
		return result;
	}

	@Override
	public AlertLog checkAlertLogDuringPeriod(AlertLogCommand alc, String startDate, String endDate) {
		AlertLog alertLog = new AlertLog();

		String fullAlertLogString = getAlertLogStringFromCertainDate(alc, startDate);

		try {
			// 조회기간동안의 로그만을 취하여 StringBuffer에 저장한다.
			String[] lines = fullAlertLogString.split(System.lineSeparator());

			boolean isStartDate = false;
			boolean isEndDate = false;

			int readStartIndex = 0;
			int readEndIndex = lines.length;

			String logTimeStamp = "";
			List<String> logContents = new ArrayList<>();
			StringBuffer sb = new StringBuffer();

			for (int i = 0; i < lines.length; i++) {
				String line = lines[i];
				
				// 조회시작일자 찾기
				if (!isStartDate) {
					LocalDate parsedDate = DateUtils.parse(line);
					if (parsedDate != null) {
						// [조회시작일자 >= 최초 로그기록일자]일 때, 최초 로그기록일자부터 읽기 시작
						String parsedDateString = DateUtils.convertDateFormat("yyyy-MM-dd", parsedDate);
						if (DateUtils.getDateDiffTime("yyyy-MM-dd", parsedDateString, startDate) >= 0) {
							isStartDate = true;
							readStartIndex = i;
							logTimeStamp = line;

							// [조회종료일자 > 조회 시작일자 >= 최초 로그기록일자]일 때 최초 로그기록일자부터 읽기 시작
							if(DateUtils.getDateDiffTime("yyyy-MM-dd", parsedDateString, endDate) > 0) {
								isEndDate = true;
								readEndIndex = i;
								break;
							}
						}
					}
				}
				
				// 로그 저장 시작 & 조회종료일자 찾기
				if (isStartDate) {
					LocalDate parsedDate = DateUtils.parse(line);
					if (parsedDate != null) { // Log TimeStamp Line

						// 현재 로그기록일자가 조회종료일자 + 1일인지 확인
						String logDate = DateUtils.convertDateFormat("yyyy-MM-dd", parsedDate);
						if (logDate.startsWith(DateUtils.addDate(endDate, 0, 0, 1))) {
							isEndDate = true;
							readEndIndex = i;
						}

						if (i == readStartIndex) {
							logTimeStamp = line;
						}

						if (i != readStartIndex) {
							alertLog.addLog(new Log(logTimeStamp, logContents));
							logContents = new ArrayList<>();
							logTimeStamp = line;
						}
					} else { // Log Content Line
						logContents.add(line);
					}

					// 로그 저장 중지
					if (!isEndDate) {
						sb.append(line);
					} else {
						break;
					}
				}
			}

			// 종료 후 fullLogString 추가
			alertLog.setFullLogString(sb.toString());

			log.info("\t▶ Alert Log READ LINE: " + (readEndIndex - readStartIndex) + "/" + alc.getReadLine());

		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return alertLog;
	}

	@Override
	public List<OSDiskUsage> checkOSDiskUsage() {
		// TODO Auto-generated method stub
		return null;
	}

	private String getAlertLogStringFromCertainDate(AlertLogCommand alc, String startDate) {
		int alertLogFileLineCnt = getAlertLogFileLineCount(alc);
		String fullAlertLogString = checkAlertLog(alc);

		// 조회시작일자의 로그를 모두 포함하도록 readLine 수를 점진적으로 늘리면서 읽는다.
		while (true) {
			String[] lines = fullAlertLogString.split(System.lineSeparator());

			// 현재 Read Line 수가 파일 최대 Line 수를 초과했을 시, 파일 전체를 읽고 반환한다.
			if (lines.length >= alertLogFileLineCnt) {
				break;
			}

			// 조회한 로그 내에서 가장 처음으로 나타나는 로그의 기록일자를 얻어낸다.
			String logDate = "";
			for (String line : lines) {
				LocalDate parsedDate = DateUtils.parse(line);
				if (parsedDate != null) {
					logDate = DateUtils.convertDateFormat("yyyy-MM-dd", parsedDate);
					break;
				}
			}

			if (logDate == null || logDate.equals("")) {
				break;
			}

			// 조회시작일자와 로그의 처음 기록일자를 비교한다.
			long diffTime = DateUtils.getDateDiffTime("yyyy-MM-dd", logDate, startDate);
			if (diffTime >= 0) { // 조회 Line 수를 더 늘려서 다시 조회
				alc.setReadLine((alc.getReadLine()) * 2);
				fullAlertLogString = checkAlertLog(alc);
			} else {
				break;
			}
		}

		return fullAlertLogString;
	}
}
