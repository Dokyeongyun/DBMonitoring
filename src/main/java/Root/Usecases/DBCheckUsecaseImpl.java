package Root.Usecases;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;

import Root.Repository.DBCheckRepository;
import Root.Utils.DBManageExcel;
import Root.Utils.DateUtils;
import Root.Utils.ExcelUtils;
import dnl.utils.text.table.MapBasedTableModel;
import dnl.utils.text.table.TextTable;

@SuppressWarnings("rawtypes")
public class DBCheckUsecaseImpl implements DBCheckUsecase {
	private DBCheckRepository dbCheckRepository;

	public DBCheckUsecaseImpl(DBCheckRepository dbCheckRepository) {
		this.dbCheckRepository = dbCheckRepository;
	}

	@Override
	public void printArchiveUsageCheck() {
		List<Map> result = dbCheckRepository.checkArchiveUsage();
		System.out.println("\t▶ Archive Usage Check");
		printMapListToTableFormat(result, 8);
	}

	@Override
	public void printTableSpaceCheck() {
		List<Map> result = dbCheckRepository.checkTableSpaceUsage();
		System.out.println("\t▶ TableSpace Usage Check");
		printMapListToTableFormat(result, 8);
	}

	@Override
	public void printASMDiskCheck() {
		List<Map> result = dbCheckRepository.checkASMDiskUsage();
		System.out.println("\t▶ ASM Disk Usage Check");
		printMapListToTableFormat(result, 8);
	}
	
	@Override
	public void writeExcelArchiveUsageCheck() throws Exception {
		List<Map> result = dbCheckRepository.checkArchiveUsage();
		String dbName = dbCheckRepository.getDBName();
		String archiveUsage = (String) result.get(0).get("USED_RATE") + "%";
		
		int year = Integer.parseInt(DateUtils.getToday("yyyy"));
		int month = Integer.parseInt(DateUtils.getToday("MM"));
		int day = Integer.parseInt(DateUtils.getToday("dd"));
		
		int colIndex = day + 2;
		int rowIndex = 0;
		if(dbName.equals("ERP")) {
			rowIndex = 7; 
		} else if(dbName.equals("POSSALE")) {
			rowIndex = 18;
		} else if(dbName.equals("GPOSSALE")) {
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
		
		Workbook workbook = ExcelUtils.getWorkbook(is, fileName+extension);
		Sheet sheet = workbook.getSheetAt(0);
		sheet.getRow(rowIndex).getCell(colIndex).setCellValue(archiveUsage);
		OutputStream os = new FileOutputStream(file);
		workbook.write(os);
	}


	/**
	 * List<Map> 형태의 데이터를 테이블 포맷으로 출력한다.
	 * 
	 * @param mapList 출력할 데이터
	 * @param indent  들여쓰기
	 */
	public void printMapListToTableFormat(List<Map> mapList, int indent) {
		// List<String> 형태의 header 리스트
		// List<Map> 형태의 data 리스트
		TextTable tt = new TextTable(new MapBasedTableModel(mapList));
		tt.printTable(System.out, 8);
		System.out.println();
	}
	
	public String mapListToTableFormatString(List<Map> mapList) {
		StringBuffer sb = new StringBuffer();
		return sb.toString();
	}
}
