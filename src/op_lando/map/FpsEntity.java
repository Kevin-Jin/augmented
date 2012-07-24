package op_lando.map;

import op_lando.map.physicquantity.Position;
import op_lando.map.state.FrameRateState;
import op_lando.resources.FontCache;
import op_lando.resources.TextureCache;

import org.lwjgl.util.Point;
import org.newdawn.slick.Color;
import org.newdawn.slick.opengl.Texture;

public class FpsEntity extends AbstractDrawable {
	private FrameRateState state;
	private int yPos;

	public FpsEntity(FrameRateState fps, int yPos) {
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
