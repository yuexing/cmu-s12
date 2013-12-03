package beauty.web.formbean;

import java.util.ArrayList;

import org.mybeans.form.FormBean;

import beauty.web.util.StringUtil;

public class RateForm extends FormBean{

	private String productId;
	private String thisRate;

	public ArrayList<String> getValidationErrors() {
		ArrayList<String> errors = new ArrayList<String>();

		if (productId == null || productId.length() == 0) {
			errors.add("productId is null");
		}

		if (thisRate == null || thisRate.length() == 0) {
			errors.add("thisRate is null");
		}

		if(errors.size() > 0)
		return errors;
		
		try{
			Integer.parseInt(productId);
		} catch(NumberFormatException e){
			errors.add("productId " + productId + " is not an integer");
		}
		
		try{
			Integer.parseInt(thisRate);
		} catch(NumberFormatException e){
			errors.add("thisRate " + thisRate + " is not an integer");
		}
		
		return errors;
	}

	/**
	 * @return the productId
	 */
	public String getProductId() {
		return productId;
	}

	/**
	 * @param productId
	 *            the productId to set
	 */
	public void setProductId(String productId) {
		this.productId = StringUtil.sanitize(productId);
	}

	/**
	 * @return the thisRate
	 */
	public String getThisRate() {
		return thisRate;
	}

	/**
	 * @param thisRate
	 *            the thisRate to set
	 */
	public void setThisRate(String thisRate) {
		this.thisRate =  StringUtil.sanitize(thisRate);
	}

}
