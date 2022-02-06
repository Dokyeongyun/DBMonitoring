package root.core.domain;

import java.util.List;

import com.opencsv.bean.CsvBindByName;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ASMDiskUsage {

	@CsvBindByName(column = "NAME")
	private String asmDiskGroupName;

	@CsvBindByName(column = "TYPE")
	private String asmDiskGroupType;

	@CsvBindByName(column = "TOTAL_RAW(MB)")
	private double totalRawSpace;

	@CsvBindByName(column = "TOTAL_USABLE(MB)")
	private double totalFreeSpace;

	@CsvBindByName(column = "FREE(MB)")
	private double freeSpace;

	@CsvBindByName(column = "USED(MB)")
	private double usedSpace;

	@CsvBindByName(column = "USED(%)")
	private double usedPercent;

	@CsvBindByName(column = "RESULT")
	private String resultMsg;

	public static String toCsvString(List<ASMDiskUsage> list) {
		StringBuffer toCsv = new StringBuffer();
		toCsv.append("NAME,TYPE,TOTAL_RAW(MB),TOTAL_USABLE(MB),USED(MB),USED(%),FREE(MB),RESULT").append("\n");

		for (ASMDiskUsage data : list) {
			toCsv.append(data.getAsmDiskGroupName()).append(",");
			toCsv.append(data.getAsmDiskGroupType()).append(",");
			toCsv.append(data.getTotalRawSpace()).append(",");
			toCsv.append(data.getTotalFreeSpace()).append(",");
			toCsv.append(data.getFreeSpace()).append(",");
			toCsv.append(data.getUsedSpace()).append(",");
			toCsv.append(data.getUsedPercent()).append(",");
			toCsv.append(data.getResultMsg()).append("\n");
		}

		return toCsv.toString();
	}
}
