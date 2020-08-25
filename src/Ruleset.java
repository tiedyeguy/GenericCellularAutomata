import java.util.ArrayList;
import java.util.Map;

import processing.data.JSONObject;

/**
 * Static class for holding all the rules
 */

public class Ruleset {
	private static Map<State, ArrayList<Rule>> rulesets;
	
	public Ruleset(JSONObject rules) {
		
	}
	
	public ArrayList<Rule> getRulesFor(State s) {
		return rulesets.get(s);
	}
}
