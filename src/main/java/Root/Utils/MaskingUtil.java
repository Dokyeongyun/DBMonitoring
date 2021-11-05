package com.daiso.adflow.utils;

public class MaskingUtil {

	public static final String defaultMaskingSymbol = "*";
	public static final String defaultSeparator = "-";

	/**
	 * 주민등록번호 마스킹
	 * ※ 주민등록번호는 13자리 고정
	 * ■ 기준 : 마지막 6자리 마스킹
	 * 
	 * @param rrn 주민등록번호 (13자리, 포맷무관)
	 * @return ex) 123456-1****** (구분자 "-", 마스킹 기호 "*")
	 */
	public static String maskingRRN(String rrn) {
		return maskingRRN(rrn, defaultMaskingSymbol, defaultSeparator);
	}

	/**
	 * 주민등록번호 마스킹
	 * ※ 주민등록번호는 13자리 고정
	 * ■ 기준 : 마지막 6자리 마스킹
	 * 
	 * @param rrn           주민등록번호 (13자리, 포맷무관)
	 * @param maskingSymbol 마스킹 기호
	 * @param seperator     구분자
	 * @return ex) 123456-1****** (구분자 "-", 마스킹 기호 "*")
	 */
	public static String maskingRRN(String rrn, String maskingSymbol, String separator) { 
		rrn = extractOnlyNumbers(rrn);
		if(!checkValidLength(rrn, 13)) {
			return null;
		}
		rrn = maskingString(rrn, maskingSymbol, 7);
		rrn = divideBySeperator(rrn, separator, 6, 7);
		return rrn;
	}

	/**
	 * 운전면허번호 마스킹
	 * ※ 운전면허번호는 12자리 고정
	 * ■ 기준 : 마지막 6자리 마스킹
	 * 
	 * @param drivingNum 운전면허번호 (12자리, 포맷무관)
	 * @return ex) 11-11-11****-** (구분자 "-", 마스킹 기호 "*")
	 */
	public static String maskingDrivingLicenseNum(String drivingNum) {
		return maskingDrivingLicenseNum(drivingNum, defaultMaskingSymbol, defaultSeparator);
	}

	/**
	 * 운전면허번호 마스킹
	 * ※ 운전면허번호는 12자리 고정
	 * ■ 기준 : 마지막 6자리 마스킹
	 * 
	 * @param drivingNum    운전면허번호 (12자리, 포맷무관)
	 * @param maskingSymbol 마스킹 기호
	 * @param separator     구분자
	 * @return ex) 11-11-11****-** (구분자 "-", 마스킹 기호 "*")
	 */
	public static String maskingDrivingLicenseNum(String drivingNum, String maskingSymbol, String separator) {
		drivingNum = extractOnlyNumbers(drivingNum);
		if(!checkValidLength(drivingNum, 12)) {
			return null;
		}
		drivingNum = maskingString(drivingNum, maskingSymbol, 6);
		drivingNum = divideBySeperator(drivingNum, separator, 2, 2, 6, 2);
		return drivingNum;
	}

	/**
	 * 여권번호 마스킹
	 * ※ 국내 여권번호는 9자리
	 * ■ 기준 : 마지막 4자리 제외 마스킹
	 * 
	 * @param passportNum 여권번호 (포맷무관)
	 * @return ex) *****1234 (마스킹 기호 "*")
	 */
	public static String maskingPassportNum(String passportNum) {
		return maskingPassportNum(passportNum, defaultMaskingSymbol);
	}

	/**
	 * 여권번호 마스킹
	 * ※ 국내 여권번호는 9자리
	 * ■ 기준 : 마지막 4자리 제외 마스킹
	 * 
	 * @param passportNum   여권번호 (포맷무관)
	 * @param maskingSymbol 마스킹 기호
	 * @return ex) *****1234 (마스킹 기호 "*")
	 */
	public static String maskingPassportNum(String passportNum, String maskingSymbol) {
		passportNum = passportNum.trim();
		if(!checkValidLength(passportNum, 9)) {
			return null;
		}
		passportNum = maskingString(passportNum, maskingSymbol, 0, passportNum.length() - 5);
		return passportNum;
	}

