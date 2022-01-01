package root.utils;


public class MaskingUtil {

	public static final String defaultMaskingSymbol = "*";
	public static final String defaultSeparator = "-";

	/**
	 * 주�?�등록번?�� 마스?��
	 * ?? 주�?�등록번?��?�� 13?���? 고정
	 * ?�� 기�? : 마�?�? 6?���? 마스?��
	 * ?�� 길이초과/길이�?�? ?�� : ?�� 7?���? 마스?��
	 * 
	 * @param rrn 주�?�등록번?�� (13?���?, ?��맷무�?)
	 * @return ex) 123456-1****** (구분?�� "-", 마스?�� 기호 "*")
	 */
	public static String maskingRRN(String rrn) {
		return maskingRRN(rrn, defaultMaskingSymbol, defaultSeparator);
	}

	/**
	 * 주�?�등록번?�� 마스?��
	 * ?? 주�?�등록번?��?�� 13?���? 고정
	 * ?�� 기�? : 마�?�? 6?���? 마스?��
	 * ?�� 길이초과/길이�?�? ?�� : ?�� 7?���? 마스?��
	 * 
	 * @param rrn           주�?�등록번?�� (13?���?, ?��맷무�?)
	 * @param maskingSymbol 마스?�� 기호
	 * @param separator     구분?��
	 * @return ex) 123456-1****** (구분?�� "-", 마스?�� 기호 "*")
	 */
	public static String maskingRRN(String rrn, String maskingSymbol, String separator) { 
		rrn = checkNullValue(rrn);
		rrn = extractOnlyNumbers(rrn);
		rrn = maskingString(rrn, maskingSymbol, 7);
		rrn = divideBySeparator(rrn, separator, 6, 7);
		
		return rrn;
	}

	/**
	 * ?��?��면허번호 마스?��
	 * ?? ?��?��면허번호?�� 12?���? 고정
	 * ?�� 기�? : 마�?�? 6?���? 마스?��
	 * ?�� 길이초과/길이�?�? ?�� : ?�� 6?���? 마스?��
	 * 
	 * @param drivingNum ?��?��면허번호 (12?���?, ?��맷무�?)
	 * @return ex) 11-11-11****-** (구분?�� "-", 마스?�� 기호 "*")
	 */
	public static String maskingDrivingLicenseNum(String drivingNum) {
		return maskingDrivingLicenseNum(drivingNum, defaultMaskingSymbol, defaultSeparator);
	}

	/**
	 * ?��?��면허번호 마스?��
	 * ?? ?��?��면허번호?�� 12?���? 고정
	 * ?�� 기�? : 마�?�? 6?���? 마스?��
	 * ?�� 길이초과/길이�?�? ?�� : ?�� 6?���? 마스?��
	 * 
	 * @param drivingNum    ?��?��면허번호 (12?���?, ?��맷무�?)
	 * @param maskingSymbol 마스?�� 기호
	 * @param separator     구분?��
	 * @return ex) 11-11-11****-** (구분?�� "-", 마스?�� 기호 "*")
	 */
	public static String maskingDrivingLicenseNum(String drivingNum, String maskingSymbol, String separator) {
		drivingNum = checkNullValue(drivingNum);
		drivingNum = extractOnlyNumbers(drivingNum);
		drivingNum = maskingString(drivingNum, maskingSymbol, 6);
		drivingNum = divideBySeparator(drivingNum, separator, 2, 2, 6, 2);
		return drivingNum;
	}

	/**
	 * ?��권번?�� 마스?��
	 * ?? �??�� ?��권번?��?�� 9?���?
	 * ?�� 기�? : 마�?�? 4?���? ?��?�� 마스?�� 
	 * ?�� 길이초과/길이�?�? ?�� : ?�� 5?���? 마스?��
	 * 
	 * @param passportNum ?��권번?�� (?��맷무�?)
	 * @return ex) *****1234 (마스?�� 기호 "*")
	 */
	public static String maskingPassportNum(String passportNum) {
		return maskingPassportNum(passportNum, defaultMaskingSymbol);
	}

