package op_lando.map.entity;

import java.util.Collection;

import op_lando.map.Drawable;
import op_lando.map.physicquantity.Position;

public interface Entity {
	Collection<? extends Drawable> getDrawables();

	Position getPosition();

	void setPosition(Position pos);

	boolean flipHorizontally();

	float getRotation();

	void update(double tDelta);
}
