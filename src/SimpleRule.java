import processing.data.JSONObject;

/**
 * Class that defines rules for any one dimensional automata
 */
public class SimpleRule extends Rule {
	private int rule;
	private State result;
	
	
	public SimpleRule(int rule) {
		this.rule = rule;
	}
	
	/**
	 * The rule in this context should be binary, i.e. "10010011". If its just an integer, pass to other constructor
	 * @param rule - binary rule for the string
	 */
	public SimpleRule(String rule) {
		this.rule = Integer.parseInt(rule, 2);
	}

	@Override
	public boolean isTrue(Cell[] neighbors) {
		int stateNumber = 0;
		if(neighbors.length < Math.pow(3, Settings.getDimension().getDimensionNumber()))
			return false;
		
		for(int i = 0; i < neighbors.length; i++) {
			stateNumber += (neighbors[i].getState().getName().equals("default") ? 0 : 1) * ((int)Math.pow(2, i));
		}
		int stateAcceptDigit = (rule>>(stateNumber))%2;
		
		result = stateAcceptDigit == 1 ? State.getState("live") : State.getState("default");
		
		return true;
	}

	@Override
	public State getState() {
		return result;
	}

	@Override
	public JSONObject saveToJson() {
		JSONObject jsonRule = new JSONObject();
		jsonRule.setInt("rule", rule);
		return jsonRule;
	}

	@Override
	public void loadFromJSON(JSONObject jsonable) {
		rule = jsonable.getInt("rule");
	}
	
	
}
