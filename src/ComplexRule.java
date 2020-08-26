import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;



/**
 * This is a rule for 2 and 3 dimensional grids
 */
public class ComplexRule extends Rule {
	private Map<State, Range> stateThresholds;
	private State nextState;
	
	public ComplexRule(JsonObject ruleObject, State nextState) {
		this.nextState = nextState;
		
		for(Entry<String, JsonElement> threshold: ruleObject.entrySet()) {
			String thresholdName = threshold.getKey();
			State ruleState = State.getState(thresholdName);
			if(ruleState != null)
				stateThresholds.put(ruleState, new Range(threshold.getValue().getAsString()));
			else if(thresholdName.equalsIgnoreCase("any")) {
				//TODO: FIX BAD LOGIC
				Range validRange = new Range(threshold.getValue().getAsString());
				State.getAllStates().forEach((state)->stateThresholds.put(state, validRange));
			} else {
				System.err.println("THERE WAS A PROBLEM, RULE " + thresholdName + " : "+ threshold.getValue().getAsString() 
						+ " DOES NOT REFERENCE VALID STATE");
			}
		}
	}

	
	@Override
	public boolean isTrue(Map<State, Number> neighbors) {
		for(State discreteState : stateThresholds.keySet()) {
			if(!stateThresholds.get(discreteState).contains(neighbors.get(discreteState)))
				return false;
		};
		return true;
	}

	@Override
	public State getState() {
		return nextState;
	}
}
