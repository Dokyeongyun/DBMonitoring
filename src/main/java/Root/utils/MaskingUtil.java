package root.utils;


public class MaskingUtil {

	public static final String defaultMaskingSymbol = "*";
	public static final String defaultSeparator = "-";

	/**
	 * ì£¼ë?¼ë“±ë¡ë²ˆ?˜¸ ë§ˆìŠ¤?‚¹
	 * ?? ì£¼ë?¼ë“±ë¡ë²ˆ?˜¸?Š” 13?ë¦? ê³ ì •
	 * ?–  ê¸°ì? : ë§ˆì?ë§? 6?ë¦? ë§ˆìŠ¤?‚¹
	 * ?–  ê¸¸ì´ì´ˆê³¼/ê¸¸ì´ë¶?ì¡? ?‹œ : ?• 7?ë¦? ë§ˆìŠ¤?‚¹
	 * 
	 * @param rrn ì£¼ë?¼ë“±ë¡ë²ˆ?˜¸ (13?ë¦?, ?¬ë§·ë¬´ê´?)
	 * @return ex) 123456-1****** (êµ¬ë¶„? "-", ë§ˆìŠ¤?‚¹ ê¸°í˜¸ "*")
	 */
	public static String maskingRRN(String rrn) {
		return maskingRRN(rrn, defaultMaskingSymbol, defaultSeparator);
	}

	/**
	 * ì£¼ë?¼ë“±ë¡ë²ˆ?˜¸ ë§ˆìŠ¤?‚¹
	 * ?? ì£¼ë?¼ë“±ë¡ë²ˆ?˜¸?Š” 13?ë¦? ê³ ì •
	 * ?–  ê¸°ì? : ë§ˆì?ë§? 6?ë¦? ë§ˆìŠ¤?‚¹
	 * ?–  ê¸¸ì´ì´ˆê³¼/ê¸¸ì´ë¶?ì¡? ?‹œ : ?• 7?ë¦? ë§ˆìŠ¤?‚¹
	 * 
	 * @param rrn           ì£¼ë?¼ë“±ë¡ë²ˆ?˜¸ (13?ë¦?, ?¬ë§·ë¬´ê´?)
	 * @param maskingSymbol ë§ˆìŠ¤?‚¹ ê¸°í˜¸
	 * @param separator     êµ¬ë¶„?
	 * @return ex) 123456-1****** (êµ¬ë¶„? "-", ë§ˆìŠ¤?‚¹ ê¸°í˜¸ "*")
	 */
	public static String maskingRRN(String rrn, String maskingSymbol, String separator) { 
		rrn = checkNullValue(rrn);
		rrn = extractOnlyNumbers(rrn);
		rrn = maskingString(rrn, maskingSymbol, 7);
		rrn = divideBySeparator(rrn, separator, 6, 7);
		
		return rrn;
	}

	/**
	 * ?š´? „ë©´í—ˆë²ˆí˜¸ ë§ˆìŠ¤?‚¹
	 * ?? ?š´? „ë©´í—ˆë²ˆí˜¸?Š” 12?ë¦? ê³ ì •
	 * ?–  ê¸°ì? : ë§ˆì?ë§? 6?ë¦? ë§ˆìŠ¤?‚¹
	 * ?–  ê¸¸ì´ì´ˆê³¼/ê¸¸ì´ë¶?ì¡? ?‹œ : ?• 6?ë¦? ë§ˆìŠ¤?‚¹
	 * 
	 * @param drivingNum ?š´? „ë©´í—ˆë²ˆí˜¸ (12?ë¦?, ?¬ë§·ë¬´ê´?)
	 * @return ex) 11-11-11****-** (êµ¬ë¶„? "-", ë§ˆìŠ¤?‚¹ ê¸°í˜¸ "*")
	 */
	public static String maskingDrivingLicenseNum(String drivingNum) {
		return maskingDrivingLicenseNum(drivingNum, defaultMaskingSymbol, defaultSeparator);
	}

