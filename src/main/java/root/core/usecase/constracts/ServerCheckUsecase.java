package root.core.usecase.constracts;

import java.util.List;

import root.core.domain.AlertLog;
import root.core.domain.AlertLogCommand;
import root.core.domain.AlertLogCommandPeriod;
import root.core.domain.OSDiskUsage;

public interface ServerCheckUsecase {
	void printAlertLog(AlertLogCommand alc);

	void printAlertLogDuringPeriod(AlertLogCommandPeriod alcp);

	void printOSDiskUsage();

	void writeExcelOSDiskUsage() throws Exception;

	void writeCsvOSDiskUsage() throws Exception;

	List<OSDiskUsage> getCurrentOSDiskUsage();

	AlertLog getAlertLogDuringPeriod(AlertLogCommandPeriod alcp);
}
