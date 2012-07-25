package op_lando.map;

import java.util.List;

import op_lando.map.collisions.BoundingPolygon;
import op_lando.map.collisions.CollisionInformation;

public abstract class CollidableDrawable extends AbstractDrawable implements Collidable {
	protected final BoundingPolygon baseBoundPoly;
	protected BoundingPolygon transformedBoundPoly;

	protected CollidableDrawable(BoundingPolygon baseBoundPoly, BoundingPolygon boundPoly) {
		this.baseBoundPoly = baseBoundPoly;
		transformedBoundPoly = boundPoly;
	}

	@Override
	public void collision(CollisionInformation collisionInformation, List<Collidable> collidablesList) {
		//TODO: implement
		System.out.println(this + " collided with "+ collisionInformation.getCollidedWith());
	}

	@Override
	public boolean isVisible() {
		return getWidth() != 0 && getHeight() != 0;
	}

	@Override
	public int compareTo(Collidable other) {
		return this.getMovabilityIndex() - other.getMovabilityIndex();
	}

	@Override
	public BoundingPolygon getBoundingPolygon() {
		return transformedBoundPoly;
	}

	public BoundingPolygon getUntransformedBoundingPolygon() {
		return baseBoundPoly;
	}
}
