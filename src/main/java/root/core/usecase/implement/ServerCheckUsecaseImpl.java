package root.core.usecase.implement;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import dnl.utils.text.table.TextTable;
import dnl.utils.text.table.csv.CsvTableModel;
import root.core.domain.AlertLog;
import root.core.domain.AlertLogCommand;
import root.core.domain.AlertLogCommandPeriod;
import root.core.domain.Log;
import root.core.domain.OSDiskUsage;
import root.core.repository.constracts.ReportRepository;
import root.core.repository.constracts.ServerCheckRepository;
import root.core.usecase.constracts.ServerCheckUsecase;
import root.utils.ConsoleUtils;
import root.utils.CsvUtils;
import root.utils.DBManageExcel;
import root.utils.DateUtils;
import root.utils.ExcelSheet;

public class ServerCheckUsecaseImpl implements ServerCheckUsecase {
	private ServerCheckRepository serverCheckRepository;
	private ReportRepository reportRepository;

	public ServerCheckUsecaseImpl(ServerCheckRepository serverCheckRepository, ReportRepository reportRepository) {
		this.serverCheckRepository = serverCheckRepository;
		this.reportRepository = reportRepository;
	}

	@Override
	public void printAlertLog(AlertLogCommand alc) {
		String result = serverCheckRepository.checkAlertLog(alc);
		if (result.indexOf("ORA-") >= 0) {
			System.out.println("\t" + ConsoleUtils.BACKGROUND_RED + ConsoleUtils.FONT_WHITE
					+ "▶ Alert Log : ORA ERROR!! Alert Log 확인 필요" + ConsoleUtils.RESET + "\n");
		} else {
			System.out.println("\t▶ Alert Log : SUCCESS!\n");
		}
	}

