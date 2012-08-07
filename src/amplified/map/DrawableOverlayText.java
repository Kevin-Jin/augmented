package amplified.map;

import org.lwjgl.util.Point;
import org.newdawn.slick.TrueTypeFont;

@SuppressWarnings("deprecation")
public class DrawableOverlayText {
	private Point relativePosition;
	private TrueTypeFont font;
	private String message;

	public DrawableOverlayText(Point relativePosition, TrueTypeFont font, String message) {
		this.relativePosition = relativePosition;
		this.font = font;
		this.message = message;
	}

	public TrueTypeFont getFont() {
		return font;
	}

	public String getMessage() {
		return message;
	}

	public Point getRelativePosition() {
		return relativePosition;
	}
}
