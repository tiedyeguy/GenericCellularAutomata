
public class PrevStateInfo {
	private int ticksOnCurrState;

	private State state;
	
	public PrevStateInfo(int ticksOnCurrState, State state) {
		this.ticksOnCurrState = ticksOnCurrState;
		this.state = state;
	}
	
	public int getTicksOnCurrState() {
		return ticksOnCurrState;
	}

	public State getState() {
		return state;
	}
	
	@Override
	public String toString() {
		return "Ticks on state: " + ticksOnCurrState + "\nState Recorded: " + state.getName();
	}
}
