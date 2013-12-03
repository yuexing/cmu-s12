package beauty.web.formbean;

import java.util.ArrayList;

public class PRForm extends BaseForm {

	private String pid;
	private int pidInt;

	public ArrayList<String> getValidationErrors() {
		ArrayList<String> errors = new ArrayList<String>();
		
		errors.addAll(super.getValidationErrors());
		
		if (pid == null || pid.length() == 0) {
				errors.add("pid is null");
		}
		
		try{
			pidInt = Integer.parseInt(pid);
		} catch(NumberFormatException e){
			errors.add("pid " + pid + " is not an integer");
		}
		return errors;
	}
	
	/**
	 * @return the pid
	 */
	public int getPid() {
		return pidInt;
	}

	/**
	 * @param pid the pid to set
	 */
	public void setPid(String pid) {
		this.pid = pid;
	}
	
	
}
