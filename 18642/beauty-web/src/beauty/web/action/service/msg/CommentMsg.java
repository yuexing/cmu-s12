package beauty.web.action.service.msg;

import beauty.web.action.service.msg.bean.MComment;



public class CommentMsg extends BaseMsg {

	private MComment[] comments;
	/**
	 * @return the comments
	 */
	public MComment[] getComments() {
		return comments;
	}

	/**
	 * @param comments the comments to set
	 */
	public void setComments(MComment[] comments) {
		this.comments = comments;
	}

	
}
