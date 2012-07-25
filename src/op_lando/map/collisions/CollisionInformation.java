package op_lando.map.collisions;

import op_lando.map.Collidable;

import org.lwjgl.util.vector.Vector2f;

public class CollisionInformation {
	private Vector2f minTranslation, surface;
	private Collidable otherCollidable;

	public CollisionInformation(Vector2f minTranslation, Vector2f surface, Collidable collidedWith) {
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

	public Collidable getCollidedWith() {
		return otherCollidable;
	}

	public void setCollidedWith(Collidable collidables) {
		otherCollidable = collidables;
	}

	@Override
	public String toString() {
		return "Translation vector of " + minTranslation + " and surface vector of : " + surface;
	}
}
