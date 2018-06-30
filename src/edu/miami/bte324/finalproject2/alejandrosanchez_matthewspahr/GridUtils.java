package edu.miami.bte324.finalproject2.alejandrosanchez_matthewspahr;

/**
 * @author alejandrosanchez
 * @author matthewspahr
 *
 */
public class GridUtils {
	public static void printGrid(Grid grid) {
		System.out.print("   |");
		
		for (Integer i = 0; i < grid.getSize(); i++) {
			String s =  "  " + (i < 10? "0" + i.toString() : i.toString()) + "  |";
			System.out.print(s);
		}
		
		System.out.println();
		printGridline(grid);
		
		for (Integer i = 0; i < grid.getSize(); i++) {
			String s = (i < 10? "0" + i.toString() : i.toString()) + " |";
			System.out.print(s);
			printGridPoints(grid, i);
			printGridline(grid);
		}
	}
	
	private static void printGridline(Grid grid) {
		System.out.print("----");
		
		for (int i = 0; i < grid.getSize(); i++) {
			System.out.print("-------");
		}
		
		System.out.println();
	}
	
	private static void printGridPoints(Grid grid, int i) {
		for (Integer j = 0; j < grid.getSize(); j++) {
			String s =  "   " + grid.getPointArray()[i][j].getState() +"  |";
			System.out.print(s);
		}
		System.out.println();
	}
}
