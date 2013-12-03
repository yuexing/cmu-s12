package beauty.web.util;

import java.security.*;

public class StringUtil {

	private static final String url_pattern = "^(http://)?[a-zA-Z_0-9\\-]+(\\.\\w[a-zA-Z_0-9\\-]+)+(/[#&\\n\\-=?\\+\\%/\\.\\w]+)?$";
	private static final String phone_pattern = "^\\(?\\d{3}\\)?\\-?\\d{3}\\-?\\d{4}$";

	private static final String email_pattern = 
			"^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
			+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
	
	/**
	 * generate std format of the str for search
	 * @param str
	 * @return
	 */
	public static String getStdFrom(String str) {
		if (str != null){
		return str.toLowerCase().replaceAll("\\W", "");
		} else {
			return null;
		}
	}

	/**
	 * sanitize input string
	 * @param s
	 * @return
	 */
	public static String sanitize(String s) {
		if (s != null) {
			return s.trim().replace("<", "&lt;").replace(">", "&gt;");
		} else {
			return null;
		}
	}
	
	public static boolean isURL(String s){
		return s.matches(url_pattern);
	}
	
	public static boolean isPhone(String s){
		return s.matches(phone_pattern);
	}
	
	public static boolean isEmail(String s){
		return s.matches(email_pattern);
	}
	
	public static String hash(String clear) {
		// hash twice
		return hash(clear, 2);
	}
	
	private static String hash(String clear, int times) {
		if(times == 0)
			return clear;
		
		times--;
		
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("SHA1");
		} catch (NoSuchAlgorithmException e) {
			throw new AssertionError(
					"Can't find the SHA1 algorithm in the java.security package");
		}

		// variate uncertainty
		md.update(String.valueOf(System.nanoTime()).getBytes());
		
		md.update(clear.getBytes());
		
		byte[] digestBytes = md.digest();

		// Format the digest as a String
		StringBuffer digestSB = new StringBuffer();
		for (int i = 0; i < digestBytes.length; i++) {
			int lowNibble = digestBytes[i] & 0x0f;
			int highNibble = (digestBytes[i] >> 4) & 0x0f;
			digestSB.append(Integer.toHexString(highNibble));
			digestSB.append(Integer.toHexString(lowNibble));
		}
		
		String digestStr = digestSB.toString();

		return hash(digestStr, times);
	}
}
