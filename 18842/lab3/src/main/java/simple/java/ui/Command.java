package simple.java.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import simple.java.core.Core;

/**
 * this is a shell
 * check input and pass to core
 * @author amy
 *
 */
public class Command {

	private Core core;
	private boolean shutdown = false;
	public enum CommandType{send, recv, end, gsend}
	
	public Command(Core core){
		this.core = core;
	}
	
	public void run(){
		String command = "";
		
		while(!shutdown){
			System.out.println(core.meName + " Enter Command:");
			BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
			
			try {
				command = input.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
			//(g)send A ask ask
			if(command.startsWith(CommandType.send.toString())
					|| command.startsWith(CommandType.gsend.toString())){
				String[] vars = command.split("\\s+");
				if(vars.length != 4){
					System.out.println("Error Command!");
				}else{
					core.parse(vars);
				}
			}
			//end
			else if(command.startsWith(CommandType.end.toString())){
				shutdown= true;
			}
			//recv
			else if(command.startsWith(CommandType.recv.toString())){
				core.parse("recv");
			}
			//error
			else{
				System.out.println("Error Command!");
			}
		}
	}
	
	public void showMsg(Object obj){
		System.out.println("received: " + obj);
	}
}
