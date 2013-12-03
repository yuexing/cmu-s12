package simple.java.manager;

import java.util.*;

import simple.java.model.Node;

public class NodeManager {

	public static Map<String, Node> nodes;
	private static String myname;
	private static int myindex = 0;
	private static boolean set;
	
	static{
		nodes = new HashMap<String, Node>();
	}
	
	/**
	 * for performance
	 * @param node
	 */
	public static void setMyname(String name){
		myname = name;
	}
	
	public static String getMyname(){
		return myname;
	}
	
	public static int getMyindex(){
		return myindex;
	}
	
	public static int count(){
		return nodes.size();
	}
	
	public static void addNode(Node node){
		if(!set){
			if(node.name.equals(myname)){
				set = true;
				myindex--;
			}
			myindex++;
		}		
		nodes.put(node.name, node);
	}
	
	public static Node getNode(String name){
		Node tmp = null;
		if(nodes.containsKey(name))
			tmp = nodes.get(name);
		return tmp;
	}
	
	public static int getIndex(String name){
		if(!nodes.containsKey(name)){
			return -1;
		}
		Iterator<String> it= nodes.keySet().iterator();
		int index = 0;
		while(it.hasNext()){
			if(it.next().equals(name)){
				return index;
			}
			index++;
		}
		return -1;
	}
	
	public static void clear(){
		nodes.clear();
	}
	
	public static Collection<Node> getNodes(){
		return nodes.values();
	}
}
