package Utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import lombok.Getter;

public class UnitUtilsTest {
	
	@Getter
	public enum FileSize {
		B("B", 1), KB("KB", 2), MB("MB", 3), GB("GB", 4), TB("TB", 5), PB("PB", 6);

		private String unit;
		private int order;

		FileSize(String unit, int order) {
			this.unit = unit;
			this.order = order;
		}
	}
	
	@Test
	public void convert_B_to_B() {
		double before = 23.0;
		double after = UnitUtilsTest.convertFileUnit(FileSize.B, FileSize.B, before);
		
		assertEquals(23, after);
	}

	@Test
	public void convert_B_to_KB() {
		double before = 1024.0;
		double after = UnitUtilsTest.convertFileUnit(FileSize.B, FileSize.KB, before);
		
		assertEquals(1, after);
	}
	
	@Test
	public void convert_B_to_MB() {
		double before = 123124144.0;
		double after = UnitUtilsTest.convertFileUnit(FileSize.B, FileSize.MB, before);
		
		assertEquals(117.4203338623046875, after);
	}
	
	@Test
	public void convert_B_to_GB() {
		double before = 5123523.0;
		double after = UnitUtilsTest.convertFileUnit(FileSize.B, FileSize.GB, before);
		
		assertEquals(0.004771652631461620330810546875, after);
	}
	
	@Test
	public void convert_B_to_TB() {
		double before = 23532156213463.0;
		double after = UnitUtilsTest.convertFileUnit(FileSize.B, FileSize.TB, before);
		
		assertEquals(21.402371397437491395976394414902, after);
	}
	
	@Test
	public void convert_B_to_PB() {
		double before = 124124521352.0;
		double after = UnitUtilsTest.convertFileUnit(FileSize.B, FileSize.PB, before);
		
		assertEquals(1.1024472122045381183852441608906e-4, after);
	}
	
	@Test
	public void convert_KB_to_B() {
		double before = 124124521352.0;
		double after = UnitUtilsTest.convertFileUnit(FileSize.KB, FileSize.B, before);
		
		assertEquals(127103509864448.0, after);
	}
	
	@Test
	public void convert_MB_to_B() {
		double before = 1.0;
		double after = UnitUtilsTest.convertFileUnit(FileSize.MB, FileSize.B, before);
		
		assertEquals(1048576.0, after);
	}
	
	@Test
	public void convert_GB_to_B() {
		double before = 0.33;
		double after = UnitUtilsTest.convertFileUnit(FileSize.GB, FileSize.B, before);
		
		assertEquals(354334801.92, after);
	}
	
	public static double convertFileUnit(FileSize beforeUnit, FileSize afterUnit, double value) {
		return beforeUnit == afterUnit ? value : value / Math.pow(1024, (afterUnit.order - beforeUnit.order));
	}
}
