package root.core.repository.implement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.Session;

import root.common.server.implement.JschServer;
import root.core.domain.AlertLog;
import root.core.domain.AlertLogCommand;
import root.core.repository.constracts.ServerMonitoringRepository;

public class LinuxServerMonitoringRepositoryTest {

	@Mock
	public static JschServer jsch = mock(JschServer.class);

	public static ServerMonitoringRepository repo;

	public static String alertLogString = "";

	@BeforeAll
	public static void before() {
		repo = new LinuxServerMonitoringRepository(jsch);
		StringBuffer sb = new StringBuffer();
		sb.append("2022-03-24T13:38:35.065184+09:00").append("\n");
		sb.append("Thread 1 advanced to log sequence 7606 (LGWR switch)").append("\n");
		sb.append("  Current log# 5 seq# 7606 mem# 0: +REDO/1003346093").append("\n");
		sb.append("2022-03-24T13:39:00.180206+09:00").append("\n");
		sb.append("Archived Log entry 13398 added for T-1.S-7605 ID 0x8155080b LAD:1").append("\n");
		sb.append("2022-03-25T14:24:57.291344+09:00").append("\n");
		sb.append("Session (223,56276): RECO logon successful: Inbound connection from client").append("\n");
		sb.append("Session (223,56276): RECO logon successful: DB Logon User: RECO, ").append("\n");
		sb.append("Session (223,56276): RECO logon successful: Client IP Address: -").append("\n");
		sb.append("2022-03-26T18:04:53.572965+09:00").append("\n");
		sb.append("Thread 1 advanced to log sequence 7607 (LGWR switch)").append("\n");
		sb.append("  Current log# 1 seq# 7607 mem# 0: +REDO/DBERP/ONLINELOG/group_1.257.966360593").append("\n");
		sb.append("2022-03-27T18:05:16.929231+09:00").append("\n");
		sb.append("Archived Log entry 13400 added for T-1.S-7606 ID 0x8155080b LAD:1").append("\n");
		sb.append("2022-03-28T00:02:21.629284+09:00").append("\n");
		sb.append("TABLE SYS.WRI$_OPTSTAT_HISTHEAD_HISTORY: ADDED INTERVAL PARTITION SYS_P38333)").append("\n");
		sb.append("TABLE SYS.WRI$_OPTSTAT_HISTGRM_HISTORY: ADDED INTERVAL PARTITION SYS_P38336)").append("\n");
		sb.append("2022-03-28T01:00:18.971650+09:00").append("\n");
		sb.append("ALTER SYSTEM ARCHIVE LOG").append("\n");
		sb.append("2022-03-29T01:00:18.980968+09:00").append("\n");
		sb.append("Thread 1 advanced to log sequence 7608 (LGWR switch)").append("\n");
		sb.append("  Current log# 6 seq# 7608 mem# 0: +REDO/1003346037").append("\n");
		sb.append("2022-03-30T01:00:38.903080+09:00").append("\n");
		sb.append("Archived Log entry 13401 added for T-1.S-7607 ID 0x8155080b LAD:1").append("\n");
		sb.append("2022-03-31T02:56:51.984291+09:00").append("\n");
		sb.append("Starting control autobackup").append("\n");
		alertLogString = sb.toString();
	}

	@Test
	public void checkAlertLogTest() {
		Session session = mock(Session.class);
		Channel channel = mock(Channel.class);
		AlertLogCommand alc = mock(AlertLogCommand.class);
		InputStream in = new ByteArrayInputStream(alertLogString.getBytes());

		when(repo.getSession()).thenReturn(session);
		when(jsch.openExecChannel(session, alc.getCommand())).thenReturn(channel);
		when(jsch.connectChannel(channel)).thenReturn(in);
		doNothing().when(jsch).disConnectChannel(channel);
		doNothing().when(channel).disconnect();

		String result = repo.checkAlertLog(alc);
		assertEquals(result, alertLogString);
	}

	@Test
	public void getAlertLogFileLineCountTest() {
		Session session = mock(Session.class);
		Channel channel = mock(Channel.class);
		AlertLogCommand alc = mock(AlertLogCommand.class);
		InputStream in = new ByteArrayInputStream("26".getBytes());

		when(repo.getSession()).thenReturn(session);
		when(jsch.openExecChannel(session, "cat " + alc.getReadFilePath() + " | wc -l")).thenReturn(channel);
		when(jsch.connectChannel(channel)).thenReturn(in);
		doNothing().when(jsch).disConnectChannel(channel);
		doNothing().when(channel).disconnect();

		try {
			String result = IOUtils.toString(in, "UTF-8");
			int fileLineCnt = Integer.parseInt(result.trim());
			assertEquals(fileLineCnt, 26);
		} catch (Exception e) {
			fail();
		}
	}

	@Test
	public void checkAlertLogDuringPeriod() {
		Session session = mock(Session.class);
		Channel channel = mock(Channel.class);
		Channel channel2 = mock(Channel.class);
		AlertLogCommand alc = mock(AlertLogCommand.class);
		InputStream in = new ByteArrayInputStream(alertLogString.getBytes());
		InputStream in2 = new ByteArrayInputStream("26".getBytes());

		when(repo.getSession()).thenReturn(session);
		when(jsch.openExecChannel(session, "cat " + alc.getReadFilePath() + " | wc -l")).thenReturn(channel2);
		when(jsch.connectChannel(channel2)).thenReturn(in2);
		doNothing().when(jsch).disConnectChannel(channel2);
		doNothing().when(channel2).disconnect();

		when(jsch.openExecChannel(session, alc.getCommand())).thenReturn(channel);
		when(jsch.connectChannel(channel)).thenReturn(in);
		doNothing().when(jsch).disConnectChannel(channel);
		doNothing().when(channel).disconnect();

		AlertLog alertLog = repo.checkAlertLogDuringPeriod(alc, "2022-03-24", "2022-03-29");

		assertEquals(alertLog.getTotalLineCount(), 14);
		assertEquals(alertLog.getAlertLogs().size(), 8);
	}
}
