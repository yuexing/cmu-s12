package beauty.web.formbean;

import java.util.ArrayList;

import beauty.web.model.Product;
import beauty.web.util.StringUtil;

public class ProductPostForm extends FileForm {

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

	public void populateProduct(Product p) {
		p.setName(name);
		p.setBrandId(brandIdInt);
		p.setCategoryId(categoryIdInt);
		p.setIntroduction(introduction);
		p.setPrice(Double.parseDouble(price));
		p.setStdForm(StringUtil.getStdFrom(name));
	}

	public ArrayList<String> getValidationErrors() {
		ArrayList<String> errors = new ArrayList<String>();

		if (!this.isEdit()) {
			errors.addAll(super.getValidationErrors());
		}
		if (name == null || name.length() == 0) {
			errors.add("name is null");
		}
		if (categoryId == null || categoryId.length() == 0) {
			errors.add("categoryId is null");
		}
		if (price == null || price.length() == 0) {
			errors.add("price is null");
		}
		if (introduction == null || introduction.length() == 0) {
			errors.add("introduction is null");
		}
		if (tagIds == null || tagIds.length() == 0) {
			errors.add("tags is null");
		}
		if (benefitIds == null || benefitIds.length() == 0) {
			errors.add("benefits is null");
		}

		if (!this.isEdit() && (brandId == null || brandId.length() == 0)) {
			errors.add("brandId is null");
		}

		if (errors.size() > 0)
			return errors;
		if (!this.isEdit()) {
			try {
				brandIdInt = Integer.parseInt(brandId);
			} catch (NumberFormatException e) {
				errors.add("brandId " + brandId + " is not an integer");
			}
		}

		try {
			categoryIdInt = Integer.parseInt(categoryId);
		} catch (NumberFormatException e) {
			errors.add("categoryId " + categoryId + " is not an integer");
		}

		try {
			Double.parseDouble(price);
		} catch (NumberFormatException e) {
			errors.add("price " + price + " is not an integer");
		}

		if (errors.size() > 0)
			return errors;

		if (!this.isEdit()) {
			if (brandIdInt == -1) {
				errors.add("shoud select an id under brand");
			}
		}

		if (categoryIdInt == -1) {
			errors.add("shoud select an id under category");
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
	 * @param brandIdInt
	 *            the brandIdInt to set
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
	 * @param categoryIdInt
	 *            the categoryIdInt to set
	 */
	public void setCategoryIdInt(int categoryIdInt) {
		this.categoryIdInt = categoryIdInt;
	}
}