package Root.Repository;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import Root.Model.AlertLogCommand;
import Root.Model.AlertLogCommandPeriod;
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
	public String checkAlertLogDuringPeriod(AlertLogCommandPeriod alcp) {
		String alertLog = this.checkAlertLog(alcp);
		String dateFormat = alcp.getDateFormat();
		String dateFormatRegex = alcp.getDateFormatRegex();
		int dateLength = dateFormat.equals("yyyy-MM-dd") ? 11 : 24;
		
		try {
			String startDate = alcp.getFromDate();
			String endDate = alcp.getToDate();
			
			// ��ȸ���������� �α׸� ��� �����ϵ��� readLine ���� ���������� �ø��鼭 �д´�.
			while(true) {
				String[] lines = alertLog.split("\n");

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
					alertLog = this.checkAlertLog(alcp);
		        } else {
		        	break;
		        }
			}
			
			// ��ȸ�Ⱓ������ �α׸��� ���Ͽ� StringBuffer�� �����Ѵ�.
			String[] lines = alertLog.split("\n");
			
			boolean isStartDate = false;
			boolean isEndDate = false;
			int readStartIndex = 0;
			int readEndIndex = lines.length;
			int realNumberOfReadLine = 0;
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
					if(Pattern.matches("^"+dateFormatRegex, linePrefix)) {
						linePrefix = DateUtils.convertDateFormat(dateFormat, "yyyy-MM-dd", linePrefix, Locale.ENGLISH);
						if(linePrefix.startsWith(DateUtils.addDate(endDate, 0, 0, 1))) {
							isEndDate = true;
							readEndIndex = i;
						}
					}
					
					// �α� ���� ����
					if(isEndDate == false) {
						sb.append(line);	
					} else {
						break;
					}
				}
			}
			
			alertLog = sb.toString();
			realNumberOfReadLine = readEndIndex - readStartIndex;
			System.out.println("\t�� Alert Log READ LINE: " + realNumberOfReadLine + "/" + alcp.getReadLine());

		} catch (Exception e) {
			e.printStackTrace();
		} 
		
		return alertLog;
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
