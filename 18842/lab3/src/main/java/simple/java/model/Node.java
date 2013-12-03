package simple.java.model;

import simple.java.manager.GroupManager;

public class Node {

	public final String name;
	public final String ip;
	public final int port;
	// group name
	private String gname;

	public Node(String name, String ip, int port) {
		this.name = name;
		this.ip = ip;
		this.port = port;
	}

	public String getGname() {
		return gname;
	}

	public void setGname(String gname) {
		this.gname = gname;
	}

	public String configStr() {
		String toStr = "\n- Name : " + this.name + "\n  IP : " + this.ip
				+ "\n  Port : " + this.port;

		Group group = GroupManager.getGroupByName(gname);
		if (group != null) {
			toStr += "\n  Group : ";
			toStr += "\n   - " + group.name;
			for (String name : group.nodeNames) {
				toStr += "\n   - " + name;
			}
		}
		return toStr;
	}
}
