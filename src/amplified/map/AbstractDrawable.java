package amplified.map;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.Color;

import amplified.map.physicquantity.Position;

public abstract class AbstractDrawable implements Drawable {
	protected final Vector2f getBottomLeftCorner() {
		return new Vector2f(0, 0);
	}

	protected final Vector2f getCenter() {
		return new Vector2f(getTexture().getImageWidth() / 2f, getTexture().getImageHeight() / 2f);
	}

	public abstract Position getPosition();

	public Vector2f getOrigin() {
		return getBottomLeftCorner();
	}

	public final Vector2f getDrawPosition() {
		return new Vector2f((float) getPosition().getX() + getOrigin().getX() * getScale().getX(), (float) getPosition().getY() - getOrigin().getY() * -getScale().getY() + getHeight());
	}

	public float getWidth() {
		return getTexture().getImageWidth();
	}

	public float getHeight() {
		return getTexture().getImageHeight();
	}

	public float getRotation() {
		return 0;
	}

	public Vector2f getScale() {
		return new Vector2f(getWidth() / getTexture().getImageWidth(), -getHeight() / getTexture().getImageHeight());
	}

	public boolean flipHorizontally() {
		return false;
	}

	public Color getTint() {
		return Color.white;
	}

	public DrawableOverlayText getCaption() {
		return null;
	}

	public final Matrix4f getWorldMatrix() {
		return new Matrix4f()
				.translate(new Vector2f(getDrawPosition()))
				.rotate(getRotation(), new Vector3f(0, 0, 1))
				.scale(new Vector3f(getScale().getX(), getScale().getY(), 1))
				.translate(new Vector2f(getOrigin()).negate(null))
				.translate(new Vector2f(getWidth() / 2, 0))
				.scale(new Vector3f(flipHorizontally() ? -1 : 1, 1, 1))
				.translate(new Vector2f(-getWidth() / 2, 0));
	}
}
