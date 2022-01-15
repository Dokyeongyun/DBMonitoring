package root.core.repository.implement;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import root.common.server.implement.JschServer;
import root.core.domain.AlertLog;
import root.core.domain.AlertLogCommand;
import root.core.domain.AlertLogCommandPeriod;
import root.core.domain.Log;
import root.core.domain.OSDiskUsage;
import root.core.repository.constracts.ServerCheckRepository;
import root.utils.DateUtils;
import root.utils.UnitUtils;

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
			e.printStackTrace();
		}
		return session;
	}
	
	@Override
	public void disConnectSession(Session session) {
		if(session.isConnected() == true && session != null) {
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
			e.printStackTrace();
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
			e.printStackTrace();
		} 
		
		return result;
	}
	
	@Override
	public AlertLog checkAlertLogDuringPeriod(AlertLogCommandPeriod alcp) {
		AlertLog alertLog = new AlertLog();
		int alertLogFileLineCnt = this.getAlertLogFileLineCount(alcp);
		String fullAlertLogString = this.checkAlertLog(alcp);
		String dateFormat = alcp.getDateFormat();
		String dateFormatRegex = alcp.getDateFormatRegex();
		int dateLength = dateFormat.equals("yyyy-MM-dd") ? 11 : 24;
		
		try {
			String startDate = alcp.getFromDate();
			String endDate = alcp.getToDate();
			
			// ��ȸ���������� �α׸� ��� �����ϵ��� readLine ���� ���������� �ø��鼭 �д´�.
			while(true) {
				String[] lines = fullAlertLogString.split("\n");

				// ���� Read Line ���� ���� �ִ� Line ���� �ʰ����� ��, ���� ��ü�� �а� ��ȯ�Ѵ�.
				if(lines.length >= alertLogFileLineCnt) {
					break;
				}
				
				// ��ȸ�� �α� ������ ���� ó������ ��Ÿ���� �α��� ������ڸ� ����.
				String logDate = "";
				for(String line : lines) {
					String linePrefix = line.substring(0, line.length() < dateLength ? line.length() : dateLength);
					if(Pattern.matches("^"+dateFormatRegex, linePrefix)) {
						logDate = DateUtils.convertDateFormat(dateFormat, "yyyy-MM-dd", linePrefix, Locale.ENGLISH);
						break;
					}
				}
				
				// ��ȸ�������ڿ� �α��� ó�� ������ڸ� ���Ѵ�.
				long diffTime = DateUtils.getDateDiffTime("yyyy-MM-dd", logDate, startDate);
		        if(diffTime >= 0) { // ��ȸ Line ���� �� �÷��� �ٽ� ��ȸ
		        	alcp.setReadLine(String.valueOf(Integer.parseInt(alcp.getReadLine()) * 2));
		        	fullAlertLogString = this.checkAlertLog(alcp);
		        } else {
		        	break;
		        }
			}
			
			// ��ȸ�Ⱓ������ �α׸��� ���Ͽ� StringBuffer�� �����Ѵ�.
			String[] lines = fullAlertLogString.split("\n");
			
			boolean isStartDate = false;
			boolean isEndDate = false;
			int readStartIndex = 0;
			int readEndIndex = lines.length;
			int realNumberOfReadLine = 0;
			String logTimeStamp = "";
			List<String> logContents = new ArrayList<>();
			StringBuffer sb = new StringBuffer();
			
			for(int i=0; i<lines.length; i++) {
				String line = lines[i];
				
				// ��ȸ�������� ã��
				if(isStartDate == false) {
					String linePrefix = line.substring(0, line.length() < dateLength ? line.length() : dateLength);
					if(Pattern.matches("^"+dateFormatRegex, linePrefix)) {
						linePrefix = DateUtils.convertDateFormat(dateFormat, "yyyy-MM-dd", linePrefix, Locale.ENGLISH);
						if(linePrefix.startsWith(startDate)) {
							isStartDate = true;
							readStartIndex = i;
						}
					}
				} 
				
				// �α� ���� ���� & ��ȸ�������� ã��
				if(isStartDate == true) {
					String linePrefix = line.substring(0, line.length() < dateLength ? line.length() : dateLength);
					if(Pattern.matches("^"+dateFormatRegex, linePrefix)) { // LogTimeStamp Line
						// ��ȸ������������ Ȯ��
						linePrefix = DateUtils.convertDateFormat(dateFormat, "yyyy-MM-dd", linePrefix, Locale.ENGLISH);
						if(linePrefix.startsWith(DateUtils.addDate(endDate, 0, 0, 1))) {
							isEndDate = true;
							readEndIndex = i;
						}
						
						if(i == readStartIndex) {
							logTimeStamp = line;
						}
						if(i != readStartIndex) {
							alertLog.addLog(new Log(logTimeStamp, logContents));
							logContents = new ArrayList<>();
							logTimeStamp = line;
						}
					} else { // Log Content Line
						logContents.add(line);
					}
					
					// �α� ���� ����
					if(isEndDate == false) {
						sb.append(line);	
					} else {
						break;
					}
				}
			}
			// ���� �� ������ �α� �߰��ϱ�
			alertLog.addLog(new Log(logTimeStamp, logContents));
			alertLog.setFullLogString(sb.toString());

			realNumberOfReadLine = readEndIndex - readStartIndex;
			System.out.println("\t�� Alert Log READ LINE: " + realNumberOfReadLine + "/" + alcp.getReadLine());

		} catch (Exception e) {
			e.printStackTrace();
		} 
		
		return alertLog;
	}
	
	@Override
	public List<OSDiskUsage> checkOSDiskUsage(String command) {
		List<OSDiskUsage> list = new ArrayList<>();
		try {
			Session session = this.getSession();
			session = this.connectSession(session);
			Channel channel = jsch.openExecChannel(session, command);
			InputStream in = jsch.connectChannel(channel);
			String result = IOUtils.toString(in, "UTF-8");
			list = stringToOsDiskUsageList(result);
			jsch.disConnectChannel(channel);
		}		
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return list;
	}
	
	public List<OSDiskUsage> stringToOsDiskUsageList (String result) {
		StringTokenizer st = new StringTokenizer(result);
		List<String> header = Arrays.asList(new String[] {"Filesystem", "Size", "Used", "Avail", "Use%", "Mounted on"});
		List<OSDiskUsage> list = new ArrayList<>();
		
		boolean isHeader = true;
		int index = 0;

		OSDiskUsage row = new OSDiskUsage();
		while(st.hasMoreElements()) {
			String next = st.nextToken();
			if(!isHeader) {
				String headerName = header.get(index++);
				
				switch(headerName) {
				case "Filesystem":
					row.setFileSystem(next);
					break;
				case "Size":
					row.setTotalSpace(UnitUtils.parseFileSizeString(next));
					break;
				case "Used":
					row.setUsedSpace(UnitUtils.parseFileSizeString(next));
					break;
				case "Avail":
					row.setFreeSpace(UnitUtils.parseFileSizeString(next));
					break;
				case "Use%":
					row.setUsedPercent(UnitUtils.parseFileSizeString(next));
					break;
				case "Mounted on":
					row.setMountedOn(next);
					break;
				}
				
				if(index == 6) {
					list.add(row);
					row = new OSDiskUsage();
					index = 0;
				}
			}
			if(next.equals("on")) isHeader = false;
		}
		
		return list;
	}
}
