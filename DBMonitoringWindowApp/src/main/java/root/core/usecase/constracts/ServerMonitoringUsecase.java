package root.core.usecase.constracts;

import java.util.List;

import root.common.server.implement.AlertLogCommand;
import root.core.domain.AlertLog;
import root.core.domain.OSDiskUsage;

public interface ServerMonitoringUsecase {
	void printAlertLog(AlertLogCommand alc);

	void printAlertLogDuringPeriod(AlertLogCommand alc, String startDate, String endDate);

	void printOSDiskUsage();

	void writeExcelOSDiskUsage() throws Exception;

	void writeCsvOSDiskUsage() throws Exception;

	List<OSDiskUsage> getCurrentOSDiskUsage();

	AlertLog getAlertLogDuringPeriod(AlertLogCommand alc, String startDate, String endDate, String... searchKeywords);
}
