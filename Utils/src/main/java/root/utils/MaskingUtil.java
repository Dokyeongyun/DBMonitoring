package root.utils;


public class MaskingUtil {

	public static final String defaultMaskingSymbol = "*";
	public static final String defaultSeparator = "-";

	/**
	 * μ£Όλ?Όλ±λ‘λ²?Έ λ§μ€?Ή
	 * ?? μ£Όλ?Όλ±λ‘λ²?Έ? 13?λ¦? κ³ μ 
	 * ?  κΈ°μ? : λ§μ?λ§? 6?λ¦? λ§μ€?Ή
	 * ?  κΈΈμ΄μ΄κ³Ό/κΈΈμ΄λΆ?μ‘? ? : ? 7?λ¦? λ§μ€?Ή
	 * 
	 * @param rrn μ£Όλ?Όλ±λ‘λ²?Έ (13?λ¦?, ?¬λ§·λ¬΄κ΄?)
	 * @return ex) 123456-1****** (κ΅¬λΆ? "-", λ§μ€?Ή κΈ°νΈ "*")
	 */
	public static String maskingRRN(String rrn) {
		return maskingRRN(rrn, defaultMaskingSymbol, defaultSeparator);
	}

	/**
	 * μ£Όλ?Όλ±λ‘λ²?Έ λ§μ€?Ή
	 * ?? μ£Όλ?Όλ±λ‘λ²?Έ? 13?λ¦? κ³ μ 
	 * ?  κΈ°μ? : λ§μ?λ§? 6?λ¦? λ§μ€?Ή
	 * ?  κΈΈμ΄μ΄κ³Ό/κΈΈμ΄λΆ?μ‘? ? : ? 7?λ¦? λ§μ€?Ή
	 * 
	 * @param rrn           μ£Όλ?Όλ±λ‘λ²?Έ (13?λ¦?, ?¬λ§·λ¬΄κ΄?)
	 * @param maskingSymbol λ§μ€?Ή κΈ°νΈ
	 * @param separator     κ΅¬λΆ?
	 * @return ex) 123456-1****** (κ΅¬λΆ? "-", λ§μ€?Ή κΈ°νΈ "*")
	 */
	public static String maskingRRN(String rrn, String maskingSymbol, String separator) { 
		rrn = checkNullValue(rrn);
		rrn = extractOnlyNumbers(rrn);
		rrn = maskingString(rrn, maskingSymbol, 7);
		rrn = divideBySeparator(rrn, separator, 6, 7);
		
		return rrn;
	}

	/**
	 * ?΄? λ©΄νλ²νΈ λ§μ€?Ή
	 * ?? ?΄? λ©΄νλ²νΈ? 12?λ¦? κ³ μ 
	 * ?  κΈ°μ? : λ§μ?λ§? 6?λ¦? λ§μ€?Ή
	 * ?  κΈΈμ΄μ΄κ³Ό/κΈΈμ΄λΆ?μ‘? ? : ? 6?λ¦? λ§μ€?Ή
	 * 
	 * @param drivingNum ?΄? λ©΄νλ²νΈ (12?λ¦?, ?¬λ§·λ¬΄κ΄?)
	 * @return ex) 11-11-11****-** (κ΅¬λΆ? "-", λ§μ€?Ή κΈ°νΈ "*")
	 */
	public static String maskingDrivingLicenseNum(String drivingNum) {
		return maskingDrivingLicenseNum(drivingNum, defaultMaskingSymbol, defaultSeparator);
	}

	/**
	 * ?΄? λ©΄νλ²νΈ λ§μ€?Ή
	 * ?? ?΄? λ©΄νλ²νΈ? 12?λ¦? κ³ μ 
	 * ?  κΈ°μ? : λ§μ?λ§? 6?λ¦? λ§μ€?Ή
	 * ?  κΈΈμ΄μ΄κ³Ό/κΈΈμ΄λΆ?μ‘? ? : ? 6?λ¦? λ§μ€?Ή
	 * 
	 * @param drivingNum    ?΄? λ©΄νλ²νΈ (12?λ¦?, ?¬λ§·λ¬΄κ΄?)
	 * @param maskingSymbol λ§μ€?Ή κΈ°νΈ
	 * @param separator     κ΅¬λΆ?
	 * @return ex) 11-11-11****-** (κ΅¬λΆ? "-", λ§μ€?Ή κΈ°νΈ "*")
	 */
	public static String maskingDrivingLicenseNum(String drivingNum, String maskingSymbol, String separator) {
		drivingNum = checkNullValue(drivingNum);
		drivingNum = extractOnlyNumbers(drivingNum);
		drivingNum = maskingString(drivingNum, maskingSymbol, 6);
		drivingNum = divideBySeparator(drivingNum, separator, 2, 2, 6, 2);
		return drivingNum;
	}

