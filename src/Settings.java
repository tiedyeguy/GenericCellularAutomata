
public class Settings {
	private static boolean wrapping;
	private static int dimension;
	private static NeighborType neighborType;
	
	public static void init(boolean wrapping, int dimension, NeighborType neighborType) {
		Settings.wrapping = wrapping;
		Settings.dimension = dimension;
		Settings.neighborType = neighborType;
	}
	
	public static boolean isWrapping() {
		return wrapping;
	}
	
	public static int getDimension() {
		return dimension;
	}
	
	public static NeighborType getNeighborType() {
		return neighborType;
	}
}
