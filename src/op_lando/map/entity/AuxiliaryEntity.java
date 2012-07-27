package op_lando.map.entity;

import op_lando.map.collisions.BoundingPolygon;

public interface AuxiliaryEntity<E extends Enum<E>> extends DrawableEntity {
	E getType();

	void setFlip(boolean flip);

	BoundingPolygon getSelfBoundingPolygon();

	void recalculateSelfBoundingPolygon();

	void addToPosition(double x, double y);
}
