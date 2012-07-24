package op_lando.map;

import op_lando.map.collisions.BoundingPolygon;

public abstract class CollidableDrawable extends AbstractDrawable {
	protected final BoundingPolygon baseBoundPoly;
	protected BoundingPolygon transformedBoundPoly;

	protected CollidableDrawable(BoundingPolygon baseBoundPoly, BoundingPolygon boundPoly) {
		this.baseBoundPoly = baseBoundPoly;
		transformedBoundPoly = boundPoly;
	}

	public BoundingPolygon getBoundingPolygon() {
		return transformedBoundPoly;
	}

	public BoundingPolygon getUntransformedBoundingPolygon() {
		return baseBoundPoly;
	}
}
