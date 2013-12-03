package lab1.service.impl;

public class Logger {

	private static boolean isActive = true; // by default

	public static void log(String s) {
		if (isActive) {
			System.out.println(s);
		}
	}

	/**
	 * @param isActive the isActive to set
	 */
	public static void setActive(boolean active) {
		isActive = active;
	}
}
