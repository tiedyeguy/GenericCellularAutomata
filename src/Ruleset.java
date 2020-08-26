import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * Static class for holding all the rules
 */

public class Ruleset {
	public static Ruleset instance;
	private Map<State, ArrayList<Rule>> rulesets;

	private Ruleset(JsonObject ruleset) {
		rulesets = new HashMap<State, ArrayList<Rule>>();
		State.addAllStates(ruleset.keySet());
		
		for(Entry<String, JsonElement> stateEntry : ruleset.entrySet()) {
			ArrayList<Rule> entryRules = new ArrayList<Rule>();
			State currentState = State.getState(stateEntry.getKey());
			for(Entry<String, JsonElement> stateAttribute : stateEntry.getValue().getAsJsonObject().entrySet()) {
				if(stateAttribute.getKey().equals("color")) {
					currentState.setColor(stateAttribute.getValue().getAsString());
				} else if(stateAttribute.getKey().equals("hotkey")) {
					currentState.setHotkey(stateAttribute.getValue().getAsString().charAt(0));
				} else {
					entryRules.add(new ComplexRule(stateAttribute.getValue().getAsJsonObject(), State.getState(stateAttribute.getKey())));					
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
	public static void createRuleset(JsonObject rulesObj) {
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


//while(!ruleset.peek().equals(JsonToken.END_DOCUMENT)){
//	JsonToken nextToken = ruleset.peek();
//	
//	if(nextToken.equals(JsonToken.BEGIN_OBJECT)){
//		ruleset.beginObject();
//	} else if(nextToken.equals(JsonToken.NAME)){
//		String name  =  ruleset.nextName();
//		System.out.println(name);
//	} else if(nextToken.equals(JsonToken.STRING)){
//		String value =  ruleset.nextString();
//		System.out.println(value);
//	} else if(nextToken.equals(JsonToken.NUMBER)){
//		long value =  ruleset.nextLong();
//		System.out.println(value);
//	} else if(nextToken.equals(JsonToken.BEGIN_ARRAY)) {
//		ruleset.beginArray();
//	} else if(nextToken.equals(JsonToken.END_OBJECT)) {
//		ruleset.endObject();
//	} else if(nextToken.equals(JsonToken.END_ARRAY)) {
//		ruleset.endArray();
//	}
//}
//} catch (IOException e) {
//	e.printStackTrace();
//}
