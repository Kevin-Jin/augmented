package amplified.map;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.lwjgl.util.vector.Vector2f;
import org.newdawn.slick.Color;
import org.newdawn.slick.opengl.Texture;

import amplified.map.collisions.BoundingPolygon;
import amplified.map.collisions.CollisionInformation;
import amplified.map.collisions.Polygon;
import amplified.map.physicquantity.Position;
import amplified.map.physicquantity.Velocity;
import amplified.resources.TextureCache;

public class Platform extends AbstractCollidable {
	private final double x1, x2, y1, y2;

	public Platform(double x1, double x2, double y1, double y2) {
		super(new BoundingPolygon(new Polygon[] {
			new Polygon(new Vector2f[] {
				//assert dimension of platform texture is 2x2
				new Vector2f((float) 0, (float) 2),
				new Vector2f((float) 2, (float) 2),
				new Vector2f((float) 2, (float) 0),
				new Vector2f((float) 0, (float) 0)
			})
		}), new BoundingPolygon(new Polygon[] {
			new Polygon(new Vector2f[] {
				new Vector2f((float) x1, (float) y1),
				new Vector2f((float) x2, (float) y1),
				new Vector2f((float) x2, (float) y2),
				new Vector2f((float) x1, (float) y2)
			})
		}));
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
	public void collision(CollisionInformation collisionInfo, List<CollidableDrawable> otherCollidables, Map<CollidableDrawable, Set<CollisionInformation>> log) {
		
	}

	@Override
	public boolean isVisible() {
		return true;
	}

	@Override
	public int getMovabilityIndex() {
		return 0;
	}

	@Override
	public Position getPosition() {
		return new Position(x1, y2);
	}

	@Override
	public Velocity getVelocity() {
		return new Velocity(0, 0);
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
