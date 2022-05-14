package root.core.domain.enums;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.Getter;

@Getter
public enum UsageUIType implements EnumType {

	NUMERIC("1", "수치 값"), GRAPHIC_BAR("2", "막대형 그래픽"), GRAPHIC_PIE("3", "원형 그래픽");

	private String code;
	private String name;

	private UsageUIType(String code, String name) {
		this.code = code;
		this.name = name;
	}

	private static final Map<String, UsageUIType> UsageUITypeMap = Collections
			.unmodifiableMap(Stream.of(values()).collect(Collectors.toMap(UsageUIType::getCode, Function.identity())));

	public static UsageUIType find(String code) {
		return Optional.ofNullable(UsageUITypeMap.get(code)).orElse(NUMERIC);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public List<String> getNames() {
		return Arrays.asList(values()).stream().map(ui -> ui.getName()).collect(Collectors.toList());
	}
}
