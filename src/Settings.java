import java.io.File;

import processing.data.JSONArray;
import processing.data.JSONObject;

/**
 * Contains user preferences that don't fall under a cell state
 */
public class Settings {
	//Default video name for recorded videos
	private static final String DEFAULT_VIDEO_NAME = "WhyDidntYouSpecifyTheVideoNameYouFuckingIdiot";
	
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
	//Represents the depth for the past if grid history is displayed
	private static int time_depth;
	//The number of frames to record in a video, -1 if not recording
	private static int framesToRecord;
	//The speed of a recording in frames per second, -1 if not recording
	private static int frameSpeed;
	//The name of the video if recording
	private static String videoName;
	
	/**
	 * Initializes the settings with user preferences
	 * @param jsonAutomata - The JSON file that contains all the information for the simulation, see template on github
	 * @return - Returns the array of the initial state, to be fed into the grid
	 */
	public static JSONArray init(JSONObject automataObj) {
		Settings.wrapping = automataObj.getBoolean("wrap");
		Settings.dimension = Dimension.valueOfLabel(automataObj.getString("dimensions", "2"));
		Settings.neighborType = NeighborType.typeOfChar((automataObj.getString("type", "M")).toLowerCase().charAt(0));
		JSONObject size = automataObj.getJSONObject("size");
		State.createRuleset(automataObj.getJSONObject("rules"));	
		Settings.xSize = size.getInt("x", 1);
		Settings.ySize = size.getInt("y", 1);
		Settings.zSize = size.getInt("z", 1);
		Settings.time_depth = automataObj.getInt("time-depth", 1);
		Settings.framesToRecord = automataObj.getInt("record-frames", -1);
		Settings.frameSpeed = automataObj.getInt("video-speed", -1);
		Settings.videoName = automataObj.getString("video-name", DEFAULT_VIDEO_NAME);
		
		while(new File(videoName + ".mp4").exists()) {
			System.out.println("heppening");
			videoName += " again";
		}
		
		return automataObj.getJSONArray("initial_state");
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
	 * Gets the frame speed of the video recording in FPS
	 * @return - Integer FPS, -1 if not recording
	 */
	public static int getFrameSpeed() {
		return frameSpeed;
	}

	/**
	 * Gets the number of frames to record
	 * @return - Integer FPS, -1 if not recording
	 */
	public static int getFramesToRecord() {
		return framesToRecord;
	}
	
	/**
	 * Should the cell simulation be recorded?
	 * @return - true iff the automata should be recording
	 */
	public static boolean isRecording() {
		return framesToRecord != -1;
	}

	/**
	 * The name that the recorded video is saved under
	 * @return - String name of video, always a valid String even if not recording
	 */
	public static String getVideoName() {
		return videoName;
	}
	
	/**
	 * Flips whether the dimension is timed
	 * @param trackPast - If true, the past will be tracked. If false, untracked
	 */
	public static void trackTime(boolean trackPast) {
		if(dimension.isTimed() && !trackPast) {
			if(dimension == Dimension.ONE_TIME) {
				dimension = Dimension.ONE;
			} else if(dimension == Dimension.TWO_TIME) {
				dimension = Dimension.TWO;
			}
		} else if(!dimension.isTimed() && trackPast) {
			if(dimension == Dimension.ONE) {
				if(time_depth == 1) {
					time_depth = 50;
				}
				dimension = Dimension.ONE_TIME;
			} else if(dimension == Dimension.TWO) {
				if(time_depth == 1) {
					time_depth = 50;
				}
				dimension = Dimension.TWO_TIME;
			}
		}  
	}

	/**
	 * Saves settings to JSON file
	 * @param grid - needs the grid in order to save initial state
	 * @return - JSON object containing file
	 */
	public static JSONObject saveToJSON(Grid g) {
		JSONObject savedJSON = new JSONObject();
		savedJSON.setBoolean("wrap", wrapping);
		JSONObject size = new JSONObject();
		size.setInt("x", xSize);
		if(ySize > 1)
			size.setInt("y", ySize);
		if(zSize > 1)
			size.setInt("z", zSize);
		savedJSON.setJSONObject("size", size);
		savedJSON.setString("dimensions", dimension.value);
		if(dimension.isTimed()) {
			savedJSON.setInt("time-depth", time_depth);
		}
		savedJSON.setString("type", ""+neighborType.type);
		savedJSON.setJSONObject("rules", State.saveRuleset());
		if(Settings.frameSpeed != -1) {
			savedJSON.setInt("record-frames", framesToRecord);
			savedJSON.setInt("video-speed", frameSpeed);
			savedJSON.setString("video-name", videoName);
		}
		JSONArray initialState = g.getJsonArray();
		if(initialState != null)
			savedJSON.setJSONArray("initial_state", initialState);
		return savedJSON;
	}
	
	/**
	 * Returns all the global settings as strings
	 * @return
	 */
	public static String toStaticString() {
		String settingsString = "Program Settings\n";
		settingsString += "Wrap: " + wrapping + "\n";
		settingsString += "Dimension: " + dimension.value + "\n";
		settingsString += "Neighbor Type: " + neighborType.type + "\n";
		settingsString += "Size: (" + xSize + ", " + ySize + ", " + zSize + ")\n";
		settingsString += "Time Depth: " + time_depth + "\n";
		settingsString += "Is Recording: " + isRecording() + "\n";
		settingsString += "\tVideo Speed: " + frameSpeed + "fps \n";
		settingsString += "\tNumber of Frames to Record: " + framesToRecord + "\n";
		settingsString += "\tName of recorded video: " + videoName + "\n";
		return settingsString;
	}
}