	/**
	 * 신용카드번호, 현금영수증카드번호 마스킹
	 * ※ 신용카드번호는 13~16자리
	 * ※ 현금영수증카드번호는 13~19자리
	 * ■ 기준 : 앞 4자리 이후 8자리 마스킹
	 * 
	 * @param creditCardNum 신용카드, 현금영수증카드 번호 (포맷무관)
	 * @return ex) 1234********1234 (마스킹 기호 "*")
	 */
	public static String maskingCreditCardNum(String creditCardNum) {
		return maskingCreditCardNum(creditCardNum, defaultMaskingSymbol);
	}

	/**
	 * 신용카드번호, 현금영수증카드번호 마스킹
	 * ※ 신용카드번호는 13~16자리
	 * ※ 현금영수증카드번호는 13~19자리
	 * ■ 기준 : 앞 4자리 이후 8자리 마스킹
	 * 
	 * @param creditCardNum 신용카드, 현금영수증카드 번호 (포맷무관)
	 * @param maskingSymbol 마스킹 기호
	 * @return ex) 1234********1234 (마스킹 기호 "*")
	 */
	public static String maskingCreditCardNum(String creditCardNum, String maskingSymbol) {
		creditCardNum = extractOnlyNumbers(creditCardNum);
		if(!checkValidLength(creditCardNum, 13, 19)) {
			return null;
		}
		creditCardNum = maskingString(creditCardNum, maskingSymbol, 4, 4 + 8 - 1);
		return creditCardNum;
	}

	/**
	 * 개인통관고유번호 마스킹
	 * ※ 개인통관고유번호는 13자리 고정 ('P' + 12자리 숫자)
	 * ■ 기준 : 앞 3자리 이후 9자리 마스킹
	 * 
	 * @param pccc 개인통관고유번호 (13자리)
	 * @return ex) P12*********4 (마스킹 기호 "*")
	 */
	public static String maskingPCCC(String pccc) {
		return maskingPCCC(pccc, defaultMaskingSymbol);

	}

	/**
	 * 개인통관고유번호 마스킹
	 * ※ 개인통관고유번호는 13자리 고정 ('P' + 12자리 숫자)
	 * ■ 기준 : 앞 3자리 이후 9자리 마스킹
	 * 
	 * @param pccc          개인통관고유번호 (13자리)
	 * @param maskingSymbol 마스킹 기호
	 * @return ex) P12*********4 (마스킹 기호 "*")
	 */
	public static String maskingPCCC(String pccc, String maskingSymbol) {
		pccc = "P" + extractOnlyNumbers(pccc);
		if(!checkValidLength(pccc, 13)) {
			return null;
		}
		pccc = maskingString(pccc, maskingSymbol, 3, 3 + 9 - 1);
		return pccc;
	}

	/**
	 * 계좌번호 마스킹
	 * ※ 계좌번호는 11~14자리
	 * ■ 기준 : 앞 3자리 이후 4자리 마스킹
	 * 
	 * @param accountNum 계좌번호 (포맷무관)
	 * @return ex) 110****81220 (마스킹 기호 "*", 자릿수 은행별 상이)
	 */
	public static String maskingAccountNum(String accountNum) {
		return maskingAccountNum(accountNum, defaultMaskingSymbol);
	}

	/**
	 * 계좌번호 마스킹
	 * ※ 계좌번호는 11~14자리
	 * ■ 기준 : 앞 3자리 이후 4자리 마스킹
	 * 
	 * @param accountNum    계좌번호 (포맷무관)
	 * @param maskingSymbol 마스킹 기호
	 * @return ex) 110****81220 (마스킹 기호 "*", 자릿수 은행별 상이)
	 */
	public static String maskingAccountNum(String accountNum, String maskingSymbol) {
		accountNum = extractOnlyNumbers(accountNum);
		if(!checkValidLength(accountNum, 11, 14)) {
			return null;
		}
		accountNum = maskingString(accountNum, maskingSymbol, 3, 3 + 4 - 1);
		return accountNum;
	}