	/**
	 * ?��권번?�� 마스?��
	 * ?? �??�� ?��권번?��?�� 9?���?
	 * ?�� 기�? : 마�?�? 4?���? ?��?�� 마스?�� 
	 * ?�� 길이초과/길이�?�? ?�� : ?�� 5?���? 마스?��
	 * 
	 * @param passportNum   ?��권번?�� (?��맷무�?)
	 * @param maskingSymbol 마스?�� 기호
	 * @return ex) *****1234 (마스?�� 기호 "*")
	 */
	public static String maskingPassportNum(String passportNum, String maskingSymbol) {
		passportNum = checkNullValue(passportNum);
		passportNum = passportNum.replace(" ", "");
		passportNum = maskingString(passportNum, maskingSymbol, 0, 4);
		return passportNum;
	}

	/**
	 * ?��?��카드번호, ?��금영?��증카?��번호 마스?��
	 * ?? ?��?��카드번호?�� 13~16?���?
	 * ?? ?��금영?��증카?��번호?�� 13~19?���?
	 * ?�� 기�? : ?�� 4?���? ?��?�� 8?���? 마스?��
	 * ?�� 길이초과/길이�?�? ?�� : ?�� 4?���? ?��?�� 모두 마스?��
	 * 
	 * @param creditCardNum ?��?��카드, ?��금영?��증카?�� 번호 (?��맷무�?)
	 * @return ex) 1234********1234 (마스?�� 기호 "*")
	 */
	public static String maskingCreditCardNum(String creditCardNum) {
		return maskingCreditCardNum(creditCardNum, defaultMaskingSymbol);
	}

	/**
	 * ?��?��카드번호, ?��금영?��증카?��번호 마스?��
	 * ?? ?��?��카드번호?�� 13~16?���?
	 * ?? ?��금영?��증카?��번호?�� 13~19?���?
	 * ?�� 기�? : ?�� 4?���? ?��?�� 8?���? 마스?��
	 * ?�� 길이초과/길이�?�? ?�� : ?�� 4?���? ?��?�� 모두 마스?��
	 * 
	 * @param creditCardNum ?��?��카드, ?��금영?��증카?�� 번호 (?��맷무�?)
	 * @param maskingSymbol 마스?�� 기호
	 * @return ex) 1234********1234 (마스?�� 기호 "*")
	 */
	public static String maskingCreditCardNum(String creditCardNum, String maskingSymbol) {
		creditCardNum = checkNullValue(creditCardNum);
		creditCardNum = extractOnlyNumbers(creditCardNum);
		if(!checkValidLength(creditCardNum, 13, 19)) {
			creditCardNum = maskingString(creditCardNum, maskingSymbol, 4);
		} else {
			creditCardNum = maskingString(creditCardNum, maskingSymbol, 4, 4 + 8 - 1);	
		}
		return creditCardNum;
	}

	/**
	 * 개인?���?고유번호 마스?��
	 * ?? 개인?���?고유번호?�� 13?���? 고정 ('P' + 12?���? ?��?��)
	 * ?�� 기�? : ?�� 3?���? ?��?�� 9?���? 마스?��
	 * ?�� 길이초과/길이�?�? ?�� : ?�� 3?���? ?��?�� 모두 마스?��
	 * 
	 * @param pccc 개인?���?고유번호 (13?���?)
	 * @return ex) P12*********4 (마스?�� 기호 "*")
	 */
	public static String maskingPCCC(String pccc) {
		return maskingPCCC(pccc, defaultMaskingSymbol);
	}

	/**
	 * 개인?���?고유번호 마스?��
	 * ?? 개인?���?고유번호?�� 13?���? 고정 ('P' + 12?���? ?��?��)
	 * ?�� 기�? : ?�� 3?���? ?��?�� 9?���? 마스?��
	 * ?�� 길이초과/길이�?�? ?�� : ?�� 3?���? ?��?�� 모두 마스?��
	 * 
	 * @param pccc          개인?���?고유번호 (13?���?)
	 * @param maskingSymbol 마스?�� 기호
	 * @return ex) P12*********4 (마스?�� 기호 "*")
	 */
	public static String maskingPCCC(String pccc, String maskingSymbol) {
		pccc = checkNullValue(pccc);
		pccc = "P" + extractOnlyNumbers(pccc);
		if(!checkValidLength(pccc, 13)) {
			pccc = maskingString(pccc, maskingSymbol, 3);
		} else {
			pccc = maskingString(pccc, maskingSymbol, 3, 3 + 9 - 1);	
		}
		return pccc;
	}

