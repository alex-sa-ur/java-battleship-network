package edu.miami.bte324.finalproject2.alejandrosanchez_matthewspahr;

/**
 * @author alejandrosanchez
 * @author matthewspahr
 *
 */
public class GridPoint {
	public static final String UNDISCOVERED = "o", HIT = "x", MISS = "/";
	private String state;
	private boolean hasShip;
	
	public GridPoint() {
		this.state = GridPoint.UNDISCOVERED;
		this.hasShip = false;
	}
	
	public void receiveShot() {
		if (this.state.equals(GridPoint.UNDISCOVERED)) {
			if (this.hasShip) this.state = GridPoint.HIT;
			else this.state = GridPoint.MISS;
		}
	}
	
	public String getState() {
		return this.state;
	}
	
	public void setState(String state) {
		if (state.equals(GridPoint.UNDISCOVERED) || state.equals(GridPoint.HIT) || state.equals(GridPoint.MISS)) {
			this.state = state;
		}
	}
	
	public boolean getHasShip() {
		return this.hasShip;
	}
	
	public void setHasShip(boolean hasShip) {
		this.hasShip = hasShip;
	}
}
