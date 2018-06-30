package edu.miami.bte324.finalproject2.alejandrosanchez_matthewspahr;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

/**
 * @author alejandrosanchez
 * @author matthewspahr
 *
 */
public class BattleshipGame implements Serializable{
	private static final long serialVersionUID = -8318301177805232290L;
	
	private Player p1, p2, inTurn, winner;
	private ArrayList<Player> spectators = new ArrayList<Player>();
	private boolean coin;
	
	public BattleshipGame(Player p1, Player p2, int gridSize, int shipNum) {
		Grid gridP1 = new Grid(gridSize, shipNum);
		Grid gridP2 = new Grid(gridSize, shipNum);
		
		p1.setGrid(gridP1);
		p2.setGrid(gridP2);
		
		this.p1 = p1;
		this.p2 = p2;
		
		
		StartGame();
	}
	
	private void CoinFlip() {
		Random coinFlip = new Random();
		Integer coinValue = coinFlip.nextInt(2);
		if (coinValue.equals(1)) this.coin = false;
		else this.coin = true;
	}
	
	public void StartGame() {
		CoinFlip();
		if (coin == true) inTurn = p1;
		else inTurn = p2;
	}
	
	public void EndTurn() {
		CheckStatus();
		
		if(p1.getGrid().getLeft() == 0 || p2.getGrid().getLeft() == 0) {
			EndGame();
		}
		
		if (inTurn.equals(p1)) inTurn = p2;
		else inTurn = p1;
	}
	
	public String CheckStatus() {
		String status;
		
		status = 	"Current status of the game: " + System.lineSeparator() + 
					p1.getLogin().getUsername() + "has  " + p1.getGrid().getLeft() + " ships left" + System.lineSeparator() + 
					p2.getLogin().getUsername() + "has  " + p2.getGrid().getLeft() + " ships left" + System.lineSeparator();
		
		return status;
	}
	
	public String EndGame() {
		if (p2.getGrid().getLeft() == 0) winner = p1;
		else winner = p2;
		
		return EndScreen(winner.getLogin().getUsername());
	}
	
	private String EndScreen(String s) {
		String space = " ";
		
		for(int i = 0; i < (41 - s.length())/2; i++) {
			space = space + " ";
		}
		
		return
				"*******************************************" + System.lineSeparator() +
				"                 Game Over!                " + System.lineSeparator() +
				"                                           " + System.lineSeparator() +
				"                  Winner:                  " + System.lineSeparator() +
								space + s 					  + System.lineSeparator() + 
				"*******************************************";
	}
	
	public Player getInTurn() {
		return this.inTurn;
	}
	
	public ArrayList<Player> getSpectators() {
		return this.spectators;
	}
	
	public Player getPlayer1() {
		return this.p1;
	}
	
	public Player getPlayer2() {
		return this.p2;
	}
	
	public Player getWinner() {
		return this.winner;
	}
}