	/**
	 * 계좌번호 마스?��
	 * ?? 계좌번호?�� 11~14?���?
	 * ?�� 기�? : ?�� 3?���? ?��?�� 4?���? 마스?��
	 * ?�� 길이초과/길이�?�? ?�� : ?�� 3?���? ?��?�� 모두 마스?��
	 * 
	 * @param accountNum 계좌번호 (?��맷무�?)
	 * @return ex) 110****81220 (마스?�� 기호 "*", ?��릿수 ???���? ?��?��)
	 */
	public static String maskingAccountNum(String accountNum) {
		return maskingAccountNum(accountNum, defaultMaskingSymbol);
	}

	/**
	 * 계좌번호 마스?��
	 * ?? 계좌번호?�� 11~14?���?
	 * ?�� 기�? : ?�� 3?���? ?��?�� 4?���? 마스?��
	 * ?�� 길이초과/길이�?�? ?�� : ?�� 3?���? ?��?�� 모두 마스?��
	 * 
	 * @param accountNum    계좌번호 (?��맷무�?)
	 * @param maskingSymbol 마스?�� 기호
	 * @return ex) 110****81220 (마스?�� 기호 "*", ?��릿수 ???���? ?��?��)
	 */
	public static String maskingAccountNum(String accountNum, String maskingSymbol) {
		accountNum = checkNullValue(accountNum);
		accountNum = extractOnlyNumbers(accountNum);
		if(!checkValidLength(accountNum, 11, 14)) {
			accountNum = maskingString(accountNum, maskingSymbol, 3);
		} else {
			accountNum = maskingString(accountNum, maskingSymbol, 3, 3 + 4 - 1);	
		}
		return accountNum;
	}
	
	/**
	 * ?���? 마스?�� (?���?/?���?)
	 * ?�� ?��?��값에 ?���??�� ?��?��?��?�� ?��?���? ?���?명으�? ?��?��?���? ?���??�� ?��?��?��?��?���? ?��??경우 ?��문명?���? ?��?��?��  
	 * 
	 * @param name ?���?
	 * @return 
	 */
	public static String maskingName(String name) {
		return maskingName(name, defaultMaskingSymbol);
	}
	
	/**
	 * ?���? 마스?�� (?���?/?���?)
	 * ?�� ?��?��값에 ?���??�� ?��?��?��?�� ?��?���? ?���?명으�? ?��?��?���? ?���??�� ?��?��?��?��?���? ?��??경우 ?��문명?���? ?��?��?��  
	 * 
	 * @param name ?���?
	 * @return 
	 */
	public static String maskingName(String name, String maskingSymbol) {
		name = checkNullValue(name);
		if(name.matches(".*[?��-?��?��-?���?-?��]+.*")) {
			name = maskingNameInKorean(name, maskingSymbol);
		} else {
			name = maskingNameInEnglish(name, maskingSymbol);
		}
		return name;
	}

	/**
	 * ?���?(?���?) 마스?��
	 * ?�� 기�? : 	3?���? ?��?��	- ?��, ?�� 1?���? ?��?�� 마스?��
	 * 			2?���? 	- ?�� 1?���? ?�� 마스?��
	 * 
	 * @param name ?���?(?���?)
	 * @return ex) ?��**?��, ?��*?��, ?��* (마스?�� 기호 "*")
	 */
	public static String maskingNameInKorean(String name) {
		return maskingNameInKorean(name, defaultMaskingSymbol);
	}

	/**
	 * ?���?(?���?) 마스?��
	 * ?�� 기�? : 	3?���? ?��?��	- ?��, ?�� 1?���? ?��?�� 마스?��
	 * 			2?���? 	- ?�� 1?���? ?�� 마스?�� 
	 * 
	 * @param name          ?���?(?���?)
	 * @param maskingSymbol 마스?�� 기호
	 * @return ex) ?��**?��, ?��*?��, ?��* (마스?�� 기호 "*")
	 */
	public static String maskingNameInKorean(String name, String maskingSymbol) {
		name = checkNullValue(name);
		name = name.replace(" ", "");
		if (name.length() >= 3) {
			name = maskingString(name, maskingSymbol, 1, name.length() - 1 - 1);
		} else if (name.length() == 2) {
			name = maskingString(name, maskingSymbol, 1);
		}
		return name;
	}

