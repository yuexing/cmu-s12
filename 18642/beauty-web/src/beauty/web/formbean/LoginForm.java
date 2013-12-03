package beauty.web.formbean;

import java.util.ArrayList;

public class LoginForm extends BaseForm {

	private String email;
	private String password;

	public ArrayList<String> getValidationErrors() {
		ArrayList<String> errors = new ArrayList<String>();

		if (email == null || email.length() == 0)
			errors.add("E-mail Address is required");
		if (password == null || password.length() == 0)
			errors.add("Password is required");

		if (errors.size() > 0) {
			return errors;
		}
		return errors;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
