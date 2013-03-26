package amplified.map;

import org.newdawn.slick.opengl.Texture;

import amplified.map.physicquantity.Position;
import amplified.map.state.Input;
import amplified.resources.TextureCache;

public class CursorOverlay extends AbstractDrawable {
	private Input state;

	public CursorOverlay(Input input) {
		state = input;
	}

	@Override
	public Position getPosition() {
		return new Position(state.markedPointer().getX() - getWidth() / 2, state.markedPointer().getY() - getHeight() / 2);
	}

	@Override
	public float getWidth() {
		return !state.isCutscene() ? super.getWidth() : 0;
	}

	@Override
	public float getHeight() {
		return !state.isCutscene() ? super.getHeight() : 0;
	}

	@Override
	public Texture getTexture() {
		return TextureCache.getTexture("mouse");
	}
}
