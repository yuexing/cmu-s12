package lab1.model;

import java.io.Serializable;
import java.util.*;

import lab1.exception.OptionException;

/**
 * This models a set of same kind options. For example, an option set of colors.
 * 
 * @author amixyue
 * 
 */
public class OptionSet implements Serializable {

	private static final long serialVersionUID = 1L;
	private String name;
	/* for choosing a particular option in an option set */
	private String choice;
	private HashMap<String, Option> options = new LinkedHashMap<String, Option>();

	public OptionSet() {

	}

	public OptionSet(String name) {
		this.name = name;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the options
	 */
	public ArrayList<Option> getOptions() {
		return (ArrayList<Option>) options.values();
	}

	/**
	 * @param options
	 *            the options to set
	 */
	public void setOptions(ArrayList<Option> options) {
		/* do deep and safe copy */
		this.options.clear();
		for (Option o : options) {
			this.options.put(o.getName(), o);
		}
	}

	/**
	 * add an option to the options. This can also be used to update an option,
	 * though not that efficient.
	 * 
	 * @param i
	 *            specify the position.
	 * @param name
	 *            The name of the option
	 * @param price
	 *            The price of the option
	 * @throws OptionException
	 */
	public void addOption(String name, int price) {
		this.options.put(name, new Option(name, price));
	}

	/**
	 * remove an option
	 * 
	 * @param name
	 * @throws OptionException
	 */
	public void removeOption(String name) throws OptionException {
		this.options.remove(name);
	}

	/**
	 * update an option's price
	 * 
	 * @param name
	 * @param price
	 * @throws OptionException
	 */
	public void updateOption(String name, int price) throws OptionException {
		Option o = getOption(name);
		o.setPrice(price);
	}

	/**
	 * get an option according to its name
	 * 
	 * @param name
	 *            The name of the option
	 * @return The option on success, null on error
	 * @throws OptionException
	 */
	public Option getOption(String name) throws OptionException {
		for (Option o : options.values()) {
			if (o.getName().equals(name)) {
				return o;
			}
		}
		throw new OptionException(String.format(
				"No option %s in option set %s", name, this.name));
	}

	/**
	 * get the price for an option according to its name.
	 * 
	 * @param name
	 *            The name of the option
	 * @return the price of the specific option.
	 * @throws OptionException
	 */
	public int getOptionPrice(String name) throws OptionException {
		return getOption(name).getPrice();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder strb = new StringBuilder();
		strb.append(this.name + ":\n");
		for (Option o : this.options.values()) {
			strb.append(o + "\n");
		}
		return strb.toString();
	}

	/**
	 * Apply the first one as the default choice, this is definitely much more
	 * user friendly.
	 * 
	 * @return the choice
	 */
	public String getChoice() {
		if (choice != null) {
			return choice;
		} else {
			return this.options.keySet().iterator().next();
		}
	}

	/**
	 * @param choice
	 *            the choice to set
	 */
	public void setChoice(String choice) throws OptionException {
		for (Option o : this.options.values()) {
			if (o.getName().equals(choice)) {
				this.choice = choice;
				return;
			}
		}
		throw new OptionException(String.format(
				"No option %s in option set %s", choice, this.name));
	}

	/**
	 * Apply the first one as the default choice, this is definitely much more
	 * user friendly.
	 * 
	 * @return
	 * @throws OptionException
	 */
	public int getChoicePrice() throws OptionException {
		if (this.choice != null) {
			return this.getOptionPrice(this.choice);
		} else
			return this.getOptionPrice(this.options.keySet().iterator().next());
	}
}
