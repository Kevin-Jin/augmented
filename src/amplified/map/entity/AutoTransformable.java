package amplified.map.entity;

import org.newdawn.slick.Color;

import amplified.map.physicquantity.Position;

public interface AutoTransformable {
	void setWidth(double d);

	void setHeight(double d);

	float getWidth();

	float getHeight();

	Position getPosition();

	void setRotation(float normalize);

	float getRotation();

	boolean flipHorizontally();

	void setCaption(String s);

	void initializeCaption(String font, Color color);
}
