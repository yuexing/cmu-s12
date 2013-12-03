package beauty.web.formbean;

import java.util.ArrayList;

import org.mybeans.form.FormBean;

import beauty.web.action.Type;
import beauty.web.util.StringUtil;

public class TypeForm extends FormBean {

	private String type;
	private String id;
	private Type typeType;
	private int idInt;

	public ArrayList<String> getValidationErrors() {
		ArrayList<String> errors = new ArrayList<String>();

		if (type == null || type.length() == 0) {
			errors.add("type is null");
		}

		if (errors.size() > 0) {
			return errors;
		}

		// init type and id
		try {
			typeType = Type.valueOf(type);
		} catch (IllegalArgumentException e) {
			errors.add("type " + type + " is not valid");
		}
		if (typeType != Type.none) {
			try {
				idInt = Integer.parseInt(id);
			} catch (NumberFormatException e) {
				errors.add("id " + id + " is not an integer");
			}
		}

		return errors;
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
	 * @return the typeType
	 */
	public Type getTypeType() {
		return typeType;
	}

	/**
	 * @param typeType
	 *            the typeType to set
	 */
	public void setTypeType(Type typeType) {
		this.typeType = typeType;
	}

	/**
	 * @return the idInt
	 */
	public int getIdInt() {
		return idInt;
	}

	/**
	 * @param idInt
	 *            the idInt to set
	 */
	public void setIdInt(int idInt) {
		this.idInt = idInt;
	}
}
