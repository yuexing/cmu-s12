package lab1.service;

import lab1.exception.OptionException;
import lab1.model.*;

/**
 * This models a interface to edit an automotive and its options in
 * multi-threading environment. The basic idea is that because synchronization
 * can be very inefficient when the system is in single-threading environment,
 * thus we expose this interface for multi-thread without tampering with the
 * original system.
 * 
 * At present, the functionality of this interface is limited since we just use
 * it for demo now.
 * 
 * @author amixyue
 * 
 */
public class EditOptions {

	private static Automotive auto;

	/**
	 * set the automotive to modify
	 * 
	 * @param a
	 */
	public static void setAuto(Automotive a) {
		auto = a;
	}

	/**
	 * modify the maker and base prince together. If you need to modify one of
	 * them, just call the setter instead since there is no race at all.
	 * 
	 * @param maker
	 */
	public static void setBasic(String maker, int basePrice) {
		synchronized (auto) {
			auto.setMaker(maker);
			auto.setBasePrice(basePrice);
		}
	}

	/**
	 * load and update the baseprice, there will be races without
	 * synchronization.
	 * 
	 * @param delta
	 *            delta can be negative to decrease the price.
	 */
	public static void addPrice(int delta) {
		synchronized (auto) {
			int basePrice = auto.getBasePrice();
			auto.setBasePrice(basePrice + delta);
		}
	}

	/**
	 * add an option set of a specific name
	 * 
	 * @param optionSetName
	 */
	public static void addOptionSet(String optionSetName) {
		synchronized (auto) {
			auto.addOptionSet(optionSetName);
		}
	}

	/**
	 * remove the option set of a specific name
	 * 
	 * @param optionSetName
	 * @throws OptionException
	 */
	public static void removeOptionSet(String optionSetName)
			throws OptionException {
		synchronized (auto) {
			auto.removeOptionSet(optionSetName);
		}
	}

	/**
	 * Add an option to the car
	 * 
	 * @param optionSetName
	 * @param optionName
	 */
	public static void addOption(String optionSetName, String optionName,
			int price) {
		synchronized (auto) {
			auto.addOption(optionSetName, optionName, price);
		}
	}

	/**
	 * update an option to the car
	 * 
	 * @param optionSetName
	 * @param optionName
	 * @throws OptionException
	 */
	public static void updateOption(String optionSetName, String optionName,
			int price) throws OptionException {
		synchronized (auto) {
			auto.updateOption(optionSetName, optionName, price);
		}
	}

	/**
	 * remove an option of the automotive
	 * 
	 * @param optionSetName
	 * @param optionName
	 * @throws OptionException
	 */
	public static void removeOption(String optionSetName, String optionName)
			throws OptionException {
		synchronized (auto) {
			auto.removeOption(optionSetName, optionName);
		}
	}
}
