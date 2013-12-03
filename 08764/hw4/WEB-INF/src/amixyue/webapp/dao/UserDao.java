package amixyue.webapp.dao;

import java.util.ArrayList;
import java.util.Collections;

import org.genericdao.*;
import amixyue.webapp.model.User;

/*
 * Yue Xing, yuexing@andrew.cmu.edu, 08764
 */
public class UserDao extends GenericDAO<User> {
	
	public UserDao(String tableName, ConnectionPool connectionPool) throws DAOException {
		super(User.class, tableName, connectionPool);
//		this.tableName = tableName;
//		this.pool = connectionPool;
	}
	
	public ArrayList<User> getUsersByScount(int count){
		ArrayList<User> users = new ArrayList<User>();
		User[] us = null;
		try {
			us = this.match();
		} catch (RollbackException e) {
			e.printStackTrace();
		}
		for(User u: us){
			users.add(u);
		}
		Collections.sort(users);
		if(users.size() > count){
			users = (ArrayList<User>) users.subList(0, count);
		}
		return users;
	}
	
	public static void main(String[] args) throws Exception{
		String jdbcDriver = "com.mysql.jdbc.Driver";
		String jdbcURL    = "jdbc:mysql:///webapp";		
		ConnectionPool pool = new ConnectionPool(jdbcDriver,jdbcURL);
		UserDao userDao = new UserDao("yuexing_user", pool);
		User u = new User();
		userDao.createAutoIncrement(u);
	}
}
