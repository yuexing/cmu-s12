package model.hw2.amixyue;

import lombok.*;

import java.util.*;

/**
 * 
 * @author Yue Xing, yuexing@andrew.cmu.edu, 08764
 *
 */
@NoArgsConstructor
@AllArgsConstructor
public @Data class User {

	private int id;
	private String fname;
	private String lname;
	private String email;
	private String password;
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
