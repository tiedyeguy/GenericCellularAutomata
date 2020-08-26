
public class Settings {
	private static boolean wrap;
	private static int dimension;
	private static NeighborType neighborType;
	
	public static void init(boolean wrap, int dimension, NeighborType neighborType) {
		Settings.wrap = wrap;
		Settings.dimension = dimension;
		Settings.neighborType = neighborType;
	}
	
	public static boolean isWrap() {
		return wrap;
	}
	
	public static int getDimension() {
		return dimension;
	}
	
	public static NeighborType getNeighborType() {
		return neighborType;
	}
}
