import processing.data.JSONObject;

/**
 * Orientation Rule is the rule that defines rulesets that are orientation dependent
 */
public class OrientationRule extends Rule {
	private State[] orientedArray;
	private boolean symmetric;

	public OrientationRule(JSONObject ruleObject, State nextState) {
		super(nextState);
		orientedArray = new State[(int)Math.pow(2, Settings.getDimension().getDimensionNumber())];

		loadFromJSON(ruleObject);
	}

	@Override
	public JSONObject saveToJson() {
		JSONObject orientationRule = new JSONObject();
		if(Settings.getDimension().getDimensionNumber() == 2) {
			orientationRule.setString("left", orientedArray[0] != null ? orientedArray[0].getName() : "*");
			orientationRule.setString("top", orientedArray[1] != null ? orientedArray[1].getName() : "*");
			orientationRule.setString("right", orientedArray[2] != null ? orientedArray[2].getName() : "*");
			orientationRule.setString("bottom", orientedArray[3] != null ? orientedArray[3].getName() : "*");
		} else if(Settings.getDimension().getDimensionNumber() == 1) {
			orientationRule.setString("left", orientedArray[0] != null ? orientedArray[0].getName() : "*");
			orientationRule.setString("right", orientedArray[1] != null ? orientedArray[1].getName() : "*");			
		}
		
		if(symmetric) {
			orientationRule.setBoolean("symmetric", symmetric);
		}
		return orientationRule;
	}

	@Override
	public void loadFromJSON(JSONObject jsonable) {
		if(Settings.getDimension().getDimensionNumber() == 2) {
			orientedArray[0] = State.getState(jsonable.getString("left", "*"));
			orientedArray[1] = State.getState(jsonable.getString("top", "*"));
			orientedArray[2] = State.getState(jsonable.getString("right", "*"));
			orientedArray[3] = State.getState(jsonable.getString("bottom", "*"));
		} else if(Settings.getDimension().getDimensionNumber() == 1) {
			orientedArray[0] = State.getState(jsonable.getString("left", "*"));
			orientedArray[1] = State.getState(jsonable.getString("right", "*"));			
		}
		symmetric = jsonable.getBoolean("symmetric", false);
	}

	@Override
	public boolean isTrue(Cell[] neighbors) {
		if(Settings.isWrapping() || neighbors.length >= (Math.pow(3, Settings.getDimension().getDimensionNumber())-1)) {
			State[] orderedNeighbors = new State[orientedArray.length];
			if(Settings.getDimension().getDimensionNumber() == 1) {
				orderedNeighbors[0] = neighbors[0].getState();
				orderedNeighbors[1] = neighbors[1].getState();
			} else if(Settings.getDimension().getDimensionNumber() == 2) {
				orderedNeighbors[0] = neighbors[2].getState();
				orderedNeighbors[1] = neighbors[0].getState();				
				orderedNeighbors[2] = neighbors[3].getState();
				orderedNeighbors[3] = neighbors[1].getState();
			}

			if(symmetric) {
				for(int shift = 0; shift < orientedArray.length; shift++) {
					if(stateArraysMatch(orientedArray, orderedNeighbors, shift)) {
						return true;
					}
				}
			} else {
				return stateArraysMatch(orientedArray, orderedNeighbors, 0);
			}
		}
		return false;
	}

	/**
	 * Checks to see if two arrays of states match, accounting for wildcards in array 1 and a shift factor applied to arr2
	 * @param arr1 - Array of States. Can contain nulls, which match any state
	 * @param arr2 - Array of States, shifted with wrapping to the right by shift factor
	 * @param shift - Integer number of positions to right shift arr2
	 * @return - True iff the arrays 'match' with shifting and wildcard nulls
	 */
	private boolean stateArraysMatch(State[] arr1, State[] arr2, int shift) {
		for(int i = 0; i < orientedArray.length; i++) {
			if(arr1[i] != null && arr1[i] != arr2[(i+shift)%arr2.length])
				return false;
		}
		return true;
	}

	@Override
	public String toString() {
		String s = "Oriented Rule: Transitions to " + getToState().getName() + "\n";
		for(int i = 0; i < orientedArray.length; i++) {
			s += "\t" + i + ":" + (orientedArray[i] == null ? "*" : orientedArray[i].getName()) + "\n";
		}
		s += "Symmetric " + symmetric + "\n";
		return s;
	}
}
