package amplified.map.entity;

import org.lwjgl.util.vector.Vector2f;

import amplified.map.collisions.BoundingPolygon;

public interface BodyEntity<E extends Enum<E>> extends DrawableEntity {
	Vector2f getAttachPoint(E part);

	boolean flipHorizontally();

	void setBoundingPolygon(BoundingPolygon parent);

	BoundingPolygon getSelfBoundingPolygon();
}
