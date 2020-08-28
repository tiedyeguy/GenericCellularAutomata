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
		JSONArray init_state = Settings.init(new File("test.json")); // TODO: Allow user to pick file
		grid = new Grid(Settings.getXDimension(), Settings.getYDimension(), Settings.getZDimension());

		if (init_state != null) {
			int i = 0;
			JSONObject initCell = init_state.getJSONObject(i);
			while (initCell != null) {
				grid.setCellStateAtPos(initCell.getInt("x"), initCell.getInt("y", 0), initCell.getInt("z", 0),
						State.getState(initCell.getString("state")));
				i++;
				initCell = init_state.getJSONObject(i, null);
			}
		}

		// FIXME z should not always be one, y should sometimes be set manually (1d)
		Cell.setSize(
				new PVector((float) width / Settings.getXDimension(), (float) height / Settings.getYDimension(), 50));

		penState = State.getAllStates().stream().filter((state) -> !state.getName().equals("default")).findAny().get();
		userDrawing = Settings.getDimension().isDrawn2D();

		if (!userDrawing) {
			// TODO generalize based on z (0.5 * zDim * cell size.z)
			camera = new PeasyCam(this, width / 2, height / 2, 50 * 7.5, 60);
		}

		// TODO adjustable speed
		// TODO save initial state
		// TODO clear grid
		// TODO _TIME
	}

	public void draw() {
		background(0);

		if (!userDrawing) {
			grid.prepareAllStates();
			grid.updateAllStates();
		}
		grid.draw(this);

		if (userDrawing) {
			noCursor();
			fill(penState.getRed(), penState.getGreen(), penState.getBlue());

			pushMatrix();
			translate(mouseX, mouseY);
			noStroke();
			sphere(15);
			popMatrix();
		} else {
			cursor();
		}
	}

	public void mousePressed() {
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
		if (key == ' ' && Settings.getDimension().isDrawn2D()) {
			userDrawing = !userDrawing;
		} else if (userDrawing) {
			State newState = State.getStateFromHotkey(key);

			if (newState != null)
				penState = newState;
		}
	}
}
