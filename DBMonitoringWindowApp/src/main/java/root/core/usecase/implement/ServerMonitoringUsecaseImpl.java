package root.core.usecase.implement;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import dnl.utils.text.table.TextTable;
import dnl.utils.text.table.csv.CsvTableModel;
import lombok.extern.slf4j.Slf4j;
import root.common.server.implement.AlertLogCommand;
import root.core.domain.AlertLog;
import root.core.domain.Log;
import root.core.domain.OSDiskUsage;
import root.core.repository.constracts.ReportRepository;
import root.core.repository.constracts.ServerMonitoringRepository;
import root.core.usecase.constracts.ServerMonitoringUsecase;
import root.utils.ConsoleUtils;
import root.utils.CsvUtils;
import root.utils.DBManageExcel;
import root.utils.DateUtils;
import root.utils.ExcelSheet;

@Slf4j
public class ServerMonitoringUsecaseImpl implements ServerMonitoringUsecase {
	private ServerMonitoringRepository serverCheckRepository;
	private ReportRepository reportRepository;

	public ServerMonitoringUsecaseImpl(ServerMonitoringRepository serverCheckRepository,
			ReportRepository reportRepository) {
		this.serverCheckRepository = serverCheckRepository;
		this.reportRepository = reportRepository;
	}

	@Override
	public void printAlertLog(AlertLogCommand alc) {
		String result = serverCheckRepository.checkAlertLog(alc);
		if (result.indexOf("ORA-") >= 0) {
			System.out.println("\t" + ConsoleUtils.BACKGROUND_RED + ConsoleUtils.FONT_WHITE
					+ "�� Alert Log : ORA ERROR!! Alert Log Ȯ�� �ʿ�" + ConsoleUtils.RESET + "\n");
		} else {
			System.out.println("\t�� Alert Log : SUCCESS!\n");
		}
	}

