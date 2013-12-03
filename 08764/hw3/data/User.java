package data;

import lombok.*;

import hw2.amixyue.dao.annotation.Column;
import hw2.amixyue.dao.annotation.Table;

import java.util.*;

/**
 * 
 * @author Yue Xing, yuexing@andrew.cmu.edu, 08764
 * @date Feb 14
 */
@NoArgsConstructor
@AllArgsConstructor
@Table(name="yuexing_user")
public @Data class User {

	//will be used as primary key
	private int id;
	private String fname;
	private String lname;
	private String email;
	private String password;
	@Column(jdbcType="varchar")
	private TimeZone tzone;
	private String signature;
	
	public void copy(User user){
		if(user == null) return;
		this.email = user.getEmail();
		this.fname = user.getFname();
		this.lname = user.getLname();
		this.password = user.getPassword();
		this.tzone = user.getTzone();
	}
	
}
