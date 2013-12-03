package dao.hw2.amixyue;

import model.hw2.amixyue.User;
/**
 * 
 * @author Yue Xing, yuexing@andrew.cmu.edu, 08764
 *
 */
public interface UserDao {
	
	public int count(); 

	public int insert(User user);

	public void update(int id, User user);

	public void delete(int id);

	public User[] findAll();

	public User findByPrimaryKey(int id);

	/**
	 * email is kind of primary key
	 * make it pk?
	 * @param email
	 * @return
	 */
	public User findByEmail(String email);
}
