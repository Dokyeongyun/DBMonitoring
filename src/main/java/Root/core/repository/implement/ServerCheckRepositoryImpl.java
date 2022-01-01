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
			
			// 조회시작일자의 로그를 모두 포함하도록 readLine 수를 점진적으로 늘리면서 읽는다.
			while(true) {
				String[] lines = fullAlertLogString.split("\n");

				// 현재 Read Line 수가 파일 최대 Line 수를 초과했을 시, 파일 전체를 읽고 반환한다.
				if(lines.length >= alertLogFileLineCnt) {
					break;
				}
				
				// 조회한 로그 내에서 가장 처음으로 나타나는 로그의 기록일자를 얻어낸다.
				String logDate = "";
				for(String line : lines) {
					String linePrefix = line.substring(0, line.length() < dateLength ? line.length() : dateLength);
					if(Pattern.matches("^"+dateFormatRegex, linePrefix)) {
						logDate = DateUtils.convertDateFormat(dateFormat, "yyyy-MM-dd", linePrefix, Locale.ENGLISH);
						break;
					}
				}
				
				// 조회시작일자와 로그의 처음 기록일자를 비교한다.
				long diffTime = DateUtils.getDateDiffTime("yyyy-MM-dd", logDate, startDate);
		        if(diffTime >= 0) { // 조회 Line 수를 더 늘려서 다시 조회
		        	alcp.setReadLine(String.valueOf(Integer.parseInt(alcp.getReadLine()) * 2));
		        	fullAlertLogString = this.checkAlertLog(alcp);
		        } else {
		        	break;
		        }
			}
			
			// 조회기간동안의 로그만을 취하여 StringBuffer에 저장한다.
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
				
				// 조회시작일자 찾기
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
				
				// 로그 저장 시작 & 조회종료일자 찾기
				if(isStartDate == true) {
					String linePrefix = line.substring(0, line.length() < dateLength ? line.length() : dateLength);
					if(Pattern.matches("^"+dateFormatRegex, linePrefix)) { // LogTimeStamp Line
						// 조회종료일자인지 확인
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
					
					// 로그 저장 중지
					if(isEndDate == false) {
						sb.append(line);	
					} else {
						break;
					}
				}
			}
			// 종료 후 마지막 로그 추가하기
			alertLog.addLog(new Log(logTimeStamp, logContents));
			alertLog.setFullLogString(sb.toString());

			realNumberOfReadLine = readEndIndex - readStartIndex;
			System.out.println("\t▶ Alert Log READ LINE: " + realNumberOfReadLine + "/" + alcp.getReadLine());

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