	/**
	 * ?š´? „ë©´í—ˆë²ˆí˜¸ ë§ˆìŠ¤?‚¹
	 * ?? ?š´? „ë©´í—ˆë²ˆí˜¸?Š” 12?ë¦? ê³ ì •
	 * ?–  ê¸°ì? : ë§ˆì?ë§? 6?ë¦? ë§ˆìŠ¤?‚¹
	 * ?–  ê¸¸ì´ì´ˆê³¼/ê¸¸ì´ë¶?ì¡? ?‹œ : ?• 6?ë¦? ë§ˆìŠ¤?‚¹
	 * 
	 * @param drivingNum    ?š´? „ë©´í—ˆë²ˆí˜¸ (12?ë¦?, ?¬ë§·ë¬´ê´?)
	 * @param maskingSymbol ë§ˆìŠ¤?‚¹ ê¸°í˜¸
	 * @param separator     êµ¬ë¶„?
	 * @return ex) 11-11-11****-** (êµ¬ë¶„? "-", ë§ˆìŠ¤?‚¹ ê¸°í˜¸ "*")
	 */
	public static String maskingDrivingLicenseNum(String drivingNum, String maskingSymbol, String separator) {
		drivingNum = checkNullValue(drivingNum);
		drivingNum = extractOnlyNumbers(drivingNum);
		drivingNum = maskingString(drivingNum, maskingSymbol, 6);
		drivingNum = divideBySeparator(drivingNum, separator, 2, 2, 6, 2);
		return drivingNum;
	}

	/**
	 * ?—¬ê¶Œë²ˆ?˜¸ ë§ˆìŠ¤?‚¹
	 * ?? êµ??‚´ ?—¬ê¶Œë²ˆ?˜¸?Š” 9?ë¦?
	 * ?–  ê¸°ì? : ë§ˆì?ë§? 4?ë¦? ? œ?™¸ ë§ˆìŠ¤?‚¹ 
	 * ?–  ê¸¸ì´ì´ˆê³¼/ê¸¸ì´ë¶?ì¡? ?‹œ : ?• 5?ë¦? ë§ˆìŠ¤?‚¹
	 * 
	 * @param passportNum ?—¬ê¶Œë²ˆ?˜¸ (?¬ë§·ë¬´ê´?)
	 * @return ex) *****1234 (ë§ˆìŠ¤?‚¹ ê¸°í˜¸ "*")
	 */
	public static String maskingPassportNum(String passportNum) {
		return maskingPassportNum(passportNum, defaultMaskingSymbol);
	}

	/**
	 * ?—¬ê¶Œë²ˆ?˜¸ ë§ˆìŠ¤?‚¹
	 * ?? êµ??‚´ ?—¬ê¶Œë²ˆ?˜¸?Š” 9?ë¦?
	 * ?–  ê¸°ì? : ë§ˆì?ë§? 4?ë¦? ? œ?™¸ ë§ˆìŠ¤?‚¹ 
	 * ?–  ê¸¸ì´ì´ˆê³¼/ê¸¸ì´ë¶?ì¡? ?‹œ : ?• 5?ë¦? ë§ˆìŠ¤?‚¹
	 * 
	 * @param passportNum   ?—¬ê¶Œë²ˆ?˜¸ (?¬ë§·ë¬´ê´?)
	 * @param maskingSymbol ë§ˆìŠ¤?‚¹ ê¸°í˜¸
	 * @return ex) *****1234 (ë§ˆìŠ¤?‚¹ ê¸°í˜¸ "*")
	 */
	public static String maskingPassportNum(String passportNum, String maskingSymbol) {
		passportNum = checkNullValue(passportNum);
		passportNum = passportNum.replace(" ", "");
		passportNum = maskingString(passportNum, maskingSymbol, 0, 4);
		return passportNum;
	}

	/**
	 * ?‹ ?š©ì¹´ë“œë²ˆí˜¸, ?˜„ê¸ˆì˜?ˆ˜ì¦ì¹´?“œë²ˆí˜¸ ë§ˆìŠ¤?‚¹
	 * ?? ?‹ ?š©ì¹´ë“œë²ˆí˜¸?Š” 13~16?ë¦?
	 * ?? ?˜„ê¸ˆì˜?ˆ˜ì¦ì¹´?“œë²ˆí˜¸?Š” 13~19?ë¦?
	 * ?–  ê¸°ì? : ?• 4?ë¦? ?´?›„ 8?ë¦? ë§ˆìŠ¤?‚¹
	 * ?–  ê¸¸ì´ì´ˆê³¼/ê¸¸ì´ë¶?ì¡? ?‹œ : ?• 4?ë¦? ?´?›„ ëª¨ë‘ ë§ˆìŠ¤?‚¹
	 * 
	 * @param creditCardNum ?‹ ?š©ì¹´ë“œ, ?˜„ê¸ˆì˜?ˆ˜ì¦ì¹´?“œ ë²ˆí˜¸ (?¬ë§·ë¬´ê´?)
	 * @return ex) 1234********1234 (ë§ˆìŠ¤?‚¹ ê¸°í˜¸ "*")
	 */
	public static String maskingCreditCardNum(String creditCardNum) {
		return maskingCreditCardNum(creditCardNum, defaultMaskingSymbol);
	}

