package simple.java.model;

import java.io.Serializable;

public enum Resource implements Serializable{
printer{
	public void process(String node){
		System.out.println(node+ ": Printer is running now!");
//		try {
//			//Thread.sleep(1000);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
	}
};

public void process(String node){
	System.out.println(node + ": Resource process Now!");
	try {
		Thread.sleep(1000);
	} catch (InterruptedException e) {
		e.printStackTrace();
	}
}
}