	/**
	 * 이름(한글) 마스킹
	 * ■ 기준 : 	3자리 이상	- 앞, 뒤 1자리 제외 마스킹
	 * 			2자리 	- 앞 1자리 외 마스킹
	 * 
	 * @param name 이름(한글)
	 * @return ex) 홍**동, 홍*동, 홍* (마스킹 기호 "*")
	 */
	public static String maskingNameInKorean(String name) {
		return maskingNameInKorean(name, defaultMaskingSymbol);
	}

	/**
	 * 이름(한글) 마스킹
	 * ■ 기준 : 	3자리 이상	- 앞, 뒤 1자리 제외 마스킹
	 * 			2자리 	- 앞 1자리 외 마스킹 
	 * 
	 * @param name          이름(한글)
	 * @param maskingSymbol 마스킹 기호
	 * @return ex) 홍**동, 홍*동, 홍* (마스킹 기호 "*")
	 */
	public static String maskingNameInKorean(String name, String maskingSymbol) {
		if (name.length() >= 3) {
			name = maskingString(name, maskingSymbol, 1, name.length() - 1 - 1);
		} else if (name.length() == 2) {
			name = maskingString(name, maskingSymbol, 1);
		}
		return name;
	}

	/**
	 * 이름(영문) 마스킹
	 * ■ 기준 : 앞, 뒤 4자리 제외 마스킹
	 * 
	 * @param name 이름(영문)
	 * @return ex) Hong***dong (마스킹 기호 "*")
	 */
	public static String maskingNameInEnglish(String name) {
		return maskingNameInEnglish(name, defaultMaskingSymbol);
	}

	/**
	 * 이름(영문) 마스킹
	 * ■ 기준 : 앞, 뒤 4자리 제외 마스킹
	 * 
	 * @param name          이름(영문)
	 * @param maskingSymbol 마스킹 기호
	 * @return ex) Hong***dong (마스킹 기호 "*")
	 */
	public static String maskingNameInEnglish(String name, String maskingSymbol) {
		name = maskingString(name, maskingSymbol, 4, name.length() - 4 - 1);	
		return name;
	}

	/**
	 * 아이디 마스킹
	 * ■ 기준 : 	4자리 이상일 경우, 앞 3자리 외 마스킹
	 * 			3자리 이하일 경우, 앞 N-1자리 외 마스킹 (N=길이)
	 * 
	 * @param userId 아이디
	 * @return ex) abc*, ab*, a*, * (마스킹 기호 "*")
	 */
	public static String maskingUserId(String userId) {
		return maskingUserId(userId, defaultMaskingSymbol);
	}

	/**
	 * 아이디 마스킹
	 * ■ 기준 : 	4자리 이상일 경우, 앞 3자리 외 마스킹
	 * 			3자리 이하일 경우, 앞 N-1자리 외 마스킹 (N=길이)
	 * 
	 * @param userId        아이디
	 * @param maskingSymbol 마스킹 기호
	 * @return ex) abc*, ab*, a*, * (마스킹 기호 "*")
	 */
	public static String maskingUserId(String userId, String maskingSymbol) {
		if (userId.length() >= 4) {
			userId = maskingString(userId, maskingSymbol, 3);
		} else {
			userId = maskingString(userId, maskingSymbol, userId.length() - 1);
		}
		return userId;
	}

	/**
	 * 유선 전화번호 마스킹
	 * ■ 기준 : 가운데 국번 뒤 2자리, 뒷자리 국번 뒤 2자리 마스킹
	 * 
	 * @param telNum 유선 전화번호 (포맷무관)
	 * @return ex) 02-12**-12** (구분자 "-", 마스킹 기호 "*")
	 */
	public static String maskingTelNum(String telNum) {
		return maskingTelNum(telNum, defaultMaskingSymbol, defaultSeparator);
	}

