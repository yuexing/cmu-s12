package beauty.web.model;

import org.genericdao.PrimaryKey;

@PrimaryKey("id")
public class Deal {

	private int id;
	// redundant
	private String retailName;
	private int retailId;
	private String content;

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
	 * @return the retailName
	 */
	public String getRetailName() {
		return retailName;
	}

	/**
	 * @param retailName
	 *            the retailName to set
	 */
	public void setRetailName(String retailName) {
		this.retailName = retailName;
	}

	/**
	 * @return the content
	 */
	public String getContent() {
		return content;
	}

	/**
	 * @param content
	 *            the content to set
	 */
	public void setContent(String content) {
		this.content = content;
	}

	/**
	 * @return the retailId
	 */
	public int getRetailId() {
		return retailId;
	}

	/**
	 * @param retailId
	 *            the retailId to set
	 */
	public void setRetailId(int retailId) {
		this.retailId = retailId;
	}

	public int getOwner() {
		return owner;
	}

	public void setOwner(int owner) {
		this.owner = owner;
	}

}