	/**
	 * ?¬κΆλ²?Έ λ§μ€?Ή
	 * ?? κ΅??΄ ?¬κΆλ²?Έ? 9?λ¦?
	 * ?  κΈ°μ? : λ§μ?λ§? 4?λ¦? ? ?Έ λ§μ€?Ή 
	 * ?  κΈΈμ΄μ΄κ³Ό/κΈΈμ΄λΆ?μ‘? ? : ? 5?λ¦? λ§μ€?Ή
	 * 
	 * @param passportNum ?¬κΆλ²?Έ (?¬λ§·λ¬΄κ΄?)
	 * @return ex) *****1234 (λ§μ€?Ή κΈ°νΈ "*")
	 */
	public static String maskingPassportNum(String passportNum) {
		return maskingPassportNum(passportNum, defaultMaskingSymbol);
	}

	/**
	 * ?¬κΆλ²?Έ λ§μ€?Ή
	 * ?? κ΅??΄ ?¬κΆλ²?Έ? 9?λ¦?
	 * ?  κΈ°μ? : λ§μ?λ§? 4?λ¦? ? ?Έ λ§μ€?Ή 
	 * ?  κΈΈμ΄μ΄κ³Ό/κΈΈμ΄λΆ?μ‘? ? : ? 5?λ¦? λ§μ€?Ή
	 * 
	 * @param passportNum   ?¬κΆλ²?Έ (?¬λ§·λ¬΄κ΄?)
	 * @param maskingSymbol λ§μ€?Ή κΈ°νΈ
	 * @return ex) *****1234 (λ§μ€?Ή κΈ°νΈ "*")
	 */
	public static String maskingPassportNum(String passportNum, String maskingSymbol) {
		passportNum = checkNullValue(passportNum);
		passportNum = passportNum.replace(" ", "");
		passportNum = maskingString(passportNum, maskingSymbol, 0, 4);
		return passportNum;
	}

	/**
	 * ? ?©μΉ΄λλ²νΈ, ?κΈμ?μ¦μΉ΄?λ²νΈ λ§μ€?Ή
	 * ?? ? ?©μΉ΄λλ²νΈ? 13~16?λ¦?
	 * ?? ?κΈμ?μ¦μΉ΄?λ²νΈ? 13~19?λ¦?
	 * ?  κΈ°μ? : ? 4?λ¦? ?΄? 8?λ¦? λ§μ€?Ή
	 * ?  κΈΈμ΄μ΄κ³Ό/κΈΈμ΄λΆ?μ‘? ? : ? 4?λ¦? ?΄? λͺ¨λ λ§μ€?Ή
	 * 
	 * @param creditCardNum ? ?©μΉ΄λ, ?κΈμ?μ¦μΉ΄? λ²νΈ (?¬λ§·λ¬΄κ΄?)
	 * @return ex) 1234********1234 (λ§μ€?Ή κΈ°νΈ "*")
	 */
	public static String maskingCreditCardNum(String creditCardNum) {
		return maskingCreditCardNum(creditCardNum, defaultMaskingSymbol);
	}