	/**
	 * ?‹ ?š©ì¹´ë“œë²ˆí˜¸, ?˜„ê¸ˆì˜?ˆ˜ì¦ì¹´?“œë²ˆí˜¸ ë§ˆìŠ¤?‚¹
	 * ?? ?‹ ?š©ì¹´ë“œë²ˆí˜¸?Š” 13~16?ë¦?
	 * ?? ?˜„ê¸ˆì˜?ˆ˜ì¦ì¹´?“œë²ˆí˜¸?Š” 13~19?ë¦?
	 * ?–  ê¸°ì? : ?• 4?ë¦? ?´?›„ 8?ë¦? ë§ˆìŠ¤?‚¹
	 * ?–  ê¸¸ì´ì´ˆê³¼/ê¸¸ì´ë¶?ì¡? ?‹œ : ?• 4?ë¦? ?´?›„ ëª¨ë‘ ë§ˆìŠ¤?‚¹
	 * 
	 * @param creditCardNum ?‹ ?š©ì¹´ë“œ, ?˜„ê¸ˆì˜?ˆ˜ì¦ì¹´?“œ ë²ˆí˜¸ (?¬ë§·ë¬´ê´?)
	 * @param maskingSymbol ë§ˆìŠ¤?‚¹ ê¸°í˜¸
	 * @return ex) 1234********1234 (ë§ˆìŠ¤?‚¹ ê¸°í˜¸ "*")
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
	 * ê°œì¸?†µê´?ê³ ìœ ë²ˆí˜¸ ë§ˆìŠ¤?‚¹
	 * ?? ê°œì¸?†µê´?ê³ ìœ ë²ˆí˜¸?Š” 13?ë¦? ê³ ì • ('P' + 12?ë¦? ?ˆ«?)
	 * ?–  ê¸°ì? : ?• 3?ë¦? ?´?›„ 9?ë¦? ë§ˆìŠ¤?‚¹
	 * ?–  ê¸¸ì´ì´ˆê³¼/ê¸¸ì´ë¶?ì¡? ?‹œ : ?• 3?ë¦? ?´?›„ ëª¨ë‘ ë§ˆìŠ¤?‚¹
	 * 
	 * @param pccc ê°œì¸?†µê´?ê³ ìœ ë²ˆí˜¸ (13?ë¦?)
	 * @return ex) P12*********4 (ë§ˆìŠ¤?‚¹ ê¸°í˜¸ "*")
	 */
	public static String maskingPCCC(String pccc) {
		return maskingPCCC(pccc, defaultMaskingSymbol);
	}

	/**
	 * ê°œì¸?†µê´?ê³ ìœ ë²ˆí˜¸ ë§ˆìŠ¤?‚¹
	 * ?? ê°œì¸?†µê´?ê³ ìœ ë²ˆí˜¸?Š” 13?ë¦? ê³ ì • ('P' + 12?ë¦? ?ˆ«?)
	 * ?–  ê¸°ì? : ?• 3?ë¦? ?´?›„ 9?ë¦? ë§ˆìŠ¤?‚¹
	 * ?–  ê¸¸ì´ì´ˆê³¼/ê¸¸ì´ë¶?ì¡? ?‹œ : ?• 3?ë¦? ?´?›„ ëª¨ë‘ ë§ˆìŠ¤?‚¹
	 * 
	 * @param pccc          ê°œì¸?†µê´?ê³ ìœ ë²ˆí˜¸ (13?ë¦?)
	 * @param maskingSymbol ë§ˆìŠ¤?‚¹ ê¸°í˜¸
	 * @return ex) P12*********4 (ë§ˆìŠ¤?‚¹ ê¸°í˜¸ "*")
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
	 * ê³„ì¢Œë²ˆí˜¸ ë§ˆìŠ¤?‚¹
	 * ?? ê³„ì¢Œë²ˆí˜¸?Š” 11~14?ë¦?
	 * ?–  ê¸°ì? : ?• 3?ë¦? ?´?›„ 4?ë¦? ë§ˆìŠ¤?‚¹
	 * ?–  ê¸¸ì´ì´ˆê³¼/ê¸¸ì´ë¶?ì¡? ?‹œ : ?• 3?ë¦? ?´?›„ ëª¨ë‘ ë§ˆìŠ¤?‚¹
	 * 
	 * @param accountNum ê³„ì¢Œë²ˆí˜¸ (?¬ë§·ë¬´ê´?)
	 * @return ex) 110****81220 (ë§ˆìŠ¤?‚¹ ê¸°í˜¸ "*", ?ë¦¿ìˆ˜ ???–‰ë³? ?ƒ?´)
	 */
	public static String maskingAccountNum(String accountNum) {
		return maskingAccountNum(accountNum, defaultMaskingSymbol);
	}