	/**
	 * ?���?(?���?) 마스?��
	 * ?�� 기�? : ?��, ?�� 4?���? ?��?�� 마스?��
	 * 
	 * @param name ?���?(?���?)
	 * @return ex) Hong***dong (마스?�� 기호 "*")
	 */
	public static String maskingNameInEnglish(String name) {
		return maskingNameInEnglish(name, defaultMaskingSymbol);
	}

	/**
	 * ?���?(?���?) 마스?��
	 * ?�� 기�? : ?��, ?�� 4?���? ?��?�� 마스?��
	 * 
	 * @param name          ?���?(?���?)
	 * @param maskingSymbol 마스?�� 기호
	 * @return ex) Hong***dong (마스?�� 기호 "*")
	 */
	public static String maskingNameInEnglish(String name, String maskingSymbol) {
		name = checkNullValue(name);
		name = name.replace(" ", "");
		name = maskingString(name, maskingSymbol, 4, name.length() - 4 - 1);	
		return name;
	}

	/**
	 * ?��?��?�� 마스?��
	 * ?�� 기�? : 	4?���? ?��?��?�� 경우, ?�� 3?���? ?�� 마스?��
	 * 			3?���? ?��?��?�� 경우, ?�� N-1?���? ?�� 마스?�� (N=길이)
	 * 
	 * @param userId ?��?��?��
	 * @return ex) abc*, ab*, a*, * (마스?�� 기호 "*")
	 */
	public static String maskingUserId(String userId) {
		return maskingUserId(userId, defaultMaskingSymbol);
	}

	/**
	 * ?��?��?�� 마스?��
	 * ?�� 기�? : 	4?���? ?��?��?�� 경우, ?�� 3?���? ?�� 마스?��
	 * 			3?���? ?��?��?�� 경우, ?�� N-1?���? ?�� 마스?�� (N=길이)
	 * 
	 * @param userId        ?��?��?��
	 * @param maskingSymbol 마스?�� 기호
	 * @return ex) abc*, ab*, a*, * (마스?�� 기호 "*")
	 */
	public static String maskingUserId(String userId, String maskingSymbol) {
		userId = checkNullValue(userId);
		if (userId.length() >= 4) {
			userId = maskingString(userId, maskingSymbol, 3);
		} else {
			userId = maskingString(userId, maskingSymbol, userId.length() - 1);
		}
		return userId;
	}

	/**
	 * ?��?�� ?��?��번호 마스?��
	 * ?�� 기�? : �??��?�� �?�? ?�� 2?���?, ?��?���? ?�� 2?���? 마스?��
	 * 
	 * @param telNum ?��?�� ?��?��번호 (?��맷무�?)
	 * @return ex) 02-12**-12** (구분?�� "-", 마스?�� 기호 "*")
	 */
	public static String maskingTelNum(String telNum) {
		return maskingTelNum(telNum, defaultMaskingSymbol, defaultSeparator);
	}
	
	/**
	 * ?��?�� ?��?��번호 마스?��
	 * ?�� 기�? : �??��?�� �?�? ?�� 2?���?, ?��?���? ?�� 2?���? 마스?��
	 * 
	 * @param telNum1 ?��?�� ?��?��번호1
	 * @param telNum2 ?��?�� ?��?��번호2
	 * @param telNum3 ?��?�� ?��?��번호3
	 * @return ex) 02-12**-12** (구분?�� "-", 마스?�� 기호 "*")
	 */
	public static String maskingTelNum2(String telNum1, String telNum2, String telNum3) {
//		telNum1 = checkNullValue(telNum1);
//		telNum2 = checkNullValue(telNum2);
//		telNum3 = checkNullValue(telNum3);
		StringBuffer sb = new StringBuffer();
		sb.append(telNum1).append(telNum2).append(telNum3);
		return maskingTelNum(sb.toString(), defaultMaskingSymbol, defaultSeparator);
	}
	
