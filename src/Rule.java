import processing.data.JSONObject;

/**
 * The generic rule class
 */
public abstract class Rule implements Jsonable {
	private State toState;
	
	public Rule(State toState) {
		this.toState = toState;
	}
	
	/**
	 * Returns true iff the ruleset is satisfied with the given neighbors
	 * @param neighbors - The array of neighboring cells to the one you are applying this rule to
	 * @return - true if the rule can be applied validly, false if the rule can't be applied to the cell
	 */
	public abstract boolean isTrue(Cell[] neighbors);
	
	/**
	 * The state that the cell should transition to if this rule isTrue
	 * @return - The state that this rule should match
	 */
	public State getToState() {
		return toState;
	}
	
	/**
	 * Makes a Rule appropriate to the passed JSONObject
	 * @param rule - the rule object, which should either be 'complex', 'oriented', or 'any'. See readme for formatting
	 * @param toState - The state that this rule will transition to if its conditions are met
	 * @return - A rule that can determine whether a given set of neighbors meets the rule conditions
	 */
	public static Rule makeRule(JSONObject ruleObject, State toState) {
		Rule validRule;
		if(ruleObject.hasKey("*")) {
			validRule = new AnyRule(ruleObject, toState);
		} else if(ruleObject.hasKey("left")) {
			validRule = new OrientationRule(ruleObject, toState);
		} else {
			validRule = new ComplexRule(ruleObject, toState);
		}
		return validRule;
	}
}
