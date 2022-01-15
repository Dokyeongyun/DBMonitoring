package root.utils;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.time.format.TextStyle;
import java.util.Locale;

import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;

public class DBManageExcel extends ExcelUtils {

	public DBManageExcel(String sheetName) {
		super(sheetName);
	}

	/**
	 * ���� DB����͸� �������� �������� ���ø��� �����Ѵ�.
	 * 
	 * @param year
	 * @param month
	 */
	public static void createMonthlyReportInExcel(int year, int month) {

		OutputStream fos;
		try {
			String filePath = "C:\\Users\\aserv\\Documents\\WorkSpace_DBMonitoring_Quartz\\DBMonitoring\\report\\";
			String fileName = "DB��������_����_"+year+"."+month;
			String extension = ".xlsx";
			fos = new FileOutputStream(filePath + fileName + extension);
		
			ExcelUtils excel = new ExcelUtils("MonthlyReport");
			
			// CellStyle ����
			// 1. ȸ�� ���, ������ �Ǽ� �׵θ�, �߾�����
			XSSFCellStyle grayCS = excel.createCellStyle("d0cece", false);
			// 2. ȸ�� ���, ������ �Ǽ� �׵θ�, ��������
			XSSFCellStyle grayCSLeft = excel.createCellStyle("d0cece", false);
			grayCSLeft.setAlignment(HorizontalAlignment.LEFT);
			// 3. ��� ���, ������ �Ǽ� �׵θ�, �߾�����
			XSSFCellStyle whiteCS = excel.createCellStyle("ffffff", false);
			// 4. ��� ���, ������ �Ǽ� �׵θ�, ��������
			XSSFCellStyle whiteCSLeft = excel.createCellStyle("ffffff", false);
			whiteCSLeft.setAlignment(HorizontalAlignment.LEFT);
			
			// Write Header Region
			// 1. [DB Check List] �ؽ�Ʈ  
			excel.merge(0, 0, 1, 1, grayCS, "DB Check List");
			// 2. [yyyy�� MM��] �ؽ�Ʈ
			excel.merge(2, 0, 2, 1, grayCS, "2021�� 10��");
			excel.setColWidth(2, 8000);
			// 3. [Check List, ��¥ Header]
			excel.merge(0, 2, 2, 3, grayCS, "Check List");

			int dayOfMonth = DateUtils.getMonthlyDayCount(year, month);
			
			int dayTextColIndex = 3;
			int dayTextRowIndex = 2;
			for(int i=1; i<=dayOfMonth; i++) {
				String dayOfWeek = DateUtils.getDayOfWeek(year, month, i, TextStyle.SHORT, Locale.KOREAN);
				excel.setCell(dayTextColIndex+i-1, dayTextRowIndex, grayCS, DateUtils.getTwoDigitDate(i));
				excel.setCell(dayTextColIndex+i-1, dayTextRowIndex+1, DateUtils.isWeekEnd(year, month, i) ? grayCS : whiteCS, dayOfWeek);
			}
			
			// 4. DB�� Header 
			String[] dbNames = new String[] {"DBERP1", "DBERP2", "DBPOS1", "DBPOS2", "GPOS1", "GPOS2", "OGG"};
			String[] checkList1 = new String[] {"Oracle Listener(����Ŭ ����)", "Alert, Trace Log", "Archive Volume(used(%)", "Backup Check(archivelog)", "OGG Running Status", "OS Disk Usage"};
			String[] checkList2 = new String[] {"Oracle Listener(����Ŭ ����)", "Alert, Trace Log", "OS Disk Usage"};
			String[] checkList3 = new String[] {"Oracle Listener(����Ŭ ����)", "Oradata01", "Oradata02", "Oradata03", "Oradata04", "Oradata05", "Oradata06", "Alert, Trace Log", "OGG Running Status"};
			String[][] checkListType = new String[][] {checkList1, checkList2, checkList1, checkList2, checkList1, checkList2, checkList3};

			int curIndex = 4;
			for(int i=0; i<dbNames.length; i++) {
				excel.merge(1, curIndex, 2, curIndex, grayCSLeft, dbNames[i]);
				for(int j=3; j<3+DateUtils.getMonthlyDayCount(year, month); j++) {
					excel.setCell(j, curIndex, grayCS, null);
				}
				curIndex++;
				for(int j=0; j<checkListType[i].length; j++) {
					excel.merge(1, curIndex, 2, curIndex, whiteCSLeft, " - " + checkListType[i][j]);
					for(int k=3; k<3+DateUtils.getMonthlyDayCount(year, month); k++) {
						excel.setCell(k, curIndex, whiteCS, null);
					}
					curIndex++;
				}
			}
			
			excel.write(fos);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
