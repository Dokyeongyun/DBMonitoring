package root.core.domain;

import com.opencsv.bean.CsvBindByName;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ArchiveUsage {

	@CsvBindByName(column = "NAME")
	private String archiveName;

	@CsvBindByName(column = "NumberOfFiles")
	private int numberOfFiles;

	private double totalSpace;

	private double reclaimableSpace;

	private double usedSpace;

	private double usedPercent;

	private String dnt;

}