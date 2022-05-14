package root.core.domain.enums;

import lombok.Getter;

@Getter
public enum MonitoringType {

	ARCHIVE("DB", "Archive"), TABLE_SPACE("DB", "TableSpace"), ASM_DISK("DB", "ASM Disk"), OS_DISK("SERVER", "OS Disk"),
	ALERT_LOG("SERVER", "Alert Log");

	private String category;
	private String name;

	MonitoringType(String category, String name) {
		this.category = category;
		this.name = name;
	}
}
