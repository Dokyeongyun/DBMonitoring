package Root.Repository;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import Root.Model.AlertLogCommand;
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
	public String checkAlertLogDuringPeriod(AlertLogCommand alc) {
		System.out.println("ALC READ LINE: "+alc.getReadLine());
		String result = "";
		try {
			Session session = this.getSession();
			session = this.connectSession(session);
			Channel channel = jsch.openExecChannel(session, alc.getCommand());
			InputStream in = jsch.connectChannel(channel);
			result = IOUtils.toString(in, "UTF-8");
			
			String startDate = DateUtils.addDate(new Date(), "yyyy-MM-dd", 0, 0, -1);
			String endDate = DateUtils.getToday("yyyy-MM-dd");
			
			String dateRegEx = "\\d{4}-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[01])T"; 
			
			while(true) {
				Matcher dates = Pattern.compile(dateRegEx).matcher(result);	// 'yyyy-MM-ddT' 포맷의 날짜를 찾아냄
				if(dates.find()) {
					String logDate = dates.group();
					long dateDiff = DateUtils.getDateDiffTime("yyyy-MM-dd", logDate, startDate);
					if(dateDiff >= 0) {
						// 조회 Line 수를 더 늘려서 다시 조회
						alc.setReadLine(String.valueOf(Integer.parseInt(alc.getReadLine()) * 2));
						result = this.checkAlertLog(alc);
					} else {
						break;
					}
				}
			}
			
			boolean isStartDay = false;
			StringBuffer sb = new StringBuffer();
			
			dateRegEx = "^\\d{4}-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[01])T"; 
			
			String[] lines = result.split("\n");
			for(String line : lines) {
				String linePrefix = line.substring(0, line.length() < 11 ? line.length() : 11);
				if(Pattern.matches(dateRegEx, linePrefix)) {
					if(linePrefix.startsWith(startDate)) {
						isStartDay = true;
					}
				}
				
				if(isStartDay) {
					sb.append(line);
				}
			}
			
			result = sb.toString();
			
			jsch.disConnectChannel(channel);
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
		return result;
	}
	
	@Override
	public String checkOSDiskUsage(String command) {
		String result = "";
		try {
			Session session = this.getSession();
			session = this.connectSession(session);
			Channel channel = jsch.openExecChannel(session, command);
			InputStream in = jsch.connectChannel(channel);
			result = IOUtils.toString(in, "UTF-8");
			jsch.disConnectChannel(channel);
		}		
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
}
