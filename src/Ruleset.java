import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import processing.data.JSONObject;

/**
 * Static class for holding all the rules
 */

public class Ruleset {
	private static Ruleset instance;
	private Map<State, ArrayList<Rule>> rulesets;

	private Ruleset(JSONObject ruleset) {
		rulesets = new HashMap<State, ArrayList<Rule>>();
		@SuppressWarnings("unchecked")
		Set<String> states = ruleset.keys();
		State.addAllStates(states);
		
		for(String stateEntry : states) {
			ArrayList<Rule> entryRules = new ArrayList<Rule>();
			State currentState = State.getState(stateEntry);
			JSONObject stateEntryJSON = ruleset.getJSONObject(stateEntry);
			for(Object stateAttribute : stateEntryJSON.keys()) {
				String stateAttributeName = (String)stateAttribute;
				if(stateAttributeName.equals("color")) {
					currentState.setColor(stateEntryJSON.getString(stateAttributeName));
				} else if(stateAttributeName.equals("hotkey")) {
					currentState.setHotkey(stateEntryJSON.getString(stateAttributeName).charAt(0));
				} else {
					entryRules.add(new ComplexRule(stateEntryJSON.getJSONObject(stateAttributeName), State.getState(stateAttributeName)));					
				}
			}
			
			rulesets.put(currentState, entryRules);
		}
	}
	
	public static Ruleset getRuleset() {
		if(instance != null) {
			return instance;
		} else {
			System.err.println("ERROR: RULESET INSTANCE USED BEFORE CREATION");
			return null;
		}
	}

	/**
	 * Creates the ruleset for this cellular automata based on the given reader. Must be called before getRuleset()
	 * @param rulesObj - Must be a json 'rules' object, will use each entry in object to find rulesets
	 */
	public static void createRuleset(JSONObject rulesObj) {
		instance = new Ruleset(rulesObj);
	}

	/**
	 * Get rules for the specified state
	 * @param s - A valid state for which rules exist
	 * @return - An arraylist of Rule1Ds or ComplexRules that govern the transitioning of cells in given state
	 */
	public ArrayList<Rule> getRulesFor(State s) {
		return rulesets.get(s);
	}
}