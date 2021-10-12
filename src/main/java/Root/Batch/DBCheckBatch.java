package Root.Batch;

import Root.Usecases.DBCheckUsecase;

public class DBCheckBatch {
	private DBCheckUsecase dbCheckUsecase;
	
	public DBCheckBatch(DBCheckUsecase dbCheckUsecase) {
		this.dbCheckUsecase = dbCheckUsecase;
	}
	
	public void startBatchArchiveUsageCheck() {
		try {
			this.dbCheckUsecase.printArchiveUsageCheck();
			this.dbCheckUsecase.writeExcelArchiveUsageCheck();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void startBatchTableSpaceUsageCheck() {
		try {
			this.dbCheckUsecase.printTableSpaceCheck();
			this.dbCheckUsecase.writeCsvTableSpaceUsage();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void startBatchASMDiskUsageCheck() {
		try {
			this.dbCheckUsecase.printASMDiskCheck();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void writeExcelArchiveUsageCheck() {
		try {
			this.dbCheckUsecase.writeExcelArchiveUsageCheck();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
}
