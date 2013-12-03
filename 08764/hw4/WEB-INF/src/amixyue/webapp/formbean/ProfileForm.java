package amixyue.webapp.formbean;

import java.util.ArrayList;
import java.util.TimeZone;

import org.mybeans.form.FileProperty;
import org.mybeans.form.FormBean;

import amixyue.webapp.model.User;
/*
 * Yue Xing, yuexing@andrew.cmu.edu, 08764
 */
public class ProfileForm extends FormBean {

	private String email;
	private String fname;
	private String lname;
	private String tzone;
	private String signature;
	private FileProperty file;
	
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getFname() {
		return fname;
	}
	public void setFname(String fname) {
		this.fname = fname;
	}
	public String getLname() {
		return lname;
	}
	public void setLname(String lname) {
		this.lname = lname;
	}
	public String getTzone() {
		return tzone;
	}
	public void setTzone(String tzone) {
		this.tzone = tzone;
	}
	public String getSignature() {
		return signature;
	}
	public void setSignature(String signature) {
		this.signature = signature;
	}
	public FileProperty getFile() {
		return file;
	}
	public void setFile(FileProperty file) {
		this.file = file;
	}
	
	public ArrayList<String> getValidationErrors() {
		ArrayList<String> errors = new ArrayList<String>();
		
		// wired
		if (file != null) {
			if (file.getBytes().length == 0) {
				errors.add("Zero length file");
			}
		}	
		
		return errors;
	}
	
	public void populateUser(User user){
		user.setEmail(email);
		user.setFname(fname);
		user.setLname(lname);
		user.setSignature(signature);
		user.setTzone(tzone);
		int offset = TimeZone.getTimeZone(tzone).getRawOffset();
		user.setOffset(offset);
	}
}
