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

	// TODO Reflection 이용하여 객체리스트를 전달받은 후 CSV형태로 작성할 수 있도록 해보자.
	/**
	 * 파일을 작성한다.
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
