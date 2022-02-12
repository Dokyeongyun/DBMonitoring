package root.utils;

import lombok.Getter;

public class UnitUtils {

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

	/**
	 * 파일 사이즈 단위를 변환한다.
	 * 
	 * @param beforeUnit 변환 전 단위
	 * @param afterUnit  변환 후 단위
	 * @param value      변환할 값
	 * @return
	 */
	public static double convertFileUnit(FileSize beforeUnit, FileSize afterUnit, double value) {
		return beforeUnit == afterUnit ? value : value / Math.pow(1024, (afterUnit.order - beforeUnit.order));
	}
}
