package root.common.server.implement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

public class AlertLogCommandTest {

	public static int readLine = 1000;
	public static String readFilePath = "/alertLog.txt";
	public static String[] catchErrorMsg = new String[] { "1", "2" };

	@Test
	public void testConstructor1() {
		new AlertLogCommand();
	}

	@Test
	public void testConstructor2() {
		new AlertLogCommand(readLine, readFilePath);
	}

	@Test
	public void testConstructor3() {
		new AlertLogCommand(readLine, readFilePath, catchErrorMsg);
	}

	@Test
	public void testGetterSetter() {
		AlertLogCommand alc = new AlertLogCommand();
		alc.setReadLine(readLine);
		alc.setReadFilePath(readFilePath);
		alc.setCatchErrorMsg(catchErrorMsg);
		assertEquals(readLine, alc.getReadLine());
		assertEquals(readFilePath, alc.getReadFilePath());
		assertEquals(catchErrorMsg, alc.getCatchErrorMsg());
	}

	@Test
	public void testToString() {
		AlertLogCommand alc = new AlertLogCommand(readLine, readFilePath, catchErrorMsg);
		String expected = "AlertLogCommand(readLine=" + alc.getReadLine() + ", readFilePath=" + alc.getReadFilePath()
				+ ", catchErrorMsg=" + Arrays.toString(alc.getCatchErrorMsg()) + ")";
		assertEquals(expected, alc.toString());
	}
	
	@Test
	public void testEquals() {
		AlertLogCommand alc1 = new AlertLogCommand(readLine, readFilePath, catchErrorMsg);
		AlertLogCommand alc2 = new AlertLogCommand(readLine, readFilePath, catchErrorMsg);

		assertTrue(alc1.equals(alc1));
		assertTrue(alc1.equals(alc2));
		assertFalse(alc1.equals(new Object()));
		
		alc1 = null;
		assertFalse(alc2.equals(alc1));
		assertFalse(alc2.equals(new Object()));
	}
	
	@Test
	public void testHashCode() {
		AlertLogCommand alc = new AlertLogCommand(readLine, readFilePath, catchErrorMsg);
		alc.hashCode();
	}
}
