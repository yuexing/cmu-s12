package beauty.web.action.service.msg.bean;

import java.io.Serializable;

import beauty.web.model.Retail;

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

	// this is to control who can modify what
	private int owner;

	public MRetail(Retail r) {
		this.id = r.getId();
		this.name = r.getName();
		this.url = r.getUrl();
		this.streetAddress = r.getStreetAddress();
		this.city = r.getCity();
		this.state = r.getState();
		this.country = r.getCountry();
		this.formatted_address = r.getFormatted_address();
		this.postcode = r.getPostcode();
		this.phoneNumber = r.getPhoneNumber();
		this.lat = r.getLat();
		this.lng = r.getLng();
		this.owner = r.getOwner();
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

	public int getOwner() {
		return owner;
	}

	public void setOwner(int owner) {
		this.owner = owner;
	}

}
