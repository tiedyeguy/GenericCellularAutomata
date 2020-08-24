# GenericCellularAutomata

Rules for wire world
```
"initial_state": [
		{"x": 0, "y": 0, "state": "tail"},
		{"x": 1, "y": 0, "state": "head"},
		{"x": 2, "y": 0, "state": "wire"},
		{"x": 3, "y": 0, "state": "wire"},
		{"x": 4, "y": 0, "state": "wire"}
	], 
  "type": "M",
  "dimensions": 2,
  "empty": {
    "color": "black",
		"wire": {
			"wire": "2/4"
		}
  },
  "wire": {
    "color": "yellow",
    "head": {
      "head": "1"
    }
  },
  "head": {
    "color": "blue",
    "tail" {
      "any": "0-8"
    }
  },
  "tail": {
    "color": "red"
    "wire": {
      "any": "0-8"
    }
  }
}
```