	/**
	 * ? ?©μΉ΄λλ²νΈ, ?κΈμ?μ¦μΉ΄?λ²νΈ λ§μ€?Ή
	 * ?? ? ?©μΉ΄λλ²νΈ? 13~16?λ¦?
	 * ?? ?κΈμ?μ¦μΉ΄?λ²νΈ? 13~19?λ¦?
	 * ?  κΈ°μ? : ? 4?λ¦? ?΄? 8?λ¦? λ§μ€?Ή
	 * ?  κΈΈμ΄μ΄κ³Ό/κΈΈμ΄λΆ?μ‘? ? : ? 4?λ¦? ?΄? λͺ¨λ λ§μ€?Ή
	 * 
	 * @param creditCardNum ? ?©μΉ΄λ, ?κΈμ?μ¦μΉ΄? λ²νΈ (?¬λ§·λ¬΄κ΄?)
	 * @param maskingSymbol λ§μ€?Ή κΈ°νΈ
	 * @return ex) 1234********1234 (λ§μ€?Ή κΈ°νΈ "*")
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
	 * κ°μΈ?΅κ΄?κ³ μ λ²νΈ λ§μ€?Ή
	 * ?? κ°μΈ?΅κ΄?κ³ μ λ²νΈ? 13?λ¦? κ³ μ  ('P' + 12?λ¦? ?«?)
	 * ?  κΈ°μ? : ? 3?λ¦? ?΄? 9?λ¦? λ§μ€?Ή
	 * ?  κΈΈμ΄μ΄κ³Ό/κΈΈμ΄λΆ?μ‘? ? : ? 3?λ¦? ?΄? λͺ¨λ λ§μ€?Ή
	 * 
	 * @param pccc κ°μΈ?΅κ΄?κ³ μ λ²νΈ (13?λ¦?)
	 * @return ex) P12*********4 (λ§μ€?Ή κΈ°νΈ "*")
	 */
	public static String maskingPCCC(String pccc) {
		return maskingPCCC(pccc, defaultMaskingSymbol);
	}

	/**
	 * κ°μΈ?΅κ΄?κ³ μ λ²νΈ λ§μ€?Ή
	 * ?? κ°μΈ?΅κ΄?κ³ μ λ²νΈ? 13?λ¦? κ³ μ  ('P' + 12?λ¦? ?«?)
	 * ?  κΈ°μ? : ? 3?λ¦? ?΄? 9?λ¦? λ§μ€?Ή
	 * ?  κΈΈμ΄μ΄κ³Ό/κΈΈμ΄λΆ?μ‘? ? : ? 3?λ¦? ?΄? λͺ¨λ λ§μ€?Ή
	 * 
	 * @param pccc          κ°μΈ?΅κ΄?κ³ μ λ²νΈ (13?λ¦?)
	 * @param maskingSymbol λ§μ€?Ή κΈ°νΈ
	 * @return ex) P12*********4 (λ§μ€?Ή κΈ°νΈ "*")
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
	 * κ³μ’λ²νΈ λ§μ€?Ή
	 * ?? κ³μ’λ²νΈ? 11~14?λ¦?
	 * ?  κΈ°μ? : ? 3?λ¦? ?΄? 4?λ¦? λ§μ€?Ή
	 * ?  κΈΈμ΄μ΄κ³Ό/κΈΈμ΄λΆ?μ‘? ? : ? 3?λ¦? ?΄? λͺ¨λ λ§μ€?Ή
	 * 
	 * @param accountNum κ³μ’λ²νΈ (?¬λ§·λ¬΄κ΄?)
	 * @return ex) 110****81220 (λ§μ€?Ή κΈ°νΈ "*", ?λ¦Ώμ ???λ³? ??΄)
	 */
	public static String maskingAccountNum(String accountNum) {
		return maskingAccountNum(accountNum, defaultMaskingSymbol);
	}

	/**
	 * κ³μ’λ²νΈ λ§μ€?Ή
	 * ?? κ³μ’λ²νΈ? 11~14?λ¦?
	 * ?  κΈ°μ? : ? 3?λ¦? ?΄? 4?λ¦? λ§μ€?Ή
	 * ?  κΈΈμ΄μ΄κ³Ό/κΈΈμ΄λΆ?μ‘? ? : ? 3?λ¦? ?΄? λͺ¨λ λ§μ€?Ή
	 * 
	 * @param accountNum    κ³μ’λ²νΈ (?¬λ§·λ¬΄κ΄?)
	 * @param maskingSymbol λ§μ€?Ή κΈ°νΈ
	 * @return ex) 110****81220 (λ§μ€?Ή κΈ°νΈ "*", ?λ¦Ώμ ???λ³? ??΄)
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
	 * ?΄λ¦? λ§μ€?Ή (?κΈ?/?λ¬?)
	 * ?  ?? ₯κ°μ ?κΈ??΄ ?¬?¨??΄ ??Όλ©? ?κΈ?λͺμΌλ‘? ??¨?λ©? ?κΈ??΄ ?¬?¨??΄?μ§? ???κ²½μ° ?λ¬Έλͺ?Όλ‘? ??¨?¨  
	 * 
	 * @param name ?΄λ¦?
	 * @return 
	 */
	public static String maskingName(String name) {
		return maskingName(name, defaultMaskingSymbol);
	}
	
