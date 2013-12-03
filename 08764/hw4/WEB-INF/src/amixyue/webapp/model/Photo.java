package amixyue.webapp.model;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.genericdao.PrimaryKey;

/*
 * Yue Xing, yuexing@andrew.cmu.edu, 08764
 */
@PrimaryKey("uid")
public class Photo {

	public static final List<String> EXTENSIONS = Collections.unmodifiableList(Arrays.asList( new String[] {
			".jpg", ".gif", ".JPG"
	} ));
	
	private int uid;
	private byte[] bytes;
	private String contentType;
	
	public byte[] getBytes() {
		return bytes;
	}
	public void setBytes(byte[] bytes) {
		this.bytes = bytes;
	}
	public String getContentType() {
		return contentType;
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	public int getUid() {
		return uid;
	}
	public void setUid(int uid) {
		this.uid = uid;
	}
}
