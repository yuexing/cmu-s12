package beauty.web.model;

import org.genericdao.PrimaryKey;

@PrimaryKey("id")
public class ProductRetail {

	private int id;
	private int productId;
	private int retailId;
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
	 * @return the retailId
	 */
	public int getRetailId() {
		return retailId;
	}
	/**
	 * @param retailId the retailId to set
	 */
	public void setRetailId(int retailId) {
		this.retailId = retailId;
	}
	
	
}
