package edu.miami.bte324.finalproject2.alejandrosanchez_matthewspahr;

import java.net.*;
import java.io.*;
import java.util.*;

/**
 * @author alejandrosanchez
 * @author matthewspahr
 *
 */
public class Client {
	private ObjectInputStream sInput;
	private ObjectOutputStream sOutput;
	private Socket socket;
	
	private String server, username;
	private int port;
	
	boolean loggedIn=false;
	
	Client(String server, int port, String username) {
		this.server = server;
		this.port = port;
		this.username = username;
	}
	
	public boolean start() {
		try {
			socket = new Socket(server, port);
		} 
		catch(Exception ec) {
			display("Error connectiong to server:" + ec);
			return false;
		}
		
		String msg = "Connection accepted " + socket.getInetAddress() + ":" + socket.getPort();
		display(msg);
	
		try
		{
			sInput  = new ObjectInputStream(socket.getInputStream());
			sOutput = new ObjectOutputStream(socket.getOutputStream());
		}
		catch (IOException eIO) {
			display("Exception creating new Input/output Streams: " + eIO);
			return false;
		}

		new ListenFromServer().start();
		
		try
		{
			sOutput.writeObject(username);
		}
		catch (IOException eIO) {
			display("Exception doing login : " + eIO);
			disconnect();
			return false;
		}
		
		return true;
	}

	private void display(String msg) {
		System.out.println(msg);
	}
	
	void sendMessage(Message msg) {
		try {
			sOutput.writeObject(msg);
		}
		catch(IOException e) {
			display("Exception writing to server: " + e);
		}
	}

	private void disconnect() {
		try { 
			if(sInput != null) sInput.close();
		}
		catch(Exception e) {}
		try {
			if(sOutput != null) sOutput.close();
		}
		catch(Exception e) {}
        try{
			if(socket != null) socket.close();
		}
		catch(Exception e) {}
	}
	
	public static void main(String[] args) {
		int portNumber = 1500;
		String serverAddress = "localhost";
		String userName = "Anonymous";

		switch(args.length) {
			case 3:
				serverAddress = args[2];
			case 2:
				try {
					portNumber = Integer.parseInt(args[1]);
				}
				catch(Exception e) {
					System.out.println("Invalid port number.");
					System.out.println("Usage is: > java Client [username] [portNumber] [serverAddress]");
					return;
				}
			case 1: 
				userName = args[0];
			case 0:
				break;
			default:
				System.out.println("Usage is: > java Client [username] [portNumber] {serverAddress]");
			return;
		}
		
		Client client = new Client(serverAddress, portNumber, userName);
		
		if(!client.start())
			return;
		
		@SuppressWarnings("resource")
		Scanner scan = new Scanner(System.in);
		
		while(true) {
			System.out.print("> ");
			
			String msg = scan.nextLine();
			
			args = msg.split("\\s+");
			
			if (args[0].equalsIgnoreCase("LOGIN")) {
				if (args.length >= 3) {
					if (!client.loggedIn){
						if (client.username.equals("Anonymous")){
							String inputUserName = args[1];
							String inputUserPassword = args[2];
							Login LoginObject = new Login(inputUserName,inputUserPassword);
							client.sendMessage(new Message(Message.LOGIN,"login",LoginObject,0)); 
						}	
					}
					
					else{
						System.out.println("You are already logged in...");
					}
				}
				
				else {
					System.out.println("Incorrect number of arguments...");
					System.out.println("Use: LOGIN USERNAME PASSWORD...");
				}
			}
			
			else if (args[0].equalsIgnoreCase("LOGOUT")) {
				client.sendMessage(new Message(Message.LOGOUT, ""));
				break;
			}
			
			else if (args[0].equalsIgnoreCase("WHO")) {
				if (args.length > 1) {
					if (args[1].equalsIgnoreCase("-L")) client.sendMessage(new Message(Message.WHO_L, ""));
					else if (args[1].equalsIgnoreCase("-ALL")) client.sendMessage(new Message(Message.WHO_ALL, ""));
					else {
						System.out.println("Invalid argument for WHO command...");
					}
				}
				else client.sendMessage(new Message(Message.WHO, ""));
			}
			
			else if (args[0].equalsIgnoreCase("INVITE")) {
				if (args.length > 1) {
					if (args.length > 3) {
						if (args[1].equalsIgnoreCase("-R")) {
							String inputUserName = args[2];
							String response = args[3];
							msg = inputUserName + " " + response;
							client.sendMessage(new Message(Message.INVITE_R, msg));
						}
					}
					
					else if(args.length == 3) {
						System.out.println("Missing argument for INVITE -R command...");
						System.out.println("Use: INVITE -R USERNAME [y/n]...");
					}
					
					else {
						String inputUserName = args[1];
						client.sendMessage(new Message(Message.INVITE, inputUserName));
					}
				}
				
				else {
					System.out.println("Missing argument for INVITE command...");
					System.out.println("Use: INVITE USERNAME...");
				}
			}
			
			else if (args[0].equalsIgnoreCase("HIT")) {
				if (args.length > 2) {
					String x = args[1];
					String y = args[2];
					msg = x + " " + y;
					client.sendMessage(new Message(Message.HIT, msg));
				}
				
				else {
					System.out.println("Missing argument for HIT command...");
					System.out.println("Use: HIT X Y...");
				}
			}
			
			else if (args[0].equalsIgnoreCase("SPECTATE")) {
				if (args.length > 1) {
					String user = args[1];
					client.sendMessage(new Message(Message.SPECTATE, user));
				}
				
				else {
					System.out.println("Missing argument for SPECTATE command...");
					System.out.println("Use: SPECTATE USERNAME...");
				}
			}
			
			else {
				client.sendMessage(new Message(Message.MESSAGE, msg));
			}
		}
		client.disconnect();
	}
	
	class ListenFromServer extends Thread {  

		public void run() {
			while(true) {
				try {
					Object obj = sInput.readObject();
					String msg = null;
					
					if (obj instanceof String){
						msg = obj.toString();
						System.out.println(msg);
					}
					
					if (obj instanceof Message){
						msg = ((Message)obj).getMessage();
						System.out.println(msg);
					}
					System.out.print("> ");
				}
				catch(IOException e) {
					display("Server has closed the connection: " + e);
					break;
				}
				catch(ClassNotFoundException e2) {}
			}
		}
	}
}