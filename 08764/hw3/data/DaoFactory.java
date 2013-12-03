package data;

import java.lang.reflect.Proxy;
/**
 * 
 * @author Yue Xing, yuexing@andrew.cmu.edu, 08764
 * @date Feb 14
 * TODO: why not make it static?
 */
public class DaoFactory<T> {

	@SuppressWarnings("unchecked")
	public static <T> GenericDao<T> getDao(Class<T> clazz){
		return (GenericDao<T>) Proxy.newProxyInstance(
					GenericDao.class.getClassLoader(), 
					new Class[]{GenericDao.class}, 
					new DaoHandler<T>(clazz));
	}
}
