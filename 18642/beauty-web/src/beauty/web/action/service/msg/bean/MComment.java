package beauty.web.action.service.msg.bean;

import beauty.web.model.Comment;

/**
 * A comment will contain all its replys and we care more about the username
 * 
 * @author amixyue
 * 
 */
public class MComment {

	private int id;
	private int replyId = -1; // if the comment is a reply

	private int productId = -1;
	private String productName;

	private int userId = -1;
	private String userName;

	private boolean origin = false;
	private String content;

	private String image;

	private long timestamp;
	private int replyCount;

	private MComment[] comments;

	public MComment(Comment c) {
		this.id = c.getId();
		this.replyId = c.getReplyId();

		// fill product name later
		this.productId = c.getProductId();

		// fill user name later
		this.userId = c.getUserId();

		this.origin = c.isOrigin();
		this.content = c.getContent();
		if (c.getAttachmentId() != -1)
			this.image = "view.do?id=" + c.getAttachmentId();

		this.timestamp = c.getTimestamp();
		this.replyCount = c.getReplyCount();
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
	 * @return the productName
	 */
	public String getProductName() {
		return productName;
	}

	/**
	 * @param productName
	 *            the productName to set
	 */
	public void setProductName(String productName) {
		this.productName = productName;
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
	 * @return the userName
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * @param userName
	 *            the userName to set
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * @return the origin
	 */
	public boolean isOrigin() {
		return origin;
	}

	/**
	 * @param origin
	 *            the origin to set
	 */
	public void setOrigin(boolean origin) {
		this.origin = origin;
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
	 * @return the image
	 */
	public String getImage() {
		return image;
	}

	/**
	 * @param image
	 *            the image to set
	 */
	public void setImage(String image) {
		this.image = image;
	}

	/**
	 * @return the timestamp
	 */
	public long getTimestamp() {
		return timestamp;
	}

	/**
	 * @param timestamp
	 *            the timestamp to set
	 */
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
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
	 * @return the comments
	 */
	public MComment[] getComments() {
		return comments;
	}

	/**
	 * @param comments
	 *            the comments to set
	 */
	public void setComments(MComment[] comments) {
		this.comments = comments;
	}
}
