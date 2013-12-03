package amixyue.webapp.model;

import java.util.Date;
import org.genericdao.PrimaryKey;
/*
 * Yue Xing, yuexing@andrew.cmu.edu, 08764
 */
@PrimaryKey("sid")
public class Story implements Comparable<Story>{

	private int sid;
	private String content;
	private Date date;
	//redundancy for efficiency
	private String fname;
	private String lname;
	private int uid;
	
	public Story(){
	}
	public int getSid() {
		return sid;
	}
	public void setSid(int sid) {
		this.sid = sid;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
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
	public int getUid() {
		return uid;
	}
	public void setUid(int uid) {
		this.uid = uid;
	}
	@Override
	public int compareTo(Story o) {
		return -(this.date.compareTo(o.date));
	}
}
