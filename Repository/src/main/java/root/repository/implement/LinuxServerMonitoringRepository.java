package root.repository.implement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

import lombok.extern.slf4j.Slf4j;
import root.common.server.implement.AlertLogCommand;
import root.common.server.implement.JschServer;
import root.core.domain.OSDiskUsage;
import root.core.repository.constracts.ServerMonitoringRepository;
import root.utils.NumberUnitUtils;
import root.utils.NumberUnitUtils.Unit;

@Slf4j
public class LinuxServerMonitoringRepository implements ServerMonitoringRepository {
	private JschServer jsch;

	public LinuxServerMonitoringRepository(JschServer jsch) {
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
			String command = String.format("cat %s | wc -l", alc.getReadFilePath());
			String executeResult = jsch.executeCommand(command);
			StringTokenizer st = new StringTokenizer(executeResult);
			String lastToken = "0";
			while (st.hasMoreTokens()) {
				lastToken = st.nextToken();
			}

			fileLineCnt = Integer.parseInt(lastToken) + 1;
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

		return result;
	}

	@Override
	public List<OSDiskUsage> checkOSDiskUsage() {
		List<OSDiskUsage> list = new ArrayList<>();
		try {
			String command = "df --block-size=K -P";
			String result = jsch.executeCommand(command);
			list = stringToOsDiskUsageList(result);
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
}
