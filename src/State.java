import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import processing.data.JSONArray;
import processing.data.JSONObject;

/**
 * Tracks the state of the cell
 */
public class State implements Jsonable {
	private static Map<String, State> states;
	private String name;
	private boolean fades;
	private int red, green, blue, redF, greenF, blueF;
	private int fadeFrames;
	private char hotkey; 
	private ArrayList<Rule> ruleset;
	private boolean arrFormat;

	/**
	 * Gets a list of all the states in this cellular automata
	 * @return - List of state objects containing hotkey, color, user-given name, etc.
	 */
	public static List<State> getAllStates() {
		return states.values().stream().collect(Collectors.toList());
	}

	/**
	 * Gets the state associated with given stateName
	 * @param stateName - the user given name for this state
	 * @return - State object containing name, color, hotkey, etc. Could be null if no such state exists
	 */
	public static State getState(String stateName) {
		return states.get(stateName);
	}

	/**
	 * Gets the state associated with the given hotkey
	 * @param hotkey
	 * @return State object, null if there is no state for given hotkey
	 */
	public static State getStateFromHotkey(char hotkey) {
		List<State> allState = State.getAllStates();

		List<State> matchingState = allState.stream().filter((state) -> state.getHotkey() == hotkey).collect(Collectors.toList());

		if(matchingState.size() > 1) throw new IllegalStateException("Only one state should be associated with each hotkey");
		else if(matchingState.size() == 0) return null;

		return matchingState.get(0);
	}

	public State(String stateName) {
		ruleset = new ArrayList<Rule>();
		fades = false;
		name = stateName;
		hotkey = ' ';
		fadeFrames = 3;
		red = 0;
		green = 0;
		blue = 0;
	}

	/**
	 * Sets the hotkey of the state
	 * @param hotkey - Hotkey character, should be lowercase
	 */
	public void setHotkey(char hotkey) {
		this.hotkey = hotkey;
	}

	/**
	 * Get the hotkey associated with this state
	 * @return - Will be ' ' if no hotkey was set
	 */
	public char getHotkey() {
		return hotkey;
	}

	/**
	 * Gets the amount of red in the color associated with this state
	 * @return - integer form of the color, one byte. Default 0
	 */
	public int getRed() {
		return red;
	}

	/**
	 * Gets the amount of green in the color associated with this state
	 * @return - integer form of the color, one byte. Default 0
	 */
	public int getGreen() {
		return green;
	}

	/**
	 * Gets the amount of blue in the color associated with this state
	 * @return - integer form of the color, one byte. Default 0
	 */
	public int getBlue() {
		return blue;
	}

	/**
	 * Gets the amount of red in the color associated with this state initially. Only use if stateFades()
	 * @return - integer form of the color, one byte. Default 0
	 */
	public int getFirstRed() {
		return redF;
	}

	/**
	 * Gets the amount of green in the color associated with this state initially. Only use if stateFades()
	 * @return - integer form of the color, one byte. Default 0
	 */
	public int getFirstGreen() {
		return greenF;
	}

	/**
	 * Gets the amount of blue in the color associated with this state initially. Only use if stateFades()
	 * @return - integer form of the color, one byte. Default 0
	 */
	public int getFirstBlue() {
		return blueF;
	}

	/**
	 * True iff this state fades from initial colors to final colors
	 * @return - True if the state fades from getFirstRed()->getRed() and getFirstGreen()->getGreen() etc.
	 */
	public boolean stateFades() {
		return fades;
	}

	/**
	 * If stateFades() is true, this will represent the number of frames a state should take to transition from first colors to final
	 * @return - Integer number of frames a state takes to fade, 3 if unspecified
	 */
	public int getFadeFrames() {
		return fadeFrames;
	}

	/**
	 * Sets the color of the state to specified value
	 * @param color - the color of this state
	 * @param firstColor - If the color is the initial state color, true, if this is the final state color, false
	 */
	public void setColor(String color, boolean firstColor) {
		if(firstColor) {
			fades = true;
			redF = Integer.parseInt(color.substring(0, 2), 16);
			greenF = Integer.parseInt(color.substring(2, 4), 16);
			blueF = Integer.parseInt(color.substring(4, 6), 16);
		} else {
			red = Integer.parseInt(color.substring(0, 2), 16);
			green = Integer.parseInt(color.substring(2, 4), 16);
			blue = Integer.parseInt(color.substring(4, 6), 16);
		}
	}

	/**
	 * Gets the name associated with this state
	 * @return - The name of the state, always can be used with State.getState
	 */
	public String getName() {
		return name;
	}

