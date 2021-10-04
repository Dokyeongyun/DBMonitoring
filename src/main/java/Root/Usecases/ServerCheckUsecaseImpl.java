package Root.Usecases;

import Root.Model.AlertLogCommand;
import Root.Repository.ServerCheckRepository;
import Root.Utils.ConsoleUtils;

public class ServerCheckUsecaseImpl implements ServerCheckUsecase {
	private ServerCheckRepository serverCheckRepository;

	public ServerCheckUsecaseImpl(ServerCheckRepository serverCheckRepository) {
		this.serverCheckRepository = serverCheckRepository;
	}

	@Override
	public void printAlertLog(AlertLogCommand alc) {
		String result = serverCheckRepository.checkAlertLog(alc);
		
		if(result.indexOf("ORA") >= 0) {
			System.out.println("\t"+ConsoleUtils.BACKGROUND_RED + ConsoleUtils.FONT_WHITE + "▶ ORA ERROR!! Alert Log 확인 필요"+ConsoleUtils.RESET);
		} else {
			System.out.println("\t▶ SUCCESS!");
		}
	}
	
	@Override
	public void printOSDiskUsage(String command) {
		String result = serverCheckRepository.checkOSDiskUsage(command);
		System.out.println("\t▶ OS Disk Usage");
		System.out.println("\t=================================================================================");
		String[] splitLine = result.split("\n");
		for(int i=0; i<splitLine.length; i++) System.out.print("\t"+splitLine[i]);
		System.out.println("\t=================================================================================\n");
	}
}
