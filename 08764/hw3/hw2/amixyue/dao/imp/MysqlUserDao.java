package hw2.amixyue.dao.imp;

import java.sql.*;
import java.util.TimeZone;
import org.apache.log4j.Logger;

import hw2.amixyue.dao.UserDao;
import hw2.amixyue.dao.conn.ConnPool;
import hw2.amixyue.model.User;

/**
 * 
 * @author Yue Xing, yuexing@andrew.cmu.edu, 08764
 * @date Feb 14
 */
public class MysqlUserDao implements UserDao {

	private static Logger log = Logger.getLogger(MysqlUserDao.class);
	private static int last_id = 1;

	@Override
	public synchronized User read(int id) {
		User u = null;
		Connection conn = ConnPool.getConn();
		String sql = "select * from yuexing_user where id=" + id;
		Statement st;
		try {
			st = conn.createStatement();
			ResultSet rs = st.executeQuery(sql);
			while (rs.next()) {
				u = new User();
				u.setId(id);
				u.setLname(rs.getString("lname"));
				u.setFname(rs.getString("fname"));
				u.setEmail(rs.getString("email"));
				u.setPassword(rs.getString("password"));
				u.setTzone(TimeZone.getTimeZone(rs.getString("tzone")));
				u.setSignature(rs.getString("signature"));
				log.debug("read: " + u);
				break;
			}
			conn.commit();
			st.close();
		} catch (SQLException e) {
			log.debug("table not exist! please create first!");
			log.debug("create will automatically create a yuexing_user for u");

		}
		ConnPool.returnConn(conn);
		return u;
	}

	@Override
	public synchronized int create(User user) {
		Connection conn = ConnPool.getConn();

		// test if table exist
		String sql = "select * from yuexing_user";
		Statement st;
		try {
			st = conn.createStatement();
			st.executeQuery(sql);
			conn.commit();
			st.close();
		} catch (SQLException e) {
			log.debug("table does not exist!");

			sql = "create table yuexing_user (id INT PRIMARY KEY, "
					+ "lname varchar(25),fname varchar(25),"
					+ "email varchar(25),password varchar(25),"
					+ "tzone varchar(25),signature varchar(255));";
			try {
				st = conn.createStatement();
				st.executeUpdate(sql);

				conn.commit();
				st.close();
			} catch (SQLException e1) {
				log.debug("table create error!");
				if (conn != null) {
					try {
						conn.rollback();
						log.debug("Connection rollback...");
					} catch (SQLException e2) {
						e1.printStackTrace();
					}
				}
			}
		} finally {
			ConnPool.returnConn(conn);
		}

		user.setId(last_id++);
		String tzone = "";
		if (user.getTzone() != null)
			tzone = user.getTzone().getID();
		// just for sure
		sql = "insert into yuexing_user values (" + user.getId() + ",'"
				+ (user.getLname() == null ? "" : user.getLname()) + "','"
				+ (user.getFname() == null ? "" : user.getFname()) + "','"
				+ (user.getEmail() == null ? "" : user.getEmail()) + "','"
				+ (user.getPassword() == null ? "" : user.getPassword())
				+ "','" + tzone + "','"
				+ (user.getSignature() == null ? "" : user.getSignature())
				+ "');";

		log.debug(sql);

		try {
			st = conn.createStatement();
			st.executeUpdate(sql);
			conn.commit();
			st.close();
		} catch (SQLException e) {
			log.debug("table is not the same!");
			log.debug("recreate automatically now!");

			sql = "drop table yuexing_user;"
					+ "create table yuexing_user (id INT PRIMARY KEY, "
					+ "lname varchar(25),fname varchar(25),"
					+ "email varchar(25),password varchar(25),"
					+ "tzone varchar(25),signature varchar(255));";
			try {
				st = conn.createStatement();
				st.executeUpdate(sql);
				conn.commit();
				st.close();
			} catch (SQLException e1) {
				log.debug("table create error!");
				if (conn != null) {
					try {
						conn.rollback();
						log.debug("Connection rollback...");
					} catch (SQLException e2) {
						e1.printStackTrace();
					}
				}
			}
		}
		ConnPool.returnConn(conn);
		return last_id;
	}

	/**
	 * The Following are due to historical reason!
	 */
	@Override
	public synchronized int count() {
		Connection conn = ConnPool.getConn();
		String sql = "select count(*) as count from yuexing_user";
		Statement st;
		int count = 0;
		try {
			st = conn.createStatement();
			ResultSet rs = st.executeQuery(sql);
			while (rs.next()) {
				count = rs.getInt("count");
				break;
			}
			conn.commit();
			st.close();
		} catch (SQLException e) {
			log.debug("table not exist! please create first!");
			log.debug("use create will automatically create a yuexing_user for u");
		}
		ConnPool.returnConn(conn);
		return count;
	}

	@Override
	public void update(int id, User user) {

	}

	@Override
	public void delete(int id) {
		// TODO Auto-generated method stub

	}

	@Override
	public User[] findAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public User findByPrimaryKey(int id) {

		return null;
	}

	@Override
	public User findByEmail(String email) {
		User u = null;
		Connection conn = ConnPool.getConn();
		String sql = "select * from yuexing_user where email='" + email + "'";
		Statement st;
		try {
			st = conn.createStatement();
			ResultSet rs = st.executeQuery(sql);
			while (rs.next()) {
				u = new User();
				u.setId(rs.getInt("id"));
				u.setLname(rs.getString("lname"));
				u.setFname(rs.getString("fname"));
				u.setEmail(email);
				u.setPassword(rs.getString("password"));
				u.setTzone(TimeZone.getTimeZone(rs.getString("tzone")));
				u.setSignature(rs.getString("signature"));
				log.debug("read: " + u);
				break;
			}

			conn.commit();
			st.close();
		} catch (SQLException e) {
			log.debug("table not exist! please create first!");
			log.debug("use create will automatically create a yuexing_user for u");
		}
		ConnPool.returnConn(conn);
		return u;
	}

	public static void main(String[] args) {
		MysqlUserDao dao = new MysqlUserDao();
		User u = new User();
		u.setFname("yue");
		dao.create(u);
		dao.read(1);
	}
}
