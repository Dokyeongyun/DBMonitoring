package Root.Usecases;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import Root.Model.AlertLogCommand;
import Root.Model.AlertLogCommandPeriod;
import Root.Repository.ServerCheckRepository;
import Root.Utils.ConsoleUtils;
import Root.Utils.DBManageExcel;
import Root.Utils.DateUtils;
import Root.Utils.ExcelUtils;
import dnl.utils.text.table.MapBasedTableModel;
import dnl.utils.text.table.TextTable;

@SuppressWarnings("rawtypes")
public class ServerCheckUsecaseImpl implements ServerCheckUsecase {
	private ServerCheckRepository serverCheckRepository;

	public ServerCheckUsecaseImpl(ServerCheckRepository serverCheckRepository) {
		this.serverCheckRepository = serverCheckRepository;
	}

	@Override
	public void printAlertLog(AlertLogCommand alc) {
		String result = serverCheckRepository.checkAlertLog(alc);
		if(result.indexOf("ORA") >= 0) {
			System.out.println("\t"+ConsoleUtils.BACKGROUND_RED + ConsoleUtils.FONT_WHITE + "▶ Alert Log : ORA ERROR!! Alert Log 확인 필요"+ConsoleUtils.RESET+"\n");
		} else {
			System.out.println("\t▶ Alert Log : SUCCESS!\n");
		}
	}
	
	@Override
	public void printAlertLogDuringPeriod(AlertLogCommandPeriod alcp) {
		String result = serverCheckRepository.checkAlertLogDuringPeriod(alcp);
		if(result.indexOf("ORA") >= 0) {
			System.out.println("\t"+ConsoleUtils.BACKGROUND_RED + ConsoleUtils.FONT_WHITE + "▶ Alert Log : ORA ERROR!! Alert Log 확인 필요"+ConsoleUtils.RESET+"\n");
		} else {
			System.out.println("\t▶ Alert Log : SUCCESS!\n");
		}
	}
	
	@Override
	public void printOSDiskUsage(String command) {
		String result = serverCheckRepository.checkOSDiskUsage(command);
		System.out.println("\t▶ OS Disk Usage");
		List<Map> rows = osDiskCheckResultToMap(result);
		TextTable tt = new TextTable(new MapBasedTableModel(rows));
		tt.printTable(System.out, 8);
		System.out.println();
	}
	
	@Override
	public void writeExcelOSDiskUsage(String command) throws Exception {
		if(!"STS".equals(serverCheckRepository.getServerName())) return;
		
		String result = serverCheckRepository.checkOSDiskUsage(command);
		List<Map> rows = osDiskCheckResultToMap(result);
		
		int year = Integer.parseInt(DateUtils.getToday("yyyy"));
		int month = Integer.parseInt(DateUtils.getToday("MM"));
		int day = Integer.parseInt(DateUtils.getToday("dd"));
		int colIndex = day + 2;

		String filePath = "C:\\Users\\aserv\\Documents\\WorkSpace_DBMonitoring_Quartz\\DBMonitoring\\report\\";
		String fileName = "DB관리대장_종합_" + year + "." + month;
		String extension = ".xlsx";
		String file = filePath + fileName + extension;
		
		InputStream is = null;
		try {
			is = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			DBManageExcel.createMonthlyReportInExcel(year, month);
			is = new FileInputStream(file);
		}
		
		Workbook workbook = ExcelUtils.getWorkbook(is, fileName+extension);
		Sheet sheet = workbook.getSheetAt(0);

		for(Map data : rows) {
			String mountedOn = (String) data.get("Mounted on");
			String usePercent = (String) data.get("Use%");
			if(!mountedOn.startsWith("/oradata")) continue;
			int rowIndex = Integer.parseInt(mountedOn.substring(mountedOn.length()-1)) + 38;
			sheet.getRow(rowIndex).getCell(colIndex).setCellValue(usePercent);
		}

		OutputStream os = new FileOutputStream(file);
		workbook.write(os);
		workbook.close();
	}
	
	public List<Map> osDiskCheckResultToMap(String result) {
		StringTokenizer st = new StringTokenizer(result);
		List<String> header = Arrays.asList(new String[] {"Filesystem", "Size", "Used", "Avail", "Use%", "Mounted on"});
		List<Map> rows = new ArrayList<>();
		
		boolean isHeader = true;
		int index = 0;

		Map<String, String> row = new HashMap<>();
		while(st.hasMoreElements()) {
			String next = st.nextToken();
			if(!isHeader) {
				row.put(header.get(index++), next);
				if(index == 6) {
					rows.add(row);
					row = new HashMap<>();
					index = 0;
				}
			}
			if(next.equals("on")) isHeader = false;
		}
		
		return rows;
	}
}
