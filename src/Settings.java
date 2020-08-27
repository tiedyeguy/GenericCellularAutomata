/**
 * Contains user preferences that don't fall under a cell state
 */
public class Settings {
	private static boolean wrapping;
	private static Dimension dimension;
	private static NeighborType neighborType;

	/**
	 * Initializes the settings with user preferences
	 * @param whether the grid wraps around on itself (leftmost cells have neighbors on the right side)
	 * @param dimension
	 * @param how neighbors are evaluated: include diagonals or not?
	 */
	public static void init(boolean wrapping, Dimension dimension, NeighborType neighborType) {
		Settings.wrapping = wrapping;
		Settings.dimension = dimension;
		Settings.neighborType = neighborType;
	}

	public static boolean isWrapping() {
		return wrapping;
	}

	public static Dimension getDimension() {
		return dimension;
	}

	public static NeighborType getNeighborType() {
		return neighborType;
	}
}