	/**
	 * ?��?�� ?��?��번호 �??��?��?���? 마스?��
	 * ?�� 기�? : �??��?�� �?�? ?�� 2?���? 마스?��
	 * 
	 * @param middleTelNum ?��?�� ?��?��번호 �??��?��?���?
	 * @return 12**
	 */
	public static String maskingMiddleTelNum(String middleTelNum) {
		middleTelNum = checkNullValue(middleTelNum);
		middleTelNum = extractOnlyNumbers(middleTelNum);
		middleTelNum = maskingString(middleTelNum, defaultMaskingSymbol, middleTelNum.length() - 2);
		return middleTelNum;
	}

	/**
	 * ?��?�� ?��?��번호 ?��?���? 마스?��
	 * ?�� 기�? : ?��?���? ?�� 2?���? 마스?��
	 * 
	 * @param lastTelNum ?��?�� ?��?��번호 ?��?���?
	 * @return 12**
	 */
	public static String maskingLastTelNum(String lastTelNum) {
		lastTelNum = checkNullValue(lastTelNum);
		lastTelNum = extractOnlyNumbers(lastTelNum);
		lastTelNum = maskingString(lastTelNum, defaultMaskingSymbol, lastTelNum.length() - 2);
		return lastTelNum;
	}

	/**
	 * ?��?�� ?��?��번호 마스?��
	 * ?�� 기�? : �??��?�� �?�? ?�� 2?���?, ?��?���? ?�� 2?���? 마스?��
	 * 
	 * @param telNum        ?��?�� ?��?��번호 (?��맷무�?)
	 * @param maskingSymbol 마스?�� 기호
	 * @param separator     구분?��
	 * @return ex) 02-12**-12** (구분?�� "-", 마스?�� 기호 "*")
	 */
	public static String maskingTelNum(String telNum, String maskingSymbol, String separator) {
		telNum = checkNullValue(telNum);
		telNum = extractOnlyNumbers(telNum);
		String telFirst = telNum.replaceAll("(^02.{0}|^01.{1}|[0-9]{3})([0-9]+)([0-9]{4})", "$1");
		String telMiddle = telNum.replaceAll("(^02.{0}|^01.{1}|[0-9]{3})([0-9]+)([0-9]{4})", "$2");
		String telLast = telNum.replaceAll("(^02.{0}|^01.{1}|[0-9]{3})([0-9]+)([0-9]{4})", "$3");

		telMiddle = maskingString(telMiddle, maskingSymbol, telMiddle.length()-2 >= 0 ? telMiddle.length()-2 : telMiddle.length());
		telLast = maskingString(telLast, maskingSymbol, telLast.length()-2 >= 0 ? telLast.length()-2 : telLast.length());

		telNum = telFirst + telMiddle + telLast;
		telNum = divideBySeparator(telNum, separator, telFirst.length(), telMiddle.length(), telLast.length());
		
		return telNum;
	}

	/**
	 * ?��???��번호 마스?��
	 * ?�� 기�? : �??��?�� �?�? ?�� 2?���?, ?��?���? ?�� 2?���? 마스?��
	 * 
	 * @param phoneNum ?��???�� 번호 (?��맷무�?)
	 * @return ex) 010-12**-12** (구분?�� "-", 마스?�� 기호 "*")
	 */
	public static String maskingPhoneNum(String phoneNum) {
		return maskingPhoneNum(phoneNum, defaultMaskingSymbol, defaultSeparator);
	}
	
	/**
	 * ?��???��번호 마스?��
	 * ?�� 기�? : �??��?�� �?�? ?�� 2?���?, ?��?���? ?�� 2?���? 마스?��
	 * 
	 * @param phoneNum1 ?��???�� 번호1
	 * @param phoneNum2 ?��???�� 번호2
	 * @param phoneNum3 ?��???�� 번호3
	 * @return ex) 010-12**-12** (구분?�� "-", 마스?�� 기호 "*")
	 */
	public static String maskingPhoneNum2(String phoneNum1, String phoneNum2, String phoneNum3) {
		StringBuffer sb = new StringBuffer();
		sb.append(phoneNum1).append(phoneNum2).append(phoneNum3);
		return maskingTelNum(sb.toString(), defaultMaskingSymbol, defaultSeparator);	
	}
	
