package op_lando.map;

import op_lando.map.collisions.BoundingPolygon;

public abstract class CollidableDrawable extends AbstractDrawable implements Collidable {
	protected final BoundingPolygon baseBoundPoly;
	protected BoundingPolygon transformedBoundPoly;

	protected CollidableDrawable(BoundingPolygon baseBoundPoly, BoundingPolygon boundPoly) {
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
