
package lab1.model;

import java.io.Serializable;
import java.util.*;

import lab1.exception.OptionException;

/**
 * this class models an automotive model.
 * 
 * @author amixyue
 * 
 */
public class Automotive implements Serializable {

	public static final int PRINT_AUTO = 0;
	public static final int PRINT_CAR = 1;

	private static final long serialVersionUID = 1L;
	protected String maker;
	protected final String name;
	protected int basePrice;
	/* Here I apply HashMap because it is more efficient */
	protected HashMap<String, OptionSet> optionSets;

	public Automotive(String name, String maker, int basePrice) {
		this.name = name;
		this.maker = maker;
		this.basePrice = basePrice;
		this.optionSets = new LinkedHashMap<String, OptionSet>();
	}

	/**
	 * update an option's price
	 * @param optionSetName
	 * @param optionName
	 * @throws OptionException 
	 */
	public void updateOption(String optionSetName, String optionName, int price) throws OptionException {
		if (this.optionSets.get(optionSetName) == null) {
			throw new OptionException("No " + optionSetName + " in the automotive " + this.name);
		}
		this.optionSets.get(optionSetName).updateOption(optionName, price);
	}
	
	/**
	 * Add an option to the automotive
	 * @param optionSetName
	 * @param optionName
	 */
	public void addOption(String optionSetName, String optionName, int price) {
		if (this.optionSets.get(optionSetName) == null) {
			this.optionSets.put(optionSetName, new OptionSet(optionSetName));
		}

		this.optionSets.get(optionSetName).addOption(optionName, price);
	}
	/**
	 * remove an option of the automotive
	 * @param optionSetName
	 * @param optionName
	 * @throws OptionException
	 */
	public void removeOption(String optionSetName, String optionName) throws OptionException{
		if (this.optionSets.get(optionSetName) == null) {
			throw new OptionException(String.format(
					"No optionset %s ", optionSetName));
		}
		this.optionSets.get(optionSetName).removeOption(optionName);
	}

	/**
	 * get an option of an optionset of the automotive
	 * @param optionSetName
	 * @param optionName
	 * @return
	 * @throws OptionException
	 */
	public Option getOption(String optionSetName, String optionName) throws OptionException{
		if (this.optionSets.get(optionSetName) == null) {
			throw new OptionException(String.format(
					"No optionset %s ", optionSetName));
		}
		return this.optionSets.get(optionSetName).getOption(optionName);
	}
	/**
	 * @param optionSetName
	 * @return
	 * @throws OptionException 
	 */
	public OptionSet getOptionSet(String optionSetName) throws OptionException {
		if(this.optionSets.get(optionSetName) == null){
			throw new OptionException(String.format(
					"No optionset %s ", optionSetName));
		}
		return this.optionSets.get(optionSetName);
	}

	/**
	 * remove an empty option set. This can be used for empty an option set if
	 * you would like to call.
	 * 
	 * @param optionSetName
	 * @throws OptionException 
	 */
	public void removeOptionSet(String optionSetName) throws OptionException {
		if(this.optionSets.get(optionSetName) == null){
			throw new OptionException(String.format(
					"No optionset %s ", optionSetName));
		}
		this.optionSets.remove(optionSetName);
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the maker
	 */
	public String getMaker() {
		return maker;
	}

	/**
	 * @param maker
	 *            the maker to set
	 */
	public void setMaker(String maker) {
		this.maker = maker;
	}

	/**
	 * @return the basePrice
	 */
	public int getBasePrice() {
		return basePrice;
	}

	/**
	 * @param basePrice
	 *            the basePrice to set
	 */
	public void setBasePrice(int basePrice) {
		this.basePrice = basePrice;
	}

	/**
	 * add an option set
	 * 
	 * @param optionSetName
	 */
	public void addOptionSet(String optionSetName) {
		this.optionSets.put(optionSetName, new OptionSet(optionSetName));
	}

	/**
	 * this function is to find all the options and option sets a car model has.
	 * 
	 * @return an iterator that can be used to iterate through the option set
	 *         names.
	 */
	public Iterator<String> getOptionSetNamesIterator() {
		return this.optionSets.keySet().iterator();
	}

	/**
	 * set a choice for an option set.
	 * @param setName
	 * @param optionName
	 * @throws OptionException
	 */
	public void setOptionChoice(String setName, String optionName)
			throws OptionException {
		try {
			this.optionSets.get(setName).setChoice(optionName);
		} catch (NullPointerException e) {
			throw new OptionException("No such option set");
		}
	}

	/**
	 * get the choice for the option set.
	 * @param setName
	 * @return
	 * @throws OptionException
	 */
	public String getOptionChoice(String setName) throws OptionException {
		try {
			return this.optionSets.get(setName).getChoice();
		} catch (NullPointerException e) {
			throw new OptionException("No such option set");
		}
	}

	public int getOptionChoicePrice(String setName) throws OptionException {
		try {
			return this.optionSets.get(setName).getChoicePrice();
		} catch (NullPointerException e) {
			throw new OptionException("No such option set");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder strb = new StringBuilder();
		strb.append(String.format(
				"Automotive:\nname: %s\nmaker: %s\nbasePrice: %d\noptions:\n",
				this.name, this.maker, this.basePrice));
		for (String k : this.optionSets.keySet()) {
			strb.append(this.optionSets.get(k));
		}
		return strb.toString();
	}

	/**
	 * according to different type to print a car or an automotive
	 * 
	 * @param type
	 * @return
	 */
	public String print(int type) {
		if (type == PRINT_AUTO) {
			return this.toString();
		} else {
			StringBuilder strb = new StringBuilder();
			strb.append(String
					.format("Automotive:\nname: %s\nmaker: %s\nbasePrice: %d\nchoices:\n",
							this.name, this.maker, this.basePrice));
			for (String k : this.optionSets.keySet()) {
				strb.append(k + ": " + this.optionSets.get(k).getChoice() + "\n");
			}
			return strb.toString();
		}
	}

	/**
	 * get the price for a perticular car.
	 * 
	 * @return
	 * @throws OptionException
	 */
	public int getPrice() throws OptionException {
		int sum = this.basePrice;
		for (OptionSet os : this.optionSets.values()) {
			sum += os.getChoicePrice();
		}
		return sum;
	}

}
