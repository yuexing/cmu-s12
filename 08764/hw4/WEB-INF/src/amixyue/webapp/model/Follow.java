package amixyue.webapp.model;

import org.genericdao.PrimaryKey;
/**
 * uid2 will follow uid1
 * Yue Xing, yuexing@andrew.cmu.edu, 08764
 */
@PrimaryKey("fid")
public class Follow {

	private int fid;
	private int uid1;
	private int uid2;
	
	public int getFid() {
		return fid;
	}
	public void setFid(int fid) {
		this.fid = fid;
	}
	public int getUid1() {
		return uid1;
	}
	public void setUid1(int uid1) {
		this.uid1 = uid1;
	}
	public int getUid2() {
		return uid2;
	}
	public void setUid2(int uid2) {
		this.uid2 = uid2;
	}
	
}
