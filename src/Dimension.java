/**
 * The dimension of the grid. 
 * The options followed by _TIME are a dimension higher than listed, plotted over time.
 */
public enum Dimension {
	ONE, ONE_TIME, TWO, TWO_TIME, THREE;
	
	public boolean isDrawn2D() {
		return !(this == TWO_TIME || this == THREE);
	}
	
	public boolean isTimed() {
		return this == ONE_TIME || this == TWO_TIME;
	}
	
	public int getDimensionNumber() {
		if(this == ONE || this == ONE_TIME)
			return 1;
		else if(this == TWO || this == TWO_TIME)
			return 2;
		else 
			return 3;
	}
}
