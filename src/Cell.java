import java.util.ArrayList;
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
	private Stack<State> pastStates;
	
	public Cell() {
		pastStates = new Stack<State>();
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

	/**
	 * Prepares the next state of the cell, based on its state, neighbors, and rules
	 */
	public void prepareNextState() {
		ArrayList<Rule> ruleset = Ruleset.getRuleset().getRulesFor(state);
		
		Rule firstTrueRule = null;

		for (Rule rule : ruleset) {
			if (rule.isTrue(neighbors)) {
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
		pastStates.push(state);
		state = nextState;
	}
	
	/**
	 * Reverts the cell to its previous state
	 */
	public void revert() {
		nextState = state;
		state = pastStates.pop();
	}

	/**
	 * Draws the cell to the screen at the current origin
	 * 
	 * @param sketch to draw to
	 */
	public void draw(PApplet sketch) {
		sketch.pushMatrix();
		
		sketch.translate(Cell.getSize().x / 2, Cell.getSize().y / 2, Cell.getSize().z / 2);		
		sketch.fill(state.getRed(), state.getGreen(), state.getBlue());
		sketch.stroke(0);
		
		if(!Settings.getDimension().isDrawn2D() && state.getName().equals("default")) {
			sketch.noFill();
			sketch.noStroke();
		}
		
		sketch.box(Cell.getSize().x, Cell.getSize().y, Cell.getSize().z);
		
		sketch.popMatrix();
	}
}
