package op_lando.map;

import op_lando.map.collisions.BoundingPolygon;

public abstract class AbstractCollidable extends AbstractDrawable implements CollidableDrawable {
	protected final BoundingPolygon baseBoundPoly;
	protected BoundingPolygon transformedBoundPoly;

	protected AbstractCollidable(BoundingPolygon baseBoundPoly, BoundingPolygon boundPoly) {
		this.baseBoundPoly = baseBoundPoly;
		transformedBoundPoly = boundPoly;
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
