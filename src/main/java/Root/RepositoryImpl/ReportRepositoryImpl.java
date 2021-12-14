package Root.RepositoryImpl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import Root.Repository.ReportRepository;

public class ReportRepositoryImpl implements ReportRepository {
	
	private static ReportRepository reportRepository = new ReportRepositoryImpl();

	private String rootDirectory = "./report";

	private ReportRepositoryImpl() {}
	
	public static ReportRepository getInstance() {
		return reportRepository;
	}

	// TODO Reflection �̿��Ͽ� ��ü����Ʈ�� ���޹��� �� CSV���·� �ۼ��� �� �ֵ��� �غ���.
	/**
	 * ������ �ۼ��Ѵ�.
	 */
	@Override
	public void writeReportFile(String filePath, String fileName, String fileExtension, String fileContent) {
		try {
			File file = new File(rootDirectory + "/" + filePath + "/" + fileName + fileExtension);
			if(!file.exists()) {
				file.createNewFile();
			}
			BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));
			bw.append(fileContent).append("\n");
			bw.flush();
			bw.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

}
