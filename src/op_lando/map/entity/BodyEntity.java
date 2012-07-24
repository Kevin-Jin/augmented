package op_lando.map.entity;

import op_lando.map.collisions.BoundingPolygon;

import org.lwjgl.util.vector.Vector2f;

public interface BodyEntity<E extends Enum<E>> extends DrawableEntity {
	Vector2f getAttachPoint(E part);

	boolean flipHorizontally();

	void setBoundingPolygon(BoundingPolygon parent);

	BoundingPolygon getSelfBoundingPolygon();
}
