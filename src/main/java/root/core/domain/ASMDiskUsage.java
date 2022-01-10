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
public class ASMDiskUsage {

	@CsvBindByName(column = "NAME")
	private String asmDiskGroupName;

	@CsvBindByName(column = "TYPE")
	private String asmDiskGroupType;

	@CsvCustomBindByName(column = "TOTAL_RAW(MB)", converter = UnitStringConverter.class)
	private UnitString totalRawSpace;

	@CsvCustomBindByName(column = "TOTAL_USABLE(MB)", converter = UnitStringConverter.class)
	private UnitString totalFreeSpace;

	@CsvCustomBindByName(column = "FREE(MB)", converter = UnitStringConverter.class)
	private UnitString freeSpace;

	@CsvCustomBindByName(column = "USED(MB)", converter = UnitStringConverter.class)
	private UnitString usedSpace;

	@CsvCustomBindByName(column = "USED(%)", converter = UnitStringConverter.class)
	private UnitString usedPercent;

	@CsvBindByName(column = "RESULT")
	private String resultMsg;

	public static String toCsvString(List<ASMDiskUsage> list) {
		StringBuffer toCsv = new StringBuffer();
		toCsv.append("NAME,TYPE,TOTAL_RAW(MB),TOTAL_USABLE(MB),USED(MB),USED(%),FREE(MB),RESULT").append("\n");

		for (ASMDiskUsage data : list) {
			toCsv.append(data.getAsmDiskGroupName()).append(",");
			toCsv.append(data.getAsmDiskGroupType()).append(",");
			toCsv.append(data.getTotalRawSpace().getValue()).append(data.getTotalRawSpace().getUnit()).append(",");
			toCsv.append(data.getTotalFreeSpace().getValue()).append(data.getTotalFreeSpace().getUnit()).append(",");
			toCsv.append(data.getFreeSpace().getValue()).append(data.getFreeSpace().getUnit()).append(",");
			toCsv.append(data.getUsedSpace().getValue()).append(data.getUsedSpace().getUnit()).append(",");
			toCsv.append(data.getUsedPercent().getValue()).append(data.getUsedPercent().getUnit()).append(",");
			toCsv.append(data.getResultMsg()).append("\n");
		}

		return toCsv.toString();
	}
}
