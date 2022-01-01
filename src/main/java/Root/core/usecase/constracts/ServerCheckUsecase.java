package root.core.usecase.constracts;

import java.util.List;

import root.core.domain.AlertLog;
import root.core.domain.AlertLogCommand;
import root.core.domain.AlertLogCommandPeriod;
import root.core.domain.OSDiskUsage;

public interface ServerCheckUsecase {
	void printAlertLog(AlertLogCommand alc);
	void printAlertLogDuringPeriod(AlertLogCommandPeriod alcp);
	void printOSDiskUsage(String command);
	
	void writeExcelOSDiskUsage(String command) throws Exception;
	void writeCsvOSDiskUsage(String command) throws Exception;
	
	List<OSDiskUsage> getCurrentOSDiskUsage(String command);
	AlertLog getAlertLogDuringPeriod(AlertLogCommandPeriod alcp);
}
