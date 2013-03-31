package amplified.map;

import org.newdawn.slick.Color;

public class RetractablePlatform extends Platform implements Switchable {
	private final Color color;
	private final boolean inverted;
	private boolean extended;

	public RetractablePlatform(double x1, double x2, double y1, double y2, boolean inverted, Color color) {
		super(x1, x2, y1, y2);
		this.inverted = inverted;
		this.color = color;
		extended = !inverted;
	}

	@Override
	public float getWidth() {
		return (extended) ? super.getWidth() : 0;
	}

	@Override
	public float getHeight() {
		return (extended) ? super.getHeight() : 0;
	}

	@Override
	public Color getTint() {
		return color;
	}

	@Override
	public boolean isVisible() {
		return extended;
	}

	public void reset() {
		extended = !inverted;
	}

	public void switchActivated() {
		extended = inverted;
	}

	public void switchDeactivated() {
		extended = !inverted;
	}
}
