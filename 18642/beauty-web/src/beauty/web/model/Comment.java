package beauty.web.model;

import org.genericdao.PrimaryKey;

@PrimaryKey("id")
public class Comment implements Comparable<Comment> {

	public static enum Type {
		origin, reply
	}

	private int id;
	private int replyId = -1; // if the comment is a reply
	private int productId = -1;
	private int userId = -1;
	private String content;
	private int attachmentId = -1;
	private long timestamp;
	private int replyCount;

	private Type type;

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

	/**
	 * @return the replyId
	 */
	public int getReplyId() {
		return replyId;
	}

	/**
	 * @param replyId
	 *            the replyId to set
	 */
	public void setReplyId(int replyId) {
		this.replyId = replyId;
	}

	/**
	 * @return the productId
	 */
	public int getProductId() {
		return productId;
	}

	/**
	 * @param productId
	 *            the productId to set
	 */
	public void setProductId(int productId) {
		this.productId = productId;
	}

	/**
	 * @return the userId
	 */
	public int getUserId() {
		return userId;
	}

	/**
	 * @param userId
	 *            the userId to set
	 */
	public void setUserId(int userId) {
		this.userId = userId;
	}

	/**
	 * @return the content
	 */
	public String getContent() {
		return content;
	}

	/**
	 * @param content
	 *            the content to set
	 */
	public void setContent(String content) {
		this.content = content;
	}

	/**
	 * @return the attachmentId
	 */
	public int getAttachmentId() {
		return attachmentId;
	}

	/**
	 * @param attachmentId
	 *            the attachmentId to set
	 */
	public void setAttachmentId(int attachmentId) {
		this.attachmentId = attachmentId;
	}

	@Override
	public int compareTo(Comment o) {
		if (this.timestamp < o.timestamp) {
			return 1;
		} else if (this.timestamp > o.timestamp) {
			return -1;
		} else {
			return 0;
		}
	}

	/**
	 * @return the timestamp
	 */
	public long getTimestamp() {
		return timestamp;
	}

	/**
	 * @param l
	 *            the timestamp to set
	 */
	public void setTimestamp(long l) {
		this.timestamp = l;
	}

	/**
	 * @return the replyCount
	 */
	public int getReplyCount() {
		return replyCount;
	}

	/**
	 * @param replyCount
	 *            the replyCount to set
	 */
	public void setReplyCount(int replyCount) {
		this.replyCount = replyCount;
	}

	/**
	 * @return the type
	 */
	public Type getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(Type type) {
		this.type = type;
	}

	public boolean isOrigin() {
		return this.type == Type.origin;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Comment [id=" + id + ", replyId=" + replyId + ", productId="
				+ productId + ", userId=" + userId + ", content=" + content
				+ ", attachmentId=" + attachmentId + ", timestamp=" + timestamp
				+ ", replyCount=" + replyCount + ", type=" + type + "]";
	}
	
}
