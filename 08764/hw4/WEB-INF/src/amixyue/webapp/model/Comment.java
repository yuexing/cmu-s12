package amixyue.webapp.model;

import java.util.Date;

import org.genericdao.PrimaryKey;
/*
 * Yue Xing, yuexing@andrew.cmu.edu, 08764
 */
@PrimaryKey("cid")
public class Comment implements Comparable<Comment>{

	private int cid;
	private String content;
	private Date date;
	//foreign key
	private int sid;
	//redundancy
	private String fname;
	private String lname;
	private int uid;
	
	public int getCid() {
		return cid;
	}
	public void setCid(int cid) {
		this.cid = cid;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public int getSid() {
		return sid;
	}
	public void setSid(int sid) {
		this.sid = sid;
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
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	/**
	 * desc
	 */
	@Override
	public int compareTo(Comment c) {
		return -(this.date.compareTo(c.date));
	}
	
	
}