	@Override
	public void printAlertLogDuringPeriod(AlertLogCommand alc, String startDate, String endDate) {
		AlertLog result = getAlertLogDuringPeriod(alc, startDate, endDate);
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
					+ "�� Alert Log : ORA ERROR!! Alert Log Ȯ�� �ʿ�" + ConsoleUtils.RESET + "\n");
			System.out.println("\t��" + errorLogContents.size() + "���� ERROR�� �߻��߽��ϴ�. Alert Log�� Ȯ���Ͻðڽ��ϱ�? (Y/N)��");

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
								"\t" + ConsoleUtils.FONT_RED + "�߸� �Է��ϼ̽��ϴ�. Y �Ǵ� N�� �Է����ּ���." + ConsoleUtils.RESET);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			if (isCheck == true) {
				int errorLogIndex = 0;
				System.out.println("\t��ERROR [" + (errorLogIndex + 1) + "/" + errorLogContents.size()
						+ "] (Enter: ��������Ȯ��, ����: ��������Ȯ��, q: ����)��\n");
				System.out.print(errorLogContents.get(errorLogIndex).errorLogToString());

				boolean isExit = false;
				while (true) {
					try {
						String input = br.readLine().trim().toUpperCase();

						if ("".equals(input) || "ENTER".equals(input)) {
							errorLogIndex++;
							if (errorLogIndex >= errorLogContents.size()) {
								System.out.println("\t�ظ����� ERROR�Դϴ�. �����Ͻðڽ��ϱ�? (Y/N)��");

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
									System.out.println("\t�������");
									break;
								} else {
									System.out.println("\t�ظ�ɾ �Է����ּ���. (Enter: ��������Ȯ��, ����: ��������Ȯ��, q: ����)");
								}
							} else {
								System.out.println("\t��ERROR [" + (errorLogIndex + 1) + "/" + errorLogContents.size()
										+ "] (Enter: ��������Ȯ��, ����: ��������Ȯ��, q: ����)��\n");
								System.out.print(errorLogContents.get(errorLogIndex).errorLogToString());
							}
						} else if ("Q".equals(input)) {
							System.out.println("\t�������");
							break;
						} else {
							boolean isWrongInput = false;
							try {
								int inputIndex = Integer.parseInt(input) - 1;
								if (inputIndex >= errorLogContents.size() || inputIndex < 0) {
									isWrongInput = true;
								} else {
									errorLogIndex = inputIndex;
									System.out.println("\t��ERROR [" + (errorLogIndex + 1) + "/"
											+ errorLogContents.size() + "] (Enter: ��������Ȯ��, ����: ��������Ȯ��, q: ����)��\n");
									System.out.print(errorLogContents.get(errorLogIndex).errorLogToString());
								}
							} catch (NumberFormatException e) {
								isWrongInput = true;
							}

							if (isWrongInput == true) {
								System.out.println("\t" + ConsoleUtils.FONT_RED
										+ "�߸� �Է��ϼ̽��ϴ�. (Enter: ��������Ȯ��, ����: ��������Ȯ��, q: ����)" + ConsoleUtils.RESET);
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
					+ "�� OS Disk Usage : 80% �ʰ�!! Ȯ�� �ʿ�" + ConsoleUtils.RESET);
		} else {
			System.out.println("\t�� OS Disk Usage : SUCCESS!");
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
	public AlertLog getAlertLogDuringPeriod(AlertLogCommand alc, String startDate, String endDate,
			String... searchKeywords) {
		log.debug(String.format("alert log file monitoring, %s (%s ~ %s), Search Keywords: %s", alc.getReadFilePath(),
				startDate, endDate, StringUtils.join(searchKeywords, ",")));

		long start = System.currentTimeMillis();

		AlertLog alertLog = new AlertLog();
		String fullAlertLogString = getAlertLogStringFromCertainDate(alc, startDate);

		try {
			// ��ȸ�Ⱓ������ �α׸��� ���Ͽ� StringBuffer�� �����Ѵ�.
			String[] lines = fullAlertLogString.split(System.lineSeparator());

			boolean isStartDate = false;
			boolean isEndDate = false;

			int readStartIndex = 0;
			int readEndIndex = lines.length;

			String logTimeStamp = "";
			List<String> logContents = new ArrayList<>();
			StringBuffer sb = new StringBuffer();
			boolean containsSearchKeyword = searchKeywords.length == 0;

			for (int i = 0; i < lines.length; i++) {
				String line = lines[i];

				// ��ȸ�������� ã��
				if (!isStartDate) {
					LocalDate parsedDate = DateUtils.parse(line);
					if (parsedDate != null) {
						// [��ȸ�������� >= ���� �αױ������]�� ��, ���� �αױ�����ں��� �б� ����
						String parsedDateString = DateUtils.convertDateFormat("yyyy-MM-dd", parsedDate);
						if (DateUtils.getDateDiffTime("yyyy-MM-dd", parsedDateString, startDate) >= 0) {
							isStartDate = true;
							readStartIndex = i;
							logTimeStamp = line;

							// [��ȸ�������� > ��ȸ �������� >= ���� �αױ������]�� �� ���� �αױ�����ں��� �б� ����
							if (DateUtils.getDateDiffTime("yyyy-MM-dd", parsedDateString, endDate) > 0) {
								isEndDate = true;
								readEndIndex = i;
								break;
							}
						}
					}
				}

				// �α� ���� ���� & ��ȸ�������� ã��
				if (isStartDate) {
					LocalDate parsedDate = DateUtils.parse(line);
					if (parsedDate != null) { // Log TimeStamp Line

						// ���� �αױ�����ڰ� ��ȸ�������� + 1������ Ȯ��
						String logDate = DateUtils.convertDateFormat("yyyy-MM-dd", parsedDate);
						if (logDate.startsWith(DateUtils.addDate(endDate, 0, 0, 1))) {
							isEndDate = true;
							readEndIndex = i;
						}

						if (i == readStartIndex) {
							logTimeStamp = line;
						}

						if (i != readStartIndex) {
							if (containsSearchKeyword) {
								alertLog.addLog(new Log(alertLog.getAlertLogs().size(), logTimeStamp, logContents));
								containsSearchKeyword = searchKeywords.length == 0;
							}
							logContents = new ArrayList<>();
							logTimeStamp = line;
						}
					} else { // Log Content Line

						// �˻� Ű���尡 ���ԵǾ����� Ȯ��
						for (String keyword : searchKeywords) {
							if (line.contains(keyword) || containsSearchKeyword) {
								containsSearchKeyword = true;
								break;
							}
						}
						logContents.add(line);
					}

					// �α� ���� ����
					if (!isEndDate) {
						sb.append(line);
					} else {
						break;
					}
				}
			}

			// ���� �� fullLogString �߰� & Alert log file path ����
			alertLog.setFullLogString(sb.toString());
			alertLog.setFilePath(alc.getReadFilePath());

			log.info("\t�� Alert Log READ LINE: " + (readEndIndex - readStartIndex) + "/" + alc.getReadLine());

		} catch (Exception e) {
			log.error(e.getMessage());
		}

		long end = System.currentTimeMillis();
		log.info(String.format("Alert Log monitoring result (Log count: %d, Total line count: %d",
				alertLog.getAlertLogs().size(), alertLog.getTotalLineCount()));
		log.debug(String.format("Alert Log monitoring elapsed time: (%,.3f)ms", (end - start) / 1000.0));

		return alertLog;
	}

	private String getAlertLogStringFromCertainDate(AlertLogCommand alc, String startDate) {
		int alertLogFileLineCnt = serverCheckRepository.getAlertLogFileLineCount(alc);
		String fullAlertLogString = serverCheckRepository.checkAlertLog(alc);

		// ��ȸ���������� �α׸� ��� �����ϵ��� readLine ���� ���������� �ø��鼭 �д´�.
		while (true) {
			String[] lines = fullAlertLogString.split(System.lineSeparator());

			// ���� Read Line ���� ���� �ִ� Line ���� �ʰ����� ��, ���� ��ü�� �а� ��ȯ�Ѵ�.
			if (lines.length >= alertLogFileLineCnt) {
				break;
			}

			// ��ȸ�� �α� ������ ���� ó������ ��Ÿ���� �α��� ������ڸ� ����.
			String logDate = "";
			for (String line : lines) {
				LocalDate parsedDate = DateUtils.parse(line);
				if (parsedDate != null) {
					logDate = DateUtils.convertDateFormat("yyyy-MM-dd", parsedDate);
					break;
				}
			}

			if (logDate == null || logDate.equals("")) {
				break;
			}

			// ��ȸ�������ڿ� �α��� ó�� ������ڸ� ���Ѵ�.
			long diffTime = DateUtils.getDateDiffTime("yyyy-MM-dd", logDate, startDate);
			if (diffTime >= 0) { // ��ȸ Line ���� �� �÷��� �ٽ� ��ȸ
				alc.setReadLine((alc.getReadLine()) * 2);
				fullAlertLogString = serverCheckRepository.checkAlertLog(alc);
			} else {
				break;
			}
		}

		return fullAlertLogString;
	}
}
