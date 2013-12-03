package beauty.web.action.service.msg.bean;

import beauty.web.model.Category;

public class MCategory {

	private int id;
	private String name;
	private int productCount;

	// this is to control who can modify what
	private int owner;

	public MCategory(Category c) {
		this.id = c.getId();
		this.name = c.getName();
		this.productCount = c.getProductCount();
		this.owner = c.getOwner();
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

	public int getProductCount() {
		return productCount;
	}

	public void setProductCount(int productCount) {
		this.productCount = productCount;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "MCategory [id=" + id + ", name=" + name + ", productCount="
				+ productCount + "]";
	}

	public int getOwner() {
		return owner;
	}

	public void setOwner(int owner) {
		this.owner = owner;
	}

}
