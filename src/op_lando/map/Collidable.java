package op_lando.map;

import java.util.List;

import op_lando.map.collisions.BoundingPolygon;
import op_lando.map.collisions.CollisionInformation;

public interface Collidable {
	BoundingPolygon getBoundingPolygon();

	boolean collision(CollisionInformation collisionInfo, List<Collidable> otherCollidables);

	boolean isVisible();

	int getMovabilityIndex();
}
