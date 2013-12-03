package beauty.web.model;

import org.genericdao.PrimaryKey;

@PrimaryKey("id")
public class Product {

	private int id;
	private int attachmentId = -1;
	private String name;
	private String stdForm; // stdform
	private int brandId;
	private int categoryId;
	private double price;
	private int rate = 0;
	private int rateNum = 0;
	private String introduction;
	private int commentNum = 0;

	// this is to control who can modify what
	private int owner;

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(int id) {
		this.id = id;
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
	 * @return the brand_id
	 */
	/**
	 * @return the attachmentId
	 */
	public int getAttachmentId() {
		return attachmentId;
	}

	/**
	 * @param attachmentId
	 *            the attachmentId to set
	 */
	public void setAttachmentId(int attachmentId) {
		this.attachmentId = attachmentId;
	}

	/**
	 * @return the brandId
	 */
	public int getBrandId() {
		return brandId;
	}

	/**
	 * @param brandId
	 *            the brandId to set
	 */
	public void setBrandId(int brandId) {
		this.brandId = brandId;
	}

	/**
	 * @return the categoryId
	 */
	public int getCategoryId() {
		return categoryId;
	}

	/**
	 * @param categoryId
	 *            the categoryId to set
	 */
	public void setCategoryId(int categoryId) {
		this.categoryId = categoryId;
	}

	/**
	 * @return the price
	 */
	public double getPrice() {
		return price;
	}

	/**
	 * @param price
	 *            the price to set
	 */
	public void setPrice(double price) {
		this.price = price;
	}

	/**
	 * @return the rate
	 */
	public int getRate() {
		return rate;
	}

	/**
	 * @param rate
	 *            the rate to set
	 */
	public void setRate(int rate) {
		this.rate = rate;
	}

	/**
	 * @return the rateNum
	 */
	public int getRateNum() {
		return rateNum;
	}

	/**
	 * @param rateNum
	 *            the rateNum to set
	 */
	public void setRateNum(int rateNum) {
		this.rateNum = rateNum;
	}

	/**
	 * @return the introduction
	 */
	public String getIntroduction() {
		return introduction;
	}

	/**
	 * @param introduction
	 *            the introduction to set
	 */
	public void setIntroduction(String introduction) {
		this.introduction = introduction;
	}

	/**
	 * @return the commentNum
	 */
	public int getCommentNum() {
		return commentNum;
	}

	/**
	 * @param commentNum
	 *            the commentNum to set
	 */
	public void setCommentNum(int commentNum) {
		this.commentNum = commentNum;
	}

	/**
	 * @return the stdForm
	 */
	public String getStdForm() {
		return stdForm;
	}

	/**
	 * @param stdForm
	 *            the stdForm to set
	 */
	public void setStdForm(String stdForm) {
		this.stdForm = stdForm;
	}

	public int getOwner() {
		return owner;
	}

	public void setOwner(int owner) {
		this.owner = owner;
	}

}
