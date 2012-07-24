package op_lando.map;

import org.lwjgl.util.vector.Matrix4f;
import org.newdawn.slick.Color;
import org.newdawn.slick.opengl.Texture;

public interface Drawable {
	Texture getTexture();

	Matrix4f getTransformationMatrix();

	Color getTint();

	DrawableOverlayText getCaption();
}
