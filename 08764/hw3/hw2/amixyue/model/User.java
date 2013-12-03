package hw2.amixyue.model;

import lombok.*;

import java.util.*;
import hw2.amixyue.dao.annotation.*;
/**
 * 
 * @author Yue Xing, yuexing@andrew.cmu.edu, 08764
 *
 */
@NoArgsConstructor
@AllArgsConstructor
@Table(name="yuexing_user")
public @Data class User {

	@PrimaryKey
	@Column(exclude=true)
	private int id;	
	private String fname;
	private String lname;
	private String email;
	private String password;
	@Column(name="timezone", jdbcType="varchar")
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
