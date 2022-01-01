package root.utils;

import root.core.domain.UnitString;

public class UnitUtils {

	/**
	 * 130.3G 등의 파일 사이즈 문자열을 FileSize 객체로 변환 후 반환한다.
	 * @param fileSizeString
	 * @return
	 */
	public static UnitString parseFileSizeString(String fileSizeString) {
		int endIdx = fileSizeString.length();;
		for(int i=0; i<fileSizeString.length(); i++) {
			char c = fileSizeString.charAt(i);
			if(c >= '0' && c <= '9' || c == '.') {
				// PASS
			} else {
				endIdx = i;
				break;
			}
		}
		
		double fileSize = Double.valueOf(fileSizeString.substring(0, endIdx));
		String unitString = fileSizeString.substring(endIdx);
		
		return new UnitString(fileSize, unitString);
	}
}
