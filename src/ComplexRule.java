import java.util.Map;

import com.google.gson.JsonObject;



/**
 * This is a rule for 2 and 3 dimensional grids
 */
public class ComplexRule extends Rule {
	private Map<State, Range> stateThresholds;

	public ComplexRule(JsonObject ruleObject) {
//		for(Entry<String, > rule : ruleObject) {
//			
//		}
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
		return null;
	}
}
