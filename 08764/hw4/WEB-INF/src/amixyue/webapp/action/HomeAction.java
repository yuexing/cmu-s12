package amixyue.webapp.action;

import java.util.*;

import javax.servlet.http.*;

import org.apache.log4j.Logger;
import org.genericdao.MatchArg;
import org.genericdao.RollbackException;

import amixyue.webapp.dao.*;
import amixyue.webapp.model.*;
/*
 * Yue Xing, yuexing@andrew.cmu.edu, 08764
 */
public class HomeAction extends Action {
	private static final Logger log = Logger.getLogger(WelcomeAction.class);
	private final int count = 10;
	private StoryDao storyDao;
	private CommentDao commentDao;
	private FollowDao followDao;
	private UserDao userDao;

	public HomeAction(Model model) {
		storyDao = model.getStoryDao();
		commentDao = model.getCommentDao();
		followDao = model.getFollowDao();
		userDao = model.getUserDao();
	}

	@Override
	public String getName() {
		return "home.do";
	}

	@SuppressWarnings("static-access")
	@Override
	public String perform(HttpServletRequest request) {
		User u = null;
		int uid = 0;
		boolean isFollowing = false;
		//session user
		User su = (User) request.getSession().getAttribute("user");
		int suid = su.getUid();	
		
		if(request.getParameter("uid")!=null){
			uid  = Integer.parseInt(request.getParameter("uid"));
			try {
				u = userDao.read(uid);
				if(u==null){
					return "welcome.do";
				}
			} catch (RollbackException e) {
				e.printStackTrace();
			}
		}else{
			u = su;
			uid = suid;
		}		
		
		// story
		ArrayList<Story> storys = new ArrayList<Story>();
		ArrayList<StoryWrapper> sws = new ArrayList<StoryWrapper>();
		storyDao.getStorysByUser(uid, storys);
		
		// following	
		ArrayList<User> friends = new ArrayList<User>();
		try {			
			//uid2
			Follow[] fss = followDao.match(MatchArg.equals("uid2",
					uid));
			
			for(Follow fs : fss){
				User tmp = userDao.read((Integer)fs.getUid1());
				storyDao.getStorysByUser(tmp.getUid(), storys);
				friends.add(tmp);
			}
			
			fss = followDao.match(MatchArg.equals("uid1", uid));
			for(Follow f: fss){
				if(f.getUid2() == suid){
					isFollowing = true;
				}
			}
		} catch (RollbackException e) {
			e.printStackTrace();
		}
		
		//sort
		Collections.sort(storys);
		if(storys.size() > count){
			storys = (ArrayList<Story>) storys.subList(0, count);
		}
		
		//comment
		for(Story s: storys){
			Comment[] comments;
			try {
				comments = commentDao.match(MatchArg.equals("sid", s.getSid()));
				StoryWrapper sw = new StoryWrapper();
				sw.setStory(s);
				sw.addComments(comments);
				sw.orderComments();
				sws.add(sw);
			} catch (RollbackException e) {
				e.printStackTrace();
			}			
		}
		
		request.setAttribute("storys", sws);
		request.setAttribute("friends", friends);
		request.setAttribute("isFollowing", isFollowing);
		request.setAttribute("homeuser", u);
		return "home.jsp";
	}

}
