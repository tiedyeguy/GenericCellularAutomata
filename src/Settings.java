import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import processing.data.JSONArray;
import processing.data.JSONObject;

/**
 * Contains user preferences that don't fall under a cell state
 */
public class Settings {
	//Whether the grid wraps around on itself (leftmost cells have neighbors on the right side)
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
	//Represents whether this ruleset is 'simple' or 'complex', complex rules have more than two discrete states while simple rulesets are represented by integers
	private static boolean isSimple;
	//Represents the depth for the past if grid history is displayed
	private static int time_depth;
	
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
			try {
				State.createRuleset(automataObj.getJSONObject("rules"));			
				Settings.isSimple = false;
			} catch(RuntimeException e) {
				State.createRuleset(automataObj.getInt("rules", 0));
				Settings.isSimple = true;
			}
			
			Settings.wrapping = automataObj.getBoolean("wrap");
			Settings.dimension = Dimension.valueOfLabel(automataObj.getString("dimensions", "2"));
			Settings.neighborType = NeighborType.typeOfChar((automataObj.getString("type", "M")).toLowerCase().charAt(0));
			JSONObject size = automataObj.getJSONObject("size");
			Settings.xSize = size.getInt("x", 1);
			Settings.ySize = size.getInt("y", 1);
			Settings.zSize = size.getInt("z", 1);
			Settings.time_depth = automataObj.getInt("time-depth", 1);
			return automataObj.getJSONArray("initial_state");
		} catch (FileNotFoundException e) {
			System.err.println("JSON Config File not found");
			e.printStackTrace();
			return null;
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
	 * If the dimension is timed, this tracks how deep the display goes
	 * @return - integer depth, should be 1 or greater
	 */
	public static int getTimeDepth() {
		return time_depth;
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
	
	/**
	 * A simple ruleset is one represented by a binary string, consists of two states. Complex rulesets are game of life, wire world, etc.
	 * @return - True iff the ruleset is a simple ruleset (requires cells to be in their own neighbor array), false if complex (cell not included in neighbor array)
	 */
	public static boolean isSimpleRuleset() {
		return isSimple;
	}
	
	/**
	 * Saves settings to JSON file
	 * @param jsonFile - the file to save JSON settings to
	 * @param grid - needs the grid in order to save initial state
	 */
	public static void saveToJSON(File jsonFile, Grid g) {
		JSONObject savedJSON = new JSONObject();
		savedJSON.setBoolean("wrap", wrapping);
		JSONObject size = new JSONObject();
		size.setInt("x", xSize);
		if(ySize > 1)
			size.setInt("y", ySize);
		if(zSize > 1)
			size.setInt("z", zSize);
		savedJSON.setJSONObject("size", size);
		savedJSON.setString("dimension", dimension.value);
		savedJSON.setString("neighborType", ""+neighborType.type);
		if(isSimple)
			savedJSON.setJSONObject("rules", State.saveRuleset());
		else
			savedJSON.setString("rules", State.saveRuleset().getString("rules"));
		JSONArray initialState = g.getJsonArray();
		if(initialState != null)
			savedJSON.setJSONArray("initial_state", initialState);
		savedJSON.save(jsonFile, "");
	}
}
