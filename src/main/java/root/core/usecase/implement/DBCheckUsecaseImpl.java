package root.core.usecase.implement;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import dnl.utils.text.table.TextTable;
import dnl.utils.text.table.csv.CsvTableModel;
import root.core.domain.ASMDiskUsage;
import root.core.domain.ArchiveUsage;
import root.core.domain.MonitoringResult;
import root.core.domain.TableSpaceUsage;
import root.core.repository.constracts.DBCheckRepository;
import root.core.repository.constracts.ReportRepository;
import root.core.usecase.constracts.DBCheckUsecase;
import root.utils.ConsoleUtils;
import root.utils.CsvUtils;
import root.utils.DBManageExcel;
import root.utils.DateUtils;
import root.utils.ExcelUtils;

public class DBCheckUsecaseImpl implements DBCheckUsecase {
	private DBCheckRepository dbCheckRepository;
	private ReportRepository reportRepository;

	public DBCheckUsecaseImpl(DBCheckRepository dbCheckRepository, ReportRepository reportRepository) {
		this.dbCheckRepository = dbCheckRepository;
		this.reportRepository = reportRepository;
	}

	@Override
	public void printArchiveUsageCheck() {
		MonitoringResult<ArchiveUsage> result = dbCheckRepository.checkArchiveUsage();
		System.out.println("\t▶ Archive Usage Check");
		try {
			TextTable tt = new TextTable(
					new CsvTableModel(CsvUtils.toCsvString(result.getMonitoringResults(), ArchiveUsage.class)));
			tt.printTable(System.out, 8);
			System.out.println();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void printTableSpaceCheck() {
		MonitoringResult<TableSpaceUsage> result = dbCheckRepository.checkTableSpaceUsage();
		System.out.println("\t▶ TableSpace Usage Check");
		try {
			TextTable tt = new TextTable(
					new CsvTableModel(CsvUtils.toCsvString(result.getMonitoringResults(), TableSpaceUsage.class)));
			tt.printTable(System.out, 8);
			System.out.println();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void printASMDiskCheck() {
		MonitoringResult<ASMDiskUsage> result = dbCheckRepository.checkASMDiskUsage();
		System.out.println("\t▶ ASM Disk Usage Check");
		try {
			TextTable tt = new TextTable(
					new CsvTableModel(CsvUtils.toCsvString(result.getMonitoringResults(), ASMDiskUsage.class)));
			tt.printTable(System.out, 8);
			System.out.println();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// TODO General한 양식작성이 가능하도록.. 현재 Hard Coding이 너무 많음
	@Override
	public void writeExcelArchiveUsageCheck() throws Exception {
		MonitoringResult<ArchiveUsage> result = dbCheckRepository.checkArchiveUsage();
		String dbName = dbCheckRepository.getDBName();
		double archiveUsage = result.getMonitoringResults().get(0).getUsedPercent();

		if (archiveUsage >= 90) {
			System.out.println("\t" + ConsoleUtils.BACKGROUND_RED + ConsoleUtils.FONT_WHITE
					+ "▶ Archive Usage Check : Usage 90% 초과!" + ConsoleUtils.RESET + "\n");
		} else {
			System.out.println("\t▶ Archive Usage Check : SUCCESS\n");
		}

		int year = Integer.parseInt(DateUtils.getToday("yyyy"));
		int month = Integer.parseInt(DateUtils.getToday("MM"));
		int day = Integer.parseInt(DateUtils.getToday("dd"));

		int colIndex = day + 2;
		int rowIndex = 0;
		if (dbName.equals("ERP")) {
			rowIndex = 7;
		} else if (dbName.equals("POSSALE")) {
			rowIndex = 18;
		} else if (dbName.equals("GPOSSALE")) {
			rowIndex = 29;
		}

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

		Workbook workbook = ExcelUtils.getWorkbook(is, fileName + extension);
		Sheet sheet = workbook.getSheetAt(0);
		sheet.getRow(rowIndex).getCell(colIndex).setCellValue(archiveUsage + "%");
		OutputStream os = new FileOutputStream(file);
		workbook.write(os);
	}

	@Override
	public void writeCsvArchiveUsage() {
		MonitoringResult<ArchiveUsage> result = dbCheckRepository.checkArchiveUsage();
		reportRepository.writeReportFile("ArchiveUsage", dbCheckRepository.getDBName(), ".txt", result);
	}

	@Override
	public void writeCsvTableSpaceUsage() {
		MonitoringResult<TableSpaceUsage> result = dbCheckRepository.checkTableSpaceUsage();
		reportRepository.writeReportFile("TableSpaceUsage", dbCheckRepository.getDBName(), ".txt", result);
	}

	@Override
	public void writeCsvASMDiskUsage() {
		MonitoringResult<ASMDiskUsage> result = dbCheckRepository.checkASMDiskUsage();
		reportRepository.writeReportFile("ASMDiskUsage", dbCheckRepository.getDBName(), ".txt", result);
	}

	@Override
	public MonitoringResult<ArchiveUsage> getCurrentArchiveUsage() {
		return dbCheckRepository.checkArchiveUsage();
	}

	@Override
	public MonitoringResult<TableSpaceUsage> getCurrentTableSpaceUsage() {
		return dbCheckRepository.checkTableSpaceUsage();
	}

	@Override
	public MonitoringResult<ASMDiskUsage> getCurrentASMDiskUsage() {
		return dbCheckRepository.checkASMDiskUsage();
	}
}
