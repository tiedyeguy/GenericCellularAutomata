/**
 * The generic rule class
 */
public abstract class Rule implements Jsonable{

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
	public abstract State getState();
}
