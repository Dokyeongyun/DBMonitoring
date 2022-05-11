package root.core.domain.enums;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.Getter;

@Getter
public enum RoundingDigits {

	ONE(1), TWO(2), THREE(3), FOUR(4), FIVE(5);

	private int digits;

	private RoundingDigits(int digits) {
		this.digits = digits;
	}

	private static final Map<Integer, RoundingDigits> roundingDigitsMap = Collections.unmodifiableMap(
			Stream.of(values()).collect(Collectors.toMap(RoundingDigits::getDigits, Function.identity())));

	public static RoundingDigits find(final int digits) {
		return Optional.ofNullable(roundingDigitsMap.get(digits)).orElse(TWO);
	}

	public static RoundingDigits find(final String digits) {
		try {
			return find(Integer.parseInt(digits));
		} catch (NumberFormatException e) {
			return TWO;
		}
	}
}
