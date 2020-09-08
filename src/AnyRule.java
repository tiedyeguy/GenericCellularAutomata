import processing.data.JSONObject;

/**
 * A type of rule that defines a set of conditions for transitioning from the current state to the next state. 
 * 		Specifically, tracks if a given threshold of values matches the number of non-default neighbors
 */
public class AnyRule extends Rule {
	private Range thresh;
	
	
	public AnyRule(JSONObject jsonable, State toState) {
		super(toState);
		loadFromJSON(jsonable);
	}

	@Override
	public JSONObject saveToJson() {
		JSONObject anyRule = new JSONObject();
		anyRule.setString("*", thresh.toString());
		return anyRule;
	}

	@Override
	public void loadFromJSON(JSONObject jsonable) {
		thresh = new Range(jsonable.getString("*"));
	}

	@Override
	public boolean isTrue(Cell[] neighbors) {
		State defaultState = State.getState("default");
		int total = 0;
		for(Cell neighbor : neighbors) 
			if(neighbor.getState() != defaultState)
				total++;
		
		return thresh.contains(total);
	}

}
