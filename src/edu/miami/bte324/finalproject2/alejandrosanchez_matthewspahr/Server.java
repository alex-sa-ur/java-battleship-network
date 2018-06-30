package edu.miami.bte324.finalproject2.alejandrosanchez_matthewspahr;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author alejandrosanchez
 * @author matthewspahr
 *
 */
public class Server {
	private static int uniqueID;
	private ArrayList<ClientThread> ctList;
	private ArrayList<BattleshipGame> bsList;
	private SimpleDateFormat sdf;
	private int port;
	private boolean keepGoing;
	
	private CopyOnWriteArrayList<Player> playerData;
	
	private static int 		minGridSize = 8, maxGridSize = 24;
	private static int		minShipNum = 1, maxShipNum = 10;
	
	public Server(int port) {
		this.port = port;
		this.sdf = new SimpleDateFormat("HH:mm:ss");
		this.ctList = new ArrayList<ClientThread>();
		this.bsList = new ArrayList<BattleshipGame>();
		
		playerData = Globals.readPlayersFromFile(Globals.USERS_INPUT_FILE);
	}
	
	public void start() {
		this.keepGoing = true;
		
		try {
			ServerSocket serverSocket = new ServerSocket(port);
			
			while (this.keepGoing) {
				display("Server waiting for Clients on port " + port + ".");
				
				Socket socket = serverSocket.accept();
				
				if(!keepGoing) break;
				ClientThread ct = new ClientThread(socket);
				ctList.add(ct);
				ct.start();
			}
			
			try {
				serverSocket.close();
				
				for (int i = 0; i < ctList.size(); i++) {
					ClientThread ct = ctList.get(i);
					
					try {
						ct.sInput.close();
						ct.sOutput.close();
						ct.socket.close();
					}
					
					catch (IOException ioE) {}
				}
			}
			
			catch(Exception e) {
				display("Exception closing the server and clients: " + e);
			}
		} 
		
		catch(IOException e){
			String msg = sdf.format(new Date()) + " Exception on new ServerSocket: " + e + "\n";
			display(msg);
		};
	}
	
	public void display(String msg) {
		msg = sdf.format(new Date()) + " " + msg;
		System.out.println(msg);
	}
	
	private synchronized void broadcast(String msg) {
		msg = sdf.format(new Date()) + " " + msg;
		System.out.println(msg);
		
		for(ClientThread ct: ctList) {
			if (!ct.writeMessage(msg)) {
				ctList.remove(ctList.indexOf(ct));
				display("Disconnected Client " + ct.username + " removed from list.");
			}
		}
	}

	synchronized void remove(int id) {
		for(ClientThread ct: ctList) {
			if (ct.id == id) {
				ctList.remove(ctList.indexOf(ct));
				return;
			}
		}
	}
	
	public static void main(String[] args) {
		int portNumber = 1500;
		
		switch(args.length) {
		case 1:
			try {
				portNumber = Integer.parseInt(args[0]);
			}
			
			catch (Exception e) {
				System.out.println("Invalid port number.");
				System.out.println("Usage is: > java Server [portNumber]");
				return;
			}
			
		case 0:
			break;
			
		default:
			System.out.println("Usage is: > java Server [portNumber]");
			return;
		}
		
		Server server = new Server(portNumber);
		server.start();
	}
	
	class ClientThread extends Thread{
		Socket socket;
		ObjectInputStream sInput;
		ObjectOutputStream sOutput;
		int id;
		String username;
		Message m;
		String date;
		
		ClientThread(Socket socket) {
			this.id = ++uniqueID;
			this.socket = socket;
			System.out.println("Thread trying to create Object Input/Output Streams");
			try
			{
				sOutput = new ObjectOutputStream(socket.getOutputStream());
				sInput  = new ObjectInputStream(socket.getInputStream());
				username = (String) sInput.readObject();
				display(username + " just connected.");
			}
			catch (IOException e) {
				display("Exception creating new Input/output Streams: " + e);
				return;
			}
			
			catch (ClassNotFoundException e) {}
			
            date = new Date().toString();
		}
		
