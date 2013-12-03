package beauty.web.action.service.msg;

import beauty.web.action.service.msg.bean.MRetail;


public class RetailMsg extends BaseMsg{

	private MRetail[] retails;

	/**
	 * @return the retails
	 */
	public MRetail[] getRetails() {
		return retails;
	}

	/**
	 * @param retails the retails to set
	 */
	public void setRetails(MRetail[] retails) {
		this.retails = retails;
	}
	
	
}