	/**
	 * ê³„ì¢Œë²ˆí˜¸ ë§ˆìŠ¤?‚¹
	 * ?? ê³„ì¢Œë²ˆí˜¸?Š” 11~14?ë¦?
	 * ?–  ê¸°ì? : ?• 3?ë¦? ?´?›„ 4?ë¦? ë§ˆìŠ¤?‚¹
	 * ?–  ê¸¸ì´ì´ˆê³¼/ê¸¸ì´ë¶?ì¡? ?‹œ : ?• 3?ë¦? ?´?›„ ëª¨ë‘ ë§ˆìŠ¤?‚¹
	 * 
	 * @param accountNum    ê³„ì¢Œë²ˆí˜¸ (?¬ë§·ë¬´ê´?)
	 * @param maskingSymbol ë§ˆìŠ¤?‚¹ ê¸°í˜¸
	 * @return ex) 110****81220 (ë§ˆìŠ¤?‚¹ ê¸°í˜¸ "*", ?ë¦¿ìˆ˜ ???–‰ë³? ?ƒ?´)
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
	 * ?´ë¦? ë§ˆìŠ¤?‚¹ (?•œê¸?/?˜ë¬?)
	 * ?–  ?…? ¥ê°’ì— ?•œê¸??´ ?¬?•¨?˜?–´ ?ˆ?œ¼ë©? ?•œê¸?ëª…ìœ¼ë¡? ?Œ?‹¨?•˜ë©? ?•œê¸??´ ?¬?•¨?˜?–´?ˆì§? ?•Š??ê²½ìš° ?˜ë¬¸ëª…?œ¼ë¡? ?Œ?‹¨?•¨  
	 * 
	 * @param name ?´ë¦?
	 * @return 
	 */
	public static String maskingName(String name) {
		return maskingName(name, defaultMaskingSymbol);
	}
	
	/**
	 * ?´ë¦? ë§ˆìŠ¤?‚¹ (?•œê¸?/?˜ë¬?)
	 * ?–  ?…? ¥ê°’ì— ?•œê¸??´ ?¬?•¨?˜?–´ ?ˆ?œ¼ë©? ?•œê¸?ëª…ìœ¼ë¡? ?Œ?‹¨?•˜ë©? ?•œê¸??´ ?¬?•¨?˜?–´?ˆì§? ?•Š??ê²½ìš° ?˜ë¬¸ëª…?œ¼ë¡? ?Œ?‹¨?•¨  
	 * 
	 * @param name ?´ë¦?
	 * @return 
	 */
	public static String maskingName(String name, String maskingSymbol) {
		name = checkNullValue(name);
		if(name.matches(".*[?„±-?…?…-?…£ê°?-?£]+.*")) {
			name = maskingNameInKorean(name, maskingSymbol);
		} else {
			name = maskingNameInEnglish(name, maskingSymbol);
		}
		return name;
	}

	/**
	 * ?´ë¦?(?•œê¸?) ë§ˆìŠ¤?‚¹
	 * ?–  ê¸°ì? : 	3?ë¦? ?´?ƒ	- ?•, ?’¤ 1?ë¦? ? œ?™¸ ë§ˆìŠ¤?‚¹
	 * 			2?ë¦? 	- ?• 1?ë¦? ?™¸ ë§ˆìŠ¤?‚¹
	 * 
	 * @param name ?´ë¦?(?•œê¸?)
	 * @return ex) ?™**?™, ?™*?™, ?™* (ë§ˆìŠ¤?‚¹ ê¸°í˜¸ "*")
	 */
	public static String maskingNameInKorean(String name) {
		return maskingNameInKorean(name, defaultMaskingSymbol);
	}

