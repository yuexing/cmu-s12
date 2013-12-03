package beauty.web.model;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.genericdao.PrimaryKey;

@PrimaryKey("id")
public class Photo {

	public static final List<String> EXTENSIONS = Collections
			.unmodifiableList(Arrays.asList(new String[] { ".jpg", ".gif",
					".JPG" }));

	private int id;
	private byte[] bytes;
	private String contentType;

	// this is to control who can modify what
	private int owner;

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

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	public int getOwner() {
		return owner;
	}

	public void setOwner(int owner) {
		this.owner = owner;
	}

}
