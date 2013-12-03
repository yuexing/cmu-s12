package amixyue.webapp.util;

public class Util {

	public static String sanitize(String str){
		return str.replace("<", "&lt;").replaceAll(">", "&gt;");
	}
	
	public static <T> String join(Class<T>[] ss, String separator){
		StringBuilder sb = new StringBuilder();
		for(Class<T> s: ss){
			sb.append(s);
			sb.append(separator);
		}
		String tmp = sb.toString();
		return tmp.substring(0, tmp.lastIndexOf(separator));
	}
}
