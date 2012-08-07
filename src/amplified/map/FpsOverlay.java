package amplified.map;

import org.lwjgl.util.Point;
import org.newdawn.slick.Color;
import org.newdawn.slick.opengl.Texture;

import amplified.map.physicquantity.Position;
import amplified.map.state.FrameRateState;
import amplified.resources.FontCache;
import amplified.resources.TextureCache;

public class FpsOverlay extends AbstractDrawable {
	private FrameRateState state;
	private int yPos;

	public FpsOverlay(FrameRateState fps, int yPos) {
		state = fps;
		this.yPos = yPos;
	}

	@Override
	public Position getPosition() {
		return new Position(0, yPos);
	}

	@Override
	public Texture getTexture() {
		return TextureCache.getTexture("spacer");
	}

	@Override
	public Color getTint() {
		return Color.yellow;
	}

	@Override
	public DrawableOverlayText getCaption() {
		return new DrawableOverlayText(new Point(0, 0), FontCache.getFont("fps"), state.getDisplayFps());
	}
}
