import peasy.PeasyCam;
import processing.core.PApplet;
import processing.core.PVector;

public class Driver extends PApplet {

	Grid grid;
	PeasyCam camera;
	
	public static void main(String[] args) {
		PApplet.main("Driver");
	}

	public void settings() {
		size(800, 800, P3D);
	}

	public void setup() {
		Settings.init(true, 3, NeighborType.NEUMANN);
		
		camera = new PeasyCam(this, 200);
		
		grid = new Grid(3, 3, 3);
		
		Cell.setSize(new PVector(20, 30, 40));
	}

	public void draw() {
		background(0);
		
		grid.draw(this);
	}
}