	/**
	 * 유선 전화번호 마스킹
	 * ■ 기준 : 가운데 국번 뒤 2자리, 뒷자리 국번 뒤 2자리 마스킹
	 * 
	 * @param telNum        유선 전화번호 (포맷무관)
	 * @param maskingSymbol 마스킹 기호
	 * @param separator     구분자
	 * @return ex) 02-12**-12** (구분자 "-", 마스킹 기호 "*")
	 */
	public static String maskingTelNum(String telNum, String maskingSymbol, String separator) {
		StringBuffer result = new StringBuffer();

		telNum = extractOnlyNumbers(telNum);
		String telFirst = telNum.replaceAll("(^02.{0}|^01.{1}|[0-9]{3})([0-9]+)([0-9]{4})", "$1");
		String telMiddle = telNum.replaceAll("(^02.{0}|^01.{1}|[0-9]{3})([0-9]+)([0-9]{4})", "$2");
		String telLast = telNum.replaceAll("(^02.{0}|^01.{1}|[0-9]{3})([0-9]+)([0-9]{4})", "$3");

		telMiddle = maskingString(telMiddle, maskingSymbol, telMiddle.length()-2 >= 0 ? telMiddle.length()-2 : telMiddle.length());
		telLast = maskingString(telLast, maskingSymbol, telLast.length()-2 >= 0 ? telLast.length()-2 : telLast.length());

		result.append(telFirst).append(separator).append(telMiddle).append(separator).append(telLast);
		return result.toString();
	}

	/**
	 * 휴대폰번호 마스킹
	 * ■ 기준 : 가운데 국번 뒤 2자리, 뒷자리 국번 뒤 2자리 마스킹
	 * 
	 * @param phoneNum 휴대폰 번호 (포맷무관)
	 * @return ex) 010-12**-12** (구분자 "-", 마스킹 기호 "*")
	 */
	public static String maskingPhoneNum(String phoneNum) {
		return maskingPhoneNum(phoneNum, defaultMaskingSymbol, defaultSeparator);
	}

	/**
	 * 휴대폰번호 마스킹
	 * ■ 기준 : 가운데 국번 뒤 2자리, 뒷자리 국번 뒤 2자리 마스킹
	 * 
	 * @param phoneNum      휴대폰 번호 (포맷무관)
	 * @param maskingSymbol 마스킹 기호
	 * @param separator     구분자
	 * @return ex) 010-12**-12** (구분자 "-", 마스킹 기호 "*")
	 */
	public static String maskingPhoneNum(String phoneNum, String maskingSymbol, String separator) {
		StringBuffer result = new StringBuffer();

		phoneNum = extractOnlyNumbers(phoneNum);
		String phoneFirst = phoneNum.replaceAll("(^02.{0}|^01.{1}|[0-9]{3})([0-9]+)([0-9]{4})", "$1");
		String phoneMiddle = phoneNum.replaceAll("(^02.{0}|^01.{1}|[0-9]{3})([0-9]+)([0-9]{4})", "$2");
		String phoneLast = phoneNum.replaceAll("(^02.{0}|^01.{1}|[0-9]{3})([0-9]+)([0-9]{4})", "$3");

		phoneMiddle = maskingString(phoneMiddle, maskingSymbol, phoneMiddle.length()-2 >= 0 ? phoneMiddle.length()-2 : phoneMiddle.length());
		phoneLast = maskingString(phoneLast, maskingSymbol, phoneLast.length()-2 >= 0 ? phoneLast.length()-2 : phoneLast.length());

		result.append(phoneFirst).append(separator).append(phoneMiddle).append(separator).append(phoneLast);
		return result.toString();
	}

	/**
	 * 이메일 마스킹
	 * ■ 기준 : 	@ 기준 앞 ID가 3자리 이상일 경우, 앞 2자리 외 마스킹
	 * 			@ 기준 앞 ID가 2자리 이하일 경우, 앞 N-1자리 외 마스킹 (N=길이)
	 * 
	 * @param email         이메일
	 * @return ex) ab*@daiso.co.kr
	 */
	public static String maskingEmail(String email) {
		return maskingEmail(email, defaultMaskingSymbol);
	}

