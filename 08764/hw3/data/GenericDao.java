package data;
/**
 * 
 * @author Yue Xing, yuexing@andrew.cmu.edu, 08764
 * @date Feb 14
 * TODO: why not make it static?
 */
public interface GenericDao<T> {
	
	int create(T t);
	
	T[] read(T t);
	
	boolean delete(T t);
	
	boolean updateByPK(T t);
	
	void doit();
}
