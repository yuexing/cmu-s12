package beauty.web.model;

import org.genericdao.PrimaryKey;

@PrimaryKey("id")
public class Tag implements Comparable<Tag> {

	private int id;
	private String name;
	private String stdForm;
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
	public int compareTo(Tag o) {
		return this.name.compareTo(o.name);
	}

	public String getStdForm() {
		return stdForm;
	}

	public void setStdForm(String stdForm) {
		this.stdForm = stdForm;

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
