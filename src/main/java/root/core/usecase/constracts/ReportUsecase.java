package root.core.usecase.constracts;

import java.util.List;

public interface ReportUsecase {
	<T> List<T> getMonitoringReportData(Class<T> clazz, String alias);
}
