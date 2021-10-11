package Root.Repository;

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

import Root.Model.AlertLog;
import Root.Model.AlertLogCommand;
import Root.Model.AlertLogCommandPeriod;
import Root.Model.Log;
import Root.Model.OSDiskUsage;
import Root.RemoteServer.JschUtil;
import Root.Utils.DateUtils;

public class ServerCheckRepositoryImpl implements ServerCheckRepository {
	private JschUtil jsch;
	
	public ServerCheckRepositoryImpl(JschUtil jsch) {
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
					double totalSpace = Double.parseDouble(next.substring(0, next.length()-1));
					if(next.substring(next.length()-1).equals("G")) {
						totalSpace = Double.parseDouble(next.substring(0, next.length()-1)) * 1000;
					} 
					row.setTotalSpace(totalSpace);
					row.setTotalSpaceString(next);
					break;
				case "Used":
					double usedSpace = Double.parseDouble(next.substring(0, next.length()-1));
					if(next.substring(next.length()-1).equals("G")) {
						usedSpace = Double.parseDouble(next.substring(0, next.length()-1)) * 1000;
					}
					row.setUsedSpace(usedSpace);
					row.setUsedSpaceString(next);
					break;
				case "Avail":
					double availableSpace = Double.parseDouble(next.substring(0, next.length()-1));
					if(next.substring(next.length()-1).equals("G")) {
						availableSpace = Double.parseDouble(next.substring(0, next.length()-1)) * 1000;
					}
					row.setAvailableSpace(availableSpace);
					row.setAvailableSpaceString(next);
					break;
				case "Use%":
					double usedPercent = Double.parseDouble(next.substring(0, next.length()-1));
					row.setUsedPercent(usedPercent);
					row.setUsedPercentString(next.trim());
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
