import java.util.ArrayList;
import java.util.stream.Stream;

/**
 * Utility class defining a range of values
 */
public class Range {
	private ArrayList<SubRange> ranges;
	private ArrayList<Number> singles;
	
	public Range(String range) {
		ranges = new ArrayList<SubRange>();
		while(range.length() > 0) {
			int commaIndex = range.indexOf(",");
			String processString;
			if(commaIndex != -1) { 
				processString = range.substring(0, commaIndex);
				range = range.substring(commaIndex + 1);
			} else {
				processString = range;
				range = "";
			}
			int splitIndex = processString.indexOf("-");
			if(splitIndex != -1) {
				double min = Double.parseDouble(processString.substring(0, splitIndex));
				double max = Double.parseDouble(processString.substring(splitIndex + 1));
				ranges.add(new SubRange(min, max));
			} else {
				singles.add(Double.parseDouble(processString));
			}
		}
	}
	
	/**
	 * Used to check if a number falls into this range
	 * @param number - Number checked for inclusion
	 * @return - Returns true iff this range contains number, false otherwise
	 */
	public boolean contains(Number number) {
		return Stream.concat(ranges.stream().map((range)->range.contains(number)),
							singles.stream().map((singleValid)->singleValid==number))
					 .reduce((acc, next)->acc || next).orElseGet(()->false);
	}
		
	/**
	 *	Defines a range with a minimum and maximum value (inclusive)
	 */
	private class SubRange {
		private double min;
		private double max;
		
		public SubRange(double min, double max) {
			this.min = min;
			this.max = max;
		}
		
		public boolean contains(Number number) {
			return number.doubleValue() <= max && number.doubleValue() >= min;
		}
	}
}