	/**
	 * Creates the ruleset for this cellular automata based on the given object
	 * @param rulesObj - Must be a json 'rules' object, will use each entry in object to find states
	 */
	@SuppressWarnings("unchecked")
	public static void createRuleset(JSONObject rulesObj) {
		states = new HashMap<String, State>();
		for(String stateEntry : (Set<String>)rulesObj.keys()) {
			State state = new State(stateEntry);
			states.put(stateEntry, state);
		}
		states.values().forEach((state)->state.loadFromJSON(rulesObj.getJSONObject(state.getName())));
	}

	/**
	 * Creates the ruleset for this cellular automata based on the given integer, for Rules1D automata
	 * @param ruleNumber - The number that defines which rules this is
	 */
//	public static void createRuleset(int ruleNumber) {
//		states = new HashMap<String, State>();
//		State liveState = new State("live");
//		liveState.setColor("FFFFFF", false);
//		State deadState = new State("default");
//
//		//		SimpleRule simpleRule = new SimpleRule(ruleNumber);
//
//		//		liveState.getRules().add(simpleRule);
//		//		deadState.getRules().add(simpleRule);
//		states.put("live", liveState);
//		states.put("default", deadState);
//	}

	/**
	 * Saves the ruleset as JSON
	 * @return - JSON representation of all the rules
	 */
	public static JSONObject saveRuleset() {
		JSONObject ruleset = new JSONObject();
		states.forEach((name, state)->ruleset.setJSONObject(name, state.saveToJson()));
		return ruleset;
	}

	/**
	 * Get rules for the specified state
	 * @param s - A valid state for which rules exist
	 * @return - An arraylist of Rules that govern the transitioning of cells in given state
	 */
	public ArrayList<Rule> getRules() {
		return ruleset;
	}

	/**
	 * Sets up the rules based on a JSONArray of rules
	 * @param ruleArray - A JSONArray consisting of appropriately formatted rules
	 */
	private void loadRules(JSONArray ruleArray) {
		for(int i = 0; i < ruleArray.size(); i++) {
			JSONObject ruleObject = ruleArray.getJSONObject(i);
			String toState = (String)ruleObject.remove("toState");;
			ruleset.add(Rule.makeRule(ruleObject, State.getState(toState)));
		}
	}

	@Override
	public void loadFromJSON(JSONObject jsonable) {
		arrFormat = false;
		for(Object stateAttribute : jsonable.keys()) {
			String stateAttributeName = (String)stateAttribute;
			if(stateAttributeName.equals("color")) {
				setColor(jsonable.getString(stateAttributeName), false);
			} else if(stateAttributeName.equals("first-color")) {
				setColor(jsonable.getString(stateAttributeName), true);
			} else if(stateAttributeName.equals("fade-frames")) {
				fadeFrames = jsonable.getInt(stateAttributeName);
			} else if(stateAttributeName.equals("hotkey")) {
				setHotkey(jsonable.getString(stateAttributeName).toLowerCase().charAt(0));
			} else if(stateAttributeName.equals("rules")) {
				arrFormat = true;
				loadRules(jsonable.getJSONArray(stateAttributeName));
			}
			else {
				ruleset.add(Rule.makeRule(jsonable.getJSONObject(stateAttributeName), State.getState(stateAttributeName)));					
			}
		}
	}


	@Override
	public JSONObject saveToJson() {
		JSONObject stateJson = new JSONObject();
		String color = Integer.toHexString(red<<16 | green<<8 | blue);
		while(color.length() < 6) {
			color = "0" + color;
		}
		stateJson.setString("color", color);
		stateJson.setString("hotkey", ""+hotkey);
		if(arrFormat) {
			JSONArray rules = new JSONArray();
			int i = 0;
			for(Rule rule : ruleset) {
				JSONObject ruleObj = rule.saveToJson();
				ruleObj.setString("toState", rule.getToState().getName());
				rules.setJSONObject(i, ruleObj);
				i++;
			}
			stateJson.setJSONArray("rules", rules);
		} else
			ruleset.forEach((rule)->stateJson.setJSONObject(rule.getToState().getName(),rule.saveToJson()));

		return stateJson;
	}

	public String toString() {
		String stateString = "State " + getName() + "\n";
		if(stateFades())
			stateString += "\tFirst Red: " + redF + ", First Blue: " + blueF + ", First Green: " + greenF + ", Transition to ";
		stateString += "Red: " + red + ", Blue: " + blue + ", Green: " + green + "\n";
		stateString += "\tHotkey: " + hotkey + "\n";
		stateString += "Governed By: " + getRules();
		return stateString;
	}
}
