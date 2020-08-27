import peasy.PeasyCam;
import processing.core.PApplet;
import processing.core.PVector;

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
		// TODO: replace grid dimensions and settings initialization with what's given
		// by the json; update cell size accordingly
		grid = new Grid(2, 3, 4);
		Settings.init(true, Dimension.TWO, NeighborType.NEUMANN);
		Cell.setSize(new PVector(400, 800 / 3f, 1));
		// penState should initially be set to the first state (not the default state)
		// set up ruleset

		// TODO: fill in initial states according to json

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
			if(mouseButton == LEFT)
			grid.handleClick(mouseX, mouseY, penState);
			else if(mouseButton == RIGHT)
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