	/**
	 * 이메일 마스킹
	 * ■ 기준 : 	@ 기준 앞 ID가 3자리 이상일 경우, 앞 2자리 외 마스킹
	 * 			@ 기준 앞 ID가 2자리 이하일 경우, 앞 N-1자리 외 마스킹 (N=길이)
	 * 
	 * @param email         이메일
	 * @param maskingSymbol 마스킹 기호
	 * @return ex) ab*@daiso.co.kr
	 */
	public static String maskingEmail(String email, String maskingSymbol) {
		StringBuffer result = new StringBuffer();

		String[] split = email.split("@");
		String emailFirst = split[0];
		String emailLast = split[1];

		if (emailFirst.length() >= 3) {
			emailFirst = maskingString(emailFirst, maskingSymbol, 2);
		} else {
			emailFirst = maskingString(emailFirst, maskingSymbol, emailFirst.length() - 1);
		}

		result.append(emailFirst).append("@").append(emailLast);
		return result.toString();
	}

	// =============================== MaskingUtil 공통 함수 =============================== //

	/**
	 * 문자열 마스킹
	 * 
	 * @param str           마스킹 대상 문자열
	 * @param maskingSymbol 마스킹 기호
	 * @param from          마스킹 시작 인덱스
	 * @param to            마스킹 종료 인덱스
	 * @return
	 */
	public static String maskingString(String str, String maskingSymbol, int from, int to) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < str.length(); i++) {
			if (i >= from && i <= to) {
				sb.append(maskingSymbol);
			} else {
				sb.append(str.charAt(i));
			}
		}
		return sb.toString();
	}

	/**
	 * 문자열 마스킹 (시작 인덱스부터 문자열의 마지막까지 마스킹)
	 * 
	 * @param str           마스킹 대상 문자열
	 * @param maskingSymbol 마스킹 기호
	 * @param from          마스킹 시작 인덱스
	 * @return
	 */
	public static String maskingString(String str, String maskingSymbol, int from) {
		return maskingString(str, maskingSymbol, from, str.length());
	}

	/**
	 * 문자열에서 숫자만 추출
	 * 
	 * @param str
	 * @return
	 */
	public static String extractOnlyNumbers(String str) {
		return str.replaceAll("[^0-9]", "");
	}

	/**
	 * 문자열을 구분자를 이용하여 분리 
	 * ※ 총 문자열의 길이에 맞추어 입력 권고
	 * ※ 문자열 길이를 초과하거나 부족할 경우, 문자열 길이만큼만 출력 
	 * 
	 * @param str            문자열
	 * @param seperator      구분자
	 * @param lengthOfChunks 구분된 문자열별 문자열 길이
	 * @return
	 */
	public static String divideBySeperator(String str, String seperator, int... lengthOfChunks) {
		StringBuffer result = new StringBuffer();

		int tempIdx = 0;
		for (int i = 0; i < lengthOfChunks.length; i++) {
			int startIdx = tempIdx <= str.length() ? tempIdx : str.length();
			int endIdx = tempIdx + lengthOfChunks[i] <= str.length() ? tempIdx + lengthOfChunks[i] : str.length();
			result.append(str.substring(startIdx, endIdx));
			tempIdx += lengthOfChunks[i];
			if (i != lengthOfChunks.length - 1) {
				result.append(seperator);
			}
		}
		if(tempIdx < str.length()) {
			result.append(str.substring(tempIdx));
		}

		return result.toString();
	}
	
	/**
	 * 문자열의 길이값 확인
	 * 
	 * @param str		문자열
	 * @param length	기대되는 문자열의 길이
	 * @return			조건 충족여부
	 */
	public static boolean checkValidLength(String str, int length) {
		return str.length() == length ? true : false;
	}
	
	/**
	 * 문자열의 길이값 확인 (범위)
	 * 
	 * @param str		문자열
	 * @param from		문자열 자릿수 범위 시작
	 * @param to		문자열 자릿수 범위 끝
	 * @return			조건 충족여부
	 */
	public static boolean checkValidLength(String str, int from, int to) {
		return str.length() >= from && str.length() <= to ? true : false;
	}

}
