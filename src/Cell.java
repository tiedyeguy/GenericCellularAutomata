import java.util.ArrayList;
import java.util.Arrays;
import java.util.Stack;

import processing.core.PApplet;
import processing.core.PVector;

/**
 * Represents a single cell - has a state, updates itself, and is drawable
 */
public class Cell {
	private static PVector size;
	private Cell[] neighbors;
	private State state;
	private State nextState = State.getState("default");
	private Stack<PrevStateInfo> pastStates;
	private int ticksOnCurrState = 0;

	public Cell() {
		pastStates = new Stack<PrevStateInfo>();
	}

	public void setNeighbors(Cell[] neighbors) {
		this.neighbors = neighbors;
	}

	public static PVector getSize() {
		return size;
	}

	public static void setSize(PVector size) {
		Cell.size = size;
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
		nextState = state;
	}

	public String toString() {
		String n = state.getName() + "\n";
		for(Cell neighbor : neighbors)
			n += neighbor.getState().getName();
		return n;
	}

	@SuppressWarnings("unchecked")
	public Cell deepClone() {
		Cell clone = new Cell();

		if (!Settings.isRecording())
			clone.pastStates = (Stack<PrevStateInfo>) pastStates.clone();
		clone.neighbors = Arrays.copyOf(neighbors, neighbors.length);

		clone.state = state;
		clone.nextState = nextState;

		clone.ticksOnCurrState = ticksOnCurrState;

		return clone;
	}

	/**
	 * Prepares the next state of the cell, based on its state, neighbors, and rules
	 */
	public void prepareNextState() {
		ArrayList<Rule> ruleset = state.getRules();

		Rule firstTrueRule = null;

		for (Rule rule : ruleset) {
			if (rule.isTrue(neighbors)) {
				if (firstTrueRule != null) {
					throw new IllegalStateException(
							firstTrueRule.toString() + "\n AND \n" + rule.toString() + " \nconflict with each other.");
				} else {
					firstTrueRule = rule;
					nextState = rule.getToState();
				}
			}
		}
	}

	/**
	 * Updates the state of the cell with the prepared state
	 */
	public void updateState() {
		if (!Settings.isRecording())
			pastStates.push(new PrevStateInfo(ticksOnCurrState, state));

		if (state == nextState) {
			if (ticksOnCurrState < state.getFadeFrames()) {
				ticksOnCurrState++;
			}
		} else {
			ticksOnCurrState = 0;
		}

		state = nextState;
	}

	/**
	 * Reverts the cell to its previous state
	 */
	public void revert() {
		nextState = state;
		PrevStateInfo pastStateInfo = pastStates.pop();
		state = pastStateInfo.getState();
		ticksOnCurrState = pastStateInfo.getTicksOnCurrState();
	}

	/**
	 * Draws the cell to the screen at the current origin
	 * 
	 * @param sketch to draw to
	 */
	public void draw(PApplet sketch) {
		sketch.pushMatrix();

		sketch.translate(Cell.getSize().x / 2, Cell.getSize().y / 2, Cell.getSize().z / 2);

		int r, g, b;
		if (state.stateFades()) {
			r = (int) ((Math.abs(state.getFirstRed() - state.getRed())) / (float) state.getFadeFrames())
					* ticksOnCurrState + state.getFirstRed();
			g = ((int) ((Math.abs(state.getFirstGreen() - state.getGreen())) / (float) state.getFadeFrames())
					* ticksOnCurrState + state.getFirstGreen());
			b = (int) ((Math.abs(state.getFirstBlue() - state.getBlue())) / (float) state.getFadeFrames())
					* ticksOnCurrState + state.getFirstBlue();
		} else {
			r = state.getRed();
			g = state.getGreen();
			b = state.getBlue();
		}

		sketch.fill(r, g, b);
		// TODO update optimization - could perform updates recursively with neighbors?

		sketch.box(Cell.getSize().x, Cell.getSize().y, Cell.getSize().z);

		sketch.popMatrix();
	}
}
