package beauty.web.formbean;

import java.util.ArrayList;

public class BrandGetForm extends BaseForm {
	
	private String name;
	
	public BrandGetForm(){
		this.get = true;
	}

	public ArrayList<String> getValidationErrors() {
		ArrayList<String> errors = new ArrayList<String>();

		errors.addAll(super.getValidationErrors());
		
		return errors;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	
}
