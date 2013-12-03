package beauty.web.formbean;

import java.util.ArrayList;

import org.mybeans.form.FormBean;

import beauty.web.action.Type;
import beauty.web.util.StringUtil;

public class SearchForm extends FormBean {
	private String q;
	private String type;
	private String id;
	private Type typeType;
	private int idInt;

	public ArrayList<String> getValidationErrors() {
		ArrayList<String> errors = new ArrayList<String>();

		if (q == null || q.length() == 0) {
			errors.add("query is null");
		}

		if (type != null) {
			// init type and id
			try {
				typeType = Type.valueOf(type);
			} catch (IllegalArgumentException e) {
				errors.add("type " + type + " is not valid");
			}
			
			try {
				idInt = Integer.parseInt(id);
			} catch (NumberFormatException e) {
				errors.add("id " + id + " is not an integer");
			}
			
			if(errors.size() > 0){
				return errors;
			}
			
			if(idInt == -1){
				errors.add("shoud select an id under " + type);
			}
		}
		return errors;
	}

	/**
	 * @return the q
	 */
	public String getQ() {
		return q;
	}

	/**
	 * @param q the q to set
	 */
	public void setQ(String q) {
		this.q = q;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type the type to set
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
	 * @param id the id to set
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
	 * @param typeType the typeType to set
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
	 * @param idInt the idInt to set
	 */
	public void setIdInt(int idInt) {
		this.idInt = idInt;
	}	
}
