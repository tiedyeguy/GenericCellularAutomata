import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import processing.data.JSONArray;
import processing.data.JSONObject;

/**
 * Contains user preferences that don't fall under a cell state
 */
public class Settings {
	// Whether the grid wraps around on itself (leftmost cells have neighbors on the right side)
	private static boolean wrapping;
	//Dimensions of the grid, including whether the grid tracks time
	private static Dimension dimension;
	// How neighbors are evaluated: include diagonals or not?
	private static NeighborType neighborType;
	//The x dimension of the grid
	private static int xSize;
	//The y dimension of the grid, 1 if one dimensional
	private static int ySize;
	//The z dimension of the grid, 1 if one or two dimensional
	private static int zSize;

	/**
	 * Initializes the settings with user preferences
	 * @param jsonAutomata - The JSON file that contains all the information for the simulation, see template on github
	 * @return - Returns the array of the initial state, to be fed into the grid
	 */
	public static JSONArray init(File jsonAutomata) {
		try {
			Scanner sc = new Scanner(jsonAutomata);
			String jsonAsStr = "";
			do {
				jsonAsStr += sc.nextLine();
			} while(sc.hasNext());
			sc.close();

			JSONObject automataObj = JSONObject.parse(jsonAsStr);

			Ruleset.createRuleset(automataObj.getJSONObject("rules"));
			Settings.wrapping = automataObj.getBoolean("wrapping");
			setDimension(automataObj.getString("dimensions", "2"));
			setNeighborType(automataObj.getString("type", "M"));
			JSONObject size = automataObj.getJSONObject("size");
			Settings.xSize = size.getInt("x", 1);
			Settings.ySize = size.getInt("y", 1);
			Settings.zSize = size.getInt("z", 1);
			return automataObj.getJSONArray("initial_state");
		} catch (FileNotFoundException e) {
			System.err.println("JSON Config File not found");
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Converts between the string representation to the NeighborType Enum
	 * @param neighborType - Sets the global neighbor type from a string representation
	 */
	private static void setNeighborType(String neighborType) {
		switch(neighborType) {
		case "N":
			Settings.neighborType = NeighborType.NEUMANN;
			break;
		case "M":
			Settings.neighborType = NeighborType.MOORE;
			break;
		default:
			Settings.neighborType = NeighborType.MOORE;		
		}
	}

	/**
	 * Converts between the String representation to the Dimension Enum
	 * @param dimension - Sets the global dimension type from this string representation
	 */
	private static void setDimension(String dimension) {
		switch(dimension) {
		case "1":
			Settings.dimension = Dimension.ONE;
			break;
		case "1-time":
			Settings.dimension = Dimension.ONE_TIME;
			break;
		case "2":
			Settings.dimension = Dimension.TWO;
			break;
		case "2-time":
			Settings.dimension = Dimension.TWO_TIME;
			break;
		case "3":
			Settings.dimension = Dimension.THREE;
			break;
		default: 
			Settings.dimension = Dimension.TWO;
		}
	}

	public static boolean isWrapping() {
		return wrapping;
	}

	public static Dimension getDimension() {
		return dimension;
	}

	public static NeighborType getNeighborType() {
		return neighborType;
	}

	/**
	 * Gets the max X dimension of this automata
	 * @return - integer size value
	 */
	public static int getXDimension() {
		return xSize;
	}

	/**
	 * Gets the max Y dimension of this automata
	 * @return - integer size value, 1 if one dimensional grid
	 */
	public static int getYDimension() {
		return ySize;
	}

	/**
	 * Gets the max Z dimension of this automata
	 * @return - integer size value, 1 if one dimensional or two dimensional grid
	 */
	public static int getZDimension() {
		return zSize;
	}
}
