package Root.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UnitString {
	private double value;
	private String unit;
	
	@Override
	public String toString() {
		return String.valueOf(value);
	}
}