	/**
	 * ?´ë¦?(?•œê¸?) ë§ˆìŠ¤?‚¹
	 * ?–  ê¸°ì? : 	3?ë¦? ?´?ƒ	- ?•, ?’¤ 1?ë¦? ? œ?™¸ ë§ˆìŠ¤?‚¹
	 * 			2?ë¦? 	- ?• 1?ë¦? ?™¸ ë§ˆìŠ¤?‚¹ 
	 * 
	 * @param name          ?´ë¦?(?•œê¸?)
	 * @param maskingSymbol ë§ˆìŠ¤?‚¹ ê¸°í˜¸
	 * @return ex) ?™**?™, ?™*?™, ?™* (ë§ˆìŠ¤?‚¹ ê¸°í˜¸ "*")
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
	 * ?´ë¦?(?˜ë¬?) ë§ˆìŠ¤?‚¹
	 * ?–  ê¸°ì? : ?•, ?’¤ 4?ë¦? ? œ?™¸ ë§ˆìŠ¤?‚¹
	 * 
	 * @param name ?´ë¦?(?˜ë¬?)
	 * @return ex) Hong***dong (ë§ˆìŠ¤?‚¹ ê¸°í˜¸ "*")
	 */
	public static String maskingNameInEnglish(String name) {
		return maskingNameInEnglish(name, defaultMaskingSymbol);
	}

	/**
	 * ?´ë¦?(?˜ë¬?) ë§ˆìŠ¤?‚¹
	 * ?–  ê¸°ì? : ?•, ?’¤ 4?ë¦? ? œ?™¸ ë§ˆìŠ¤?‚¹
	 * 
	 * @param name          ?´ë¦?(?˜ë¬?)
	 * @param maskingSymbol ë§ˆìŠ¤?‚¹ ê¸°í˜¸
	 * @return ex) Hong***dong (ë§ˆìŠ¤?‚¹ ê¸°í˜¸ "*")
	 */
	public static String maskingNameInEnglish(String name, String maskingSymbol) {
		name = checkNullValue(name);
		name = name.replace(" ", "");
		name = maskingString(name, maskingSymbol, 4, name.length() - 4 - 1);	
		return name;
	}

	/**
	 * ?•„?´?”” ë§ˆìŠ¤?‚¹
	 * ?–  ê¸°ì? : 	4?ë¦? ?´?ƒ?¼ ê²½ìš°, ?• 3?ë¦? ?™¸ ë§ˆìŠ¤?‚¹
	 * 			3?ë¦? ?´?•˜?¼ ê²½ìš°, ?• N-1?ë¦? ?™¸ ë§ˆìŠ¤?‚¹ (N=ê¸¸ì´)
	 * 
	 * @param userId ?•„?´?””
	 * @return ex) abc*, ab*, a*, * (ë§ˆìŠ¤?‚¹ ê¸°í˜¸ "*")
	 */
	public static String maskingUserId(String userId) {
		return maskingUserId(userId, defaultMaskingSymbol);
	}

	/**
	 * ?•„?´?”” ë§ˆìŠ¤?‚¹
	 * ?–  ê¸°ì? : 	4?ë¦? ?´?ƒ?¼ ê²½ìš°, ?• 3?ë¦? ?™¸ ë§ˆìŠ¤?‚¹
	 * 			3?ë¦? ?´?•˜?¼ ê²½ìš°, ?• N-1?ë¦? ?™¸ ë§ˆìŠ¤?‚¹ (N=ê¸¸ì´)
	 * 
	 * @param userId        ?•„?´?””
	 * @param maskingSymbol ë§ˆìŠ¤?‚¹ ê¸°í˜¸
	 * @return ex) abc*, ab*, a*, * (ë§ˆìŠ¤?‚¹ ê¸°í˜¸ "*")
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
	 * ?œ ?„  ? „?™”ë²ˆí˜¸ ë§ˆìŠ¤?‚¹
	 * ?–  ê¸°ì? : ê°??š´?° êµ?ë²? ?’¤ 2?ë¦?, ?’·?ë¦? ?’¤ 2?ë¦? ë§ˆìŠ¤?‚¹
	 * 
	 * @param telNum ?œ ?„  ? „?™”ë²ˆí˜¸ (?¬ë§·ë¬´ê´?)
	 * @return ex) 02-12**-12** (êµ¬ë¶„? "-", ë§ˆìŠ¤?‚¹ ê¸°í˜¸ "*")
	 */
	public static String maskingTelNum(String telNum) {
		return maskingTelNum(telNum, defaultMaskingSymbol, defaultSeparator);
	}
	
