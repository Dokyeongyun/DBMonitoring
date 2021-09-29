package Root.Usecases;

import java.util.List;
import java.util.Map;

import Root.Repository.DBCheckRepository;

public class DBCheckUsecaseImpl implements DBCheckUsecase {
	private DBCheckRepository dbCheckRepository;

	public DBCheckUsecaseImpl(DBCheckRepository dbCheckRepository) {
		this.dbCheckRepository = dbCheckRepository;
	}

	@Override
	public void printArchiveUsageCheck() {
		List<Map<String, Object>> result = dbCheckRepository.checkArchiveUsage();
		System.out.println("\t¢º Archive Usage Check");
		System.out.println(mapListToString(result));
	}

	@Override
	public void printTableSpaceCheck() {
		List<Map<String, Object>> result = dbCheckRepository.checkTableSpaceUsage();
		System.out.println("\t¢º TableSpace Usage Check");
		System.out.println(mapListToString(result));
	}

	@Override
	public void printASMDiskCheck() {
		List<Map<String, Object>> result = dbCheckRepository.checkASMDiskUsage();
		System.out.println("\t¢º ASM Disk Usage Check");
		System.out.println(mapListToString(result));
	}

	public String mapListToString(List<Map<String, Object>> mapList) {
		StringBuffer sb = new StringBuffer();

		sb.append("\t========================================================================================================\n");
		for(Map<String, Object> data : mapList) {
			for(String key : data.keySet()) {
				sb.append("\t"+key+" | ");
			}
			sb.append("\n");
			break;
		}

		for(Map<String, Object> data : mapList) {
			for(String key : data.keySet()) {
				sb.append("\t"+data.get(key)+" | ");
			}
			sb.append("\n");
		}
		sb.append("\t========================================================================================================\n");
		return sb.toString();
	}
}
