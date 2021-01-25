# GenericCellularAutomata

### Purpose

	The purpose of this project was to create a program that, when run, could simulate any given generic cellular automata with as few restrictions as possible. The rules and type of cellular automata are passed into the program as JSON objects, and you may choose which ruleset to simulate when running the program. The code currently supports:

###### One Dimensional Automata:
	- Elementary Moore's neighborhood automata
	- Optional wrapping
	- Multiple states
	- Can walk backward or forward frames and modify state
	- Can record run and play back later
###### Two Dimensional Automata:
	- Moore or Neumann neighborhood
	- Multiple states
	- Optional wrapping
	- Can walk backward or forward frames and modify state
	- Two possible rule input schemes for flexibility
	- Can view past states by projection of the time dimension into the third spatial dimension
	- Can record run and play back later
###### Three Dimensional Automata:
	- Moore or Neumann Neighborhood
	- Multiple states
	- Optional wrapping
	- Initial state configuration, no modification
	- Can record run and play back later

### States
	States are handled the same way across this program. If no states are specifically listed using the rules notation later described, then two states are automatically created, default and live. Each cell is either a default cell, or a live cell, with default cells being transparent. If states are specified, then they are specified in the rule system specification.   

### Rule Systems

#### One Dimensional Elemental
	You may specify in the JSON rule document a "rules" tag, with a single integer number 0-255 representing the elementary one dimensional rule that the program should use. Here is an example JSON for [rule 30](https://mathworld.wolfram.com/Rule30.html): 
```
{
  "initial_state": [{"x": 16, "state": "live"}],
  "size": {
    "x": 32
  },
  "time-depth": 30,
  "dimensions": "1-time",
  "wrap": true,
  "rules": 30
}
```
#### Orientation
	If using orientation rules, you must use a Neumann neighborhood. This system is slower, but allows higher levels of control over the state changes. You specify the states in the rule scheme, and each state may have a 'left', 'right', 'top', and 'bottom', or just a 'left' and 'right' in the case of one dimensional automata. These directions hold the name of another state that must be in that position to trigger the state change. The labels will match rotated situations, may be left off for wildcards, or can be specified with a '*' for wildcard. The 'left' field must always be included.

#### Complex

	These rules are the most common. These rules consist of a listing of states, and under each state is a sublisting of states that this state can transition to if conditions are met. Those conditions are specified in the sublist as 'thresholds' of surrounding state that must be met in order to transition. As an example, see [Conway's Game of Life](https://en.wikipedia.org/wiki/Conway%27s_Game_of_Life) in our JSON format: 
	
	

```
{
  "size": {
    "x": 15,
    "y": 15,
  },
  "type": "M",
  "dimensions": 2,
  "wrap": true,
  "rules": {
    "default": {
      "color": "000000",
      "live": {
        "live": "3"
      }
    },
    "live": {
      "color": "FFFFFF",
      "default": {
        "live": "0-1,4-8"
      }
    }
  }
}
```
