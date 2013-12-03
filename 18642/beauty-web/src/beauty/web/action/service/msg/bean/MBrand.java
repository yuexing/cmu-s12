package beauty.web.action.service.msg.bean;

import beauty.web.model.Brand;

public class MBrand {

	private int id;
	private String name;
	private String image;
	private int productCount;

	// this is to control who can modify what
	private int owner;

	public MBrand(Brand b) {
		this.id = b.getId();
		this.name = b.getName();
		this.image = "view.do?id=" + b.getAttachmentId();
		this.productCount = b.getProductCount();
		this.owner = b.getOwner();
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

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
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
		return "MBrand [id=" + id + ", name=" + name + ", image=" + image
				+ ", productCount=" + productCount + "]";
	}

	public int getOwner() {
		return owner;
	}

	public void setOwner(int owner) {
		this.owner = owner;
	}

}
