package root.core.usecase.constracts;

import java.util.List;
import java.util.Map;

import root.core.domain.MonitoringResult;
import root.utils.UnitUtils.FileSize;

public interface ReportUsecase {

	<T extends MonitoringResult> List<T> getMonitoringReportData(Class<T> clazz, String alias, FileSize unit,
			int round);

	<T extends MonitoringResult> Map<String, List<T>> getMonitoringReportDataByTime(Class<T> clazz, String alias,
			FileSize unit, int round, String inquiryDate);

	<T extends MonitoringResult> Map<Integer, Long> getMonitoringReportCountByTime(Class<T> clazz, String alias,
			FileSize unit, int round, String inquiryDate);

	<T extends MonitoringResult> Map<Integer, List<String>> getMonitoringReportTimesByTime(Class<T> clazz, String alias,
			FileSize unit, int round, String inquiryDate);
	
	<T extends MonitoringResult> List<String> getMonitoringHistoryDays(Class<T> clazz, String alias);
}
