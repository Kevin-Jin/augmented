package amplified.resources.map;

import java.util.Collections;
import java.util.List;

import amplified.map.entity.AutoTransform;
import amplified.map.physicquantity.Position;

public class OverlayInfo {
	private final Position pos;
	private final int width, height;
	private final String imageName;
	private final List<AutoTransform> autoTransforms;

	public OverlayInfo(Position pos, int width, int height, String imageName, List<AutoTransform> autoTransforms) {
		this.pos = pos;
		this.width = width;
		this.height = height;
		this.imageName = imageName;
		this.autoTransforms = Collections.unmodifiableList(autoTransforms);
	}

	public Position getPosition() {
		return pos;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public String getImageName() {
		return imageName;
	}

	public List<AutoTransform> getAutoTransforms() {
		return autoTransforms;
	}
}
