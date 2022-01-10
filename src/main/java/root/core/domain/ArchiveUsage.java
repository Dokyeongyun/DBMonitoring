package root.core.domain;

import java.util.List;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvCustomBindByName;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import root.javafx.CustomView.UnitStringConverter;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ArchiveUsage {

	@CsvBindByName(column = "NAME")
	private String archiveName;

	@CsvBindByName(column = "NumberOfFiles")
	private int numberOfFiles;

	@CsvCustomBindByName(column = "LimitSpace(G)", converter = UnitStringConverter.class)
	private UnitString totalSpace;

	@CsvCustomBindByName(column = "ReclaimableSpace(G)", converter = UnitStringConverter.class)
	private UnitString reclaimableSpace;

	@CsvCustomBindByName(column = "UsedSpace(G)", converter = UnitStringConverter.class)
	private UnitString usedSpace;

	@CsvCustomBindByName(column = "Used(%)", converter = UnitStringConverter.class)
	private UnitString usedPercent;
	
	@CsvBindByName(column = "DNT")
	private String dnt;
	
	public static String toCsvString(List<ArchiveUsage> list) {
		StringBuffer toCsv = new StringBuffer();
		toCsv.append("NAME,NumberOfFiles,Used(%),UsedSpace(G),ReclaimableSpace(G),LimitSpace(G),DNT").append("\n");
		
		for(ArchiveUsage data : list) {
			toCsv.append(data.getArchiveName()).append(",");
			toCsv.append(data.getNumberOfFiles()).append(",");
			toCsv.append(data.getUsedPercent().getValue()).append(data.getUsedPercent().getUnit()).append(",");
			toCsv.append(data.getUsedSpace().getValue()).append(data.getUsedSpace().getUnit()).append(",");
			toCsv.append(data.getReclaimableSpace().getValue()).append(data.getReclaimableSpace().getUnit()).append(",");
			toCsv.append(data.getTotalSpace().getValue()).append(data.getTotalSpace().getUnit()).append(",");
			toCsv.append(data.getDnt()).append("\n");
		}
		
		return toCsv.toString();
	}
}