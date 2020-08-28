import java.util.HashMap;
import java.util.Map;

import processing.data.JSONObject;


/**
 * This is a rule for 2 and 3 dimensional grids
 */
public class ComplexRule extends Rule {
	private Map<State, Range> stateThresholds;
	private State nextState;
	
	public ComplexRule(JSONObject ruleObject, State nextState) {
		this.nextState = nextState;
		stateThresholds = new HashMap<State, Range>();
		
		for(Object threshold: ruleObject.keys()) {
			String thresholdName = (String)threshold;
			State ruleState = State.getState(thresholdName);
			if(ruleState != null)
				stateThresholds.put(ruleState, new Range(ruleObject.getString(thresholdName)));
			else if(thresholdName.equalsIgnoreCase("any")) {
				//TODO: FIX BAD LOGIC
				Range validRange = new Range(ruleObject.getString(thresholdName));
				State.getAllStates().forEach((state)->stateThresholds.put(state, validRange));
			} else {
				System.err.println("THERE WAS A PROBLEM, RULE " + thresholdName + " : "+ ruleObject.getString(thresholdName) 
						+ " DOES NOT REFERENCE VALID STATE");
			}
		}
	}

	
	@Override
	public boolean isTrue(Cell[] neighbors) {
		Map<State, Number> neighborCounts = new HashMap<State, Number>();

		for (Cell neighbor : neighbors) {
			if (neighborCounts.containsKey(neighbor.getState())) {
				neighborCounts.put(neighbor.getState(), neighborCounts.get(neighbor.getState()).intValue() + 1);
			} else {
				neighborCounts.put(neighbor.getState(), 1);
			}
		}
		
		for(State discreteState : stateThresholds.keySet()) {
			if(!stateThresholds.get(discreteState).contains(neighborCounts.getOrDefault(discreteState, 0)))
				return false;
		}
		return true;
	}

	@Override
	public State getState() {
		return nextState;
	}
}
