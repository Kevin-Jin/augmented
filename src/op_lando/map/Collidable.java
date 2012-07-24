package op_lando.map;

import op_lando.map.collisions.BoundingPolygon;

public interface Collidable {
	BoundingPolygon getBoundingPolygon();
}
