package beauty.web.formbean;

import java.util.ArrayList;

import beauty.web.model.Retail;
import beauty.web.util.AddressFormat;
import beauty.web.util.StringUtil;

public class RetailForm extends BaseForm {

	private String name;
	private String url;
	private String streetAddress;
	private String city;
	private String state;
	private String country;
	private String postcode;
	private String phoneNumber;

	public void populateRetail(Retail r) {
		r.setName(name);
		r.setUrl(url);
		r.setStreetAddress(streetAddress);
		r.setCity(city);
		r.setState(state);
		r.setCountry(country);
		r.setPostcode(postcode);
		r.setPhoneNumber(phoneNumber);
		r.setFormatted_address(streetAddress + " " + city + " " + " " + state
				+ " " + country);

		if (this.isForce() || this.isEdit()) {
			// r has to be set lat and lng
			Retail tmpR = new Retail();
			tmpR.setState(r.getState());
			tmpR.setCity(r.getCity());
			tmpR.setCountry(r.getCountry());
			tmpR.setStreetAddress(r.getStreetAddress());
			new AddressFormat().format(tmpR);
			r.setLat(tmpR.getLat());
			r.setLng(tmpR.getLng());
		} else {
			new AddressFormat().format(r);
		}
	}

	public void populateForm(Retail r) {
		this.city = r.getCity();
		this.country = r.getCountry();
		this.name = r.getName();
		this.state = r.getState();
		this.streetAddress = r.getStreetAddress();
		this.url = r.getUrl();
		this.phoneNumber = r.getPhoneNumber();
		this.postcode = r.getPostcode();
	}

	public ArrayList<String> getValidationErrors() {
		ArrayList<String> errors = new ArrayList<String>();
		errors.addAll(super.getValidationErrors());

		if (!this.isGet()) {
			if (name == null || name.length() == 0) {
				errors.add("Name is null");
			}

			if (streetAddress == null || streetAddress.length() == 0) {
				errors.add("Street Address is null");
			}
			if (city == null || city.length() == 0) {
				errors.add("City is null");
			}
			if (state == null || state.length() == 0) {
				errors.add("State is null");
			}
			if (postcode == null || postcode.length() == 0) {
				errors.add("postcode is null");
			}
			if (phoneNumber == null || phoneNumber.length() == 0) {
				errors.add("PhoneNumber is null");
			}

			if (!StringUtil.isPhone(phoneNumber)) {
				errors.add("Phone Number format error! Please enter 10-digit phone number");
			}
			if (!StringUtil.isURL(url)) {
				errors.add("URL format error! ");
			}
		}
		return errors;
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
		this.name = StringUtil.sanitize(name);
	}

	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @param url
	 *            the url to set
	 */
	public void setUrl(String url) {
		this.url = StringUtil.sanitize(url);
	}

	/**
	 * @return the streetAddress
	 */
	public String getStreetAddress() {
		return streetAddress;
	}

	/**
	 * @param streetAddress
	 *            the streetAddress to set
	 */
	public void setStreetAddress(String streetAddress) {
		this.streetAddress = StringUtil.sanitize(streetAddress);
	}

	/**
	 * @return the city
	 */
	public String getCity() {
		return city;
	}

	/**
	 * @param city
	 *            the city to set
	 */
	public void setCity(String city) {
		this.city = StringUtil.sanitize(city);
	}

	/**
	 * @return the state
	 */
	public String getState() {
		return state;
	}

	/**
	 * @param state
	 *            the state to set
	 */
	public void setState(String state) {
		this.state = StringUtil.sanitize(state);
	}

	/**
	 * @return the postcode
	 */
	public String getPostcode() {
		return postcode;
	}

	/**
	 * @param postcode
	 *            the postcode to set
	 */
	public void setPostcode(String postcode) {
		this.postcode = StringUtil.sanitize(postcode);
	}

	/**
	 * @return the phoneNumber
	 */
	public String getPhoneNumber() {
		return phoneNumber;
	}

	/**
	 * @param phoneNumber
	 *            the phoneNumber to set
	 */
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = StringUtil.sanitize(phoneNumber);
	}

	/**
	 * @return the country
	 */
	public String getCountry() {
		return country;
	}

	/**
	 * @param country
	 *            the country to set
	 */
	public void setCountry(String country) {
		this.country = StringUtil.sanitize(country);
	}

}
