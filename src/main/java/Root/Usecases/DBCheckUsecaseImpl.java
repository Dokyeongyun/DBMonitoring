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
		System.out.println("\t�� Archive Usage Check");
		printMapListToTableFormat(result, 8);
	}

	@Override
	public void printTableSpaceCheck() {
		List<Map> result = dbCheckRepository.checkTableSpaceUsage();
		System.out.println("\t�� TableSpace Usage Check");
		printMapListToTableFormat(result, 8);
	}

	@Override
	public void printASMDiskCheck() {
		List<Map> result = dbCheckRepository.checkASMDiskUsage();
		System.out.println("\t�� ASM Disk Usage Check");
		printMapListToTableFormat(result, 8);
	}

	/**
	 * List<Map> ������ �����͸� ���̺� �������� ����Ѵ�.
	 * 
	 * @param mapList ����� ������
	 * @param indent  �鿩����
	 */
	public void printMapListToTableFormat(List<Map> mapList, int indent) {
		// List<String> ������ header ����Ʈ
		// List<Map> ������ data ����Ʈ
		TextTable tt = new TextTable(new MapBasedTableModel(mapList));
		tt.printTable(System.out, 8);
	}
	
	public String mapListToTableFormatString(List<Map> mapList) {
		StringBuffer sb = new StringBuffer();
		return sb.toString();
	}
}
