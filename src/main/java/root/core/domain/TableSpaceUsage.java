package root.core.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TableSpaceUsage {

	private String tableSpaceName;

	private double totalSpace;

	private double freeSpace;

	private double usedSpace;

	private double usedPercent;
}