	/**
	 * ?��???��번호 �??��?��?���? 마스?��
	 * ?�� 기�? : �??��?�� �?�? ?�� 2?���? 마스?��
	 * 
	 * @param middlePhoneNum ?��???��번호 �??��?��?���?
	 * @return 12**
	 */
	public static String maskingMiddlePhoneNum(String middlePhoneNum) {
		middlePhoneNum = checkNullValue(middlePhoneNum);
		middlePhoneNum = extractOnlyNumbers(middlePhoneNum);
		middlePhoneNum = maskingString(middlePhoneNum, defaultMaskingSymbol, middlePhoneNum.length() - 2);
		return middlePhoneNum;
	}

	/**
	 * ?��???��번호 ?��?���? 마스?��
	 * ?�� 기�? : ?��?���? ?�� 2?���? 마스?��
	 * 
	 * @param lastPhoneNum ?��???��번호 ?��?���?
	 * @return 12**
	 */
	public static String maskingLastPhoneNum(String lastPhoneNum) {
		lastPhoneNum = checkNullValue(lastPhoneNum);
		lastPhoneNum = extractOnlyNumbers(lastPhoneNum);
		lastPhoneNum = maskingString(lastPhoneNum, defaultMaskingSymbol, lastPhoneNum.length() - 2);
		return lastPhoneNum;
	}

	/**
	 * ?��???��번호 마스?��
	 * ?�� 기�? : �??��?�� �?�? ?�� 2?���?, ?��?���? ?�� 2?���? 마스?��
	 * 
	 * @param phoneNum      ?��???�� 번호 (?��맷무�?)
	 * @param maskingSymbol 마스?�� 기호
	 * @param separator     구분?��
	 * @return ex) 010-12**-12** (구분?�� "-", 마스?�� 기호 "*")
	 */
	public static String maskingPhoneNum(String phoneNum, String maskingSymbol, String separator) {
		phoneNum = checkNullValue(phoneNum);
		phoneNum = extractOnlyNumbers(phoneNum);

		String phoneFirst = phoneNum.replaceAll("(^02.{0}|^01.{1}|[0-9]{3})([0-9]+)([0-9]{4})", "$1");
		String phoneMiddle = phoneNum.replaceAll("(^02.{0}|^01.{1}|[0-9]{3})([0-9]+)([0-9]{4})", "$2");
		String phoneLast = phoneNum.replaceAll("(^02.{0}|^01.{1}|[0-9]{3})([0-9]+)([0-9]{4})", "$3");

		phoneMiddle = maskingString(phoneMiddle, maskingSymbol, phoneMiddle.length()-2 >= 0 ? phoneMiddle.length()-2 : phoneMiddle.length());
		phoneLast = maskingString(phoneLast, maskingSymbol, phoneLast.length()-2 >= 0 ? phoneLast.length()-2 : phoneLast.length());

		phoneNum = phoneFirst + phoneMiddle + phoneLast;
		phoneNum = divideBySeparator(phoneNum, separator, phoneFirst.length(), phoneMiddle.length(), phoneLast.length());
		return phoneNum;
	}

	/**
	 * ?��메일 마스?��
	 * ?�� 기�? : 	@ 기�? ?�� ID�? 3?���? ?��?��?�� 경우, ?�� 2?���? ?�� 마스?��
	 * 			@ 기�? ?�� ID�? 2?���? ?��?��?�� 경우, ?�� N-1?���? ?�� 마스?�� (N=길이)
	 * 
	 * @param email         ?��메일
	 * @return ex) ab*@daiso.co.kr
	 */
	public static String maskingEmail(String email) {
		return maskingEmail(email, defaultMaskingSymbol);
	}

	/**
	 * ?��메일 마스?��
	 * ?�� 기�? : 	@ 기�? ?�� ID�? 3?���? ?��?��?�� 경우, ?�� 2?���? ?�� 마스?��
	 * 			@ 기�? ?�� ID�? 2?���? ?��?��?�� 경우, ?�� N-1?���? ?�� 마스?�� (N=길이)
	 * 
	 * @param email         ?��메일
	 * @param maskingSymbol 마스?�� 기호
	 * @return ex) ab*@daiso.co.kr
	 */
	public static String maskingEmail(String email, String maskingSymbol) {
		email = checkNullValue(email);
		StringBuffer result = new StringBuffer();

		String[] split = email.split("@", 2);
		String emailId = split[0];
		String emailAddress = split.length >= 2 ? split[1] : "";
		
		if (emailId.length() >= 3) {
			emailId = maskingString(emailId, maskingSymbol, 2);
		} else {
			emailId = maskingString(emailId, maskingSymbol, emailId.length() - 1);
		}
		result.append(emailId).append(emailAddress.isEmpty() ? "" : "@").append(emailAddress);

		return result.toString();
	}

