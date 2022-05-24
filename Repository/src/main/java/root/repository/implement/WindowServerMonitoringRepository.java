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
			String command = String.format("tail -%d %s", alc.getReadLine() - 1, alc.getReadFilePath());
			result = jsch.executeCommand(command);
//			result = result.replace("\r\n", "\n");
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		log.debug(alc.toString());
		return result;
	}

	@Override
	public List<OSDiskUsage> checkOSDiskUsage() {
		List<OSDiskUsage> list = new ArrayList<>();
		try {
			String command = "wmic logicaldisk get deviceid,filesystem,size,freespace";
			String result = jsch.executeCommand(command);
			list = stringToOsDiskUsageList(result);
		} catch (Exception e) {
			log.error(e.getMessage());
		}

		return list;
	}

	private List<OSDiskUsage> stringToOsDiskUsageList(String result) {
		StringTokenizer st = new StringTokenizer(result);
		List<String> header = Arrays.asList(new String[] { "DeviceID", "FileSystem", "FreeSpace", "Size" });
		List<OSDiskUsage> list = new ArrayList<>();

		boolean isHeader = true;
		int index = 0;

		OSDiskUsage row = new OSDiskUsage();
		while (st.hasMoreElements()) {
			String next = st.nextToken();
			if (!isHeader) {
				String headerName = header.get(index++);

				switch (headerName) {
				case "DeviceID":
					row.setMountedOn(next);
					break;
				case "FileSystem":
					row.setFileSystem(next);
					break;
				case "FreeSpace":
					row.setFreeSpace(Double.valueOf(next));
					break;
				case "Size":
					row.setTotalSpace(Double.valueOf(next));
					break;
				}

				if (index == 4) {
					double total = row.getTotalSpace();
					double free = row.getFreeSpace();
					double used = total - free;
					row.setUsedSpace(used);
					row.setUsedPercent(used / total * 100);

					list.add(row);
					row = new OSDiskUsage();
					index = 0;
				}
			}

			if (next.equals("Size")) {
				isHeader = false;
			}
		}

		return list;
	}
}
