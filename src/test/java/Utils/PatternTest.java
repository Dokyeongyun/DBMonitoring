package Utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;

public class PatternTest {

	private static Pattern JDBC_CONNINFO_KEY_PATTERN = Pattern.compile("(.*).jdbc.(.*)");

	private static Pattern MONITORING_PRESET_KEY_PATTERN = Pattern.compile("monitoring.setting.preset.(.*).filepath");

	@Test
	public void jdbcPropPatternTest() {
		// {dbName}.jdbc

		String s = "erp.jdbc.id";
		Matcher m = JDBC_CONNINFO_KEY_PATTERN.matcher(s);
		if (m.matches()) {
			String dbName = m.group(1);
			assertEquals(dbName, "erp");
		} else {
			fail();
		}
	}

	@Test
	public void monitoringSettingPresetPatternTest() {
		String s = "monitoring.setting.preset.default.filepath";
		Matcher m = MONITORING_PRESET_KEY_PATTERN.matcher(s);

		if (m.matches()) {
			assertEquals(m.group(1), "default");
		} else {
			fail();
		}
	}

}
