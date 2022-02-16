package root.applications;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * ConsoleApp은 Console을 통해 입/출력을 수행합니다.
 * 
 * @author DKY
 *
 */
public class ConsoleApp {
	
	public static final String DEFAULT_CONFIG_DIR = "./config";
	public static final Pattern IS_ONLY_NUMBER = Pattern.compile("^[0-9]*?");

	public static void main(String[] args) {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		while (true) {
			System.out.println("접속정보 설정파일을 선택해주세요.");
			List<String> configFiles = Arrays.asList(new File(DEFAULT_CONFIG_DIR).list())
					.stream()
					.filter(fileName -> fileName.endsWith(".properties"))
					.collect(Collectors.toList());

			if (configFiles.size() == 0) {
				System.out.println("접속정보 설정파일이 존재하지 않습니다. 프로그램을 종료합니다.");
				return;
			}

			for (int i = 0; i < configFiles.size(); i++) {
				System.out.println(String.format("[%d] %s", (i + 1), configFiles.get(i)));
			}

			try {
				String input = br.readLine().trim();
				if(!isOnlyNumber(input)) {
					System.out.println("잘못 입력하셨습니다. 접속정보 설정파일을 다시 선택해주세요.");
					continue;
				}
				
				int selectedId = Integer.valueOf(input);
				if (!isOnlyNumber(input) || selectedId <= 0 || selectedId > configFiles.size()) {
					System.out.println("잘못 입력하셨습니다. 접속정보 설정파일을 다시 선택해주세요.");
				} else {
					// TODO Show menu
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}

//			String propertiesFilePath = ".\\config\\application.properties";
//
//			try {
//				PropertiesUtils.loadCombinedConfiguration();
//				String lastUsePropertiesFile = PropertiesUtils.combinedConfig.getString("filepath.config.lastuse");
//				PropertiesUtils.loadAppConfiguration(lastUsePropertiesFile, "connInfoConfig");
//				PropertiesUtils.loadAppConfiguration(propertiesFilePath);
//				PropertiesUtils.loadCombinedConfiguration();
//				PropertiesUtils.loadAppConfiguration(".\\config\\connectioninfo\\test.properties", "connInfoConfig");
//			} catch (Exception e) {
//				System.out.println("configuration loading error\n" + e + "\n");
//				return;
//			}
//
//			String dbMonitoring = PropertiesUtils.propConfig.getString("monitoring.db");
//			String serverMonitoring = PropertiesUtils.propConfig.getString("monitoring.server");
	}
	
    public static boolean isOnlyNumber(final String str) {
    	if(str == "") return false;
        return IS_ONLY_NUMBER.matcher(str).matches();
    }
}
