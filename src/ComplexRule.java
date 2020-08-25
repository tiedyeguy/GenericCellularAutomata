import java.io.IOException;
import java.util.Map;

import com.google.gson.stream.JsonReader;



/**
 * This is a rule for 2 and 3 dimensional grids
 */
public class ComplexRule extends Rule {
	private Map<State, Range> stateThresholds;

	public ComplexRule(JsonReader rule) {
		try {
			while(rule.hasNext()) {
				System.out.println(rule.peek());
				rule.skipValue();
			}
		} catch (IOException e) {
			e.printStackTrace();
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
		return null;
	}
}
