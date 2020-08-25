import processing.core.PApplet;

public class Grid {
	private Cell[][][] cells;

	public Grid(int len) {
		initCells(len, 1, 1);
	}

	public Grid(int len, int width) {
		initCells(len, width, 1);
	}

	public Grid(int len, int width, int height) {
		initCells(len, width, height);
	}

	/**
	 * Initializes the cells array with the specified dimensions
	 * 
	 * @param length
	 * @param width
	 * @param height
	 */
	private void initCells(int len, int width, int height) {
		cells = new Cell[height][width][len];

		for (int z = 0; z < cells.length; z++)
			for (int y = 0; y < cells[y].length; y++)
				for (int x = 0; x < cells[z][y].length; x++)
					cells[z][y][x] = new Cell();
	}

	/**
	 * Prepares all of the cells states to be updated later (all cells must be prepared before all are updated)
	 */
	public void prepareAllStates() {
		for (int z = 0; z < cells.length; z++)
			for (int y = 0; y < cells[y].length; y++)
				for (int x = 0; x < cells[z][y].length; x++)
					cells[z][y][x].prepareNextState();
	}

	/**
	 * Updates all of the cells to their prepared next state (all cells must be prepared before all are updated)
	 */
	public void updateAllStates() {
		for (int z = 0; z < cells.length; z++)
			for (int y = 0; y < cells[y].length; y++)
				for (int x = 0; x < cells[z][y].length; x++)
					cells[z][y][x].updateState();
	}

	/**
	 * Draws all cells to the screen
	 * 
	 * @param sketch to draw to
	 */
	public void draw(PApplet sketch) {
		sketch.pushMatrix();
		
		for (int z = 0; z < cells.length; z++) {
			for (int y = 0; y < cells[y].length; y++) {
				for (int x = 0; x < cells[z][y].length; x++) {
					cells[z][y][x].draw(sketch);
					sketch.translate(Cell.getSize().x, 0, 0);
				}
				sketch.translate(Cell.getSize().x, Cell.getSize().y, 0);
			}
			sketch.translate(Cell.getSize().x, Cell.getSize().y, Cell.getSize().z);
		}
		
		sketch.popMatrix();
	}

}
