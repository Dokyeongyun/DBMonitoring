package Root.Usecases;

import Root.Model.AlertLogCommand;
import Root.Model.AlertLogCommandPeriod;

public interface ServerCheckUsecase {
	void printAlertLog(AlertLogCommand alc);
	void printAlertLogDuringPeriod(AlertLogCommandPeriod alcp);
	void printOSDiskUsage(String command);
	
	void writeExcelOSDiskUsage(String command) throws Exception;
}
