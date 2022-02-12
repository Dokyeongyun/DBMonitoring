package root.core.usecase.constracts;

import java.util.List;

import root.core.domain.MonitoringResult;
import root.utils.UnitUtils.FileSize;

public interface ReportUsecase {
	<T extends MonitoringResult> List<T> getMonitoringReportData(Class<T> clazz, String alias, FileSize unit, int round);
}
