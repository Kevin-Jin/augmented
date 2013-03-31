package amplified.map;

import amplified.map.collisions.BoundingPolygon;

public abstract class AbstractCollidable extends AbstractDrawable implements CollidableDrawable {
	protected final BoundingPolygon baseBoundPoly;
	protected BoundingPolygon transformedBoundPoly;

	protected AbstractCollidable(BoundingPolygon baseBoundPoly, BoundingPolygon boundPoly) {
		this.baseBoundPoly = baseBoundPoly;
		transformedBoundPoly = boundPoly;
	}

	public boolean isVisible() {
		return getWidth() != 0 && getHeight() != 0;
	}

	public BoundingPolygon getBoundingPolygon() {
		return transformedBoundPoly;
	}

	public BoundingPolygon getUntransformedBoundingPolygon() {
		return baseBoundPoly;
	}
}
