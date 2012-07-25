package op_lando.map;

import java.util.List;

import op_lando.map.collisions.BoundingPolygon;
import op_lando.map.collisions.CollisionInformation;

public interface CollidableDrawable extends Drawable {
	BoundingPolygon getBoundingPolygon();

	boolean collision(CollisionInformation collisionInfo, List<CollidableDrawable> otherCollidables);

	boolean isVisible();

	int getMovabilityIndex();
}
