package lab.amixyue.model;

import java.io.Serializable;
import java.util.ArrayList;

import lab.amixyue.constant.GStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
@AllArgsConstructor
public @Data class Group implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String name;
	private ArrayList<Node> nodes;
	private GStatus status;
}
