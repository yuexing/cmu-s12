package hw2.amixyue.dao.imp;

import hw2.amixyue.dao.UserDao;
import hw2.amixyue.db.ArrayDB;
import hw2.amixyue.model.User;

import java.util.*;

/**
 * 
 * @author Yue Xing, yuexing@andrew.cmu.edu, 08764
 *
 */
public class ArrayUserDao implements UserDao {

	
	
	@Override
	public int create(User user) {
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


	@Override
	public User read(int id) {
		// TODO Auto-generated method stub
		return null;
	}

}
