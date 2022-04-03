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
			// ��ȸ�Ⱓ������ �α׸��� ���Ͽ� StringBuffer�� �����Ѵ�.
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

				// ��ȸ�������� ã��
				if (!isStartDate) {
					LocalDate parsedDate = DateUtils.parse(line);
					if (parsedDate != null && DateUtils.convertDateFormat("yyyy-MM-dd", parsedDate).equals(startDate)) {
						isStartDate = true;
						readStartIndex = i;
						break;
					}
				}

				// �α� ���� ���� & ��ȸ�������� ã��
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

					// �α� ���� ����
					if (!isEndDate) {
						sb.append(line);
					} else {
						break;
					}
				}
			}

			// ���� �� ������ �α� �߰��ϱ�
			alertLog.addLog(new Log(logTimeStamp, logContents));
			alertLog.setFullLogString(sb.toString());

			log.info("\t�� Alert Log READ LINE: " + (readEndIndex - readStartIndex) + "/" + alc.getReadLine());

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

		// ��ȸ���������� �α׸� ��� �����ϵ��� readLine ���� ���������� �ø��鼭 �д´�.
		while (true) {
			String[] lines = fullAlertLogString.split("\n");

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

			// ��ȸ�������ڿ� �α��� ó�� ������ڸ� ���Ѵ�.
			long diffTime = DateUtils.getDateDiffTime("yyyy-MM-dd", logDate, startDate);
			if (diffTime >= 0) { // ��ȸ Line ���� �� �÷��� �ٽ� ��ȸ
				alc.setReadLine(String.valueOf(Integer.parseInt(alc.getReadLine()) * 2));
				fullAlertLogString = this.checkAlertLog(alc);
			} else {
				break;
			}
		}

		return fullAlertLogString;
	}
}
