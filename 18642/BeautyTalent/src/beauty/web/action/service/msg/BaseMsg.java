package beauty.web.action.service.msg;

import java.util.*;

public class BaseMsg {
	
	List<String> errors;
	

	/**
	 * @return the errors
	 */
	public List<String> getErrors() {
		return errors;
	}

	/**
	 * @param errors2 the errors to set
	 */
	public void setErrors(List<String> errors2) {
		this.errors = errors2;
	}
	
	public void addError(String e){
		if(this.errors == null){
			this.errors = new ArrayList<String>();
		}
		this.errors.add(e);
	}
} 