	@Override
	public void printAlertLogDuringPeriod(AlertLogCommandPeriod alcp) {
		AlertLog result = serverCheckRepository.checkAlertLogDuringPeriod(alcp);
		List<Log> logContents = result.getAlertLogs();

		boolean isError = false;
		List<Log> errorLogContents = new ArrayList<>();
		for (Log log : logContents) {
			String logContent = log.getFullLogString();
			if (logContent.indexOf("ORA-") >= 0) {
				isError = true;
				errorLogContents.add(log);
			}
		}

		if (isError == true) {
			System.out.println("\t" + ConsoleUtils.BACKGROUND_RED + ConsoleUtils.FONT_WHITE
					+ "▶ Alert Log : ORA ERROR!! Alert Log 확인 필요" + ConsoleUtils.RESET + "\n");
			System.out.println("\t※" + errorLogContents.size() + "개의 ERROR가 발생했습니다. Alert Log를 확인하시겠습니까? (Y/N)※");

			boolean isCheck = false;
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			while (true) {
				try {
					String input = br.readLine().trim().toUpperCase();

					if ("Y".equals(input)) {
						isCheck = true;
						break;
					} else if ("N".equals(input)) {
						isCheck = false;
						break;
					} else {
						System.out.println(
								"\t" + ConsoleUtils.FONT_RED + "잘못 입력하셨습니다. Y 또는 N을 입력해주세요." + ConsoleUtils.RESET);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			if (isCheck == true) {
				int errorLogIndex = 0;
				System.out.println("\t※ERROR [" + (errorLogIndex + 1) + "/" + errorLogContents.size()
						+ "] (Enter: 다음에러확인, 숫자: 지정에러확인, q: 종료)※\n");
				System.out.print(errorLogContents.get(errorLogIndex).errorLogToString());

				boolean isExit = false;
				while (true) {
					try {
						String input = br.readLine().trim().toUpperCase();

						if ("".equals(input) || "ENTER".equals(input)) {
							errorLogIndex++;
							if (errorLogIndex >= errorLogContents.size()) {
								System.out.println("\t※마지막 ERROR입니다. 종료하시겠습니까? (Y/N)※");

								while (true) {
									String exitInput = br.readLine().trim().toUpperCase();

									if ("Y".equals(exitInput)) {
										isExit = true;
										break;
									} else if ("N".equals(exitInput)) {
										isExit = false;
										break;
									}
								}

								if (isExit == true) {
									System.out.println("\t※종료※");
									break;
								} else {
									System.out.println("\t※명령어를 입력해주세요. (Enter: 다음에러확인, 숫자: 지정에러확인, q: 종료)");
								}
							} else {
								System.out.println("\t※ERROR [" + (errorLogIndex + 1) + "/" + errorLogContents.size()
										+ "] (Enter: 다음에러확인, 숫자: 지정에러확인, q: 종료)※\n");
								System.out.print(errorLogContents.get(errorLogIndex).errorLogToString());
							}
						} else if ("Q".equals(input)) {
							System.out.println("\t※종료※");
							break;
						} else {
							boolean isWrongInput = false;
							try {
								int inputIndex = Integer.parseInt(input) - 1;
								if (inputIndex >= errorLogContents.size() || inputIndex < 0) {
									isWrongInput = true;
								} else {
									errorLogIndex = inputIndex;
									System.out.println("\t※ERROR [" + (errorLogIndex + 1) + "/"
											+ errorLogContents.size() + "] (Enter: 다음에러확인, 숫자: 지정에러확인, q: 종료)※\n");
									System.out.print(errorLogContents.get(errorLogIndex).errorLogToString());
								}
							} catch (NumberFormatException e) {
								isWrongInput = true;
							}

							if (isWrongInput == true) {
								System.out.println("\t" + ConsoleUtils.FONT_RED
										+ "잘못 입력하셨습니다. (Enter: 다음에러확인, 숫자: 지정에러확인, q: 종료)" + ConsoleUtils.RESET);
							}
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			} else {
				System.out.println("\t※종료※");
			}
		} else {
			System.out.println("\t▶ Alert Log : SUCCESS!\n");
		}
	}

	@Override
	public void printOSDiskUsage() {
		List<OSDiskUsage> result = serverCheckRepository.checkOSDiskUsage();

		boolean isError = false;
		for (OSDiskUsage data : result) {
			if (data.getUsedPercent() >= 80) {
				isError = true;
				// data.setUsedPercentString(ConsoleUtils.FONT_RED + data.getUsedPercentString()
				// + ConsoleUtils.RESET);
			}
		}

		if (isError == true) {
			System.out.println("\t" + ConsoleUtils.BACKGROUND_RED + ConsoleUtils.FONT_WHITE
					+ "▶ OS Disk Usage : 80% 초과!! 확인 필요" + ConsoleUtils.RESET);
		} else {
			System.out.println("\t▶ OS Disk Usage : SUCCESS!");
		}
		try {
			System.out.println(result);
			TextTable tt = new TextTable(new CsvTableModel(CsvUtils.toCsvString(result, OSDiskUsage.class)));
			tt.printTable(System.out, 8);
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println();
	}

	@Override
	public void writeExcelOSDiskUsage() throws Exception {
		if (!"STS".equals(serverCheckRepository.getServerName()))
			return;

		List<OSDiskUsage> result = serverCheckRepository.checkOSDiskUsage();

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

		Workbook workbook = ExcelSheet.getWorkbook(is, fileName + extension);
		Sheet sheet = workbook.getSheetAt(0);

		for (OSDiskUsage data : result) {
			String mountedOn = data.getMountedOn();
			double usePercent = data.getUsedPercent();

			if (!mountedOn.startsWith("/oradata")) {
				continue;
			}

			int rowIndex = Integer.parseInt(mountedOn.substring(mountedOn.length() - 1)) + 38;
			sheet.getRow(rowIndex).getCell(colIndex).setCellValue(usePercent + "%");
		}

		OutputStream os = new FileOutputStream(file);
		workbook.write(os);
		workbook.close();
		is.close();
	}

	@Override
	public void writeCsvOSDiskUsage() throws Exception {
		String monitoringDate = DateUtils.format(new Date(), "yyyyMMdd");
		String monitoringTime = DateUtils.format(new Date(), "HHmmss");

		List<OSDiskUsage> result = serverCheckRepository.checkOSDiskUsage();
		for (OSDiskUsage os : result) {
			os.setMonitoringDate(monitoringDate);
			os.setMonitoringTime(monitoringTime);
		}
		reportRepository.writeReportFile("OSDiskUsage", serverCheckRepository.getServerName(), ".txt", result,
				OSDiskUsage.class);
	}

	@Override
	public List<OSDiskUsage> getCurrentOSDiskUsage() {
		List<OSDiskUsage> result = serverCheckRepository.checkOSDiskUsage();
		return result;
	}

	@Override
	public AlertLog getAlertLogDuringPeriod(AlertLogCommandPeriod alcp) {
		AlertLog result = serverCheckRepository.checkAlertLogDuringPeriod(alcp);
		return result;
	}
}
