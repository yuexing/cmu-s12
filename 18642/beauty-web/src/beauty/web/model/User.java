package beauty.web.model;

import org.genericdao.PrimaryKey;

@PrimaryKey("id")
public class User {
	
	public static enum Type{
		admin, retail, manufacturer, user
	}
	
	private int id;
	private String email;
	private String password;
	private Type type;
	
	// if the user is a retail
	private int rid = -1;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
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
	public Type getType() {
		return type;
	}
	public void setType(Type type) {
		this.type = type;
	}
	/**
	 * @return the rid
	 */
	public int getRid() {
		return rid;
	}
	/**
	 * @param rid the rid to set
	 */
	public void setRid(int rid) {
		this.rid = rid;
	}
	
	public boolean isRetail(){
		return this.type == Type.retail;
	}
	
	public boolean isAdmin(){
		return this.type == Type.admin;
	}
	
	public boolean isManu(){
		return this.type == Type.manufacturer;
	}
	
	// has added a retail 
	// not allowed to add more
	public boolean isAdded(){
		return this.rid != -1;
	}
}
