package root.core.repository.implement;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.io.IOUtils;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import lombok.extern.slf4j.Slf4j;
import root.common.server.implement.JschServer;
import root.core.domain.AlertLog;
import root.core.domain.AlertLogCommand;
import root.core.domain.Log;
import root.core.domain.OSDiskUsage;
import root.core.repository.constracts.ServerCheckRepository;
import root.utils.DateUtils;
import root.utils.NumberUnitUtils;
import root.utils.NumberUnitUtils.Unit;

@Slf4j
public class ServerCheckRepositoryImpl implements ServerCheckRepository {
	private JschServer jsch;

	public ServerCheckRepositoryImpl(JschServer jsch) {
		this.jsch = jsch;
	}

	@Override
	public String getServerName() {
		return jsch.getServerName();
	}

	@Override
	public Session getSession() {
		return jsch.getSession();
	}

	@Override
	public Session connectSession(Session session) {
		try {
			session.connect();
		} catch (JSchException e) {
			log.error(e.getMessage());
		}
		return session;
	}

	@Override
	public void disConnectSession(Session session) {
		if (session.isConnected() == true && session != null) {
			session.disconnect();
		}
	}

	@Override
	public int getAlertLogFileLineCount(AlertLogCommand alc) {
		int fileLineCnt = 0;
		try {
			Session session = this.getSession();
			session = this.connectSession(session);
			Channel channel = jsch.openExecChannel(session, "cat " + alc.getReadFilePath() + " | wc -l");
			InputStream in = jsch.connectChannel(channel);
			String result = IOUtils.toString(in, "UTF-8");
			fileLineCnt = Integer.parseInt(result.trim());
			jsch.disConnectChannel(channel);
		} catch (Exception e) {
			log.error(e.getMessage());
		}

		return fileLineCnt;
	}

	@Override
	public String checkAlertLog(AlertLogCommand alc) {
		String result = "";
		try {
			Session session = this.getSession();
			session = this.connectSession(session);
			Channel channel = jsch.openExecChannel(session, alc.getCommand());
			InputStream in = jsch.connectChannel(channel);
			result = IOUtils.toString(in, "UTF-8");
			jsch.disConnectChannel(channel);
		} catch (Exception e) {
			log.error(e.getMessage());
		}

		return result;
	}

	@Override
	public AlertLog checkAlertLogDuringPeriod(AlertLogCommand alc, String startDate, String endDate) {
		AlertLog alertLog = new AlertLog();

		String fullAlertLogString = getAlertLogStringFromCertainDate(alc, startDate);

		try {
			// 조회기간동안의 로그만을 취하여 StringBuffer에 저장한다.
			String[] lines = fullAlertLogString.split("\n");

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
					if (parsedDate != null && DateUtils.convertDateFormat("yyyy-MM-dd", parsedDate).equals(startDate)) {
						isStartDate = true;
						readStartIndex = i;
						break;
					}
				}

				// 로그 저장 시작 & 조회종료일자 찾기
				if (isStartDate) {
					LocalDate parsedDate = DateUtils.parse(line);
					if (parsedDate != null) { // Log TimeStamp Line
						String logDate = DateUtils.convertDateFormat("yyyy-MM-dd", parsedDate);
						if (logDate.startsWith(DateUtils.addDate(endDate, 0, 0, 1))) {
							isEndDate = true;
							readEndIndex = i;
						}

						if (i == readStartIndex) {
							logTimeStamp = line;
						} else {
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

			// 종료 후 마지막 로그 추가하기
			alertLog.addLog(new Log(logTimeStamp, logContents));
			alertLog.setFullLogString(sb.toString());

			log.info("\t▶ Alert Log READ LINE: " + (readEndIndex - readStartIndex) + "/" + alc.getReadLine());

		} catch (Exception e) {
			log.error(e.getMessage());
		}

		return alertLog;
	}

	@Override
	public List<OSDiskUsage> checkOSDiskUsage() {
		List<OSDiskUsage> list = new ArrayList<>();
		try {
			Session session = this.getSession();
			session = this.connectSession(session);
			Channel channel = jsch.openExecChannel(session, "df --block-size=K -P");
			InputStream in = jsch.connectChannel(channel);
			String result = IOUtils.toString(in, "UTF-8");
			list = stringToOsDiskUsageList(result);
			jsch.disConnectChannel(channel);
		} catch (Exception e) {
			log.error(e.getMessage());
		}

		return list;
	}

	public List<OSDiskUsage> stringToOsDiskUsageList(String result) {
		StringTokenizer st = new StringTokenizer(result);
		List<String> header = Arrays
				.asList(new String[] { "Filesystem", "1024-blocks", "Used", "Available", "Capacity", "Mounted on" });
		List<OSDiskUsage> list = new ArrayList<>();

		boolean isHeader = true;
		int index = 0;

		OSDiskUsage row = new OSDiskUsage();
		while (st.hasMoreElements()) {
			String next = st.nextToken();
			if (!isHeader) {
				String headerName = header.get(index++);

				switch (headerName) {
				case "Filesystem":
					row.setFileSystem(next);
					break;
				case "1024-blocks":
					row.setTotalSpace(NumberUnitUtils.toByteValue(Unit.KiloByte,
							Double.valueOf(next.substring(0, next.indexOf("K")))));
					break;
				case "Used":
					row.setUsedSpace(NumberUnitUtils.toByteValue(Unit.KiloByte,
							Double.valueOf(next.substring(0, next.indexOf("K")))));
					break;
				case "Available":
					row.setFreeSpace(NumberUnitUtils.toByteValue(Unit.KiloByte,
							Double.valueOf(next.substring(0, next.indexOf("K")))));
					break;
				case "Capacity":
					row.setUsedPercent(Double.valueOf(next.substring(0, next.indexOf("%"))));
					break;
				case "Mounted on":
					row.setMountedOn(next);
					break;
				}

				if (index == 6) {
					list.add(row);
					row = new OSDiskUsage();
					index = 0;
				}
			}
			if (next.equals("on"))
				isHeader = false;
		}

		return list;
	}

	private String getAlertLogStringFromCertainDate(AlertLogCommand alc, String startDate) {
		int alertLogFileLineCnt = this.getAlertLogFileLineCount(alc);
		String fullAlertLogString = this.checkAlertLog(alc);

		// 조회시작일자의 로그를 모두 포함하도록 readLine 수를 점진적으로 늘리면서 읽는다.
		while (true) {
			String[] lines = fullAlertLogString.split("\n");

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

			// 조회시작일자와 로그의 처음 기록일자를 비교한다.
			long diffTime = DateUtils.getDateDiffTime("yyyy-MM-dd", logDate, startDate);
			if (diffTime >= 0) { // 조회 Line 수를 더 늘려서 다시 조회
				alc.setReadLine(String.valueOf(Integer.parseInt(alc.getReadLine()) * 2));
				fullAlertLogString = this.checkAlertLog(alc);
			} else {
				break;
			}
		}

		return fullAlertLogString;
	}
}
