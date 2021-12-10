package Root.Usecases;

import java.util.List;

import Root.Model.AlertLogCommand;
import Root.Model.AlertLogCommandPeriod;
import Root.Model.OSDiskUsage;

public interface ServerCheckUsecase {
	void printAlertLog(AlertLogCommand alc);
	void printAlertLogDuringPeriod(AlertLogCommandPeriod alcp);
	void printOSDiskUsage(String command);
	
	void writeExcelOSDiskUsage(String command) throws Exception;
	void writeCsvOSDiskUsage(String command) throws Exception;
	
	List<OSDiskUsage> getCurrentOSDiskUsage(String command);
}
