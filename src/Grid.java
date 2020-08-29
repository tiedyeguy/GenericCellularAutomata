import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import processing.core.PApplet;
import processing.data.JSONArray;
import processing.data.JSONObject;

/**
 * Holds all cells and has methods that correspond to cell methods, to call of
 * them at once
 */
public class Grid {

	private Cell[][][] cells;

	private Grid() {
	}
	
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
					cells[z][y][x].setNeighbors(getNeighbors(x, y, z));
					cells[z][y][x].setState(State.getState("default"));
				}
			}
		}
	}

	/**
	 * Gets the neighbors for the cell at the given location, based on whether the
	 * grid is wrapping and the neighbor type
	 * 
	 * @param x location of the cell to get neighbors for
	 * @param y location of the cell to get neighbors for
	 * @param z location of the cell to get neighbors for
	 * @return an array of neighbors (cells) to the given coordinates,
	 */
	private Cell[] getNeighbors(int cellX, int cellY, int cellZ) {
		List<Cell> neighbors = new ArrayList<Cell>();

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

		neighbors = neighbors.stream().distinct().collect(Collectors.toList()); //Remove duplicates

		if(!Settings.isSimpleRuleset()) {
			ArrayList<Cell> justMeList = new ArrayList<Cell>(1);
			justMeList.add(cells[cellZ][cellY][cellX]);	
			neighbors.removeAll(justMeList); //Remove Cell itself
		}
		
		Cell[] arr = new Cell[neighbors.size()];
		return neighbors.toArray(arr);
	}
	
	/**
	 * Returns a deep clone of this grid
	 * @return deep clone of this grid
	 */
	public Grid deepClone() {
		Grid clone = new Grid();
		
		clone.cells = new Cell[cells.length][cells[0].length][cells[0][0].length];
		
		for (int z = 0; z < cells.length; z++)
			for (int y = 0; y < cells[z].length; y++)
				for (int x = 0; x < cells[z][y].length; x++)
					clone.cells[z][y][x] = cells[z][y][x].deepClone();
		
		return clone;
	}

	/**
	 * Sets the state of a cell at specified position
	 * 
	 * @param x     position in grid
	 * @param y     position in grid
	 * @param z     position in grid
	 * @param state to set the specified cell to
	 */
	public void setCellStateAtPos(int x, int y, int z, State state) {
		cells[z][y][x].setState(state);
	}

	/**
	 * Draws the cell at the position corresponding to the mouse position as the
	 * specified state
	 * 
	 * @param x     position of the mouse
	 * @param y     position of the mouse
	 * @param state to draw
	 */
	public void handleClick(int mouseX, int mouseY, State penState) {
		try {
			cells[0][(int) Math.floor(mouseY / Cell.getSize().y)][(int) Math.floor(mouseX / Cell.getSize().x)].setState(penState);
		} catch (ArrayIndexOutOfBoundsException e) {
		}
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
	 * Reverts all cells to their previous state
	 */
	public void revert() {
		for (int z = 0; z < cells.length; z++)
			for (int y = 0; y < cells[z].length; y++)
				for (int x = 0; x < cells[z][y].length; x++)
					cells[z][y][x].revert();
	}

	/**
	 * Draws all cells to the screen
	 * 
	 * @param sketch to draw to
	 */
	public void draw(PApplet sketch) {
//		PVector gridSize = new PVector(Cell.getSize().x * (cells[0][0].length - 1),
//				Cell.getSize().y * (cells[0].length - 1), Cell.getSize().z * (cells.length - 1));
//
//		sketch.stroke(255);
////		sketch.box(gridSize.x*2, gridSize.y*2, gridSize.z*2);
//		sketch.rect(0, 0, sketch.width, sketch.height);
		
		sketch.stroke(0);

		sketch.pushMatrix();

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

	
	
	/**
	 * Returns JSON array form of the grid in current configuration
	 * @return - the JSONArray representation of this grid
	 */
	public JSONArray getJsonArray() {
		JSONArray arr = new JSONArray();
		for (int z = 0; z < cells.length; z++)
			for (int y = 0; y < cells[z].length; y++)
				for (int x = 0; x < cells[z][y].length; x++) {
					if(!cells[z][y][x].getState().getName().equals("default")) {						
						JSONObject cellObj = new JSONObject();
						cellObj.setInt("x", x);
						cellObj.setInt("y", y);
						cellObj.setInt("z", z);
						cellObj.setString("state", cells[z][y][x].getState().getName());
					}
				}
					
		return arr;
	}

}
