package beauty.web.formbean;

import java.util.ArrayList;
import beauty.web.model.Brand;
import beauty.web.util.StringUtil;

public class BrandPostForm extends FileForm {

	private String name;

	public void populateBrand(Brand b) {
		b.setName(name);
		b.setStdForm(StringUtil.getStdFrom(name));
	}

	public ArrayList<String> getValidationErrors() {
		ArrayList<String> errors = new ArrayList<String>();

		if (!this.isEdit()) {
			// when you are editing, there is no need to ask
			// for a file
			errors.addAll(super.getValidationErrors());
		}

		if (name == null || name.length() == 0) {
			errors.add("name is null");
		}
		return errors;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = StringUtil.sanitize(name);
	}
}
