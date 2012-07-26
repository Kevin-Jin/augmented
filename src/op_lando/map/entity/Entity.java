package op_lando.map.entity;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import op_lando.map.CollidableDrawable;
import op_lando.map.collisions.CollisionInformation;
import op_lando.map.physicquantity.Position;
import op_lando.map.state.Camera;
import op_lando.map.state.Input;
import op_lando.map.state.MapState;

public interface Entity {
	Collection<? extends DrawableEntity> getDrawables();

	Position getPosition();

	void setPosition(Position pos);

	boolean flipHorizontally();

	float getRotation();

	void preCollisionsUpdate(double tDelta, Input input, Camera camera, MapState map);

	void postCollisionsUpdate(double tDelta, Input input, Map<CollidableDrawable, Set<CollisionInformation>> log);
}
