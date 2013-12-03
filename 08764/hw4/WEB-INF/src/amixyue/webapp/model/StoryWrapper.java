package amixyue.webapp.model;

import java.util.ArrayList;
import java.util.Collections;
/*
 * Yue Xing, yuexing@andrew.cmu.edu, 08764
 */
public class StoryWrapper {

	private Story story;
	private ArrayList<Comment> comments;

	public StoryWrapper(){
		this.comments = new ArrayList<Comment>();
	}
	
	public void addComments(Comment[] cs) {
		for (Comment c : cs) {
			this.comments.add(c);
		}
	}

	public void orderComments() {
		if (this.comments != null)
			Collections.sort(this.comments);
	}

	public Story getStory() {
		return story;
	}

	public void setStory(Story story) {
		this.story = story;
	}

	public ArrayList<Comment> getComments() {
		return comments;
	}

	public void setComments(ArrayList<Comment> comments) {
		this.comments = comments;
	}
}
