package data;

import java.sql.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

/**
 * 
 * @author Yue Xing, yuexing@andrew.cmu.edu, 08764
 * @date Feb 14
 * TODO: why not make it static?
 */
public class ConnPool {
	private static Logger log = Logger.getLogger(ConnPool.class);
	private static HashSet<Connection> conns;
	private static int size;
	// connection in use
	private static int livecount;
	
	
	static {
		size = 5;
		conns = new HashSet<Connection>();
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			throw new AssertionError(e);
		}
		Connection conn = null;
		for (int i = 0; i < size; i++) {
			try {
				conn = DriverManager.getConnection("jdbc:mysql:///webapp");
				conn.setAutoCommit(false);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			conns.add(conn);
		}
	}

	public synchronized static Connection getConn() {

		while (conns.size() < 1) {
			try {
				ConnPool.class.wait();
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		}

		for (Connection conn : conns) {
			try {
				conns.remove(conn);

				if (!conn.isClosed()) {
					livecount++;
					log.debug("get size: " + conns.size() + ", live: " +livecount);
					return conn;
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		// if size > 0 but no one can use: recursive
		return getConn();
	}

	public synchronized static void returnConn(Connection conn) {
		try {
			if (!conn.isClosed()) {
				if(conns.add(conn)){
					livecount--;// can it be less than 0? if no evil can not
					log.debug("return size: " + conns.size() + ", live: " +livecount);
				}				
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		// make full
		for (int i = (size - conns.size() - livecount); i > 0; i--) {
			try {
				conn = DriverManager.getConnection("jdbc:mysql:///webapp");
				conn.setAutoCommit(false);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			conns.add(conn);
		}
		ConnPool.class.notifyAll();
	}

	static class Get implements Runnable {

		@Override
		public void run() {
			
			while (true) {
				Connection conn = ConnPool.getConn();
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					e.printStackTrace();
					ConnPool.returnConn(conn);
					conn = null;
				}
				Thread.yield();
				ConnPool.returnConn(conn);
			}
		}

	}

	public static void main(String[] args) {
		ExecutorService es = Executors.newCachedThreadPool();
		for (int i = 0; i < 300; i++) {
			es.execute(new Get());
		}
		try {
			Thread.sleep(50000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		es.shutdown();
		try {
			log.debug(es.awaitTermination(1000, TimeUnit.MILLISECONDS));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
