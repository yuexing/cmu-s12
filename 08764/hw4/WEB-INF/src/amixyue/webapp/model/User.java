package amixyue.webapp.model;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import org.genericdao.PrimaryKey;
/*
 * Yue Xing, yuexing@andrew.cmu.edu, 08764
 */
@PrimaryKey("uid")
public class User implements Comparable<User>{

	private int uid;
	private String fname;
	private String lname;
	private String email;
	private String hashedPassword;
	private String tzone;
	private String signature;
	// counter
	private int scount;
	private int ccount;
	private int follower;
	private int following;
	
	private int offset;
	private int salt;
	
	public boolean checkPassword(String password) {
		return this.hashedPassword.equals(hash(password));
	}

	public int getUid() {
		return uid;
	}

	public void setUid(int uid) {
		this.uid = uid;
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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	//hash password
	public void setPassword(String password) {
		this.salt = newSalt(); 
		this.hashedPassword = hash(password);
	}

	public String getHashedPassword() {
		return hashedPassword;
	}

	public void setHashedPassword(String hashedPassword) {
		this.hashedPassword = hashedPassword;
	}

	public String getTzone() {
		return tzone;
	}

	public void setTzone(String tzone) {
		this.tzone = tzone;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public int getScount() {
		return scount;
	}

	public void setScount(int scount) {
		this.scount = scount;
	}

	public int getCcount() {
		return ccount;
	}

	public void setCcount(int ccount) {
		this.ccount = ccount;
	}

	public int getFollower() {
		return follower;
	}

	public void setFollower(int follower) {
		this.follower = follower;
	}

	public int getFollowing() {
		return following;
	}

	public void setFollowing(int following) {
		this.following = following;
	}

	private String hash(String clearPassword) {
		if (salt == 0)
			return null;

		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("SHA1");
		} catch (NoSuchAlgorithmException e) {
			throw new AssertionError(
					"Can't find the SHA1 algorithm in the java.security package");
		}

		String saltString = String.valueOf(salt);

		md.update(saltString.getBytes());
		md.update(clearPassword.getBytes());
		byte[] digestBytes = md.digest();

		// Format the digest as a String
		StringBuffer digestSB = new StringBuffer();
		for (int i = 0; i < digestBytes.length; i++) {
			int lowNibble = digestBytes[i] & 0x0f;
			int highNibble = (digestBytes[i] >> 4) & 0x0f;
			digestSB.append(Integer.toHexString(highNibble));
			digestSB.append(Integer.toHexString(lowNibble));
		}
		String digestStr = digestSB.toString();

		return digestStr;
	}

	private int newSalt() {
		Random random = new Random();
		return random.nextInt(8192) + 1; // salt cannot be zero
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public int getSalt() {
		return salt;
	}

	public void setSalt(int salt) {
		this.salt = salt;
	}

	@Override
	public int compareTo(User o) {
		if(this.scount < o.scount)
			return 1;
		else if(this.scount > o.scount)
			return -1;
		return 0;
	}
}
