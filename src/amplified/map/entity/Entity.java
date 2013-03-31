package amplified.map.entity;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import amplified.map.CollidableDrawable;
import amplified.map.collisions.CollisionInformation;
import amplified.map.physicquantity.Position;
import amplified.map.state.Camera;
import amplified.map.state.Input;
import amplified.map.state.MapState;

public interface Entity extends AutoTransformable {
	Collection<? extends DrawableEntity> getDrawables();

	void move(Direction to);

	Position getPosition();

	void setPosition(Position pos);

	boolean flipHorizontally();

	float getRotation();

	void setRotation(float rot);

	void preCollisionsUpdate(double tDelta, Input input, Camera camera, MapState map);

	void postCollisionsUpdate(double tDelta, Input input, Map<CollidableDrawable, Set<CollisionInformation>> log, Camera camera);

	float getWidth();

	float getHeight();

	void setWidth(double w);

	void setHeight(double h);
}
