package op_lando;

import org.newdawn.slick.opengl.Texture;

public class CursorEntity extends AbstractDrawable {
	private Input state;

	public CursorEntity(Input input) {
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
