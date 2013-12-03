package hw2.amixyue.db;

import hw2.amixyue.model.User;

import java.util.ArrayList;
import java.util.TimeZone;

import lombok.extern.log4j.Log4j;

/**
 * A representation of database using {@link ArrayList},
 * once ArrayDB loaded by JVM, the database is inited with
 * tables and initial data.
 * <p>
 * make it singleton to add to context
 * write should be synchronized
 * TODO: more tables
 * @author Yue Xing, yuexing@andrew.cmu.edu, 08764
 *
 */
@Log4j
public class ArrayDB {

	private int ID ;
	private ArrayList<User> user_tbl;
	private static ArrayDB instance;
	
	private ArrayDB(){
		user_tbl = new ArrayList<User>();
		TimeZone tzone = TimeZone.getDefault();
		//add 4 users to user
		User tmp = new User(0,"test","test","test@test.com",
				"test",tzone,"this is a signature!this is a signature!");
		user_tbl.add(tmp);
		User tmp1 = new User(0,"test1","test1","test1@test.com",
				"test",tzone,"this is a signature!this is a signature!this is a signature!");
		user_tbl.add(tmp1);
		User tmp2 = new User(0,"test2","test2","test2@test.com",
				"test",tzone,"this is a signature!");
		user_tbl.add(tmp2);
		User tmp3 = new User(0,"test3","test3","test3@test.com",
				"test",tzone,"this is a signature!this is a signature!this is a signature!this is a signature!");
		user_tbl.add(tmp3);
		
		ID = user_tbl.size()-1;
		log.debug("user table: " );
		for(User u : user_tbl){
			log.debug(u);
		}
		log.debug("user table current id: " +ID);
	}
	
	public static ArrayDB getDB(){
		if(instance == null){
			instance = new ArrayDB();
		}
		return instance;
	}
	
	public synchronized int insert(User user) {
		if(user == null) return -1;
		user.setId(ID++);
		user_tbl.add(user);
		log.debug("insert " + user);
		log.debug("current id " + user.getId());
		return user.getId();
	}

	
	public synchronized void update(int id, User user) {
		if(user == null) return;
		if(id < 0) return;
		user_tbl.set(id, user);
		log.debug("update " + user);
	}

	
	public synchronized void delete(int id) {
		if(id < 0) return;
		user_tbl.set(id, null);
		log.debug("delete " + id);
	}
	
	public User select(int id) {
		if(id < 0) return null;
		User tmp = user_tbl.get(id);
		log.debug("select " + tmp);
		return tmp;
	}
	
	public ArrayList<User> select(){	
		log.debug("select ");
		return user_tbl;
	}
	
	/**
	 * TODO: table independent, and select
	 * @param field
	 * @param o
	 * @return
	 */
	public ArrayList<User> select(String field, Object o){
		return null;
	}
	/**
	 * TODO: move away!
	 * @param email
	 * @return
	 */
	public User select(String email){
		for(User tmp : user_tbl){
			if(tmp.getEmail().equals(email)){
				log.debug("select " + tmp);
				return tmp;
			}
		}
		return null;
	}
}
