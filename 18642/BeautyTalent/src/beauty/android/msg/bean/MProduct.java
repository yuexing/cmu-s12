package beauty.android.msg.bean;

import java.io.Serializable;

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
	
	public MProduct(){}
	
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
	public MBrand getBrand() {
		return brand;
	}
	public void setBrand(MBrand brand) {
		this.brand = brand;
	}
	public MCategory getCategory() {
		return category;
	}
	public void setCategory(MCategory category) {
		this.category = category;
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
	 * @return the benefits
	 */
	public MBenefit[] getBenefits() {
		return benefits;
	}

	/**
	 * @param benefits the benefits to set
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
	 * @param tags the tags to set
	 */
	public void setTags(MTag[] tags) {
		this.tags = tags;
	}
}
