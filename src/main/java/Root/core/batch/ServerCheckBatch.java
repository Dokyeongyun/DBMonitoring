package Root.Batch;

import Root.Model.AlertLogCommand;
import Root.Model.AlertLogCommandPeriod;
import Root.Usecase.ServerCheckUsecase;

public class ServerCheckBatch {
	private ServerCheckUsecase serverCheckUsecase;
	
	public ServerCheckBatch(ServerCheckUsecase serverCheckUsecase) {
		this.serverCheckUsecase = serverCheckUsecase;
	}
	
	public void startBatchAlertLogCheck(AlertLogCommand alc) {
		try {
			this.serverCheckUsecase.printAlertLog(alc);
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void startBatchAlertLogCheckDuringPeriod(AlertLogCommandPeriod alcp) {
		try {
			this.serverCheckUsecase.printAlertLogDuringPeriod(alcp);
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void startBatchOSDiskUsageCheck(String command) {
		try {
			this.serverCheckUsecase.printOSDiskUsage(command);
			this.serverCheckUsecase.writeExcelOSDiskUsage(command);
			this.serverCheckUsecase.writeCsvOSDiskUsage(command);
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
}