	/**
	 * ?΄λ¦? λ§μ€?Ή (?κΈ?/?λ¬?)
	 * ?  ?? ₯κ°μ ?κΈ??΄ ?¬?¨??΄ ??Όλ©? ?κΈ?λͺμΌλ‘? ??¨?λ©? ?κΈ??΄ ?¬?¨??΄?μ§? ???κ²½μ° ?λ¬Έλͺ?Όλ‘? ??¨?¨  
	 * 
	 * @param name ?΄λ¦?
	 * @return 
	 */
	public static String maskingName(String name, String maskingSymbol) {
		name = checkNullValue(name);
		if(name.matches(".*[?±-??-?£κ°?-?£]+.*")) {
			name = maskingNameInKorean(name, maskingSymbol);
		} else {
			name = maskingNameInEnglish(name, maskingSymbol);
		}
		return name;
	}

	/**
	 * ?΄λ¦?(?κΈ?) λ§μ€?Ή
	 * ?  κΈ°μ? : 	3?λ¦? ?΄?	- ?, ?€ 1?λ¦? ? ?Έ λ§μ€?Ή
	 * 			2?λ¦? 	- ? 1?λ¦? ?Έ λ§μ€?Ή
	 * 
	 * @param name ?΄λ¦?(?κΈ?)
	 * @return ex) ?**?, ?*?, ?* (λ§μ€?Ή κΈ°νΈ "*")
	 */
	public static String maskingNameInKorean(String name) {
		return maskingNameInKorean(name, defaultMaskingSymbol);
	}

	/**
	 * ?΄λ¦?(?κΈ?) λ§μ€?Ή
	 * ?  κΈ°μ? : 	3?λ¦? ?΄?	- ?, ?€ 1?λ¦? ? ?Έ λ§μ€?Ή
	 * 			2?λ¦? 	- ? 1?λ¦? ?Έ λ§μ€?Ή 
	 * 
	 * @param name          ?΄λ¦?(?κΈ?)
	 * @param maskingSymbol λ§μ€?Ή κΈ°νΈ
	 * @return ex) ?**?, ?*?, ?* (λ§μ€?Ή κΈ°νΈ "*")
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
	 * ?΄λ¦?(?λ¬?) λ§μ€?Ή
	 * ?  κΈ°μ? : ?, ?€ 4?λ¦? ? ?Έ λ§μ€?Ή
	 * 
	 * @param name ?΄λ¦?(?λ¬?)
	 * @return ex) Hong***dong (λ§μ€?Ή κΈ°νΈ "*")
	 */
	public static String maskingNameInEnglish(String name) {
		return maskingNameInEnglish(name, defaultMaskingSymbol);
	}

	/**
	 * ?΄λ¦?(?λ¬?) λ§μ€?Ή
	 * ?  κΈ°μ? : ?, ?€ 4?λ¦? ? ?Έ λ§μ€?Ή
	 * 
	 * @param name          ?΄λ¦?(?λ¬?)
	 * @param maskingSymbol λ§μ€?Ή κΈ°νΈ
	 * @return ex) Hong***dong (λ§μ€?Ή κΈ°νΈ "*")
	 */
	public static String maskingNameInEnglish(String name, String maskingSymbol) {
		name = checkNullValue(name);
		name = name.replace(" ", "");
		name = maskingString(name, maskingSymbol, 4, name.length() - 4 - 1);	
		return name;
	}

	/**
	 * ??΄? λ§μ€?Ή
	 * ?  κΈ°μ? : 	4?λ¦? ?΄??Ό κ²½μ°, ? 3?λ¦? ?Έ λ§μ€?Ή
	 * 			3?λ¦? ?΄??Ό κ²½μ°, ? N-1?λ¦? ?Έ λ§μ€?Ή (N=κΈΈμ΄)
	 * 
	 * @param userId ??΄?
	 * @return ex) abc*, ab*, a*, * (λ§μ€?Ή κΈ°νΈ "*")
	 */
	public static String maskingUserId(String userId) {
		return maskingUserId(userId, defaultMaskingSymbol);
	}

