import java.io.File;
import java.util.EmptyStackException;
import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.JFileChooser;

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
	LinkedList<Grid> pastGrids;

	public static void main(String[] args) {
		PApplet.main("Driver");
	}

	public void settings() {
		size(800, 800, P3D);
	}

	public void setup() {
		// TODO I don't know if you can do anything about this, but it'd be nice if this
		// popup was focused at the start

		File default_json = new File("startup.json");
		if (default_json.exists()) {
			setupAutomata(default_json);
		} else {
			JFileChooser fileChooser = new JFileChooser("./");
			if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
				File f = fileChooser.getSelectedFile();
				setupAutomata(f);
			}
		}
	}

	private void setupAutomata(File inputFile) {
		JSONArray init_state = Settings.init(inputFile);

		grid = new Grid(Settings.getXDimension(), Settings.getYDimension(), Settings.getZDimension());

		if (init_state != null) {		
			int i = 0;
			JSONObject initCell = init_state.getJSONObject(i);
			while (initCell != null) {
				State cellState = State.getState(initCell.getString("state", "default"));
				if (cellState == null) {
					System.err.println("STATE " + initCell.getString("state") + " IS NOT A VALID STATE. ONLY "
							+ State.getAllStates().stream().map((state) -> state.getName())
									.reduce((acc, next) -> acc + " " + next).get()
							+ "EXIST");
					System.exit(0);
				}
				grid.setCellStateAtPos(initCell.getInt("x"), initCell.getInt("y", 0), initCell.getInt("z", 0),
						cellState);
				i++;
				initCell = init_state.getJSONObject(i, null);
			}
		}

		float xCellSize = (float) width / Settings.getXDimension();
		float yCellSize = (float) height / (Settings.getDimension().getDimensionNumber() == 1 ? Settings.getTimeDepth()
				: Settings.getYDimension());
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

		// TODO rendering without stroke is about 3 times faster - include option?

		// TODO save initial state
	}

	public void draw() {
//		println(frameRate);

		background(0);

		int framesPerTick;
		if (speed == 0)
			framesPerTick = 1;
		else if (speed == 1)
			framesPerTick = 4;
		else
			framesPerTick = 60;

		if (!paused && !userDrawing && frameCount % framesPerTick == 0) {
			updateGrid();
		}

		if (Settings.getDimension().isTimed() && !userDrawing) {
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

	public void updateGrid() {
		if (Settings.getDimension().isTimed()) {
			pastGrids.add(grid.deepClone());

			if (pastGrids.size() > Settings.getTimeDepth())
				pastGrids.remove(0);
		}

		grid.prepareAllStates();
		grid.updateAllStates();
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
		} else if (key == 'L') {
			JFileChooser fileChooser = new JFileChooser("./");
			if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
				File f = fileChooser.getSelectedFile();
				setupAutomata(f);
			}
		} else if (key == 'S') {
			JFileChooser fileChooser = new JFileChooser("./");
			if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
				File f = fileChooser.getSelectedFile();
				Settings.saveToJSON(grid).save(f, "");
			}

		} else if (key == ' ') {
			if (Settings.getDimension().isDrawn2D())
				userDrawing = !userDrawing;
			else
				paused = !paused;
		} else if (key == CODED) {
			if (keyCode == UP || keyCode == DOWN) {
				speed = constrain(speed + (keyCode == UP ? -1 : 1), 0, 2);
			} else if (keyCode == RIGHT) {
				if (paused || userDrawing)
					updateGrid();
			} else if (keyCode == LEFT) {
				if (paused || userDrawing) {
					try {
						grid.revert();

						if (Settings.getDimension().isTimed()) {
							pastGrids.add(0, pastGrids.getFirst().deepClone().revert());

							if (pastGrids.size() > Settings.getTimeDepth())
								pastGrids.remove(pastGrids.size() - 1);
						}
					} catch (EmptyStackException e) {
					}
				}
			}
		} else if (userDrawing) {
			State newState = State.getStateFromHotkey(key);

			if (newState != null)
				penState = newState;
		}
	}
}
