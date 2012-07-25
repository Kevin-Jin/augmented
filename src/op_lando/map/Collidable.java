package op_lando.map;

import java.util.List;

import op_lando.map.collisions.BoundingPolygon;
import op_lando.map.collisions.CollisionInformation;

public interface Collidable {
	BoundingPolygon getBoundingPolygon();

	void collision(CollisionInformation collisionInformation, List<Collidable> collidablesList);

	boolean isVisible();

	int getMovabilityIndex();
}
