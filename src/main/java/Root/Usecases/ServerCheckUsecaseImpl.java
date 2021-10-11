package Root.Usecases;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import Root.Model.AlertLog;
import Root.Model.AlertLogCommand;
import Root.Model.AlertLogCommandPeriod;
import Root.Model.Log;
import Root.Model.OSDiskUsage;
import Root.Repository.ServerCheckRepository;
import Root.Utils.ConsoleUtils;
import Root.Utils.DBManageExcel;
import Root.Utils.DateUtils;
import Root.Utils.ExcelUtils;
import dnl.utils.text.table.TextTable;
import dnl.utils.text.table.csv.CsvTableModel;

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
			System.out.println("\t"+ConsoleUtils.BACKGROUND_RED + ConsoleUtils.FONT_WHITE + "�� Alert Log : ORA ERROR!! Alert Log Ȯ�� �ʿ�"+ConsoleUtils.RESET+"\n");
		} else {
			System.out.println("\t�� Alert Log : SUCCESS!\n");
		}
	}
	
	@Override
	public void printAlertLogDuringPeriod(AlertLogCommandPeriod alcp) {
		AlertLog result = serverCheckRepository.checkAlertLogDuringPeriod(alcp);
		List<Log> logContents = result.getAlertLogs();
		
		boolean isError = false;
		List<Log> errorLogContents = new ArrayList<>();
		for(Log log : logContents) {
			String logContent = log.getFullLogString();
			if(logContent.indexOf("ORA") >= 0) {
				isError = true;
				errorLogContents.add(log);				
			}
		}
		
		if(isError == true) {
			System.out.println("\t"+ConsoleUtils.BACKGROUND_RED + ConsoleUtils.FONT_WHITE + "�� Alert Log : ORA ERROR!! Alert Log Ȯ�� �ʿ�"+ConsoleUtils.RESET+"\n");
			System.out.println("\t��" + errorLogContents.size() + "���� ERROR�� �߻��߽��ϴ�. Alert Log�� Ȯ���Ͻðڽ��ϱ�? (Y/N)��");
			
			boolean isCheck = false;
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			while(true) {
				try {
					String input = br.readLine().trim().toUpperCase();
					
					if("Y".equals(input)) {
						isCheck = true;
						break;
					} else if ("N".equals(input)) {
						isCheck = false;
						break;
					} else {
						System.out.println("\t" + ConsoleUtils.FONT_RED + "�߸� �Է��ϼ̽��ϴ�. Y �Ǵ� N�� �Է����ּ���." + ConsoleUtils.RESET);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			if(isCheck == true) {
				int errorLogIndex = 0;
				System.out.println("\t��ERROR [" + (errorLogIndex + 1) + "/" + errorLogContents.size() + "] (Enter: ��������Ȯ��, ����: ��������Ȯ��, q: ����)��\n");
				System.out.print(errorLogContents.get(errorLogIndex).errorLogToString());
				
				boolean isExit = false;
				while(true) {
					try {
						String input = br.readLine().trim().toUpperCase();
						
						if("".equals(input) || "ENTER".equals(input)) {
							errorLogIndex++;
							if(errorLogIndex >= errorLogContents.size()) {
								System.out.println("\t�ظ����� ERROR�Դϴ�. �����Ͻðڽ��ϱ�? (Y/N)��");
								
								while(true) {
									String exitInput = br.readLine().trim().toUpperCase();
									
									if("Y".equals(exitInput)) {
										isExit = true;
										break;
									} else if("N".equals(exitInput)) {
										isExit = false;
										break;
									}
								}
								
								if(isExit == true) {
									System.out.println("\t�������");
									break;
								} else {
									System.out.println("\t�ظ�ɾ �Է����ּ���. (Enter: ��������Ȯ��, ����: ��������Ȯ��, q: ����)");
								}
							} else {
								System.out.println("\t��ERROR [" + (errorLogIndex + 1) + "/" + errorLogContents.size() + "] (Enter: ��������Ȯ��, ����: ��������Ȯ��, q: ����)��\n");
								System.out.print(errorLogContents.get(errorLogIndex).errorLogToString());	
							}
						} else if ("Q".equals(input)) {
							System.out.println("\t�������");
							break;
						} else {
							boolean isWrongInput = false;
							try {
								int inputIndex = Integer.parseInt(input) -1;
								if(inputIndex >= errorLogContents.size() || inputIndex < 0) {
									isWrongInput = true;
								} else {
									errorLogIndex = inputIndex;	
									System.out.println("\t��ERROR [" + (errorLogIndex + 1) + "/" + errorLogContents.size() + "] (Enter: ��������Ȯ��, ����: ��������Ȯ��, q: ����)��\n");
									System.out.print(errorLogContents.get(errorLogIndex).errorLogToString());	
								}
							} catch (NumberFormatException e) {
								isWrongInput = true;
							}
							
							if(isWrongInput == true) {
								System.out.println("\t" + ConsoleUtils.FONT_RED + "�߸� �Է��ϼ̽��ϴ�. (Enter: ��������Ȯ��, ����: ��������Ȯ��, q: ����)" + ConsoleUtils.RESET);
							}
						}
					} catch (IOException e) {
						e.printStackTrace();
					}	
				}
			} else {
				System.out.println("\t�������");
			}
		} else {
			System.out.println("\t�� Alert Log : SUCCESS!\n");
		}
	}
	
	@Override
	public void printOSDiskUsage(String command) {
		List<OSDiskUsage> result = serverCheckRepository.checkOSDiskUsage(command);
		System.out.println("\t�� OS Disk Usage");
		try {
			TextTable tt = new TextTable(new CsvTableModel(OSDiskUsage.toCsvString(result)));
			tt.printTable(System.out, 8);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println();
	}
	
	@Override
	public void writeExcelOSDiskUsage(String command) throws Exception {
		if(!"STS".equals(serverCheckRepository.getServerName())) return;
		
		List<OSDiskUsage> result = serverCheckRepository.checkOSDiskUsage(command);
		
		int year = Integer.parseInt(DateUtils.getToday("yyyy"));
		int month = Integer.parseInt(DateUtils.getToday("MM"));
		int day = Integer.parseInt(DateUtils.getToday("dd"));
		int colIndex = day + 2;

		String filePath = "C:\\Users\\aserv\\Documents\\WorkSpace_DBMonitoring_Quartz\\DBMonitoring\\report\\";
		String fileName = "DB��������_����_" + year + "." + month;
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

		for(OSDiskUsage data : result) {
			String mountedOn = data.getMountedOn();
			double usePercent = data.getUsedPercent();
			if(!mountedOn.startsWith("/oradata")) continue;
			int rowIndex = Integer.parseInt(mountedOn.substring(mountedOn.length()-1)) + 38;
			sheet.getRow(rowIndex).getCell(colIndex).setCellValue(usePercent+"%");
		}

		OutputStream os = new FileOutputStream(file);
		workbook.write(os);
		workbook.close();
	}
}