	/**
	 * ?œ ?„  ? „?™”ë²ˆí˜¸ ë§ˆìŠ¤?‚¹
	 * ?–  ê¸°ì? : ê°??š´?° êµ?ë²? ?’¤ 2?ë¦?, ?’·?ë¦? ?’¤ 2?ë¦? ë§ˆìŠ¤?‚¹
	 * 
	 * @param telNum1 ?œ ?„  ? „?™”ë²ˆí˜¸1
	 * @param telNum2 ?œ ?„  ? „?™”ë²ˆí˜¸2
	 * @param telNum3 ?œ ?„  ? „?™”ë²ˆí˜¸3
	 * @return ex) 02-12**-12** (êµ¬ë¶„? "-", ë§ˆìŠ¤?‚¹ ê¸°í˜¸ "*")
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
	 * ?œ ?„  ? „?™”ë²ˆí˜¸ ê°??š´?°?ë¦? ë§ˆìŠ¤?‚¹
	 * ?–  ê¸°ì? : ê°??š´?° êµ?ë²? ?’¤ 2?ë¦? ë§ˆìŠ¤?‚¹
	 * 
	 * @param middleTelNum ?œ ?„  ? „?™”ë²ˆí˜¸ ê°??š´?°?ë¦?
	 * @return 12**
	 */
	public static String maskingMiddleTelNum(String middleTelNum) {
		middleTelNum = checkNullValue(middleTelNum);
		middleTelNum = extractOnlyNumbers(middleTelNum);
		middleTelNum = maskingString(middleTelNum, defaultMaskingSymbol, middleTelNum.length() - 2);
		return middleTelNum;
	}

	/**
	 * ?œ ?„  ? „?™”ë²ˆí˜¸ ?’·?ë¦? ë§ˆìŠ¤?‚¹
	 * ?–  ê¸°ì? : ?’·?ë¦? ?’¤ 2?ë¦? ë§ˆìŠ¤?‚¹
	 * 
	 * @param lastTelNum ?œ ?„  ? „?™”ë²ˆí˜¸ ?’·?ë¦?
	 * @return 12**
	 */
	public static String maskingLastTelNum(String lastTelNum) {
		lastTelNum = checkNullValue(lastTelNum);
		lastTelNum = extractOnlyNumbers(lastTelNum);
		lastTelNum = maskingString(lastTelNum, defaultMaskingSymbol, lastTelNum.length() - 2);
		return lastTelNum;
	}

	/**
	 * ?œ ?„  ? „?™”ë²ˆí˜¸ ë§ˆìŠ¤?‚¹
	 * ?–  ê¸°ì? : ê°??š´?° êµ?ë²? ?’¤ 2?ë¦?, ?’·?ë¦? ?’¤ 2?ë¦? ë§ˆìŠ¤?‚¹
	 * 
	 * @param telNum        ?œ ?„  ? „?™”ë²ˆí˜¸ (?¬ë§·ë¬´ê´?)
	 * @param maskingSymbol ë§ˆìŠ¤?‚¹ ê¸°í˜¸
	 * @param separator     êµ¬ë¶„?
	 * @return ex) 02-12**-12** (êµ¬ë¶„? "-", ë§ˆìŠ¤?‚¹ ê¸°í˜¸ "*")
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
	 * ?œ´???°ë²ˆí˜¸ ë§ˆìŠ¤?‚¹
	 * ?–  ê¸°ì? : ê°??š´?° êµ?ë²? ?’¤ 2?ë¦?, ?’·?ë¦? ?’¤ 2?ë¦? ë§ˆìŠ¤?‚¹
	 * 
	 * @param phoneNum ?œ´???° ë²ˆí˜¸ (?¬ë§·ë¬´ê´?)
	 * @return ex) 010-12**-12** (êµ¬ë¶„? "-", ë§ˆìŠ¤?‚¹ ê¸°í˜¸ "*")
	 */
	public static String maskingPhoneNum(String phoneNum) {
		return maskingPhoneNum(phoneNum, defaultMaskingSymbol, defaultSeparator);
	}
	
