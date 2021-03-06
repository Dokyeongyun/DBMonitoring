package root.core.usecase.implement;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import root.core.domain.MonitoringResult;
import root.core.repository.constracts.ReportRepository;
import root.core.usecase.constracts.ReportUsecase;
import root.utils.CsvUtils;
import root.utils.DateUtils;
import root.utils.UnitUtils.FileSize;

@Slf4j
public class ReportUsecaseImpl implements ReportUsecase {

	private ReportRepository reportRepo;

	public ReportUsecaseImpl(ReportRepository reportRepo) {
		this.reportRepo = reportRepo;
	}

	@Override
	public <T extends MonitoringResult> List<T> getMonitoringReportData(Class<T> clazz, String alias, FileSize unit,
			int round) {

		List<T> result = null;

		try {
			List<String> headers = reportRepo.getReportHeaders(clazz, alias);
			String csvString = reportRepo.getReportContentsInCsv(clazz, alias);

			result = CsvUtils.parseCsvToBeanList(headers, csvString, clazz);
			result.forEach(data -> data.convertUnit(FileSize.B, unit, round));

		} catch (Exception e) {
			e.printStackTrace();
			log.error("Parsing error!");
		}

		return result;
	}

	@Override
	public <T extends MonitoringResult> Map<String, List<T>> getMonitoringReportDataByTime(Class<T> clazz, String alias,
			FileSize unit, int round, String inquiryDate) {

		return getMonitoringReportData(clazz, alias, unit, round)
				.stream()
				.filter(m -> inquiryDate.equals(m.getMonitoringDate()))
				.sorted(Comparator.comparing(MonitoringResult::getMonitoringDateTime))
				.collect(Collectors.groupingBy(MonitoringResult::getMonitoringDateTime,
						LinkedHashMap::new,
						Collectors.mapping(m -> m, Collectors.toList())));
	}

	@Override
	public <T extends MonitoringResult> Map<String, List<T>> getPrevMonitoringReportDataByTime(Class<T> clazz,
			String alias, FileSize unit, int round, String inquiryDateTime) {

		String prevDateTime = getPrevHistoryDateTime(clazz, alias, unit, round, inquiryDateTime);

		return getMonitoringReportData(clazz, alias, unit, round)
				.stream()
				.filter(m -> prevDateTime.equals(m.getMonitoringDateTime()))
				.sorted(Comparator.comparing(MonitoringResult::getMonitoringDateTime).reversed())
				.collect(Collectors.groupingBy(m -> m.getMonitoringDateTime(),
						Collectors.mapping(m -> m, Collectors.toList())));
	}

	private <T extends MonitoringResult> String getPrevHistoryDateTime(Class<T> clazz, String alias, FileSize unit,
			int round, String curHistoryDateTime) {

		// TODO ?????? ????????? ??????
		MonitoringResult result = getMonitoringReportData(clazz, alias, unit, round)
				.stream()
				.filter(m -> DateUtils.compareTo("yyyyMMddHHmmss", curHistoryDateTime, m.getMonitoringDateTime()) == 1)
				.sorted(Comparator.comparing(MonitoringResult::getMonitoringDateTime).reversed())
				.findFirst()
				.orElse(null);

		return result == null ? curHistoryDateTime : result.getMonitoringDateTime();
	}

	@Override
	public <T extends MonitoringResult> Map<String, List<T>> getNextMonitoringReportDataByTime(Class<T> clazz,
			String alias, FileSize unit, int round, String inquiryDateTime) {

		String nextDateTime = getNextHistoryDateTime(clazz, alias, unit, round, inquiryDateTime);

		return getMonitoringReportData(clazz, alias, unit, round)
				.stream()
				.filter(m -> nextDateTime.equals(m.getMonitoringDateTime()))
				.sorted(Comparator.comparing(MonitoringResult::getMonitoringDateTime))
				.collect(Collectors.groupingBy(m -> m.getMonitoringDateTime(),
						Collectors.mapping(m -> m, Collectors.toList())));
	}

	private <T extends MonitoringResult> String getNextHistoryDateTime(Class<T> clazz, String alias, FileSize unit,
			int round, String curHistoryDateTime) {

		// TODO ?????? ????????? ??????
		MonitoringResult result = getMonitoringReportData(clazz, alias, unit, round)
				.stream()
				.filter(m -> DateUtils.compareTo("yyyyMMddHHmmss", curHistoryDateTime, m.getMonitoringDateTime()) == -1)
				.sorted(Comparator.comparing(MonitoringResult::getMonitoringDateTime))
				.findFirst()
				.orElse(null);

		return result == null ? curHistoryDateTime : result.getMonitoringDateTime();
	}

	@Override
	public <T extends MonitoringResult> Map<Integer, Long> getMonitoringReportCountByTime(Class<T> clazz,
			String alias, FileSize unit, int round, String inquiryDate) {

		Map<Integer, Long> result = getMonitoringReportDataByTime(clazz, alias, unit, round, inquiryDate)
				.keySet()
				.stream()
				.collect(Collectors.groupingBy(
						m -> Integer.parseInt(DateUtils.convertDateFormat("yyyyMMddHHmmss", "HH", m, Locale.KOREA)),
						Collectors.counting()));

		for (int i = 0; i < 24; i++) {
			if (!result.containsKey(i)) {
				result.put(i, 0L);
			}
		}

		return result;
	}

	@Override
	public <T extends MonitoringResult> Map<Integer, List<String>> getMonitoringReportTimesByTime(Class<T> clazz,
			String alias, FileSize unit, int round, String inquiryDate) {

		Map<Integer, List<String>> result = getMonitoringReportDataByTime(clazz, alias, unit, round, inquiryDate)
				.keySet()
				.stream()
				.collect(Collectors.groupingBy(
						m -> Integer.parseInt(DateUtils.convertDateFormat("yyyyMMddHHmmss", "HH", m, Locale.KOREA)),
						LinkedHashMap::new,
						Collectors.mapping(m -> m, Collectors.toList())));

		for (int i = 0; i < 24; i++) {
			if (!result.containsKey(i)) {
				result.put(i, new ArrayList<>());
			}
		}

		return result;
	}

	@Override
	public <T extends MonitoringResult> List<String> getMonitoringHistoryDays(Class<T> clazz, String alias) {
		return getMonitoringReportData(clazz, alias, FileSize.B, 2)
				.stream()
				.map(r -> r.getMonitoringDate())
				.collect(Collectors.toList());
	}
}
