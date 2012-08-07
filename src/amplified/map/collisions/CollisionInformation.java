package amplified.map.collisions;

import org.lwjgl.util.vector.Vector2f;

import amplified.map.CollidableDrawable;

public class CollisionInformation {
	private Vector2f minTranslation, surface;
	private CollidableDrawable otherCollidable;

	public CollisionInformation(Vector2f minTranslation, Vector2f surface) {
		this.minTranslation = minTranslation;
		this.surface = surface;
	}

	public void negateMinimumTranslationVector() {
		minTranslation.negate();
	}

	public Vector2f getMinimumTranslationVector() {
		return minTranslation;
	}

	public Vector2f getCollidingSurface() {
		return surface;
	}

	public CollidableDrawable getCollidedWith() {
		return otherCollidable;
	}

	public void setCollidedWith(CollidableDrawable collidables) {
		otherCollidable = collidables;
	}

	public CollisionInformation complement(CollidableDrawable opposite) {
		CollisionInformation other = new CollisionInformation(new Vector2f(minTranslation), surface);
		other.setCollidedWith(opposite);
		other.negateMinimumTranslationVector();
		return other;
	}

	@Override
	public String toString() {
		return "Translation vector of " + minTranslation + " and surface vector of : " + surface;
	}
}
