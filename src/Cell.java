import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import processing.core.PApplet;
import processing.core.PVector;

public class Cell {
	private static PVector size;
	private Cell[] neighbors;
	private State state;
	private State nextState;

	public Cell() {
	}

	public Cell(Cell[] neighbors) {
		this.neighbors = neighbors;
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

	/**
	 * Prepares the next state of the cell, based on its state, neighbors, and rules
	 */
	public void prepareNextState() {
		ArrayList<Rule> ruleset = Ruleset.getRuleset().getRulesFor(state);

		Map<State, Number> neighborCounts = new HashMap<State, Number>();

		for (Cell neighbor : neighbors) {
			if (neighborCounts.containsKey(neighbor.getState())) {
				neighborCounts.replace(neighbor.getState(), neighborCounts.get(neighbor.getState()).intValue() + 1);
			} else {
				neighborCounts.put(neighbor.getState(), 1);
			}
		}

		Rule firstTrueRule = null;

		for (Rule rule : ruleset) {
			if (rule.isTrue(neighborCounts)) {
				if(firstTrueRule != null) {
					throw new IllegalStateException(firstTrueRule.toString() + " and " + rule.toString() + " conflict with each other.");
				}
				else {
					firstTrueRule = rule;
					nextState = rule.getState();
				}
			}
		}
	}

	/**
	 * Updates the state of the cell with the prepared state
	 */
	public void updateState() {
		state = nextState;
		nextState = null;
	}

	/**
	 * Draws the cell to the screen at the current origin
	 * 
	 * @param sketch to draw to
	 */
	public void draw(PApplet sketch) {
		sketch.box(Cell.getSize().x, Cell.getSize().y, Cell.getSize().z);
	}
}
