package op_lando.map;

import op_lando.map.physicquantity.Position;
import op_lando.map.state.Input;
import op_lando.resources.TextureCache;

import org.newdawn.slick.opengl.Texture;

public class CursorOverlay extends AbstractDrawable {
	private Input state;

	public CursorOverlay(Input input) {
		state = input;
	}

	@Override
	public Position getPosition() {
		return new Position(state.cursorPosition().getX() - getWidth() / 2, state.cursorPosition().getY() - getHeight() / 2);
	}

	@Override
	public Texture getTexture() {
		return TextureCache.getTexture("mouse");
	}
}