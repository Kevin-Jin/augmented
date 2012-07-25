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
	}

	@Override
	public boolean isVisible() {
		return getWidth() != 0 && getHeight() != 0;
	}

	@Override
	public BoundingPolygon getBoundingPolygon() {
		return transformedBoundPoly;
	}

	public BoundingPolygon getUntransformedBoundingPolygon() {
		return baseBoundPoly;
	}
}
