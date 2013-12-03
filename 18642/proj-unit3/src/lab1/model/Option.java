package lab1.model;

import java.io.Serializable;

/**
 * this class models an option.
 * 
 * @author amixyue
 * 
 */
public class Option implements Serializable {

	private static final long serialVersionUID = 1L;
	private String name;
	private int price;

	/**
	 * @param name
	 * @param price
	 */
	public Option(String name, int price) {
		super();
		this.name = name;
		this.price = price;
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
	 * @return the price
	 */
	public int getPrice() {
		return price;
	}

	/**
	 * @param price
	 *            the price to set
	 */
	public void setPrice(int price) {
		this.price = price;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Option [name=" + name + ", price=" + price + "]";
	}
}