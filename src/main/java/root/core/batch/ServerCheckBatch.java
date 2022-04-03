package root.core.batch;

import root.core.domain.AlertLogCommand;
import root.core.usecase.constracts.ServerCheckUsecase;

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
	
	public void startBatchAlertLogCheckDuringPeriod(AlertLogCommand alc, String startDate, String endDate) {
		try {
			this.serverCheckUsecase.printAlertLogDuringPeriod(alc, startDate, endDate);
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void startBatchOSDiskUsageCheck() {
		try {
			this.serverCheckUsecase.printOSDiskUsage();
			this.serverCheckUsecase.writeExcelOSDiskUsage();
			this.serverCheckUsecase.writeCsvOSDiskUsage();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
}
