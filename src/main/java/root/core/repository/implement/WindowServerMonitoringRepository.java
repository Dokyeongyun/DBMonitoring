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
			// ��ȸ�Ⱓ������ �α׸��� ���Ͽ� StringBuffer�� �����Ѵ�.
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
				
				// ��ȸ�������� ã��
				if (!isStartDate) {
					LocalDate parsedDate = DateUtils.parse(line);
					if (parsedDate != null) {
						// [��ȸ�������� >= ���� �αױ������]�� ��, ���� �αױ�����ں��� �б� ����
						String parsedDateString = DateUtils.convertDateFormat("yyyy-MM-dd", parsedDate);
						if (DateUtils.getDateDiffTime("yyyy-MM-dd", parsedDateString, startDate) >= 0) {
							isStartDate = true;
							readStartIndex = i;
							logTimeStamp = line;

							// [��ȸ�������� > ��ȸ �������� >= ���� �αױ������]�� �� ���� �αױ�����ں��� �б� ����
							if(DateUtils.getDateDiffTime("yyyy-MM-dd", parsedDateString, endDate) > 0) {
								isEndDate = true;
								readEndIndex = i;
								break;
							}
						}
					}
				}
				
				// �α� ���� ���� & ��ȸ�������� ã��
				if (isStartDate) {
					LocalDate parsedDate = DateUtils.parse(line);
					if (parsedDate != null) { // Log TimeStamp Line

						// ���� �αױ�����ڰ� ��ȸ�������� + 1������ Ȯ��
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

					// �α� ���� ����
					if (!isEndDate) {
						sb.append(line);
					} else {
						break;
					}
				}
			}

			// ���� �� fullLogString �߰�
			alertLog.setFullLogString(sb.toString());

			log.info("\t�� Alert Log READ LINE: " + (readEndIndex - readStartIndex) + "/" + alc.getReadLine());

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

		// ��ȸ���������� �α׸� ��� �����ϵ��� readLine ���� ���������� �ø��鼭 �д´�.
		while (true) {
			String[] lines = fullAlertLogString.split(System.lineSeparator());

			// ���� Read Line ���� ���� �ִ� Line ���� �ʰ����� ��, ���� ��ü�� �а� ��ȯ�Ѵ�.
			if (lines.length >= alertLogFileLineCnt) {
				break;
			}

			// ��ȸ�� �α� ������ ���� ó������ ��Ÿ���� �α��� ������ڸ� ����.
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

			// ��ȸ�������ڿ� �α��� ó�� ������ڸ� ���Ѵ�.
			long diffTime = DateUtils.getDateDiffTime("yyyy-MM-dd", logDate, startDate);
			if (diffTime >= 0) { // ��ȸ Line ���� �� �÷��� �ٽ� ��ȸ
				alc.setReadLine((alc.getReadLine()) * 2);
				fullAlertLogString = checkAlertLog(alc);
			} else {
				break;
			}
		}

		return fullAlertLogString;
	}
}
