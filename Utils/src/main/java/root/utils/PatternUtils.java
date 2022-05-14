package root.utils;

import java.util.regex.Pattern;

public class PatternUtils {
	public static final Pattern IS_ONLY_NUMBER = Pattern.compile("^[0-9]*?");

	public static boolean isOnlyNumber(final String str) {
		if (str == "") {
			return false;
		}

		return IS_ONLY_NUMBER.matcher(str).matches();
	}
}
