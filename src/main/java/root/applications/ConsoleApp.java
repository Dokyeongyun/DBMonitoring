package root.applications;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import root.core.domain.exceptions.PropertyNotLoadedException;
import root.core.service.contracts.PropertyService;
import root.core.service.implement.FilePropertyService;
import root.utils.PatternUtils;

/**
 * ConsoleApp은 Console을 통해 입/출력을 수행합니다.
 * 
 * @author DKY
 *
 */
public class ConsoleApp {

	public static final String DEFAULT_CONFIG_DIR = "./config/connectioninfo";

	private static PropertyService propService;

	public static void main(String[] args) throws IOException, PropertyNotLoadedException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		// STEP1: 접속정보 설정파일 선택
		String selectedFile = "";
		while (true) {
			System.out.println("접속정보 설정파일을 선택해주세요.");
			List<String> configFiles = Arrays.asList(new File(DEFAULT_CONFIG_DIR).list()).stream()
					.filter(fileName -> fileName.endsWith(".properties")).collect(Collectors.toList());

			if (configFiles.size() == 0) {
				System.out.println("접속정보 설정파일이 존재하지 않습니다. 프로그램을 종료합니다.");
				return;
			}

			for (int i = 0; i < configFiles.size(); i++) {
				System.out.println(String.format("[%d] %s", (i + 1), configFiles.get(i)));
			}

			String input = br.readLine().trim();
			if (!PatternUtils.isOnlyNumber(input)) {
				System.out.println("잘못 입력하셨습니다. 접속정보 설정파일을 다시 선택해주세요.");
				continue;
			}

			int selectedId = Integer.valueOf(input);
			if (!PatternUtils.isOnlyNumber(input) || selectedId <= 0 || selectedId > configFiles.size()) {
				System.out.println("잘못 입력하셨습니다. 접속정보 설정파일을 다시 선택해주세요.");
				continue;
			}

			selectedFile = configFiles.get(selectedId - 1);
			break;
		}
		System.out.println(String.format("선택된 파일은 [%s] 입니다.", selectedFile));

		// STEP2: 선택된 접속정보 설정파일 Load
		String propertiesFilePath = DEFAULT_CONFIG_DIR + "/" + selectedFile;
		try {
			propService = new FilePropertyService(propertiesFilePath);
		} catch (Exception e) {
			System.out.println("configuration loading error\n" + e + "\n");
			return;
		}

		// STEP3: 접속정보 설정파일 내, 모니터링여부 설정파일 찾기 및 LOAD
		String selectedPreset = "";
		while (true) {
			System.out.println(String.format("사용하실 모니터링여부 설정을 선택해주세요."));
			
			List<String> presetNames = propService.getMonitoringPresetNameList();
			if (presetNames.size() == 0) {
				System.out.println("모니터링여부 설정파일이 존재하지 않습니다. 프로그램을 종료합니다.");
				return;
			}
			
			for (int i = 0; i < presetNames.size(); i++) {
				System.out.println(String.format("[%d] %s", (i + 1), presetNames.get(i)));
			}
			
			String input = br.readLine().trim();
			if (!PatternUtils.isOnlyNumber(input)) {
				System.out.println("잘못 입력하셨습니다. 모니터링여부 설정파일을 다시 선택해주세요.");
				continue;
			}

			int selectedId = Integer.valueOf(input);
			if (!PatternUtils.isOnlyNumber(input) || selectedId <= 0 || selectedId > presetNames.size()) {
				System.out.println("잘못 입력하셨습니다. 모니터링여부 설정파일을 다시 선택해주세요.");
				continue;
			}

			selectedPreset = presetNames.get(selectedId - 1);
			break;
		}
		System.out.println(String.format("선택된 파일은 [%s] 입니다.", selectedPreset));
	}
}
