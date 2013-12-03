package beauty.web.formbean;

import java.util.ArrayList;

import org.mybeans.form.FormBean;

import beauty.web.util.StringUtil;

public class BaseForm extends FormBean {

	protected String force;
	protected String edit;
	// used in edit mode
	protected String id;
	protected boolean get = false;

	public ArrayList<String> getValidationErrors() {
		ArrayList<String> errors = new ArrayList<String>();

		if (this.isEdit()) {
			if (id == null || id.length() == 0) {
				errors.add("empty id for edit!");
				
			}
			
			if (errors.size() > 0)
				return errors;

			try {
				Integer.parseInt(id);
			} catch (NumberFormatException e) {
				errors.add("id " + id + " is not an integer");
			}
		}
		return errors;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id) {
		this.id = StringUtil.sanitize(id);
	}

	/**
	 * @return the get
	 */
	public boolean isGet() {
		return get;
	}

	/**
	 * @param get
	 *            the get to set
	 */
	public void setGet(boolean get) {
		this.get = get;
	}

	/**
	 * @return the force
	 */
	public boolean isForce() {
		return force != null && force.equals("true");
	}

	/**
	 * @param force the force to set
	 */
	public void setForce(String force) {
		this.force = force;
	}

	/**
	 * @return the edit
	 */
	public boolean isEdit() {
		return edit != null && edit.equals("true");
	}

	/**
	 * @param edit the edit to set
	 */
	public void setEdit(String edit) {
		this.edit = edit;
	}
}
