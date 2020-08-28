import processing.data.JSONObject;

/**
 * Jsonable objects are objects that can be saved to a Json file
 */
public interface Jsonable {
	/**
	 * Saves the object to a processing JSON object
	 * @return - a JSON object containing the information to recreate this object
	 */
	public JSONObject saveToJson();
	
	/**
	 * Loads from the given JSON object
	 * @param jsonable - a JSON object that contains the necessary information to load the object
	 */
	public void loadFromJSON(JSONObject jsonable);
}
