package op_lando.map.collisions;

import op_lando.map.CollidableDrawable;

import org.lwjgl.util.vector.Vector2f;

public class CollisionResult {
	private boolean collided;
	private CollisionInformation collisionInfo;

	public CollisionResult() {
		collided = false;
	}

	public CollisionResult(Vector2f toNegate, Vector2f surface, CollidableDrawable other) {
		collisionInfo = new CollisionInformation(toNegate, surface, other);
		collided = true;
	}

	public boolean collision() {
		return collided;
	}

	public CollisionInformation getCollisionInformation() {
		return collisionInfo;
	}

	@Override
	public String toString() {
		if (collided)
			return "Collided : " + collisionInfo;
		return "Did not collide.";
	}
}