	// =============================== MaskingUtil 공통 ?��?�� =============================== //

	/**
	 * 문자?�� 마스?��
	 * 
	 * @param str           마스?�� ???�� 문자?��
	 * @param maskingSymbol 마스?�� 기호
	 * @param from          마스?�� ?��?�� ?��?��?��
	 * @param to            마스?�� 종료 ?��?��?��
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
	 * 문자?�� 마스?�� (?��?�� ?��?��?���??�� 문자?��?�� 마�?막까�? 마스?��)
	 * 
	 * @param str           마스?�� ???�� 문자?��
	 * @param maskingSymbol 마스?�� 기호
	 * @param from          마스?�� ?��?�� ?��?��?��
	 * @return
	 */
	public static String maskingString(String str, String maskingSymbol, int from) {
		return maskingString(str, maskingSymbol, from, str.length());
	}

	/**
	 * 문자?��?��?�� ?��?���? 추출
	 * 
	 * @param str
	 * @return
	 */
	public static String extractOnlyNumbers(String str) {
		return str.replaceAll("[^0-9]", "");
	}

	/**
	 * 문자?��?�� 구분?���? ?��?��?��?�� 분리 
	 * ?? �? 문자?��?�� 길이?�� 맞추?�� ?��?�� 권고
	 * ?? 문자?�� 길이�? 초과?��거나 �?족할 경우, 문자?�� 길이만큼�? 출력 
	 * 
	 * @param str            문자?��
	 * @param separator      구분?��
	 * @param lengthOfChunks 구분?�� 문자?���? 문자?�� 길이 (0?? 무시?��?��?��.)
	 * @return
	 */
	public static String divideBySeparator(String str, String separator, int... lengthOfChunks) {
		StringBuffer result = new StringBuffer();
		
		int tempIdx = 0;
		for (int i = 0; i < lengthOfChunks.length; i++) {
			if(lengthOfChunks[i] == 0) continue;
			
			int startIdx = tempIdx;
			int endIdx = tempIdx + lengthOfChunks[i];
			
			boolean isShortBetterThanChunkLength = false;
			if(endIdx > str.length()) {
				endIdx = str.length();
				isShortBetterThanChunkLength = true;
			}
			
			result.append(str.substring(startIdx, endIdx));
			tempIdx += lengthOfChunks[i];
			
			if(isShortBetterThanChunkLength) {
				break;
			}

			if (i != lengthOfChunks.length - 1) {
				result.append(separator);
			}
		}
		
		if(tempIdx < str.length()) {
			result.append(str.substring(tempIdx));
		}

		return result.toString();
	}
	
	/**
	 * 문자?��?�� 길이�? ?��?��
	 * 
	 * @param str		문자?��
	 * @param length	기�??��?�� 문자?��?�� 길이
	 * @return			조건 충족?���?
	 */
	public static boolean checkValidLength(String str, int length) {
		return str.length() == length ? true : false;
	}
	
	/**
	 * 문자?��?�� 길이�? ?��?�� (범위)
	 * 
	 * @param str		문자?��
	 * @param from		문자?�� ?��릿수 범위 ?��?��
	 * @param to		문자?�� ?��릿수 범위 ?��
	 * @return			조건 충족?���?
	 */
	public static boolean checkValidLength(String str, int from, int to) {
		return str.length() >= from && str.length() <= to ? true : false;
	}
	
	/**
	 * ?��?��값의 NULL ?���? ?��?��
	 * NULL?�� 경우, 공백 반환
	 * 
	 * @param str
	 * @return
	 */
	public static String checkNullValue(Object obj) {
        String str = "";
        
        if (obj != null) {
    		str = obj.toString();	
        }
        
        return str;
	}

	
	public static String fillRightSpace(String str) {
		int length = 30;
		if(str.length() < length) {
			int fillLength = length - str.length();
			for(int i=0; i<fillLength; i++) {
				str += " ";	
			}
		}
		return str;
	}
}
