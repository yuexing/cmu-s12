package beauty.android.msg.bean;

import java.io.Serializable;

@SuppressWarnings("serial")
public class MRetail implements Serializable {

	protected int id;
	protected String name;
	protected String url;
	protected String streetAddress;
	protected String city;
	protected String state;
	protected String country;
	protected String formatted_address;
	protected String postcode;
	protected String phoneNumber;
	protected double lat;
	protected double lng;

	public MRetail() {
	}

	public MRetail(int id, String name, String url, String streetAddress,
			String city, String state, String country, String postcode,
			String phoneNumber, double lat, double lng) {
		super();
		this.id = id;
		this.name = name;
		this.url = url;
		this.streetAddress = streetAddress;
		this.city = city;
		this.state = state;
		this.country = country;
		this.postcode = postcode;
		this.phoneNumber = phoneNumber;
		this.lat = lat;
		this.lng = lng;
		this.formatted_address = this.streetAddress + " " + this.city + " "
				+ this.state + " " + this.country;
	}

	public static void main() {

	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getStreetAddress() {
		return streetAddress;
	}

	public void setStreetAddress(String streetAddress) {
		this.streetAddress = streetAddress;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getFormatted_address() {
		return formatted_address;
	}

	public void setFormatted_address(String formatted_address) {
		this.formatted_address = formatted_address;
	}

	public String getPostcode() {
		return postcode;
	}

	public void setPostcode(String postcode) {
		this.postcode = postcode;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLng() {
		return lng;
	}

	public void setLng(double lng) {
		this.lng = lng;
	}

}
