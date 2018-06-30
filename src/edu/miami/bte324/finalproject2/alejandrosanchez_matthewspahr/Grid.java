package edu.miami.bte324.finalproject2.alejandrosanchez_matthewspahr;

import java.util.Random;

/**
 * @author alejandrosanchez
 * @author matthewspahr
 *
 */
public class Grid {
	private int size, ships, left;
	private GridPoint[][] pointArray;

	public Grid(int size, int ships) {
		this.size = size;
		this.ships = ships;
		this.left = ships;
		this.pointArray = new GridPoint[size][size];
		
		this.setGrid();
		this.setShips();
	}
	
	private void setGrid() {
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				this.pointArray[i][j] = new GridPoint();
			}
		}
	}
	
	private void setShips() {
		for (int i = 0; i < ships; i++) {
			Random randomizer = new Random();
			
			int x = randomizer.nextInt(size);
			int y = randomizer.nextInt(size);
			
			if (!this.pointArray[x][y].getHasShip()) this.pointArray[x][y].setHasShip(true);
			else i--;
		}
	}
	
	public void targetPoint(String x, String y) {
		Integer xInt = Integer.parseInt(x); 
		Integer yInt = Integer.parseInt(y);
		
		if (this.pointArray[xInt][yInt].getState().equals(GridPoint.UNDISCOVERED)) {
			this.pointArray[xInt][yInt].receiveShot();
			if (this.pointArray[xInt][yInt].getState() == GridPoint.HIT) this.left--;
		}
	}
	
	public int getSize() {
		return this.size;
	}
	
	public int getShips() {
		return this.ships;
	}
	
	public int getLeft() {
		return this.left;
	}
	
	public GridPoint[][] getPointArray() {
		return this.pointArray;
	}
	
	public String listShipLocations() {
		String results = this. left + " ships left in the " + size + "x" + size + " grid , located at: ";
		
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				if(pointArray[i][j].getHasShip() && pointArray[i][j].getState().equals(GridPoint.UNDISCOVERED)) {
					results = results + " [" + i +"," + j + "]" ;
				}
			}
		}
		
		return results;
	}
}