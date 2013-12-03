package beauty.web.formbean;

import java.util.ArrayList;

import beauty.web.controller.Action;
import beauty.web.model.User;
import beauty.web.util.StringUtil;

public class RegisterForm extends BaseForm {

	private String email;
	private String password;
	private String confirm;
	private String type;
	private String code;
	private User.Type t;

	public ArrayList<String> getValidationErrors() {
		ArrayList<String> errors = new ArrayList<String>();

		if (email == null || email.length() == 0)
			errors.add("E-mail Address is required");
		if (password == null || password.length() == 0)
			errors.add("Password is required");
		if (confirm == null || confirm.length() == 0)
			errors.add("confirmPassword is required");
		if (type == null || type.length() == 0)
			errors.add("did not select a type");
		if(Action.enableInvite && (code == null || code.length() == 0))
			errors.add("invite code is empty");

		if (errors.size() > 0) {
			return errors;
		}

		if (!StringUtil.isEmail(email)) {
			errors.add("E-mail Address format error.e.g.:abc@abc.com");
		}

		if (!password.equals(confirm)) {
			errors.add("passwords should be the same");
		}
		
		try{
			t = User.Type.valueOf(type);
		}catch(Exception e){
			errors.add(e.getMessage());
		}
		return errors;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = StringUtil.sanitize(email);
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = StringUtil.sanitize(password);
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(String type) {
		this.type = StringUtil.sanitize(type);
	}

	/**
	 * @return the t
	 */
	public User.Type getT() {
		return t;
	}

	/**
	 * @param t the t to set
	 */
	public void setT(User.Type t) {
		this.t = t;
	}

	/**
	 * @return the confirm
	 */
	public String getConfirm() {
		return confirm;
	}

	/**
	 * @param confirm the confirm to set
	 */
	public void setConfirm(String confirm) {
		this.confirm = StringUtil.sanitize(confirm);
	}

	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * @param code the code to set
	 */
	public void setCode(String code) {
		this.code = StringUtil.sanitize(code);
	}
	
}