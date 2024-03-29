package amplified.map;

import org.lwjgl.util.vector.Matrix4f;
import org.newdawn.slick.Color;
import org.newdawn.slick.opengl.Texture;

public interface Drawable {
	Texture getTexture();

	Matrix4f getWorldMatrix();

	Color getTint();

	DrawableOverlayText getCaption();
}
