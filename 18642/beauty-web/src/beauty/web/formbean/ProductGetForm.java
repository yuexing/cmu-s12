package beauty.web.formbean;

import java.util.ArrayList;

import beauty.web.model.Product;
import beauty.web.util.StringUtil;

public class ProductGetForm extends BaseForm {

	private String name;
	private String brandId;
	private String categoryId;
	private String price;
	private String introduction;
	/* muli-select tag and benefits */
	private String tagIds;
	private String benefitIds;
	private int brandIdInt;
	private int categoryIdInt;
	
	public void populateForm(Product p){
		this.name = p.getName();
		this.price = new Double(p.getPrice()).toString();
		this.introduction = p.getIntroduction();
		this.brandId = new Integer(p.getBrandId()).toString();
		this.categoryId = new Integer(p.getCategoryId()).toString();
		// tags and benefits
	}

	public ArrayList<String> getValidationErrors() {
		ArrayList<String> errors = new ArrayList<String>();

		errors.addAll(super.getValidationErrors());
		
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
		this.introduction = StringUtil.sanitize(introduction);
	}

	/**
	 * @return the tagIds
	 */
	public String getTagIds() {
		return tagIds;
	}

	/**
	 * @param tagIds
	 *            the tagIds to set
	 */
	public void setTagIds(String tagIds) {
		this.tagIds = StringUtil.sanitize(tagIds);
	}

	/**
	 * @return the benefitIds
	 */
	public String getBenefitIds() {
		return benefitIds;
	}

	/**
	 * @param benefitIds
	 *            the benefitIds to set
	 */
	public void setBenefitIds(String benefitIds) {
		this.benefitIds = StringUtil.sanitize(benefitIds);
	}

	/**
	 * @return the brandId
	 */
	public String getBrandId() {
		return brandId;
	}

	/**
	 * @param brandId
	 *            the brandId to set
	 */
	public void setBrandId(String brandId) {
		this.brandId = StringUtil.sanitize(brandId);
	}

	/**
	 * @return the categoryId
	 */
	public String getCategoryId() {
		return categoryId;
	}

	/**
	 * @param categoryId
	 *            the categoryId to set
	 */
	public void setCategoryId(String categoryId) {
		this.categoryId = StringUtil.sanitize(categoryId);
	}

	/**
	 * @return the price
	 */
	public String getPrice() {
		return price;
	}

	/**
	 * @param price
	 *            the price to set
	 */
	public void setPrice(String price) {
		this.price = StringUtil.sanitize(price);
	}

	/**
	 * @return the brandIdInt
	 */
	public int getBrandIdInt() {
		return brandIdInt;
	}

	/**
	 * @param brandIdInt the brandIdInt to set
	 */
	public void setBrandIdInt(int brandIdInt) {
		this.brandIdInt = brandIdInt;
	}

	/**
	 * @return the categoryIdInt
	 */
	public int getCategoryIdInt() {
		return categoryIdInt;
	}

	/**
	 * @param categoryIdInt the categoryIdInt to set
	 */
	public void setCategoryIdInt(int categoryIdInt) {
		this.categoryIdInt = categoryIdInt;
	}
}