package op_lando.map;

import op_lando.map.physicquantity.Position;
import op_lando.resources.TextureCache;

import org.newdawn.slick.Color;
import org.newdawn.slick.opengl.Texture;

public class Platform extends CollidableDrawable {
	private final double x1, x2, y1, y2;

	public Platform(double x1, double x2, double y1, double y2) {
		this.x1 = x1;
		this.x2 = x2;
		this.y1 = y1;
		this.y2 = y2;
	}

	public double getLeftX() {
		return x1;
	}

	public double getRightX() {
		return x2;
	}

	public double getTopY() {
		return y1;
	}

	public double getBottomY() {
		return y2;
	}

	@Override
	public Position getPosition() {
		return new Position(x1, y2);
	}

	@Override
	public float getWidth() {
		return (float) Math.round(getRightX() - getLeftX());
	}

	@Override
	public float getHeight() {
		return (float) Math.round(getTopY() - getBottomY());
	}

	@Override
	public Texture getTexture() {
		return TextureCache.getTexture("platform");
	}

	@Override
	public Color getTint() {
		return Color.gray;
	}
}
