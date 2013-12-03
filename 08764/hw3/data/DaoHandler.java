package data;

import hw2.amixyue.dao.annotation.Column;
import hw2.amixyue.dao.annotation.Table;

import java.lang.reflect.*;
import java.sql.*;
import java.text.*;
import java.util.*;
import java.util.Date;

import lombok.extern.log4j.Log4j;
/**
 * 
 * @author Yue Xing, yuexing@andrew.cmu.edu, 08764
 * @date Feb 14
 * TODO: why not make it static?
 */
class Tuple<A, B> {
	public final A cols;
	public final B values;

	public Tuple(A a, B b) {
		cols = a;
		values = b;
	}
}

@Log4j
public class DaoHandler<T> implements InvocationHandler {

	private static Map<String, ArrayList<String>> tbCache;
	private Class<T> type;

	static {
		tbCache = new HashMap<String, ArrayList<String>>();
	}

	public DaoHandler(Class<T> clazz) {
		this.type = clazz;
	}

	private int create(Method method, Object[] args) throws Throwable {

		String sql = "insert into ";
		String colStr = "";
		String valueStr = "";
		ArrayList<String> cols = new ArrayList<String>();
		ArrayList<String> values = new ArrayList<String>();
		String tbname = type.getSimpleName().toLowerCase();
		@SuppressWarnings("rawtypes")
		ArrayList<Tuple> tuples = new ArrayList<Tuple>();

		if (type.isAnnotationPresent(Table.class)) {
			tbname = type.getAnnotation(Table.class).name();
		}

		// ignore inherit
		Field[] fields = type.getDeclaredFields();
		boolean meta = true;
		int ti = 0;
		for (Object arg : args) {
			for (Field field : fields) {
				field.setAccessible(true);
				Column column = field.getAnnotation(Column.class);
				Class<?> fieldtype = field.getType();
				// metadata or table scheme
				if (tbCache.containsKey(tbname)) {
					//log.debug("using cache");
					cols = tbCache.get(tbname);
				} else {
					if (meta) {
						if (column != null) {
							if (column.exclude())
								continue;
							if(!column.name().equals(""))
								cols.add(column.name());
							else{
								cols.add(field.getName());
							}
						} else {
							cols.add(field.getName());
						}
					}
				}
				// just care about timezone
				if (column != null) {
					if (column.exclude())
						continue;	
				}
				if(column != null && column.jdbcType() != null){
					if (column.jdbcType().equals("varchar")) {
						if (fieldtype.equals(TimeZone.class)){
						if(field.get(arg) != null) {
							values.add("'"+((TimeZone) field.get(arg)).getID()+"'");
						}else{
							values.add("'"+"'");
						}
							}
						// TODO:any shortcut with Array
					}
				}else if (fieldtype.equals(int.class)) {
					values.add(field.getInt(arg) + "");
				} else if (fieldtype.equals(long.class)) {
					values.add(field.getLong(arg) + "");
				} else if (fieldtype.equals(double.class)) {
					values.add(field.getDouble(arg) + "");
				} else if (fieldtype.equals(boolean.class)) {
					// tinyint(1)
					values.add(field.getInt(arg) + "");
				} else if (fieldtype.equals(Date.class)) {
					DateFormat df = new SimpleDateFormat("yyyy-MM-DD");
					if (fieldtype
							.isAnnotationPresent(hw2.amixyue.dao.annotation.DateFormat.class)) {
						df = new SimpleDateFormat(fieldtype.getAnnotation(
								hw2.amixyue.dao.annotation.DateFormat.class)
								.formate());
					}
					Date date = (Date) field.get(arg);
					values.add(df.format(date));
				} else if (fieldtype.isArray()
						&& fieldtype.getComponentType().equals(byte.class)) {
					// TODO: shortcut with blob
					byte[] blob = (byte[]) field.get(arg);
					values.add("?");
					// cast for me
					Tuple<Integer, byte[]> t = new Tuple<Integer, byte[]>(ti++,
							blob);
					tuples.add(t);
				} else {
					Object obj = field.get(arg);
					if (obj == null) {
						values.add("''");
					} else {
						values.add("'" + obj + "'");
					}

				}
			}
			meta = false;
		}
		// cache
		if (!tbCache.containsKey(tbname)) {
			tbCache.put(tbname, cols);
			log.debug("cached: " + tbname);
		}
		colStr = cols.toString();
		valueStr = values.toString();
		sql = sql + tbname + " (" + colStr.substring(1, colStr.length() - 1)
				+ ") values(" + valueStr.subSequence(1, valueStr.length() - 1)
				+ ")";
		log.debug(sql);

		 Connection conn = ConnPool.getConn();
		 PreparedStatement st;
		 try {
		 st = conn.prepareStatement(sql);
		 for(@SuppressWarnings("unused") Tuple<?,?> t : tuples){
		 //
		 }
		 st.execute(sql);
		 st.close();
		 conn.commit();
		 ConnPool.returnConn(conn);
		 } catch (SQLException e) {
		 e.printStackTrace();
		 ConnPool.returnConn(conn);
		 }
		// execute sql using preparedstatement
		// tuples.length>0
		return 0;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		MethodName methodName = MethodName.valueOf(method.getName());
		switch (methodName) {
		case create:
			return create(method, args);
		case read:
			log.debug("same reflection, but need a lot of work, so wait for next time");
			return null;
		default:
			return null;
		}
	}

}