	/**
	 * ?œ´???°ë²ˆí˜¸ ë§ˆìŠ¤?‚¹
	 * ?–  ê¸°ì? : ê°??š´?° êµ?ë²? ?’¤ 2?ë¦?, ?’·?ë¦? ?’¤ 2?ë¦? ë§ˆìŠ¤?‚¹
	 * 
	 * @param phoneNum1 ?œ´???° ë²ˆí˜¸1
	 * @param phoneNum2 ?œ´???° ë²ˆí˜¸2
	 * @param phoneNum3 ?œ´???° ë²ˆí˜¸3
	 * @return ex) 010-12**-12** (êµ¬ë¶„? "-", ë§ˆìŠ¤?‚¹ ê¸°í˜¸ "*")
	 */
	public static String maskingPhoneNum2(String phoneNum1, String phoneNum2, String phoneNum3) {
		StringBuffer sb = new StringBuffer();
		sb.append(phoneNum1).append(phoneNum2).append(phoneNum3);
		return maskingTelNum(sb.toString(), defaultMaskingSymbol, defaultSeparator);	
	}
	
	/**
	 * ?œ´???°ë²ˆí˜¸ ê°??š´?°?ë¦? ë§ˆìŠ¤?‚¹
	 * ?–  ê¸°ì? : ê°??š´?° êµ?ë²? ?’¤ 2?ë¦? ë§ˆìŠ¤?‚¹
	 * 
	 * @param middlePhoneNum ?œ´???°ë²ˆí˜¸ ê°??š´?°?ë¦?
	 * @return 12**
	 */
	public static String maskingMiddlePhoneNum(String middlePhoneNum) {
		middlePhoneNum = checkNullValue(middlePhoneNum);
		middlePhoneNum = extractOnlyNumbers(middlePhoneNum);
		middlePhoneNum = maskingString(middlePhoneNum, defaultMaskingSymbol, middlePhoneNum.length() - 2);
		return middlePhoneNum;
	}

	/**
	 * ?œ´???°ë²ˆí˜¸ ?’·?ë¦? ë§ˆìŠ¤?‚¹
	 * ?–  ê¸°ì? : ?’·?ë¦? ?’¤ 2?ë¦? ë§ˆìŠ¤?‚¹
	 * 
	 * @param lastPhoneNum ?œ´???°ë²ˆí˜¸ ?’·?ë¦?
	 * @return 12**
	 */
	public static String maskingLastPhoneNum(String lastPhoneNum) {
		lastPhoneNum = checkNullValue(lastPhoneNum);
		lastPhoneNum = extractOnlyNumbers(lastPhoneNum);
		lastPhoneNum = maskingString(lastPhoneNum, defaultMaskingSymbol, lastPhoneNum.length() - 2);
		return lastPhoneNum;
	}

	/**
	 * ?œ´???°ë²ˆí˜¸ ë§ˆìŠ¤?‚¹
	 * ?–  ê¸°ì? : ê°??š´?° êµ?ë²? ?’¤ 2?ë¦?, ?’·?ë¦? ?’¤ 2?ë¦? ë§ˆìŠ¤?‚¹
	 * 
	 * @param phoneNum      ?œ´???° ë²ˆí˜¸ (?¬ë§·ë¬´ê´?)
	 * @param maskingSymbol ë§ˆìŠ¤?‚¹ ê¸°í˜¸
	 * @param separator     êµ¬ë¶„?
	 * @return ex) 010-12**-12** (êµ¬ë¶„? "-", ë§ˆìŠ¤?‚¹ ê¸°í˜¸ "*")
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
	 * ?´ë©”ì¼ ë§ˆìŠ¤?‚¹
	 * ?–  ê¸°ì? : 	@ ê¸°ì? ?• IDê°? 3?ë¦? ?´?ƒ?¼ ê²½ìš°, ?• 2?ë¦? ?™¸ ë§ˆìŠ¤?‚¹
	 * 			@ ê¸°ì? ?• IDê°? 2?ë¦? ?´?•˜?¼ ê²½ìš°, ?• N-1?ë¦? ?™¸ ë§ˆìŠ¤?‚¹ (N=ê¸¸ì´)
	 * 
	 * @param email         ?´ë©”ì¼
	 * @return ex) ab*@daiso.co.kr
	 */
	public static String maskingEmail(String email) {
		return maskingEmail(email, defaultMaskingSymbol);
	}

