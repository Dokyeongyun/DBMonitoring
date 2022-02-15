package root.core.usecase.implement;

import java.util.List;

import lombok.extern.slf4j.Slf4j;
import root.core.domain.MonitoringResult;
import root.core.repository.constracts.ReportRepository;
import root.core.usecase.constracts.ReportUsecase;
import root.utils.CsvUtils;
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
}
