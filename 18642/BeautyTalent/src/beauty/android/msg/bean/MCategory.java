package beauty.android.msg.bean;

import java.io.Serializable;
@SuppressWarnings("serial")
public class MCategory implements Serializable {

	private int id;
	private String name;
	private int productCount;
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
	
	
}
