import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import processing.data.JSONObject;

/**
 * Tracks the state of the cell
 */
public class State implements Jsonable {
	private static Map<String, State> states = new HashMap<String, State>();
	private String name;
	private int red, green, blue;
	private char hotkey; 
	private ArrayList<Rule> ruleset;
	
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
	 * @return - State object containing name, color, hotkey, etc.
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
		name = stateName;
		hotkey = ' ';
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
	 * Sets the color of the state to specified value
	 * @param color - the color of this state
	 */
	public void setColor(String color) {
		red = Integer.parseInt(color.substring(0, 2), 16);
		green = Integer.parseInt(color.substring(2, 4), 16);
		blue = Integer.parseInt(color.substring(4, 6), 16);
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
	public static void createRuleset(int ruleNumber) {
		State liveState = new State("live");
		liveState.setColor("A0A080");
		State deadState = new State("default");
		
		Rule1D simpleRule = new Rule1D(ruleNumber);
		
		liveState.getRules().add(simpleRule);
		deadState.getRules().add(simpleRule);
		states.put("live", liveState);
		states.put("default", deadState);
	}

	/**
	 * Get rules for the specified state
	 * @param s - A valid state for which rules exist
	 * @return - An arraylist of Rule1Ds or ComplexRules that govern the transitioning of cells in given state
	 */
	public ArrayList<Rule> getRules() {
		return ruleset;
	}

	@Override
	public void loadFromJSON(JSONObject jsonable) {
		for(Object stateAttribute : jsonable.keys()) {
			String stateAttributeName = (String)stateAttribute;
			if(stateAttributeName.equals("color")) {
				setColor(jsonable.getString(stateAttributeName));
			} else if(stateAttributeName.equals("hotkey")) {
				setHotkey(jsonable.getString(stateAttributeName).charAt(0));
			} else {
				ruleset.add(new ComplexRule(jsonable.getJSONObject(stateAttributeName), State.getState(stateAttributeName)));					
			}
		}
	}
	

	@Override
	public JSONObject saveToJson() {
		JSONObject stateJson = new JSONObject();
		stateJson.setString("color", Integer.toHexString(red<<16 | green<<8 | blue));
		stateJson.setString("hotkey", ""+hotkey);
		ruleset.forEach((rule)->stateJson.setJSONObject(rule.getState().getName(),rule.saveToJson()));
		return stateJson;
	}
}
