package impl.dao.hw2.amixyue;

import org.apache.catalina.core.ApplicationContext;

import model.hw2.amixyue.User;
import dao.hw2.amixyue.UserDao;
import db.hw2.amixyue.ArrayDB;

import java.util.*;

/**
 * 
 * @author Yue Xing, yuexing@andrew.cmu.edu, 08764
 *
 */
public class ArrayUserDao implements UserDao {

	ApplicationContext context;
	
	
	@Override
	public int insert(User user) {
		return ArrayDB.getDB().insert(user);
	}

	
	@Override
	public void update(int id, User user) {
		ArrayDB.getDB().update(id, user);
	}

	
	@Override
	public void delete(int id) {
		ArrayDB.getDB().delete(id);
	}

	
	@Override
	public User[] findAll() {				
		ArrayList<User> users = ArrayDB.getDB().select();
		return (User[])users.toArray();
	}

	
	@Override
	public User findByPrimaryKey(int id) {
		return ArrayDB.getDB().select(id);
	}

	
	@Override
	public User findByEmail(String email) {
		return ArrayDB.getDB().select(email);
	}


	@Override
	public int count() {
		return ArrayDB.getDB().select().size();
	}

}
