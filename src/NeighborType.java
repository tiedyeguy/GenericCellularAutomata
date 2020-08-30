/**
 * How neighbors are found. Moore includes diagonals, Neumann does not.
 */
public enum NeighborType {
	MOORE('m'), NEUMANN('n');
	
	
	public final char type;
	
	private NeighborType(char typeLetter) {
		type = typeLetter;
	}

	public static NeighborType typeOfChar(char type) {
	    for (NeighborType e : values()) {
	    	if (e.type == type) {
	            return e;
	        }
	    }
	    return null;
	}
}
