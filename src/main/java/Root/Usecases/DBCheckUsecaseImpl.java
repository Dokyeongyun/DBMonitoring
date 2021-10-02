package Root.Usecases;

import java.util.*;

import Root.Repository.DBCheckRepository;
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
	}
	
	public String mapListToTableFormatString(List<Map> mapList) {
		StringBuffer sb = new StringBuffer();
		return sb.toString();
	}
}
