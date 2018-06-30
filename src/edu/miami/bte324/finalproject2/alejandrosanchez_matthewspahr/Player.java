package edu.miami.bte324.finalproject2.alejandrosanchez_matthewspahr;

/**
 * @author alejandrosanchez
 * @author matthewspahr
 *
 */
public class Player extends Person{
	private static final long serialVersionUID = 8480526455823146554L;
	
	private int 	playerID;
	private Login 	login;
	private int 	score;
	
	private boolean loggedIn	= false;
	private boolean spectating	= false;
	private boolean inGame		= false;
	
	private Player inviteSentTo;
	
	private Grid grid;
	
	public Player(	int playerID, String firstName, String lastName, 
					Login login, int score) {
		super(firstName, lastName);
		this.playerID = playerID;
		this.login = login;
		this.score = score;
	}
	
	public int getPlayerID() {
		return this.playerID;
	}
	
	public void setPlayerID(int playerID) {
		this.playerID = playerID;
	}
	
	public Login getLogin() {
		return this.login;
	}
	
	public void setLogin(Login login) {
		this.login = login;
	}
	
	public int getScore() {
		return this.score;
	}
	
	public void setScore(int score) {
		this.score = score;
	}
	
	public boolean getLoggedIn() {
		return this.loggedIn;
	}
	
	public void setLoggedIn(boolean loggedIn) {
		this.loggedIn = loggedIn;
	}
	
	public boolean getSpectating() {
		return this.spectating;
	}
	
	public void setSpectating(boolean spectating) {
		this.spectating = spectating;
	}
	
	public boolean getInGame() {
		return this.inGame;
	}
	
	public void setInGame(boolean inGame) {
		this.inGame = inGame;
	}
	
	public Grid getGrid() {
		return this.grid;
	}
	
	public void setGrid(Grid grid) {
		this.grid = grid;
	}
	
	public Player getInviteSentTo() {
		return this.inviteSentTo;
	}
	
	public void setInviteSentTo(Player inviteSentTo) {
		this.inviteSentTo = inviteSentTo;
	}
}
