package beauty.web.action.service.msg.bean;

import beauty.web.model.Tag;

public class MTag {

	private int id;
	private String name;
	private int productCount;

	// this is to control who can modify what
	private int owner;

	public MTag(Tag t) {
		this.id = t.getId();
		this.name = t.getName();
		this.productCount = t.getProductCount();
		this.owner = t.getOwner();
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
		return "MTag [id=" + id + ", name=" + name + ", productCount="
				+ productCount + "]";
	}

	public int getOwner() {
		return owner;
	}

	public void setOwner(int owner) {
		this.owner = owner;
	}
}