	/**
	 * ?´ë©”ì¼ ë§ˆìŠ¤?‚¹
	 * ?–  ê¸°ì? : 	@ ê¸°ì? ?• IDê°? 3?ë¦? ?´?ƒ?¼ ê²½ìš°, ?• 2?ë¦? ?™¸ ë§ˆìŠ¤?‚¹
	 * 			@ ê¸°ì? ?• IDê°? 2?ë¦? ?´?•˜?¼ ê²½ìš°, ?• N-1?ë¦? ?™¸ ë§ˆìŠ¤?‚¹ (N=ê¸¸ì´)
	 * 
	 * @param email         ?´ë©”ì¼
	 * @param maskingSymbol ë§ˆìŠ¤?‚¹ ê¸°í˜¸
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

	// =============================== MaskingUtil ê³µí†µ ?•¨?ˆ˜ =============================== //

	/**
	 * ë¬¸ì?—´ ë§ˆìŠ¤?‚¹
	 * 
	 * @param str           ë§ˆìŠ¤?‚¹ ???ƒ ë¬¸ì?—´
	 * @param maskingSymbol ë§ˆìŠ¤?‚¹ ê¸°í˜¸
	 * @param from          ë§ˆìŠ¤?‚¹ ?‹œ?‘ ?¸?±?Š¤
	 * @param to            ë§ˆìŠ¤?‚¹ ì¢…ë£Œ ?¸?±?Š¤
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
	 * ë¬¸ì?—´ ë§ˆìŠ¤?‚¹ (?‹œ?‘ ?¸?±?Š¤ë¶??„° ë¬¸ì?—´?˜ ë§ˆì?ë§‰ê¹Œì§? ë§ˆìŠ¤?‚¹)
	 * 
	 * @param str           ë§ˆìŠ¤?‚¹ ???ƒ ë¬¸ì?—´
	 * @param maskingSymbol ë§ˆìŠ¤?‚¹ ê¸°í˜¸
	 * @param from          ë§ˆìŠ¤?‚¹ ?‹œ?‘ ?¸?±?Š¤
	 * @return
	 */
	public static String maskingString(String str, String maskingSymbol, int from) {
		return maskingString(str, maskingSymbol, from, str.length());
	}

	/**
	 * ë¬¸ì?—´?—?„œ ?ˆ«?ë§? ì¶”ì¶œ
	 * 
	 * @param str
	 * @return
	 */
	public static String extractOnlyNumbers(String str) {
		return str.replaceAll("[^0-9]", "");
	}

	/**
	 * ë¬¸ì?—´?„ êµ¬ë¶„?ë¥? ?´?š©?•˜?—¬ ë¶„ë¦¬ 
	 * ?? ì´? ë¬¸ì?—´?˜ ê¸¸ì´?— ë§ì¶”?–´ ?…? ¥ ê¶Œê³ 
	 * ?? ë¬¸ì?—´ ê¸¸ì´ë¥? ì´ˆê³¼?•˜ê±°ë‚˜ ë¶?ì¡±í•  ê²½ìš°, ë¬¸ì?—´ ê¸¸ì´ë§Œí¼ë§? ì¶œë ¥ 
	 * 
	 * @param str            ë¬¸ì?—´
	 * @param separator      êµ¬ë¶„?
	 * @param lengthOfChunks êµ¬ë¶„?œ ë¬¸ì?—´ë³? ë¬¸ì?—´ ê¸¸ì´ (0?? ë¬´ì‹œ?•©?‹ˆ?‹¤.)
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
	 * ë¬¸ì?—´?˜ ê¸¸ì´ê°? ?™•?¸
	 * 
	 * @param str		ë¬¸ì?—´
	 * @param length	ê¸°ë??˜?Š” ë¬¸ì?—´?˜ ê¸¸ì´
	 * @return			ì¡°ê±´ ì¶©ì¡±?—¬ë¶?
	 */
	public static boolean checkValidLength(String str, int length) {
		return str.length() == length ? true : false;
	}
	
	/**
	 * ë¬¸ì?—´?˜ ê¸¸ì´ê°? ?™•?¸ (ë²”ìœ„)
	 * 
	 * @param str		ë¬¸ì?—´
	 * @param from		ë¬¸ì?—´ ?ë¦¿ìˆ˜ ë²”ìœ„ ?‹œ?‘
	 * @param to		ë¬¸ì?—´ ?ë¦¿ìˆ˜ ë²”ìœ„ ?
	 * @return			ì¡°ê±´ ì¶©ì¡±?—¬ë¶?
	 */
	public static boolean checkValidLength(String str, int from, int to) {
		return str.length() >= from && str.length() <= to ? true : false;
	}
	
	/**
	 * ?…? ¥ê°’ì˜ NULL ?—¬ë¶? ?Œ?‹¨
	 * NULL?¼ ê²½ìš°, ê³µë°± ë°˜í™˜
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
