package data;

import java.util.TimeZone;

//import java.lang.reflect.Array;
//import lombok.extern.log4j.Log4j;
//@Log4j
/**
 * 
 * @author Yue Xing, yuexing@andrew.cmu.edu, 08764
 * @date Feb 14
 * TODO: why not make it static?
 */
public class Test {
	
	public static void main(String[] args){

		User u = new User();
		GenericDao<User> dao = DaoFactory.getDao(User.class);
		
		dao.create(u);
		u.setId(1);
		u.setTzone(TimeZone.getDefault());
		dao.create(u);
	}
}
