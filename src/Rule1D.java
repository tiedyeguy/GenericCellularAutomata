import java.util.Map;

/**
 * Class that defines rules for any one dimensional automata
 */
public class Rule1D extends Rule {
	private int rule;
	
	
	public Rule1D(int rule) {
		this.rule = rule;
	}
	
	/**
	 * The rule in this context should be binary, i.e. "10010011"
	 * @param rule - binary rule for the string
	 */
	public Rule1D(String rule) {
		this.rule = Integer.parseInt(rule, 2);
	}

	@Override
	public boolean isTrue(Map<State, Integer> neighbors) {
		return false;
	}

	@Override
	public State getState() {
		return null;
	}
	
	
}
