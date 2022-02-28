package root.utils;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.Getter;

public class UnitUtils {

	@Getter
	public enum FileSize {
		B("B", 1), KB("KB", 2), MB("MB", 3), GB("GB", 4), TB("TB", 5), PB("PB", 6);

		private String unit;
		private int order;

		private static final Map<String, String> FILESIZE_MAP = Collections
				.unmodifiableMap(Stream.of(values()).collect(Collectors.toMap(FileSize::getUnit, FileSize::name)));

		FileSize(String unit, int order) {
			this.unit = unit;
			this.order = order;
		}

		public static FileSize of(final String unit) {
			try {
				return FileSize.valueOf(FILESIZE_MAP.get(unit));
			} catch (NullPointerException e) {
				return null;
			}
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

	/**
	 * ���� ������ ������ ��ȯ�ϰ�, ������ �ڸ����� �ݿø��Ѵ�.
	 * 
	 * @param beforeUnit
	 * @param afterUnit
	 * @param value
	 * @param round
	 * @return
	 */
	public static double convertFileUnit(FileSize beforeUnit, FileSize afterUnit, double value, int round) {
		double convertValue = UnitUtils.convertFileUnit(beforeUnit, afterUnit, value);
		return Double.valueOf(String.format("%." + round + "f", convertValue));
	}
}
