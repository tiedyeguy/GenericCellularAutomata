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
		Settings.init(true, Dimension.TWO_TIME, NeighborType.NEUMANN);
		Cell.setSize(new PVector(20, 30, 40));

		grid = new Grid(3, 3, 3);
		camera = new PeasyCam(this, 200);
		userDrawing = true;

		camera.setActive(Settings.getDimension() == Dimension.TWO_TIME || Settings.getDimension() == Dimension.THREE);

		stroke(255);
	}

	public void draw() {
		background(100);

		grid.draw(this);
	}

	public void mousePressed() {
//		if(userDrawing)
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
