//package Utils;
//
//import static org.junit.Assert.fail;
//import static org.junit.jupiter.api.Assertions.assertEquals;
//
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
//import org.junit.jupiter.api.Test;
//
//public class PatternTest {
//
//	private static Pattern jdbcPropPattern = Pattern.compile("(.*).jdbc.(.*)");
//
//	@Test
//	public void jdbcPropPatternTest() {
//		// {dbName}.jdbc
//
//		String s = "erp.jdbc.id";
//		Matcher m = jdbcPropPattern.matcher(s);
//		if (m.matches()) {
//			String dbName = m.group(1);
//			assertEquals(dbName, "erp");
//		} else {
//			fail();
//		}
//	}
//
//}
