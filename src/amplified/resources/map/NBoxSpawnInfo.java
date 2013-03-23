package amplified.resources.map;

import java.util.Collections;
import java.util.List;

import amplified.map.entity.AutoTransform;
import amplified.map.physicquantity.Position;

public class NBoxSpawnInfo {
	private Position pos;
	private final List<AutoTransform> autoTransforms;

	public NBoxSpawnInfo(Position pos, List<AutoTransform> autoTransforms) {
		this.pos = pos;
		this.autoTransforms = Collections.unmodifiableList(autoTransforms);
	}

	public Position getPosition() {
		return pos;
	}

	public List<AutoTransform> getAutoTransforms() {
		return autoTransforms;
	}
}
