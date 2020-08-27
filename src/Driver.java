import java.io.File;

import peasy.PeasyCam;
import processing.core.PApplet;
import processing.core.PVector;
import processing.data.JSONArray;
import processing.data.JSONObject;

/**
 * Handles drawing and drives the program
 */
public class Driver extends PApplet {

	Grid grid;
	PeasyCam camera;
	boolean userDrawing;
	State penState;

	public static void main(String[] args) {
		PApplet.main("Driver");
	}

	public void settings() {
		size(800, 800, P3D);
	}

	public void setup() {
		JSONArray init_state = Settings.init(new File("test.json")); //TODO: Allow user to pick file
		grid = new Grid(Settings.getXDimension(), Settings.getYDimension(), Settings.getZDimension());
		
		int i = 0;
		JSONObject initCell = init_state.getJSONObject(i);
		while(initCell != null) {
			grid.setCellStateAtPos(initCell.getInt("x"), initCell.getInt("y"), initCell.getInt("z"), State.getState(initCell.getString("state")));
		}
		
		Cell.setSize(new PVector(400, 800 / 3f, 1));
		// penState should initially be set to the first state (not the default state)
		camera = new PeasyCam(this, 200);
		camera.setActive(Settings.getDimension() == Dimension.TWO_TIME || Settings.getDimension() == Dimension.THREE);
		userDrawing = !camera.isActive();
	}

	public void draw() {
		background(0);

		if (userDrawing) {
			noCursor();
			fill(penState.getRed(), penState.getGreen(), penState.getGreen());
			circle(mouseX, mouseY, 20);
		} else {
			cursor();
		}

		grid.prepareAllStates();
		grid.updateAllStates();
		grid.draw(this);
	}

	public void mouseClicked() {
		mouseDown();
	}

	public void mouseDragged() {
		mouseDown();
	}

	public void mouseDown() {
		if (userDrawing) {
			if (mouseButton == LEFT)
				grid.handleClick(mouseX, mouseY, penState);
			else if (mouseButton == RIGHT)
				grid.handleClick(mouseX, mouseY, State.getState("default"));
		}
	}

	public void keyPressed() {
		if (key == ' ' && Settings.getDimension() != Dimension.THREE) {
			userDrawing = !userDrawing;
		} else if (userDrawing) {
			State newState = State.getStateFromHotkey(key);

			if (newState != null)
				penState = newState;
		}
	}
}