	/**
	 * ??΄? λ§μ€?Ή
	 * ?  κΈ°μ? : 	4?λ¦? ?΄??Ό κ²½μ°, ? 3?λ¦? ?Έ λ§μ€?Ή
	 * 			3?λ¦? ?΄??Ό κ²½μ°, ? N-1?λ¦? ?Έ λ§μ€?Ή (N=κΈΈμ΄)
	 * 
	 * @param userId        ??΄?
	 * @param maskingSymbol λ§μ€?Ή κΈ°νΈ
	 * @return ex) abc*, ab*, a*, * (λ§μ€?Ή κΈ°νΈ "*")
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
	 * ? ?  ? ?λ²νΈ λ§μ€?Ή
	 * ?  κΈ°μ? : κ°??΄?° κ΅?λ²? ?€ 2?λ¦?, ?·?λ¦? ?€ 2?λ¦? λ§μ€?Ή
	 * 
	 * @param telNum ? ?  ? ?λ²νΈ (?¬λ§·λ¬΄κ΄?)
	 * @return ex) 02-12**-12** (κ΅¬λΆ? "-", λ§μ€?Ή κΈ°νΈ "*")
	 */
	public static String maskingTelNum(String telNum) {
		return maskingTelNum(telNum, defaultMaskingSymbol, defaultSeparator);
	}
	
	/**
	 * ? ?  ? ?λ²νΈ λ§μ€?Ή
	 * ?  κΈ°μ? : κ°??΄?° κ΅?λ²? ?€ 2?λ¦?, ?·?λ¦? ?€ 2?λ¦? λ§μ€?Ή
	 * 
	 * @param telNum1 ? ?  ? ?λ²νΈ1
	 * @param telNum2 ? ?  ? ?λ²νΈ2
	 * @param telNum3 ? ?  ? ?λ²νΈ3
	 * @return ex) 02-12**-12** (κ΅¬λΆ? "-", λ§μ€?Ή κΈ°νΈ "*")
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
	 * ? ?  ? ?λ²νΈ κ°??΄?°?λ¦? λ§μ€?Ή
	 * ?  κΈ°μ? : κ°??΄?° κ΅?λ²? ?€ 2?λ¦? λ§μ€?Ή
	 * 
	 * @param middleTelNum ? ?  ? ?λ²νΈ κ°??΄?°?λ¦?
	 * @return 12**
	 */
	public static String maskingMiddleTelNum(String middleTelNum) {
		middleTelNum = checkNullValue(middleTelNum);
		middleTelNum = extractOnlyNumbers(middleTelNum);
		middleTelNum = maskingString(middleTelNum, defaultMaskingSymbol, middleTelNum.length() - 2);
		return middleTelNum;
	}

	/**
	 * ? ?  ? ?λ²νΈ ?·?λ¦? λ§μ€?Ή
	 * ?  κΈ°μ? : ?·?λ¦? ?€ 2?λ¦? λ§μ€?Ή
	 * 
	 * @param lastTelNum ? ?  ? ?λ²νΈ ?·?λ¦?
	 * @return 12**
	 */
	public static String maskingLastTelNum(String lastTelNum) {
		lastTelNum = checkNullValue(lastTelNum);
		lastTelNum = extractOnlyNumbers(lastTelNum);
		lastTelNum = maskingString(lastTelNum, defaultMaskingSymbol, lastTelNum.length() - 2);
		return lastTelNum;
	}

	/**
	 * ? ?  ? ?λ²νΈ λ§μ€?Ή
	 * ?  κΈ°μ? : κ°??΄?° κ΅?λ²? ?€ 2?λ¦?, ?·?λ¦? ?€ 2?λ¦? λ§μ€?Ή
	 * 
	 * @param telNum        ? ?  ? ?λ²νΈ (?¬λ§·λ¬΄κ΄?)
	 * @param maskingSymbol λ§μ€?Ή κΈ°νΈ
	 * @param separator     κ΅¬λΆ?
	 * @return ex) 02-12**-12** (κ΅¬λΆ? "-", λ§μ€?Ή κΈ°νΈ "*")
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
	 * ?΄???°λ²νΈ λ§μ€?Ή
	 * ?  κΈ°μ? : κ°??΄?° κ΅?λ²? ?€ 2?λ¦?, ?·?λ¦? ?€ 2?λ¦? λ§μ€?Ή
	 * 
	 * @param phoneNum ?΄???° λ²νΈ (?¬λ§·λ¬΄κ΄?)
	 * @return ex) 010-12**-12** (κ΅¬λΆ? "-", λ§μ€?Ή κΈ°νΈ "*")
	 */
	public static String maskingPhoneNum(String phoneNum) {
		return maskingPhoneNum(phoneNum, defaultMaskingSymbol, defaultSeparator);
	}
	
