package Root.Batch;

import Root.Usecases.ServerCheckUsecase;

public class ServerCheckBatch {
	private ServerCheckUsecase serverCheckUsecase;
	
	public ServerCheckBatch(ServerCheckUsecase serverCheckUsecase) {
		this.serverCheckUsecase = serverCheckUsecase;
	}
	
	public void startBatchAlertLogCheck() {
		try {
			this.serverCheckUsecase.printAlertLog();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
}
