package root.core.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UnitString {
	private double value;
	private String unit;

	public UnitString(String unitString) {

		int suffixIdx = unitString.length();
		if (unitString.endsWith("%")) {
			suffixIdx = unitString.indexOf("%");
		} else if (unitString.endsWith("G")) {
			suffixIdx = unitString.indexOf("G");
		} else if (unitString.endsWith("MB")) {
			suffixIdx = unitString.indexOf("MB");
		} else if (unitString.endsWith("M")) {
			suffixIdx = unitString.indexOf("M");
		} else if (unitString.endsWith("K")) {
			suffixIdx = unitString.indexOf("K");
		}

		this.value = Double.parseDouble(unitString.substring(0, suffixIdx));
		this.unit = unitString.substring(suffixIdx);
	}

	@Override
	public String toString() {
		return String.valueOf(value);
	}
}