package beauty.web.model;

import org.genericdao.PrimaryKey;

@PrimaryKey("id")
public class ProductBenefit {

	private int id;
	private int productId;
	private int benefitId;
	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}
	/**
	 * @return the productId
	 */
	public int getProductId() {
		return productId;
	}
	/**
	 * @param productId the productId to set
	 */
	public void setProductId(int productId) {
		this.productId = productId;
	}
	/**
	 * @return the benefitId
	 */
	public int getBenefitId() {
		return benefitId;
	}
	/**
	 * @param benefitId the benefitId to set
	 */
	public void setBenefitId(int benefitId) {
		this.benefitId = benefitId;
	}
}
