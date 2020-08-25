import java.util.Map;

/**
 * The generic rule class
 */
public abstract class Rule {

	/**
	 * Returns true iff the ruleset is satisfied with the given neighbors
	 * @param neighbors - The number of each type of cell neighboring the one you are applying this rule to
	 * @return - true if the rule can be applied validly, false if the rule can't be applied to the cell
	 */
	public abstract boolean isTrue(Map<State, Integer> neighbors);
	
	/**
	 * The state that the cell should transition to if this rule isTrue
	 * @return - The state that this rule should match
	 */
	public abstract State getState();
}
