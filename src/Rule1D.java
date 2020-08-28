/**
 * Class that defines rules for any one dimensional automata
 */
public class Rule1D extends Rule {
	private int rule;
	
	
	public Rule1D(int rule) {
		this.rule = rule;
	}
	
	/**
	 * The rule in this context should be binary, i.e. "10010011". If its just an integer, pass to other constructor
	 * @param rule - binary rule for the string
	 */
	public Rule1D(String rule) {
		this.rule = Integer.parseInt(rule, 2);
	}

	@Override
	public boolean isTrue(Cell[] neighbors) {
		int stateNumber = 0;
		for(int i = 0; i < neighbors.length; i++) {
			stateNumber += (neighbors[i].getState().getName().equals("default") ? 0 : 1) * ((int)Math.pow(2, i));
		}
		int stateAcceptDigit = (rule>>(int)Math.pow(2, stateNumber))%2;
		return stateAcceptDigit == 1;
	}

	@Override
	public State getState() {
		return null;
	}
	
	
}
