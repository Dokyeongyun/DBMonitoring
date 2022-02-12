package root.core.usecase.implement;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import dnl.utils.text.table.TextTable;
import dnl.utils.text.table.csv.CsvTableModel;
import root.core.domain.ASMDiskUsage;
import root.core.domain.ArchiveUsage;
import root.core.domain.TableSpaceUsage;
import root.core.repository.constracts.DBCheckRepository;
import root.core.repository.constracts.ReportRepository;
import root.core.usecase.constracts.DBCheckUsecase;
import root.utils.ConsoleUtils;
import root.utils.CsvUtils;
import root.utils.DBManageExcel;
import root.utils.DateUtils;
import root.utils.ExcelSheet;

public class DBCheckUsecaseImpl implements DBCheckUsecase {
	private DBCheckRepository dbCheckRepository;
	private ReportRepository reportRepository;

	public DBCheckUsecaseImpl(DBCheckRepository dbCheckRepository, ReportRepository reportRepository) {
		this.dbCheckRepository = dbCheckRepository;
		this.reportRepository = reportRepository;
	}

	@Override
	public void printArchiveUsageCheck() {
		List<ArchiveUsage> result = dbCheckRepository.checkArchiveUsage();
		System.out.println("\t▶ Archive Usage Check");

		result.getMonitoringResults().forEach(r -> {
			if (r.getUsedPercent() >= 90) {
				System.out.println("\t" + ConsoleUtils.BACKGROUND_RED + ConsoleUtils.FONT_WHITE
						+ "▶ Archive Usage Check : Usage 90% 초과! (" + r.getArchiveName() + ")" + ConsoleUtils.RESET
						+ "\n");
			} else {
				System.out.println("\t▶ Archive Usage Check : SUCCESS\n");
			}
		});
		
		try {
			TextTable tt = new TextTable(
					new CsvTableModel(CsvUtils.toCsvString(result, ArchiveUsage.class)));
			tt.printTable(System.out, 8);
			System.out.println();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void printTableSpaceCheck() {
		List<TableSpaceUsage> result = dbCheckRepository.checkTableSpaceUsage();
		System.out.println("\t▶ TableSpace Usage Check");
		try {
			TextTable tt = new TextTable(
					new CsvTableModel(CsvUtils.toCsvString(result, TableSpaceUsage.class)));
			tt.printTable(System.out, 8);
			System.out.println();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void printASMDiskCheck() {
		List<ASMDiskUsage> result = dbCheckRepository.checkASMDiskUsage();
		System.out.println("\t▶ ASM Disk Usage Check");
		try {
			TextTable tt = new TextTable(
					new CsvTableModel(CsvUtils.toCsvString(result, ASMDiskUsage.class)));
			tt.printTable(System.out, 8);
			System.out.println();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// TODO General한 양식작성이 가능하도록.. 현재 Hard Coding이 너무 많음
	@Override
	public void writeExcelArchiveUsageCheck() throws Exception {
		List<ArchiveUsage> result = dbCheckRepository.checkArchiveUsage();
		String dbName = dbCheckRepository.getDBName();

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

		String filePath = "./report/";
		String fileName = "DB관리대장_종합_" + year + "." + DateUtils.getTwoDigitDate(month);
		String extension = ".xlsx";
		File file = new File(filePath + fileName + extension);
		
		if(!file.exists()) {
			file.getParentFile().mkdirs();
			DBManageExcel.createMonthlyReportInExcel(year, month);
		}
		
		double archiveUsage = result.getMonitoringResults().get(0).getUsedPercent(); 
		Workbook workbook = ExcelSheet.getWorkbook(new FileInputStream(file), fileName + extension);
		Sheet sheet = workbook.getSheetAt(0);
		sheet.getRow(rowIndex).getCell(colIndex).setCellValue(archiveUsage + "%");
		OutputStream os = new FileOutputStream(file);
		workbook.write(os);
	}

	@Override
	public void writeCsvArchiveUsage() {
		List<ArchiveUsage> result = dbCheckRepository.checkArchiveUsage();
		reportRepository.writeReportFile("ArchiveUsage", dbCheckRepository.getDBName(), ".txt", result, ArchiveUsage.class);
	}

	@Override
	public void writeCsvTableSpaceUsage() {
		List<TableSpaceUsage> result = dbCheckRepository.checkTableSpaceUsage();
		reportRepository.writeReportFile("TableSpaceUsage", dbCheckRepository.getDBName(), ".txt", result, TableSpaceUsage.class);
	}

	@Override
	public void writeCsvASMDiskUsage() {
		List<ASMDiskUsage> result = dbCheckRepository.checkASMDiskUsage();
		reportRepository.writeReportFile("ASMDiskUsage", dbCheckRepository.getDBName(), ".txt", result, ASMDiskUsage.class);
	}

	@Override
	public List<ArchiveUsage> getCurrentArchiveUsage() {
		return dbCheckRepository.checkArchiveUsage();
	}

	@Override
	public List<TableSpaceUsage> getCurrentTableSpaceUsage() {
		return dbCheckRepository.checkTableSpaceUsage();
	}

	@Override
	public List<ASMDiskUsage> getCurrentASMDiskUsage() {
		return dbCheckRepository.checkASMDiskUsage();
	}
}
