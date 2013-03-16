package amplified.resources.map;

import amplified.map.physicquantity.Position;

public class OverlayInfo {
	private Position pos;
	private int width, height;
	private String imageName;

	public OverlayInfo(Position pos, int width, int height, String imageName) {
		this.pos = pos;
		this.width = width;
		this.height = height;
		this.imageName = imageName;
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
}
