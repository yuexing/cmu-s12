package lab1.test;

import java.util.ArrayList;

import lab1.exception.OptionException;
import lab1.model.*;
import lab1.service.*;
import lab1.service.impl.*;
import static java.lang.System.*;

/**
 * test for project unit3.
 * 
 * The basic idea is: 1) we starts two threads (t1 and t2).
 * 
 * 2) As to basic price: 2.1) t1 will decrease the basic price; 2.2) t2 will
 * increase the basic price with the same delta.
 * 
 * 3) As to options: 3.1) t1 will add and remove several options; 3.2) t2 will
 * add and remove several options;
 * 
 * The expected result should be the automotive remains unchanged.
 * 
 * @author amixyue
 * 
 */
public class Test {

	public static void main(String[] args) {
		try {
			/* creat an automotive first */
			ArrayList<Automotive> autos = new ArrayList<Automotive>();

			Loader.loadXML("automotive.xml", autos);

			/* modify the automotive */
			EditOptions.setAuto(autos.get(0));

			out.println("******************************************");
			out.println("Before changing, the automotive looks like:");
			out.println(autos.get(0));

			Thread t1 = new Thread(new Runnable() {
				public void run() {
					out.println("t1 starts running...");

					out.println("t1 update base price now ...");

					for (int i = 10; i < 100; i += 10) {
						EditOptions.addPrice(-i);
						/* yield is almost useless, but useful for test */
						Thread.yield();
					}

					out.println("t1 modify option set color");

					try {
						for (int i = 0; i < 10; i++) {
							out.println("t1 add option " + i);
							EditOptions.addOption("color", "" + i, i * 100);
							Thread.yield();
						}

						for (int i = 0; i < 10; i++) {
							out.println("t1 remove option " + i);
							EditOptions.removeOption("color", "" + i);
							Thread.yield();
						}
					} catch (OptionException e) {
						err.println("T1 error: " + e.getMessage());
					}
				}
			});

			Thread t2 = new Thread(new Runnable() {
				public void run() {
					out.println("t2 starts running...");

					out.println("t2 update base price now ...");

					for (int i = 10; i < 100; i += 10) {
						EditOptions.addPrice(i);
						Thread.yield();
					}

					out.println("t2 modify option set color");

					try {
						for (int i = 10; i < 20; i++) {
							out.println("t2 add option " + i);
							EditOptions.addOption("color", "" + i, i * 100);
							Thread.yield();
						}

						for (int i = 10; i < 20; i++) {
							out.println("t2 remove option " + i);
							EditOptions.removeOption("color", "" + i);
							Thread.yield();
						}
					} catch (OptionException e) {
						err.println("T1 error: " + e.getMessage());
					}
				}
			});

			t1.start();
			t2.start();

			t1.join();
			t2.join();

			out.println("******************************************");
			out.println("After changing, the automotive looks like:");
			out.println(autos.get(0));
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}
}
