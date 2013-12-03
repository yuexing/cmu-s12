package amixyue.webapp.dao;

import java.util.ArrayList;
import java.util.Collections;
import org.genericdao.ConnectionPool;
import org.genericdao.DAOException;
import org.genericdao.GenericDAO;
import org.genericdao.MatchArg;
import org.genericdao.RollbackException;

import amixyue.webapp.model.*;
/*
 * Yue Xing, yuexing@andrew.cmu.edu, 08764
 */
public class StoryDao extends GenericDAO<Story> {
	

	public StoryDao(String tableName, ConnectionPool connectionPool) throws DAOException {
		super(Story.class, tableName, connectionPool);
//		this.tableName = tableName;
//		this.pool = connectionPool;
	}
	
	public ArrayList<Story> getStorysOrderByTime(int count){
		ArrayList<Story> storys = new ArrayList<Story>();
		Story[] ss = null;
		try {
			ss = this.match();
		} catch (RollbackException e) {
			e.printStackTrace();
		}
		for(Story s: ss){
			storys.add(s);
		}
		Collections.sort(storys);
		if(storys.size() > count){
			storys = (ArrayList<Story>) storys.subList(0, count);
		}
		return storys;
	}
	
	public void getStorysByUser(int uid, ArrayList<Story> storys){
		try {
			Story[] ss = this.match(MatchArg.equals("uid", uid));
			for(Story s: ss){
				storys.add(s);
			}
		} catch (RollbackException e) {
			e.printStackTrace();
		}
	}
}
