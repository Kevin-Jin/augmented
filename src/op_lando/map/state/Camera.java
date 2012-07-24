package op_lando.map.state;

import java.awt.Rectangle;

import op_lando.map.physicquantity.Position;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector4f;

public class Camera {
	private final int width, height;
	private Vector2f pos;
	private Rectangle limits;

	public Camera(int width, int height) {
		this.width = width;
		this.height = height;
		pos = new Vector2f(0, 0);
	}

	private void checkLimits() {
		pos.set(
			(float) Math.min(Math.max(pos.getX(), limits.getX()), limits.getX() + limits.getWidth() - width),
			(float) Math.min(Math.max(pos.getY(), limits.getY()), limits.getY() + limits.getHeight() - height)
		);
	}

	private void setPosition(Vector2f newPos) {
		pos.set(newPos);
		if (limits != null)
			checkLimits();
	}

	public void setLimits(Rectangle bounds) {
		if (bounds != null) {
			this.limits = new Rectangle(bounds.x, bounds.y, Math.max(width, bounds.width), Math.max(height, bounds.height));
			checkLimits();
		} else {
			this.limits = null;
		}
	}

	public Matrix4f getViewMatrix(float parallaxFactor) {
		return new Matrix4f()
			.translate(new Vector2f(-pos.getX() * parallaxFactor, -pos.getY() * parallaxFactor));
	}

	public Position mouseToWorld(int x, int y) {
		return new Position(Matrix4f.transform(new Matrix4f()
				.translate(pos), new Vector4f(x, y, 1, 1), null));
	}

	public void lookAt(Position newPos) {
		setPosition(new Vector2f(Matrix4f.transform(new Matrix4f()
				.translate(new Vector2f(-width / 2f, -height / 2f)), newPos.asVector4f(), null)));
	}
}
