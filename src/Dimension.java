/**
 * The dimension of the grid. 
 * The options followed by _TIME are a dimension higher than listed, plotted over time.
 */
public enum Dimension {
	ONE("1", 1), ONE_TIME("1-time", 1), TWO("2", 2), TWO_TIME("2-time", 2), THREE("3", 3);
	
	public final String value;
	public final int dimNum;
	
	private Dimension(String val, int dim) {
		value = val;
		dimNum = dim;
	}
	
	public static Dimension valueOfLabel(String label) {
	    for (Dimension e : values()) {
	        if (e.value.equals(label)) {
	            return e;
	        }
	    }
	    return null;
	}
	
	public boolean isDrawn2D() {
		return !(this == TWO_TIME || this == THREE);
	}
	
	public boolean isTimed() {
		return this == ONE_TIME || this == TWO_TIME;
	}
	
	public int getDimensionNumber() {
		return dimNum;
	}
}
