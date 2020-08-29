import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

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
	boolean paused;
	State penState;
	int speed = 1;
	Queue<Grid> pastGrids;

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

		float xCellSize = (float) width / Settings.getXDimension();
		float yCellSize = (float) height / Settings.getYDimension();
		float zCellSize = Settings.getDimension().isDrawn2D() ? 1 : (xCellSize + yCellSize) / 2;

		Cell.setSize(new PVector(xCellSize, yCellSize, zCellSize));

		penState = State.getAllStates().stream().filter((state) -> !state.getName().equals("default")).findAny().get();
		userDrawing = Settings.getDimension().isDrawn2D();
		paused = !userDrawing;

		if (paused) {
			camera = new PeasyCam(this, width / 2, height / 2, 0.5 * Cell.getSize().z * Settings.getZDimension(), 1000);
		}

		pastGrids = new LinkedList<Grid>();
		pastGrids.add(grid.deepClone());
		
		// TODO save initial state
		// TODO _TIME
	}

	public void draw() {
		background(0);

		int framesPerTick;
		if (speed == 0)
			framesPerTick = 1;
		else if (speed == 1)
			framesPerTick = 4;
		else
			framesPerTick = 60;

		if (!paused && !userDrawing && frameCount % framesPerTick == 0) {
			if (Settings.getDimension().isTimed()) {
				pastGrids.add(grid.deepClone());

				if (pastGrids.size() > Settings.getZDimension())
					pastGrids.remove();
			}

			grid.prepareAllStates();
			grid.updateAllStates();
		}

		if (Settings.getDimension().isTimed()) {
			Iterator<Grid> i = pastGrids.iterator();

			pushMatrix();
			while (i.hasNext()) {
				i.next().draw(this);

				if (Settings.getDimension() == Dimension.ONE_TIME) {
					translate(0, Cell.getSize().y);
				} else {
					translate(0, 0, Cell.getSize().z);
				}
			}
			popMatrix();
		} else {
			grid.draw(this);
		}

		if (userDrawing) {
			noCursor();
			fill(penState.getRed(), penState.getGreen(), penState.getBlue());

			pushMatrix();
			translate(mouseX, mouseY, Cell.getSize().x);
			noStroke();
			circle(0, 0, 15);
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
		if (key == DELETE) {
			setup();
		} else if (key == ' ') {
			if (Settings.getDimension().isDrawn2D())
				userDrawing = !userDrawing;
			else
				paused = !paused;
		} else if (key == CODED) {
			if (keyCode == UP || keyCode == DOWN) {
				speed = constrain(speed + (keyCode == UP ? -1 : 1), 0, 2);
			} else if (keyCode == RIGHT) {
				grid.prepareAllStates();
				grid.updateAllStates();
			} else if (keyCode == LEFT) {
				grid.revert();
			}
		} else if (userDrawing) {
			State newState = State.getStateFromHotkey(key);

			if (newState != null)
				penState = newState;
		}
	}
}
