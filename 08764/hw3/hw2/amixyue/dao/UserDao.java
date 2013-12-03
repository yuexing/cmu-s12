package hw2.amixyue.dao;

import hw2.amixyue.model.User;
/**
 * 
 * @author Yue Xing, yuexing@andrew.cmu.edu, 08764
 *
 */
public interface UserDao {
	
	public int count(); 

	public int create(User user);

	public void update(int id, User user);

	public void delete(int id);

	public User[] findAll();

	public User findByPrimaryKey(int id);
	
	public User read(int id);

	/**
	 * email is kind of primary key
	 * make it pk?
	 * @param email
	 * @return
	 */
	public User findByEmail(String email);
}
