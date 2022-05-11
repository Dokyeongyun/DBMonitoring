package root.core.domain.enums;

import lombok.Getter;

@Getter
public enum UsageStatus {
	NORMAL("cornflowerblue"), DANGEROUS("#ff4141");

	private String color;

	UsageStatus(String color) {
		this.color = color;
	}
}