package root.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class NumberUnitUtils {
	
	@AllArgsConstructor
	@Getter
	public enum Unit {
		Byte("B", 1),
		KiloByte("KB", 2),
		MegaByte("MB", 3),
		GigaByte("GB", 4),
		TeraByte("TB", 5);
		
		private String unitString;
		private int sizeOrder;
	}

	public static double toByteValue(Unit orgUnit, double value) {
		return value * 1024 * orgUnit.sizeOrder;
	}
}
