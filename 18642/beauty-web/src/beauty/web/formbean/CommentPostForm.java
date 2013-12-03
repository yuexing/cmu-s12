package beauty.web.formbean;

import java.util.ArrayList;

import beauty.web.model.Comment;
import beauty.web.model.Comment.Type;
import beauty.web.util.StringUtil;

public class CommentPostForm extends FileForm {

	private String content;
	private String timestamp; // good for in18
	private String pid;
	private String replyId;
	private String userId; // history reason
	private String type;

	private Type typeType;

	public CommentPostForm() {
		this.get = false;
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

	public void populateComment(Comment co) {
		co.setContent(content);
		co.setTimestamp(Long.parseLong(timestamp));

		if (!this.isEdit()) {
			co.setProductId(Integer.parseInt(pid));
			co.setUserId(Integer.parseInt(this.userId));
			if (typeType == Type.reply)
				co.setReplyId(Integer.parseInt(this.replyId));
			co.setType(typeType);
		}
	}

	public ArrayList<String> getValidationErrors() {
		ArrayList<String> errors = new ArrayList<String>();

		if (content == null || content.length() == 0) {
			errors.add("content is null");
		}
		if (timestamp == null || timestamp.length() == 0) {
			errors.add("timestamp is null");
		}
		if (pid == null || pid.length() == 0) {
			errors.add("pid is null");
		}
		if (userId == null || userId.length() == 0) {
			errors.add("userId is null");
		}
		if (type == null || type.length() == 0) {
			errors.add("type is null");
		}
		if (this.isEdit()) {
			if (id == null || id.length() == 0) {
				errors.add("empty id for edit!");
			}
			
			if (errors.size() > 0)
				return errors;

			try {
				Integer.parseInt(id);
			} catch (NumberFormatException e) {
				errors.add("id " + id + " is not an integer");
			}
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
		try {
			typeType = Type.valueOf(type);
		} catch (Exception e) {
			errors.add("type " + type + "is not valid");
		}

		if (typeType == Type.reply) {
			if (replyId == null || replyId.length() == 0) {
				errors.add("replyId is null");
			} else {
				try {
					Integer.parseInt(replyId);
				} catch (NumberFormatException e) {
					errors.add("replyId " + replyId + " is not an integer");
				}
			}
		}

		try {
			Long.parseLong(timestamp);
		} catch (NumberFormatException e) {
			errors.add("timestamp " + timestamp + " is not an long");
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
	 * @param type
	 *            the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the typeType
	 */
	public Type getTypeType() {
		return typeType;
	}

	/**
	 * @param typeType
	 *            the typeType to set
	 */
	public void setTypeType(Type typeType) {
		this.typeType = typeType;
	}
}
