/**
 * The dimension of the grid. 
 * The options followed by _TIME are a dimension higher than listed, plotted over time.
 */
public enum Dimension {
	ONE, ONE_TIME, TWO, TWO_TIME, THREE;
	
	public boolean isDrawn2D() {
		return !(this == TWO_TIME || this == THREE);
	}
}