	/**
	 * ?΄???°λ²νΈ λ§μ€?Ή
	 * ?  κΈ°μ? : κ°??΄?° κ΅?λ²? ?€ 2?λ¦?, ?·?λ¦? ?€ 2?λ¦? λ§μ€?Ή
	 * 
	 * @param phoneNum1 ?΄???° λ²νΈ1
	 * @param phoneNum2 ?΄???° λ²νΈ2
	 * @param phoneNum3 ?΄???° λ²νΈ3
	 * @return ex) 010-12**-12** (κ΅¬λΆ? "-", λ§μ€?Ή κΈ°νΈ "*")
	 */
	public static String maskingPhoneNum2(String phoneNum1, String phoneNum2, String phoneNum3) {
		StringBuffer sb = new StringBuffer();
		sb.append(phoneNum1).append(phoneNum2).append(phoneNum3);
		return maskingTelNum(sb.toString(), defaultMaskingSymbol, defaultSeparator);	
	}
	
	/**
	 * ?΄???°λ²νΈ κ°??΄?°?λ¦? λ§μ€?Ή
	 * ?  κΈ°μ? : κ°??΄?° κ΅?λ²? ?€ 2?λ¦? λ§μ€?Ή
	 * 
	 * @param middlePhoneNum ?΄???°λ²νΈ κ°??΄?°?λ¦?
	 * @return 12**
	 */
	public static String maskingMiddlePhoneNum(String middlePhoneNum) {
		middlePhoneNum = checkNullValue(middlePhoneNum);
		middlePhoneNum = extractOnlyNumbers(middlePhoneNum);
		middlePhoneNum = maskingString(middlePhoneNum, defaultMaskingSymbol, middlePhoneNum.length() - 2);
		return middlePhoneNum;
	}

	/**
	 * ?΄???°λ²νΈ ?·?λ¦? λ§μ€?Ή
	 * ?  κΈ°μ? : ?·?λ¦? ?€ 2?λ¦? λ§μ€?Ή
	 * 
	 * @param lastPhoneNum ?΄???°λ²νΈ ?·?λ¦?
	 * @return 12**
	 */
	public static String maskingLastPhoneNum(String lastPhoneNum) {
		lastPhoneNum = checkNullValue(lastPhoneNum);
		lastPhoneNum = extractOnlyNumbers(lastPhoneNum);
		lastPhoneNum = maskingString(lastPhoneNum, defaultMaskingSymbol, lastPhoneNum.length() - 2);
		return lastPhoneNum;
	}

	/**
	 * ?΄???°λ²νΈ λ§μ€?Ή
	 * ?  κΈ°μ? : κ°??΄?° κ΅?λ²? ?€ 2?λ¦?, ?·?λ¦? ?€ 2?λ¦? λ§μ€?Ή
	 * 
	 * @param phoneNum      ?΄???° λ²νΈ (?¬λ§·λ¬΄κ΄?)
	 * @param maskingSymbol λ§μ€?Ή κΈ°νΈ
	 * @param separator     κ΅¬λΆ?
	 * @return ex) 010-12**-12** (κ΅¬λΆ? "-", λ§μ€?Ή κΈ°νΈ "*")
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
	 * ?΄λ©μΌ λ§μ€?Ή
	 * ?  κΈ°μ? : 	@ κΈ°μ? ? IDκ°? 3?λ¦? ?΄??Ό κ²½μ°, ? 2?λ¦? ?Έ λ§μ€?Ή
	 * 			@ κΈ°μ? ? IDκ°? 2?λ¦? ?΄??Ό κ²½μ°, ? N-1?λ¦? ?Έ λ§μ€?Ή (N=κΈΈμ΄)
	 * 
	 * @param email         ?΄λ©μΌ
	 * @return ex) ab*@daiso.co.kr
	 */
	public static String maskingEmail(String email) {
		return maskingEmail(email, defaultMaskingSymbol);
	}

