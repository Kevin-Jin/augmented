package op_lando;

import org.lwjgl.util.vector.Vector2f;

public abstract class CenterOriginedDrawable extends AbstractDrawable {
	@Override
	public Vector2f getOrigin() {
		return new Vector2f(getWidth() / 2f, getHeight() / 2f);
	}
}
