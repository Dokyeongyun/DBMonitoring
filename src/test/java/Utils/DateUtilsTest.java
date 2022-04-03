package Utils;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import root.utils.DateUtils;

public class DateUtilsTest {

	@Test
	public void parseTest() {

		List<String> dateStringList = new ArrayList<>();
		dateStringList.add("2022-01-01T04:42:24.005764+09:00");
		dateStringList.add("Fri Apr 01 01:37:34 2022");
		dateStringList.add("2022-04-03");
		dateStringList.add("2022/04/03");
		dateStringList.add("2022-04-03 16:10:01");
		dateStringList.add("2022/04/03 16:10:01");

		for (String dateString : dateStringList) {
			LocalDate date = DateUtils.parse(dateString);
			assertNotNull(date);
		}
	}
}