	/**
	 * ?΄λ©μΌ λ§μ€?Ή
	 * ?  κΈ°μ? : 	@ κΈ°μ? ? IDκ°? 3?λ¦? ?΄??Ό κ²½μ°, ? 2?λ¦? ?Έ λ§μ€?Ή
	 * 			@ κΈ°μ? ? IDκ°? 2?λ¦? ?΄??Ό κ²½μ°, ? N-1?λ¦? ?Έ λ§μ€?Ή (N=κΈΈμ΄)
	 * 
	 * @param email         ?΄λ©μΌ
	 * @param maskingSymbol λ§μ€?Ή κΈ°νΈ
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

	// =============================== MaskingUtil κ³΅ν΅ ?¨? =============================== //

	/**
	 * λ¬Έμ?΄ λ§μ€?Ή
	 * 
	 * @param str           λ§μ€?Ή ??? λ¬Έμ?΄
	 * @param maskingSymbol λ§μ€?Ή κΈ°νΈ
	 * @param from          λ§μ€?Ή ?? ?Έ?±?€
	 * @param to            λ§μ€?Ή μ’λ£ ?Έ?±?€
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
	 * λ¬Έμ?΄ λ§μ€?Ή (?? ?Έ?±?€λΆ??° λ¬Έμ?΄? λ§μ?λ§κΉμ§? λ§μ€?Ή)
	 * 
	 * @param str           λ§μ€?Ή ??? λ¬Έμ?΄
	 * @param maskingSymbol λ§μ€?Ή κΈ°νΈ
	 * @param from          λ§μ€?Ή ?? ?Έ?±?€
	 * @return
	 */
	public static String maskingString(String str, String maskingSymbol, int from) {
		return maskingString(str, maskingSymbol, from, str.length());
	}

	/**
	 * λ¬Έμ?΄?? ?«?λ§? μΆμΆ
	 * 
	 * @param str
	 * @return
	 */
	public static String extractOnlyNumbers(String str) {
		return str.replaceAll("[^0-9]", "");
	}

	/**
	 * λ¬Έμ?΄? κ΅¬λΆ?λ₯? ?΄?©??¬ λΆλ¦¬ 
	 * ?? μ΄? λ¬Έμ?΄? κΈΈμ΄? λ§μΆ?΄ ?? ₯ κΆκ³ 
	 * ?? λ¬Έμ?΄ κΈΈμ΄λ₯? μ΄κ³Ό?κ±°λ λΆ?μ‘±ν  κ²½μ°, λ¬Έμ?΄ κΈΈμ΄λ§νΌλ§? μΆλ ₯ 
	 * 
	 * @param str            λ¬Έμ?΄
	 * @param separator      κ΅¬λΆ?
	 * @param lengthOfChunks κ΅¬λΆ? λ¬Έμ?΄λ³? λ¬Έμ?΄ κΈΈμ΄ (0?? λ¬΄μ?©??€.)
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
	 * λ¬Έμ?΄? κΈΈμ΄κ°? ??Έ
	 * 
	 * @param str		λ¬Έμ?΄
	 * @param length	κΈ°λ??? λ¬Έμ?΄? κΈΈμ΄
	 * @return			μ‘°κ±΄ μΆ©μ‘±?¬λΆ?
	 */
	public static boolean checkValidLength(String str, int length) {
		return str.length() == length ? true : false;
	}
	
	/**
	 * λ¬Έμ?΄? κΈΈμ΄κ°? ??Έ (λ²μ)
	 * 
	 * @param str		λ¬Έμ?΄
	 * @param from		λ¬Έμ?΄ ?λ¦Ώμ λ²μ ??
	 * @param to		λ¬Έμ?΄ ?λ¦Ώμ λ²μ ?
	 * @return			μ‘°κ±΄ μΆ©μ‘±?¬λΆ?
	 */
	public static boolean checkValidLength(String str, int from, int to) {
		return str.length() >= from && str.length() <= to ? true : false;
	}
	
	/**
	 * ?? ₯κ°μ NULL ?¬λΆ? ??¨
	 * NULL?Ό κ²½μ°, κ³΅λ°± λ°ν
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
