package amplified.map;

import org.lwjgl.util.vector.Vector2f;
import org.newdawn.slick.opengl.Texture;

import amplified.map.physicquantity.Position;
import amplified.resources.TextureCache;

public class DrawableTexture extends AbstractDrawable {
	private final boolean centerOrigin;
	private final float width, height;
	private final float rot;
	private final Position pos;
	private final String textureName;

	public DrawableTexture(int width, int height, float rotation, Position pos, String textureName, boolean centerOrigin) {
		this.width = width;
		this.height = height;
		this.rot = rotation;
		this.pos = pos;
		this.textureName = textureName;
		this.centerOrigin = centerOrigin;
	}

	public DrawableTexture(String textureName, Position pos) {
		centerOrigin = false;
		this.textureName = textureName;
		this.pos = pos;
		width = getTexture().getImageWidth();
		height = getTexture().getImageHeight();
		rot = 0;
	}

	public DrawableTexture(int width, int height, String textureName, Position pos) {
		centerOrigin = false;
		this.width = width;
		this.height = height;
		rot = 0;
		this.pos = pos;
		this.textureName = textureName;
	}

	public DrawableTexture(float rotation, String textureName, Position pos) {
		centerOrigin = true;
		this.textureName = textureName;
		this.pos = pos;
		width = getTexture().getImageWidth();
		height = getTexture().getImageHeight();
		this.rot = rotation;
	}

	@Override
	public Position getPosition() {
		return pos;
	}

	@Override
	public Vector2f getOrigin() {
		return centerOrigin ? getCenter() : getBottomLeftCorner();
	}

	@Override
	public float getRotation() {
		return rot;
	}

	@Override
	public float getWidth() {
		return width;
	}

	@Override
	public float getHeight() {
		return height;
	}

	public Texture getTexture() {
		return TextureCache.getTexture(textureName);
	}
}
