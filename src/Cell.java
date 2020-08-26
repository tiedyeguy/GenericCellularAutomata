import processing.core.PApplet;
import processing.core.PVector;

public class Cell {
	private static PVector size;
	private Cell[] neighbors;
	private State state;
	private State nextState;
	
	public Cell() {}
	
	public Cell(Cell[] neighbors) {
		this.neighbors = neighbors;
	}
	
	public void setNeighbors(Cell[] neighbors) {
		this.neighbors = neighbors;
	}
	
	public static PVector getSize() {
		return size;
	}

	public static void setSize(PVector size) {
		Cell.size = size;
	}

	/**
	 * Prepares the next state of the cell, based on its state, neighbors, and rules
	 */
	public void prepareNextState() {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Updates the state of the cell with the prepared state
	 */
	public void updateState() {
		state = nextState;
		nextState = null;
	}
	
	/**
	 * Draws the cell to the screen at the current origin
	 * 
	 * @param sketch to draw to
	 */
	public void draw(PApplet sketch) {
		sketch.box(Cell.getSize().x, Cell.getSize().y, Cell.getSize().z);
	}
}
