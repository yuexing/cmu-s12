package beauty.web.action.service.msg.bean;

import java.io.Serializable;
import java.util.Arrays;

import beauty.web.model.Product;

@SuppressWarnings("serial")
public class MProduct implements Serializable {

	private int id;
	private String image; // image url
	private String name;
	private MBrand brand;
	private MCategory category;
	private MBenefit[] benefits;
	private MTag[] tags;
	private double price;
	private int rate = 0;
	private int rateNum = 0;
	private String introduction;
	private int commentNum = 0;

	// this is to control who can modify what
	private int owner;

	public MProduct(Product p) {
		this.id = p.getId();
		this.image = "view.do?id=" + p.getAttachmentId();
		this.name = p.getName();
		this.price = p.getPrice();
		this.rate = p.getRate();
		this.rateNum = p.getRateNum();
		this.introduction = p.getIntroduction();
		this.commentNum = p.getCommentNum();
		this.owner = p.getOwner();
	}

	public MProduct(int id, String name, String image) {
		super();
		this.id = id;
		this.image = image;
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public int getRate() {
		return rate;
	}

	public void setRate(int rate) {
		this.rate = rate;
	}

	public int getRateNum() {
		return rateNum;
	}

	public void setRateNum(int rateNum) {
		this.rateNum = rateNum;
	}

	public String getIntroduction() {
		return introduction;
	}

	public void setIntroduction(String introduction) {
		this.introduction = introduction;
	}

	public int getCommentNum() {
		return commentNum;
	}

	public void setCommentNum(int commentNum) {
		this.commentNum = commentNum;
	}

	/**
	 * @return the brand
	 */
	public MBrand getBrand() {
		return brand;
	}

	/**
	 * @param brand
	 *            the brand to set
	 */
	public void setBrand(MBrand brand) {
		this.brand = brand;
	}

	/**
	 * @return the category
	 */
	public MCategory getCategory() {
		return category;
	}

	/**
	 * @param category
	 *            the category to set
	 */
	public void setCategory(MCategory category) {
		this.category = category;
	}

	/**
	 * @return the benefits
	 */
	public MBenefit[] getBenefits() {
		return benefits;
	}

	/**
	 * @param benefits
	 *            the benefits to set
	 */
	public void setBenefits(MBenefit[] benefits) {
		this.benefits = benefits;
	}

	/**
	 * @return the tags
	 */
	public MTag[] getTags() {
		return tags;
	}

	/**
	 * @param tags
	 *            the tags to set
	 */
	public void setTags(MTag[] tags) {
		this.tags = tags;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "MProduct [id=" + id + ", image=" + image + ", name=" + name
				+ ", brand=" + brand + ", category=" + category + ", benefits="
				+ Arrays.toString(benefits) + ", tags=" + Arrays.toString(tags)
				+ ", price=" + price + ", rate=" + rate + ", rateNum="
				+ rateNum + ", introduction=" + introduction + ", commentNum="
				+ commentNum + "]";
	}

	public int getOwner() {
		return owner;
	}

	public void setOwner(int owner) {
		this.owner = owner;
	}
}
