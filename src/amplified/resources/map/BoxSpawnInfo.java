package amplified.resources.map;

import java.util.Collections;
import java.util.List;

import amplified.map.entity.AutoTransform;
import amplified.map.physicquantity.Position;

public class BoxSpawnInfo {
	private final Position pos;
	private final float initScale, minScale, maxScale;
	private final List<AutoTransform> autoTransforms;

	public BoxSpawnInfo(Position pos, float initScale, float minScale, float maxScale, List<AutoTransform> autoTransforms) {
		this.pos = pos;
		this.initScale = initScale;
		this.minScale = minScale;
		this.maxScale = maxScale;
		this.autoTransforms = Collections.unmodifiableList(autoTransforms);
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

	public List<AutoTransform> getAutoTransforms() {
		return autoTransforms;
	}
}
