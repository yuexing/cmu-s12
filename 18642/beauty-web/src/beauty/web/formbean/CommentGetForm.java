package beauty.web.formbean;

import java.util.ArrayList;

import beauty.web.model.Comment;
import beauty.web.model.Comment.Type;
import beauty.web.util.StringUtil;

public class CommentGetForm extends BaseForm {

	private String content;
	private String timestamp; // good for in18
	private String pid;
	private String replyId;
	private String userId;
	private String type;

	public CommentGetForm() {
		this.get = true;
	}

	public void populateForm(Comment co) {
		this.pid = new Integer(co.getProductId()).toString();
		this.replyId = new Integer(co.getReplyId()).toString();
		this.userId = new Integer(co.getUserId()).toString();
		this.content = co.getContent();
		this.type = co.getType().toString();
	}

	/**
	 * @return the userId
	 */
	public String getUserId() {
		return userId;
	}

	/**
	 * @param userId
	 *            the userId to set
	 */
	public void setUserId(String userId) {
		this.userId = StringUtil.sanitize(userId);
	}

	public ArrayList<String> getValidationErrors() {
		ArrayList<String> errors = new ArrayList<String>();

		errors.addAll(super.getValidationErrors());
		
		if (!this.isEdit()) {
			if (pid == null || pid.length() == 0) {
				errors.add("pid is null");
			}

			if (userId == null || userId.length() == 0) {
				errors.add("userId is null");
			}
			
			if (errors.size() > 0)
				return errors;

			try {
				Integer.parseInt(pid);
			} catch (NumberFormatException e) {
				errors.add("pid " + pid + " is not an integer");
			}

			try {
				Integer.parseInt(userId);
			} catch (NumberFormatException e) {
				errors.add("userId " + userId + " is not an integer");
			}
			
			// reply
			if (replyId != null && replyId.length() != 0) {
				try {
					Integer.parseInt(replyId);
				} catch (NumberFormatException e) {
					errors.add("replyId " + replyId + " is not an integer");
				}
				this.type = Type.reply.toString();
			} else {
				this.type = Type.origin.toString();
			}
		}

		return errors;
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
		this.content = StringUtil.sanitize(content);
	}

	/**
	 * @return the pid
	 */
	public String getPid() {
		return pid;
	}

	/**
	 * @param pid
	 *            the pid to set
	 */
	public void setPid(String pid) {
		this.pid = StringUtil.sanitize(pid);
	}

	/**
	 * @return the replyId
	 */
	public String getReplyId() {
		return replyId;
	}

	/**
	 * @param replyId
	 *            the replyId to set
	 */
	public void setReplyId(String replyId) {
		this.replyId = StringUtil.sanitize(replyId);
	}

	/**
	 * @return the timestamp
	 */
	public String getTimestamp() {
		return timestamp;
	}

	/**
	 * @param timestamp
	 *            the timestamp to set
	 */
	public void setTimestamp(String timestamp) {
		this.timestamp = StringUtil.sanitize(timestamp);
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
		this.type = type;
	}
	
}
