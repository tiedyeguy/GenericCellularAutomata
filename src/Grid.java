import java.util.ArrayList;

import processing.core.PApplet;

/**
 * Holds all cells and has methods that correspond to cell methods, to call of them at once
 */
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
			for (int y = 0; y < cells[z].length; y++)
				for (int x = 0; x < cells[z][y].length; x++)
					cells[z][y][x] = new Cell();

		for (int z = 0; z < cells.length; z++) {
			for (int y = 0; y < cells[z].length; y++) {
				for (int x = 0; x < cells[z][y].length; x++) {
					cells[z][y][x] = new Cell(getNeighbors(x, y, z), new State("default"));
				}
			}
		}

	}

	/**
	 * Gets the neighbors for the cell at the given location, 
	 * based on whether the grid is wrapping and the neighbor type
	 * @param x location of the cell to get neighbors for
	 * @param y location of the cell to get neighbors for
	 * @param z location of the cell to get neighbors for
	 * @return an array of neighbors (cells) to the given coordinates, 
	 */
	private Cell[] getNeighbors(int cellX, int cellY, int cellZ) {
		ArrayList<Cell> neighbors = new ArrayList<Cell>();

		if (Settings.getNeighborType() == NeighborType.MOORE) {
			neighbors = new ArrayList<Cell>(27);

			for (int z = cellZ - 1; z <= cellZ + 1; z++) {
				for (int y = cellY - 1; y <= cellY + 1; y++) {
					for (int x = cellX - 1; x <= cellX + 1; x++) {
						if (Settings.isWrapping()) {
							neighbors.add(cells[Math.floorMod(z, cells.length)][Math.floorMod(y, cells[0].length)][Math
									.floorMod(x, cells[0][0].length)]);
						} else {
							if (x >= 0 && x < cells[0][0].length && y >= 0 && y < cells[0].length && z >= 0
									&& z < cells.length)
								neighbors.add(cells[z][y][x]);
						}
					}
				}
			}
		} else if (Settings.getNeighborType() == NeighborType.NEUMANN) {
			neighbors = new ArrayList<Cell>(6);

			if (Settings.isWrapping()) {
				neighbors.add(cells[Math.floorMod(cellZ - 1, cells.length)][cellY][cellX]);
				neighbors.add(cells[Math.floorMod(cellZ + 1, cells.length)][cellY][cellX]);
				neighbors.add(cells[cellZ][Math.floorMod(cellY - 1, cells[0].length)][cellX]);
				neighbors.add(cells[cellZ][Math.floorMod(cellY + 1, cells[0].length)][cellX]);
				neighbors.add(cells[cellZ][cellY][Math.floorMod(cellX - 1, cells[0][0].length)]);
				neighbors.add(cells[cellZ][cellY][Math.floorMod(cellX + 1, cells[0][0].length)]);
			} else {
				if (cellZ - 1 >= 0)
					neighbors.add(cells[cellZ - 1][cellY][cellX]);
				if (cellZ + 1 < cells.length)
					neighbors.add(cells[cellZ + 1][cellY][cellX]);
				if (cellY - 1 >= 0)
					neighbors.add(cells[cellZ][cellY - 1][cellX]);
				if (cellY + 1 < cells[0].length)
					neighbors.add(cells[cellZ][cellY + 1][cellX]);
				if (cellX - 1 >= 0)
					neighbors.add(cells[cellZ][cellY][cellX - 1]);
				if (cellX + 1 < cells[0][0].length)
					neighbors.add(cells[cellZ][cellY][cellX + 1]);
			}
		}

		ArrayList<Cell> justMeList = new ArrayList<Cell>(1);
		justMeList.add(cells[cellZ][cellY][cellX]);

		neighbors.removeAll(justMeList);

		Cell[] arr = new Cell[neighbors.size()];

		return neighbors.toArray(arr);
	}

	/**
	 * Prepares all of the cells states to be updated later (all cells must be
	 * prepared before all are updated)
	 */
	public void prepareAllStates() {
		for (int z = 0; z < cells.length; z++)
			for (int y = 0; y < cells[z].length; y++)
				for (int x = 0; x < cells[z][y].length; x++)
					cells[z][y][x].prepareNextState();
	}

	/**
	 * Updates all of the cells to their prepared next state (all cells must be
	 * prepared before all are updated)
	 */
	public void updateAllStates() {
		for (int z = 0; z < cells.length; z++)
			for (int y = 0; y < cells[z].length; y++)
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

		sketch.translate(-Cell.getSize().x * (cells[0][0].length - 1) / 2,
				-Cell.getSize().y * (cells[0].length - 1) / 2, -Cell.getSize().z * (cells.length - 1) / 2);

		for (int z = 0; z < cells.length; z++) {
			for (int y = 0; y < cells[z].length; y++) {
				for (int x = 0; x < cells[z][y].length; x++) {
					cells[z][y][x].draw(sketch);
					sketch.translate(Cell.getSize().x, 0, 0);
				}
				sketch.translate(-Cell.getSize().x * cells[z][y].length, Cell.getSize().y, 0);
			}
			sketch.translate(0, -Cell.getSize().y * cells[z].length, Cell.getSize().z);
		}

		sketch.popMatrix();
	}

}
