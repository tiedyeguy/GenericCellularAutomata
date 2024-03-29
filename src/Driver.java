import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.EmptyStackException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Scanner;

import javax.swing.JFileChooser;

import peasy.PeasyCam;
import processing.core.PApplet;
import processing.core.PVector;
import processing.data.JSONArray;
import processing.data.JSONObject;

// Disable in exporting:
// speed control
// pausing
// tick count
// shift H

// wants:
// camera rotation

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
	int ticksSoFar = 0;

	public static void main(String[] args) {
		PApplet.main("Driver");
	}

	public void settings() {
		size(800, 800, P3D);
	}

	public void setup() {
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

	/**
	 * Sets up the automata with a file input
	 * 
	 * @param inputFile - Input file to read a JSON object from
	 */
	private void setupAutomata(File inputFile) {
		String jsonAsStr = "";
		try {
			Scanner sc = new Scanner(inputFile);
			do {
				jsonAsStr += sc.nextLine();
			} while (sc.hasNext());
			sc.close();
		} catch (Exception e) {
			System.err.println("JSON Config File not found");
			e.printStackTrace();
			return;
		}

		try {
			JSONObject automataObj = JSONObject.parse(jsonAsStr);
			setupAutomata(automataObj);
		} catch(RuntimeException ex) {
			System.err.println("The JSON file that you are attempting to run is not formatted correctly");
			ex.printStackTrace();
		}
	}

	/**
	 * Sets the automata with a JSON object
	 * 
	 * @param automataObj - JSON object that will be used for creation
	 */
	private void setupAutomata(JSONObject automataObj) {
		JSONArray init_state = Settings.init(automataObj);

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

		if (!Settings.getDimension().isDrawn2D()) {
			camera = new PeasyCam(this, width / 2, height / 2, 0.5 * Cell.getSize().z * Settings.getZDimension(), 1000);
		} else if (camera != null) {
			camera.setActive(false);
		}

		pastGrids = new LinkedList<Grid>();
		pastGrids.add(grid.deepClone());

		textAlign(BOTTOM, LEFT);
		textSize(40);
		// TODO rendering without stroke is about 3 times faster - include option?

		if (Settings.isRecording()) {
			speed = 0;
		}
	}

	public void draw() {
		// println(frameRate);

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
			translate(mouseX, mouseY, 2);
			noStroke();
			circle(0, 0, Cell.getSize().x);
			popMatrix();
		} else {
			cursor();
		}

		if (!Settings.isRecording()) {
			fill(255);
			stroke(0);
			text("Ticks: " + ticksSoFar, 0, height);
		}
		else {
			if(!Settings.getDimension().isDrawn2D()) {
				camera.rotateY(radians(1));
			}
			
			if (ticksSoFar < Settings.getFramesToRecord()) {
				String formatted = String.format(dataPath("%06d.png"), ticksSoFar);
				saveFrame(formatted);
			} else {
				exportVideo();
				exit();
			}
		}
	}

	public void updateGrid() {
		if (!Settings.isRecording() && Settings.getDimension().isTimed()) {
			pastGrids.add(grid.deepClone());

			if (pastGrids.size() > Settings.getTimeDepth())
				pastGrids.remove(0);
		}

		grid.prepareAllStates();
		grid.updateAllStates();

		ticksSoFar++;
	}

	public void exportVideo() {
		try {
			ProcessBuilder pb = new ProcessBuilder(".\\ffmpeg\\bin\\ffmpeg.exe", "-r",
					Integer.toString(Settings.getFrameSpeed()), "-f", "image2", "-s", width + "x" + height, "-i",
					dataPath("%06d.png"), "-vcodec", "libx264", "-crf", "25", "-pix_fmt", "yuv420p",
					Settings.getVideoName() + ".mp4");
			pb.redirectErrorStream(true);
			Process process = pb.start();
			BufferedReader inStreamReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			
			String line = "";
			while(line != null) {
				System.out.println(line);
				line = inStreamReader.readLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		File[] files = new File(dataPath("")).listFiles();
		if (files != null) {
			for (File f : files) {
				if (f.getName().endsWith(".png"))
					f.delete();
			}
		}
	}

	public void mousePressed() {
		mouseDown();
	}

	public void mouseDragged() {
		mouseDown();
	}

	public void mouseDown() {
		if (!Settings.isRecording() || ticksSoFar == 0) {
			if (userDrawing) {
				if (mouseButton == LEFT)
					grid.handleClick(mouseX, mouseY, penState);
				else if (mouseButton == RIGHT)
					grid.handleClick(mouseX, mouseY, State.getState("default"));
			}
		}
	}

	public void keyPressed() {
		if (!Settings.isRecording() || ticksSoFar == 0) {
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
			} else if (key == 'H') {
				Settings.trackTime(!Settings.getDimension().isTimed());
				setupAutomata(Settings.saveToJSON(grid));
			}
			if (key == ' ') {
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
							ticksSoFar--;

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
}
