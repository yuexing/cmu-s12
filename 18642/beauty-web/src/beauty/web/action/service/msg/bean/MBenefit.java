package beauty.web.action.service.msg.bean;

public class MBenefit {

	private int id;
	private String name;
	private int productCount;
	
	// this is to control who can modify what
	private int owner;

	public MBenefit(beauty.web.model.Benefit be) {
		this.id = be.getId();
		this.name = be.getName();
		this.productCount = be.getProductCount();
		this.owner = be.getOwner();
	}

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
	 * @return the productCount
	 */
	public int getProductCount() {
		return productCount;
	}

	/**
	 * @param productCount
	 *            the productCount to set
	 */
	public void setProductCount(int productCount) {
		this.productCount = productCount;
	}

	public int getOwner() {
		return owner;
	}

	public void setOwner(int owner) {
		this.owner = owner;
	}

}
