package amplified.map.entity;

import amplified.map.collisions.BoundingPolygon;

public interface AuxiliaryEntity<E extends Enum<E>> extends DrawableEntity {
	E getType();

	void setFlip(boolean flip);

	BoundingPolygon getSelfBoundingPolygon();

	void markStartPosition();
}
