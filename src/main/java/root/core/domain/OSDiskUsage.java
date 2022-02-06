package root.core.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class OSDiskUsage {

	private String fileSystem;

	private String mountedOn;

	private double totalSpace;

	private double freeSpace;

	private double usedSpace;

	private double usedPercent;

}
