package amplified.map;

import java.util.List;
import java.util.Map;
import java.util.Set;

import amplified.map.collisions.BoundingPolygon;
import amplified.map.collisions.CollisionInformation;
import amplified.map.physicquantity.Position;
import amplified.map.physicquantity.Velocity;

public interface CollidableDrawable extends Drawable {
	//TODO: try making a collision window rectangle, whose position is the
	//leftmost vertex's x and bottom-most vertex's y, and whose width is the
	//rightmost vertex's x subtracted by the position's x and height is the
	//topmost vertex's y subtracted by the position's y. see if using
	//this.getCollisionRectangle().intersects(other.getCollisionRectangle())
	//before doing separating axis theorem speeds up collision handling. note
	//all vertices used are in world space.

	BoundingPolygon getBoundingPolygon();

	void collision(CollisionInformation collisionInfo, List<CollidableDrawable> otherCollidables, Map<CollidableDrawable, Set<CollisionInformation>> log);

	boolean isVisible();

	int getMovabilityIndex();

	Position getPosition();

	Velocity getVelocity();
}