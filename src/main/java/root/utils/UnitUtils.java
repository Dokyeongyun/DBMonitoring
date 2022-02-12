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
	 * ���� ������ ������ ��ȯ�Ѵ�.
	 * 
	 * @param beforeUnit ��ȯ �� ����
	 * @param afterUnit  ��ȯ �� ����
	 * @param value      ��ȯ�� ��
	 * @return
	 */
	public static double convertFileUnit(FileSize beforeUnit, FileSize afterUnit, double value) {
		return beforeUnit == afterUnit ? value : value / Math.pow(1024, (afterUnit.order - beforeUnit.order));
	}
}
