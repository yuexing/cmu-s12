package beauty.web.model;

import org.genericdao.PrimaryKey;

@PrimaryKey("id")
public class Brand implements Comparable<Brand> {

	private int id;
	private String name;
	private String stdForm; // stdform
	private int attachmentId = -1;
	private int weight = 0;

	private int productCount = 0;

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

	@Override
	public int compareTo(Brand arg0) {
		return this.name.compareTo(arg0.name);
	}

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

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return name;
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

	/**
	 * @return the weight
	 */
	public int getWeight() {
		return weight;
	}

	/**
	 * @param weight
	 *            the weight to set
	 */
	public void setWeight(int weight) {
		this.weight = weight;
	}

	public int getOwner() {
		return owner;
	}

	public void setOwner(int owner) {
		this.owner = owner;
	}

}
