package edu.miami.bte324.finalproject2.alejandrosanchez_matthewspahr;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author alejandrosanchez
 * @author matthewspahr
 *
 */
public abstract class Globals {
	public static String USERS_INPUT_FILE = "resources/Players.txt";
	
	public static CopyOnWriteArrayList<Player> readPlayersFromFile(String fileName){
		CopyOnWriteArrayList<Player> playerList = new CopyOnWriteArrayList<Player>();
		String line;
		
		try (
				InputStream fis = new FileInputStream(fileName);
				InputStreamReader isr = new InputStreamReader(fis, Charset.forName("UTF-8"));
				BufferedReader br = new BufferedReader(isr);
		){
			int i = 0;
			int playerID = 0, score = 0;
			String firstName = null, lastName = null, username = null, password = null;
			Login login = null;
			
			while ((line = br.readLine()) != null) {
				if (i % 5 == 0) {
					playerID = Integer.parseInt(line.trim());
				}

				else if (i % 5 == 1) {
					String nameTokens[] = line.split("\\s+");
					firstName = nameTokens[0];
					lastName = nameTokens[nameTokens.length - 1];
				} 
				
				else if (i % 5 == 2) {
					username = line;
				} 
				
				else if (i % 5 == 3) {
					password = line;
					
					login = new Login(username, password);
				}
				else if (i % 5 == 4) {
					score = Integer.parseInt(line.trim());
					
					Player p = new Player(playerID, firstName, lastName, login, score);
					playerList.add(p);
				}	
				i++;
			}
		}
		
		catch (IOException e) {}
		
		return playerList;
	}
	
	public static void writePlayersToFile(CopyOnWriteArrayList<Player> playerData, String fileName) {
		PrintWriter writer;
		try {
			writer = new PrintWriter(fileName,"UTF-8");
			for (Player p: playerData) {
				writer.println(p.getPlayerID());
				writer.println(p.getFirstName() + " " + p.getLastName());
				writer.println(p.getLogin().getUsername());
				writer.println(p.getLogin().getPassword());
				writer.println(p.getScore());
			}
			writer.close();
		} catch (FileNotFoundException | UnsupportedEncodingException e) {}
	}
	
	public static Player authenticateUser(CopyOnWriteArrayList<Player> playerList,
			Login login) {
		for (Player p : playerList) {
			if (login.confirmLogin(p.getLogin().getUsername(), p.getLogin().getPassword())) {
				p.setLoggedIn(true);
				return p;
			}
		}
		return null;
	}
}