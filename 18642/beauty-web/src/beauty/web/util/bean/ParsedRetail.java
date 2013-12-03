package beauty.web.util.bean;

import java.util.ArrayList;

import beauty.web.model.Retail;

public class ParsedRetail extends Retail{
	
	private ArrayList<String> products = new ArrayList<String>();

	
	/**
	 * @param name
	 * @param url
	 * @param streetAddress
	 * @param city
	 * @param state
	 * @param country
	 * @param postcode
	 * @param phoneNumber
	 */
	public ParsedRetail(String name, String url, String streetAddress,
			String city, String state, String country, String postcode,
			String phoneNumber) {
		super();
		this.name = name;
		this.url = url;
		this.streetAddress = streetAddress;
		this.city = city;
		this.state = state;
		this.country = country;
		this.postcode = postcode;
		this.phoneNumber = phoneNumber;
	}
	
	public void addProduct(String product){
		this.products.add(product);
	}

	/**
	 * @return the products
	 */
	public ArrayList<String> getProducts() {
		return products;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ParsedRetail [name=" + name + ", url=" + url
				+ ", streetAddress=" + streetAddress + ", city=" + city
				+ ", state=" + state + ", country=" + country + ", postcode="
				+ postcode + ", phoneNumber=" + phoneNumber + ", products="
				+ products + "]";
	}
}
