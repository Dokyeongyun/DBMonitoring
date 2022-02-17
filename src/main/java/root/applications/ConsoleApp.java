package root.applications;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import root.utils.PatternUtils;
import root.utils.PropertiesUtils;

/**
 * ConsoleApp�� Console�� ���� ��/����� �����մϴ�.
 * 
 * @author DKY
 *
 */
public class ConsoleApp {

	public static final String DEFAULT_CONFIG_DIR = "./config/connectioninfo";

	public static void main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		// STEP1: �������� �������� ����
		String selectedFile = "";
		while (true) {
			System.out.println("�������� ���������� �������ּ���.");
			List<String> configFiles = Arrays.asList(new File(DEFAULT_CONFIG_DIR).list())
					.stream()
					.filter(fileName -> fileName.endsWith(".properties"))
					.collect(Collectors.toList());

			if (configFiles.size() == 0) {
				System.out.println("�������� ���������� �������� �ʽ��ϴ�. ���α׷��� �����մϴ�.");
				return;
			}

			for (int i = 0; i < configFiles.size(); i++) {
				System.out.println(String.format("[%d] %s", (i + 1), configFiles.get(i)));
			}

			String input = br.readLine().trim();
			if(!PatternUtils.isOnlyNumber(input)) {
				System.out.println("�߸� �Է��ϼ̽��ϴ�. �������� ���������� �ٽ� �������ּ���.");
				continue;
			}

			int selectedId = Integer.valueOf(input);
			if (!PatternUtils.isOnlyNumber(input) || selectedId <= 0 || selectedId > configFiles.size()) {
				System.out.println("�߸� �Է��ϼ̽��ϴ�. �������� ���������� �ٽ� �������ּ���.");
				continue;
			}

			selectedFile = configFiles.get(selectedId - 1);
			break;
		}
		System.out.println(String.format("���õ� ������ [%s] �Դϴ�.", selectedFile));

		// STEP2: ���õ� �������� �������� Load
		String propertiesFilePath = DEFAULT_CONFIG_DIR + "/" + selectedFile;
		try {
			PropertiesUtils.loadAppConfiguration(propertiesFilePath, "connInfoConfig");
		} catch (Exception e) {
			System.out.println("configuration loading error\n" + e + "\n");
			return;
		}
		
		// STEP3: �������� �������� ��, ����͸����� �������� ã�� �� LOAD
	}
}
