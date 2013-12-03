package lab.amixyue.util;

import java.util.ArrayList;

import lab.amixyue.model.Node;

public class NodeUtil {

	public static Node getNodeByName(ArrayList<Node> nodes, String name){
		Node tmpNode = null;
		for(Node node:nodes){
			if(node.getName().equals(name)){
				tmpNode = node;
				break;
			}
		}
		return tmpNode;
	}
	
	public static boolean NnNs(Node node, ArrayList<Node> nodes){
		for(Node tmp : nodes){
			if(node.getName().equals(tmp.getName())){
				return true;
			}
		}
		return false;
	} 
}
