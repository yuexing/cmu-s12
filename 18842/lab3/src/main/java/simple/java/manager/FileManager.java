package simple.java.manager;
import java.io.*;
public class FileManager {

	private static File target;
	private static long current;
	
	/**
	 * manage file and its initial timestamp
	 * @param path
	 */
	public static void init(String path){
		target = new File(path);
		current = target.lastModified();
	}
	/**
	 * let it do less things, just monitor
	 * @return
	 */
	public static boolean checkChange(){
		long tmp = target.lastModified();
		if(current != tmp){
			current = tmp;
			return true;
		}
		return false;
	}
}
