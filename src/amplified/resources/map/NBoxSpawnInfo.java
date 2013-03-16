package amplified.resources.map;

import amplified.map.physicquantity.Position;

public class NBoxSpawnInfo {
	private Position pos;

	public NBoxSpawnInfo(Position pos) {
		this.pos = pos;
	}

	public Position getPosition() {
		return pos;
	}
}
