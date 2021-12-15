package Root.Application;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

import Root.Repository.ReportRepository;
import Root.RepositoryImpl.ReportRepositoryImpl;
import Root.Utils.DateUtils;

public class CsvReportSeparatorApp {
	
	public static ReportRepository reportRepository = ReportRepositoryImpl.getInstance();
	
	public static void main(String[] args) throws IOException {
		/*
		 * �ش� Application������ ���������� ��ϵǾ� �ִ� .txt ������
		 * �� ��¥�� ������ .txt ���Ϸ� ������ �ۼ��ϴ� ����� �����Ѵ�.
		 */
		
		String filePath = "OSDiskUsage";
		String fileName = "STS";
		String dateFormat ="EEE MMM dd HH:mm:ss yyyy";
		String dateFormatRegex = "...\\s...\\s([0-2][0-9]|1[012])\\s\\d\\d:\\d\\d:\\d\\d\\sKST\\s\\d{4}";

		Map<String, StringBuffer> map = new HashMap<>();
		
		File file = new File("./report/" + filePath + "/"+ fileName + ".txt");
		String line = "";
		BufferedReader br = new BufferedReader(new FileReader(file));
		StringBuffer sb = new StringBuffer();
		
		String newFileName = "";
		while((line = br.readLine()) != null) {
			
			if(Pattern.matches("^"+dateFormatRegex, line)) {
				String[] split = line.split(" KST ");
				String date = line = split[0]+" "+split[1];
				String logDate = DateUtils.convertDateFormat(dateFormat, "yyyyMMdd_HHmmss", date, Locale.ENGLISH);
				newFileName = logDate + "_" + fileName;
				sb = new StringBuffer();
				sb.append(line).append("\n");
				map.put(newFileName, sb);
			} else {
				map.get(newFileName).append(line).append("\n");
			}
			
		}
		br.close();
		
		for(String key : map.keySet()) {
//			System.out.println("[" + key + "]\n" + map.get(key));
			reportRepository.writeReportFile(filePath + "/" + fileName, key, ".txt", map.get(key).toString());
		}
	}
}
