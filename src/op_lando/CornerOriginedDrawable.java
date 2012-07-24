package op_lando;

import org.lwjgl.util.vector.Vector2f;

public abstract class CornerOriginedDrawable extends AbstractDrawable {
	@Override
	public Vector2f getOrigin() {
		return new Vector2f(0, 0);
	}
}