		public void run() {
			boolean keepGoing = true;
			while(keepGoing) {
				try {
					m = (Message) sInput.readObject();
				}
				catch (IOException e) {
					display(username + " Exception reading Streams: " + e);
					break;				
				}
				catch(ClassNotFoundException e2) {
					break;
				}

				switch(m.getType()) {
				case Message.LOGIN:
					try {
						display("Server received Login request by user " + m.getLogin().getUsername());
						String msg = "Server: Received your login request! Please wait...";
						sOutput.writeObject(msg);
						
						Player loginAttempt = Globals.authenticateUser(playerData,m.getLogin()); 
						Message result = null;
						
						if (!(loginAttempt==null)){
							username = m.getLogin().getUsername();
							msg = "Server:Login Successfull...";
							result = new Message(Message.LOGIN,msg,m.getLogin(),1);
							sOutput.writeObject(result);
							
							display("Server logged in user " + username); 
						}
						
						else{
							msg = "invalid user or password";
							result = new Message(Message.LOGIN,msg,0);
							sOutput.writeObject(result);
						}
					} catch (IOException e1) {}
					break;
					
				case Message.LOGOUT:
					display(username + " disconnected with a LOGOUT message.");
					keepGoing = false;
					break;
					
				case Message.WHO:
					writeMessage("List of users connected at " + sdf.format(new Date()));
					int i = 0;
					for(ClientThread ct: ctList) {
						writeMessage((++i) + ") " + ct.username + " since " + ct.date);
					}
					
					break;
					
				case Message.WHO_L:
					writeMessage("List of users connected at " + sdf.format(new Date()) + " with scores");
					i = 0; 
					int score = 0;
					
					for(ClientThread ct: ctList) {
						for(Player p: playerData) {
							if (ct.username.equals(p.getLogin().getUsername())) score = p.getScore();
						}
						
						writeMessage((++i) + ") " + ct.username + " since " + ct.date + ", score: " + score);
					}
					
					break;
					
				case Message.WHO_ALL:
					writeMessage("List of all users with scores");
					i = 0;
					
					for(Player p: playerData) {
						writeMessage((++i) + ") " + p.getLogin().getUsername() + ", score: " + p.getScore());
					}
					
					break;
					
				case Message.INVITE:
					String[] args = m.getMessage().split("\\s+");
					String otherusername = args[0];
					Player otheruser = null;
					String msg;
					Message invMsg;
					
					for(Player p: playerData) {
						if (!username.equals(otherusername) && p.getLogin().getUsername().equals(otherusername)) otheruser = p;
					}
					
					if (!username.equals(otherusername) && otheruser != null && otheruser.getLoggedIn() && !otheruser.getInGame()) {
						for(Player p: playerData) {
							if (p.getLogin().getUsername().equals(username)) {
								for(ClientThread ct: ctList) {
									if (p.getInviteSentTo() != null) {
										if(ct.username.equals(p.getInviteSentTo().getLogin().getUsername())) {
											msg = username + " has cancelled their invite!";
											invMsg = new Message(Message.INVITE,msg);
											try {
												ct.sOutput.writeObject(invMsg);
											} catch (IOException e) {}
										}
									}
									
									if(ct.username.equals(otherusername)) {
										msg = username + " has invited you to play!";
										invMsg = new Message(Message.INVITE,msg);
										try {
											ct.sOutput.writeObject(invMsg);
										} catch (IOException e) {}
									}
								}
								p.setInviteSentTo(otheruser);
							}
						}
						msg = "Invite has been sent to " + otherusername + "!";
						invMsg = new Message(Message.INVITE,msg);
						try {
							sOutput.writeObject(invMsg);
						} catch (IOException e) {}
						
					}
					
					else {
						msg = otherusername + " is not available to play";
						invMsg = new Message(Message.INVITE,msg);
						try {
							sOutput.writeObject(invMsg);
						} catch (IOException e) {}
					}
					
					break;
					
				case Message.INVITE_R:
					args = m.getMessage().split("\\s+");
					otherusername = args[0];
					otheruser = null;
					
					Player user = null;
					String response = args[1];
					
					for(Player p: playerData) {
						if (!username.equals(otherusername) && p.getLogin().getUsername().equals(otherusername)) otheruser = p;
					}
					
					if (!username.equals(otherusername) && 
							((otheruser.getInviteSentTo() != null? 
									otheruser.getInviteSentTo().getLogin().getUsername().equals(username):false) 
							&& otheruser != null && otheruser.getLoggedIn() && !otheruser.getInGame())) {
						if (response.equalsIgnoreCase("y")) {
							msg = "You accepted " + otherusername + "'s invite!";
							invMsg = new Message(Message.INVITE,msg);
							try {
								sOutput.writeObject(invMsg);
							} catch (IOException e) {}
							
							for(ClientThread ct: ctList) {
								if(ct.username.equals(otherusername)) {
									msg = username + " accepted your invite";
									invMsg = new Message(Message.INVITE,msg);
									try {
										ct.sOutput.writeObject(invMsg);
									} catch (IOException e) {}
								}
							}
							
							for(Player p: playerData) {
								if (p.getLogin().getUsername().equals(username)) {
									p.setInGame(true);
									user = p;
								}
								if (p.getLogin().getUsername().equals(otherusername)) {
									p.setInGame(true);
									otheruser = p;
								};
							}
							
							Random randomizer = new Random();
							
							int gridSize		= randomizer.nextInt((maxGridSize - minGridSize) + 1) + minGridSize;
							int shipNum 		= randomizer.nextInt((maxShipNum - minShipNum) + 1) + minShipNum;
							
							bsList.add(new BattleshipGame(user, otheruser, gridSize, shipNum));
							
							for (BattleshipGame bs: bsList) {
								if (bs.getPlayer1().getLogin().getUsername().equals(username) ||
									bs.getPlayer2().getLogin().getUsername().equals(username)) {
									for(ClientThread ct: ctList) {
										if(ct.username.equals(otherusername)) {
											msg = "Game start! P1: " + username + " P2: " + otherusername;
											Message strtMsg = new Message(Message.MESSAGE,msg);
											try {
												ct.sOutput.writeObject(strtMsg);
												sOutput.writeObject(strtMsg);
											} catch (IOException e) {}
											
											msg = "It is " + bs.getInTurn().getLogin().getUsername() + "'s turn!";
											strtMsg = new Message(Message.MESSAGE,msg);
											try {
												ct.sOutput.writeObject(strtMsg);
												sOutput.writeObject(strtMsg);
											} catch (IOException e) {}
										}
									}
								}
							}
							
							for(Player p: playerData) {
								if (p.getLogin().getUsername().equals(username)) {
									msg = p.getGrid().listShipLocations();
									Message updtMsg = new Message(Message.MESSAGE,msg);
									try {
										sOutput.writeObject(updtMsg);
									} catch (IOException e) {}
								}
								if (p.getLogin().getUsername().equals(otherusername)) {
									msg = p.getGrid().listShipLocations();
									Message updtMsg = new Message(Message.MESSAGE,msg);
									for(ClientThread ct: ctList) {
										if(ct.username.equals(otherusername)) {
											try {
												ct.sOutput.writeObject(updtMsg);
											} catch (IOException e) {}
										}
									}
								}
							}
						}
						
						else if (response.equalsIgnoreCase("n")) {
							msg = "You declined " + otherusername + "'s invite!";
							invMsg = new Message(Message.INVITE,msg);
							try {
								sOutput.writeObject(invMsg);
							} catch (IOException e) {}
							
							for(ClientThread ct: ctList) {
								if(ct.username.equals(otherusername)) {
									msg = username + " declined your invite";
									invMsg = new Message(Message.INVITE,msg);
									try {
										ct.sOutput.writeObject(invMsg);
									} catch (IOException e) {}
								}
							}
						}
						
						else {
							msg = "Invalid invite response! Use y/n";
							invMsg = new Message(Message.INVITE,msg);
							try {
								sOutput.writeObject(invMsg);
							} catch (IOException e) {}
						}
					}
					
					else {
						msg = otherusername + " is not available to play";
						invMsg = new Message(Message.INVITE,msg);
						try {
							sOutput.writeObject(invMsg);
						} catch (IOException e) {}
					}
					
					break;
					
				case Message.HIT:
					args = m.getMessage().split("\\s+");
					int x = Integer.parseInt(args[0]);
					int y = Integer.parseInt(args[1]);
					String msg1 = username + " attacks ";
					String msg2 = "'s coordinate " + "[" + args[0] + "," + args[1] +"] and ";
					String attackedPlayer = "";
					msg = null;
					Message outcomeMsg = null;
					
					for(BattleshipGame g: bsList) {
						if(g.getPlayer1().getLogin().getUsername().equals(username)
								&& g.getInTurn().getLogin().getUsername().equals(username)) {
							if (x < g.getPlayer1().getGrid().getSize() && x >= 0 &&
									y < g.getPlayer1().getGrid().getSize() && y >=0) {
								g.getPlayer2().getGrid().targetPoint(args[0], args[1]);
								attackedPlayer = g.getPlayer2().getLogin().getUsername();
								
								if(g.getPlayer2().getGrid().getPointArray()[x][y].getState() == GridPoint.HIT) {
									msg2 = msg2 + "hit!";
									msg = msg1 + attackedPlayer + msg2;
									g.EndTurn();
								}
								
								if(g.getPlayer2().getGrid().getPointArray()[x][y].getState() == GridPoint.MISS) {
									msg2 = msg2 + "missed!";
									msg = msg1 + attackedPlayer + msg2;
									g.EndTurn();
								}
							}
							
							else {
								msg = "invalid coordinate! coordinates start at 0 and end at grid size - 1";
								Message endMsg = new Message (Message.HIT, msg);
								try {
									sOutput.writeObject(endMsg);
								} catch (IOException e) {}
							}
						}
						
						else if(g.getPlayer2().getLogin().getUsername().equals(username)
								&& g.getInTurn().getLogin().getUsername().equals(username)) {
							if (x < g.getPlayer1().getGrid().getSize() && x >= 0 &&
									y < g.getPlayer1().getGrid().getSize() && y >=0) {
								g.getPlayer1().getGrid().targetPoint(args[0], args[1]);
								attackedPlayer = g.getPlayer1().getLogin().getUsername();
								if(g.getPlayer1().getGrid().getPointArray()[x][y].getState() == GridPoint.HIT) {
									msg2 = msg2 + "hit!";
									msg = msg1 + attackedPlayer + msg2;
									g.EndTurn();
								}
								
								if(g.getPlayer1().getGrid().getPointArray()[x][y].getState() == GridPoint.MISS) {
									msg2 = msg2 + "missed!";
									msg = msg1 + attackedPlayer + msg2;
									g.EndTurn();
								}
							}
							
							else {
								msg = "invalid coordinate! coordinates start at 0 and end at grid size - 1";
								Message endMsg = new Message (Message.HIT, msg);
								try {
									sOutput.writeObject(endMsg);
								} catch (IOException e) {}
							}
						}
						
						else if (!g.getInTurn().getLogin().getUsername().equals(username) &&
								(	g.getPlayer1().getLogin().getUsername().equals(username) ||
									g.getPlayer2().getLogin().getUsername().equals(username))) {
							msg = "It it not your turn to attack";
							outcomeMsg = new Message(Message.HIT, msg);
							try {
								sOutput.writeObject(outcomeMsg);
							} catch (IOException e) {}
							break;
						}
					}
					
					if (msg != null) {
						outcomeMsg = new Message(Message.HIT, msg);
						for(ClientThread ct: ctList) {
							if(ct.username.equals(attackedPlayer)) {
								try {
									ct.sOutput.writeObject(outcomeMsg);
									sOutput.writeObject(outcomeMsg);
								} catch (IOException e) {}
							}
						}
						
						for(BattleshipGame g: bsList) {
							if(g.getPlayer1().getLogin().getUsername().equals(username) || 
									g.getPlayer2().getLogin().getUsername().equals(username)){
								if (g.getPlayer1().getGrid().getLeft() != 0 ||
										g.getPlayer2().getGrid().getLeft() != 0){
									for(Player p: playerData) {
										if (p.getLogin().getUsername().equals(username)) {
											msg1 = p.getGrid().listShipLocations();
											Message statusMsg1 = new Message(Message.MESSAGE,msg1);
											try {
												sOutput.writeObject(statusMsg1);
											} catch (IOException e) {}
										}
									}
									
									for(ClientThread ct: ctList) {
										if(ct.username.equals(attackedPlayer)) {
											for(Player p: playerData) {
												if (p.getLogin().getUsername().equals(attackedPlayer) && 
														p.getGrid().getLeft() != 0) {
													msg2 = p.getGrid().listShipLocations();
													Message statusMsg2 = new Message(Message.MESSAGE,msg2);
													try {
														ct.sOutput.writeObject(statusMsg2);
													} catch (IOException e) {}
												}
											}
										}
									}
								}
							}
						}
						
						for (BattleshipGame g: bsList) {
							if (g.getPlayer1().getGrid().getLeft() == 0 ||
									g.getPlayer2().getGrid().getLeft() == 0) {
								msg = g.EndGame();
								Message endMsg = new Message (Message.HIT, msg);
								
								for(ClientThread ct: ctList) {
									if(ct.username.equals(attackedPlayer)) {
										try {
											sOutput.writeObject(endMsg);
											ct.sOutput.writeObject(endMsg);
										} catch (IOException e) {}
									}
								}
								
								for(Player p: playerData) {
									if (g.getWinner().equals(p)) p.setScore(p.getScore() + 1);
								}
								Globals.writePlayersToFile(playerData, Globals.USERS_INPUT_FILE);
								g.getPlayer1().setInGame(false);
								g.getPlayer2().setInGame(false);
								bsList.remove(g);
								break;
							}
						}
					}
					break;
					
				case Message.SPECTATE:
					user = null;
					Player spec = null;
					args = m.getMessage().split("\\s+");
					String name = args[0];
					
					for(Player p: playerData) {
						if (p.getLogin().getUsername().equals(name)) user = p;
						if (p.getLogin().getUsername().equals(username)) spec = p;
					}
					
					for(BattleshipGame g: bsList) {
						if (g.getPlayer1().getLogin().getUsername().equals(user.getLogin().getUsername()) ||
							g.getPlayer2().getLogin().getUsername().equals(user.getLogin().getUsername())) {
							if (spec != null) g.getSpectators().add(spec);
						}
					}
					
					break;
					
				case Message.MESSAGE:
					broadcast(username + ": " + m.getMessage());
					break;
				}
			}
			remove(id);
			close();
		}
		
		private void close() {
			try {
				if(sOutput != null) sOutput.close();
			}
			catch(Exception e) {}
			try {
				if(sInput != null) sInput.close();
			}
			catch(Exception e) {};
			try {
				if(socket != null) socket.close();
			}
			catch (Exception e) {}
		}
		
		private boolean writeMessage(String msg) {
			if(!socket.isConnected()) {
				close();
				return false;
			}
			try {
				sOutput.writeObject(msg);
			}
			catch(IOException e) {
				display("Error sending message to " + username);
				display(e.toString());
			}
			return true;
		}
	}
}