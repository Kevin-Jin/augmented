package amplified.resources.map;

import amplified.map.physicquantity.Position;

public class RectangleSpawnInfo {
	private Position pos;
	private float initScale, minScale, maxScale;

	public RectangleSpawnInfo(Position pos, float initScale, float minScale, float maxScale) {
		this.pos = pos;
		this.initScale = initScale;
		this.minScale = minScale;
		this.maxScale = maxScale;
	}

	public Position getPosition() {
		return pos;
	}

	public float getStartScale() {
		return initScale;
	}

	public float getMinimumScale() {
		return minScale;
	}

	public float getMaximumScale() {
		return maxScale;
	}
}
