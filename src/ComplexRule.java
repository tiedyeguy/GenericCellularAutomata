import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import processing.data.JSONObject;


/**
 * This is a rule for 2 and 3 dimensional grids
 */
public class ComplexRule extends Rule {
	private Map<State, Range> stateThresholds;
	private Range anyButDefault;
	private State nextState;
	
	public ComplexRule(JSONObject ruleObject, State nextState) {
		this.nextState = nextState;
		stateThresholds = new HashMap<State, Range>();
		
		loadFromJSON(ruleObject);
	}

	
	@Override
	public boolean isTrue(Cell[] neighbors) {
		if(anyButDefault == null) {
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
		} else {
			int nonDeadCount = 0;
			State deadState = State.getState("default");
			for(Cell neighbor : neighbors) {
				if(!neighbor.getState().equals(deadState))
					nonDeadCount++;
			}
			
			if(anyButDefault.contains(nonDeadCount)) {
				return true;
			} else {
				return false;
			}
		}
	}

	@Override
	public State getState() {
		return nextState;
	}


	@Override
	public JSONObject saveToJson() {
		JSONObject jsonRule = new JSONObject();
		
		for(State state: stateThresholds.keySet()) {
			jsonRule.setString(state.getName(), stateThresholds.get(state).toString());
		}
		if(anyButDefault != null)
			jsonRule.setString("any", anyButDefault.toString());
		return jsonRule;
	}


	@Override
	public void loadFromJSON(JSONObject jsonable) {
		for(Object threshold: jsonable.keys()) {
			String thresholdName = (String)threshold;
			State ruleState = State.getState(thresholdName);
			if(ruleState != null)
				stateThresholds.put(ruleState, new Range(jsonable.getString(thresholdName)));
			else if(thresholdName.equalsIgnoreCase("any")) {
				Range validRange = new Range(jsonable.getString(thresholdName));
				anyButDefault = validRange;
			} else {
				System.err.println("THERE WAS A PROBLEM, RULE " + thresholdName + " : "+ jsonable.getString(thresholdName) 
						+ " DOES NOT REFERENCE VALID STATE");
			}
		}
	}
	
	@Override
	public String toString() {
		String strThresholds = "Transitions to State: " + nextState.getName() + "\n";
		for(Entry<State, Range> e : stateThresholds.entrySet()) {
			strThresholds += "\t" + e.getKey() + ":" + e.getValue() + "\n";
		}
		if(anyButDefault != null) {
			strThresholds += "\t" + anyButDefault + "/n";
		}
		return strThresholds;
	}
}
