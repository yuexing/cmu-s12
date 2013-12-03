package beauty.web.action.service.msg;

import beauty.web.action.service.msg.bean.MProduct;

public class ProductMsg extends BaseMsg {

	private MProduct[] products;

	/**
	 * @return the products
	 */
	public MProduct[] getProducts() {
		return products;
	}

	/**
	 * @param products the products to set
	 */
	public void setProducts(MProduct[] products) {
		this.products = products;
	}
}
