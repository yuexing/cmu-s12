package beauty.web.formbean;

import java.util.ArrayList;

import beauty.web.model.Deal;
import beauty.web.util.StringUtil;

public class DealForm extends BaseForm {

	private String content;

	public void populateDeal(Deal d) {
		d.setContent(content);
	}

	public ArrayList<String> getValidationErrors() {
		ArrayList<String> errors = new ArrayList<String>();

		errors.addAll(super.getValidationErrors());

		if (!this.isGet()) {
			// add and edit
			if (content == null || content.length() == 0) {
				errors.add("content is null");
			}
		}
		return errors;
	}

	/**
	 * @return the name
	 */
	public String getContent() {
		return content;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setContent(String content) {
		this.content = StringUtil.sanitize(content);
	}
}
