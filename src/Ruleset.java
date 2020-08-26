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
		Map<String, State> states = new HashMap<String, State>();
		
		for(Entry<String, JsonElement> entry : ruleset.entrySet()) {
			ArrayList<Rule> entryRules = new ArrayList<Rule>();
			for(Entry<String, JsonElement> subEntry : entry.getValue().getAsJsonObject().entrySet()) {
				if(!subEntry.getKey().equals("color")) {
					entryRules.add(new ComplexRule(subEntry.getValue().getAsJsonObject()));
				}
			}
			states.put(entry.getKey(), new State());
			rulesets.put(states.get(entry.getKey()), entryRules);
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
