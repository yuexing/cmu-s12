package beauty.web.action.service.msg.bean;

import java.io.Serializable;

import beauty.web.model.Deal;

@SuppressWarnings("serial")
public class MDeal implements Serializable {

	private int id;
	private MRetail retail;
	private String content;

	public MDeal(Deal d){
		this.id = d.getId();
		this.content = d.getContent();
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
	 * @return the retail
	 */
	public MRetail getRetail() {
		return retail;
	}

	/**
	 * @param retail the retail to set
	 */
	public void setRetail(MRetail retail) {
		this.retail = retail;
	}
	
}
