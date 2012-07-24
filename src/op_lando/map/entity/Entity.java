package op_lando.map.entity;

import java.util.Collection;

import op_lando.map.physicquantity.Position;
import op_lando.map.state.Camera;
import op_lando.map.state.Input;

public interface Entity {
	Collection<? extends DrawableEntity> getDrawables();

	Position getPosition();

	void setPosition(Position pos);

	boolean flipHorizontally();

	float getRotation();

	void update(double tDelta, Input input, Camera camera);
}
