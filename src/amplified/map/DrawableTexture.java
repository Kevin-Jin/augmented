package amplified.map;

import org.lwjgl.util.Point;
import org.lwjgl.util.vector.Vector2f;
import org.newdawn.slick.Color;
import org.newdawn.slick.opengl.Texture;

import amplified.map.entity.AutoTransformable;
import amplified.map.physicquantity.Position;
import amplified.resources.FontCache;
import amplified.resources.TextureCache;

public class DrawableTexture extends AbstractDrawable implements AutoTransformable {
	private final boolean centerOrigin;
	private final Position pos;
	private final String textureName;
	private float width, height;
	private float rot;
	private String font;
	private Color color;
	private String caption;

	public DrawableTexture(int width, int height, float rotation, Position pos, String textureName, boolean centerOrigin) {
		this.width = width;
		this.height = height;
		this.rot = rotation;
		this.pos = pos;
		this.textureName = textureName;
		this.centerOrigin = centerOrigin;
		color = Color.white;
	}

	public DrawableTexture(String textureName, Position pos) {
		centerOrigin = false;
		this.textureName = textureName;
		this.pos = pos;
		width = getTexture().getImageWidth();
		height = getTexture().getImageHeight();
		rot = 0;
		color = Color.white;
	}

	public DrawableTexture(int width, int height, String textureName, Position pos) {
		centerOrigin = false;
		this.width = width;
		this.height = height;
		rot = 0;
		this.pos = pos;
		this.textureName = textureName;
		color = Color.white;
	}

	public DrawableTexture(float rotation, String textureName, Position pos) {
		centerOrigin = true;
		this.textureName = textureName;
		this.pos = pos;
		width = getTexture().getImageWidth();
		height = getTexture().getImageHeight();
		this.rot = rotation;
		color = Color.white;
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

	@Override
	public Color getTint() {
		return color;
	}

	public Texture getTexture() {
		return TextureCache.getTexture(textureName);
	}

	public void setWidth(double d) {
		width = (float) d;
	}

	public void setHeight(double d) {
		height = (float) d;
	}

	public void setRotation(float normalize) {
		rot = normalize;
	}

	@Override
	public DrawableOverlayText getCaption() {
		if (font == null || caption == null)
			return null;
		return new DrawableOverlayText(new Point(0, 0), FontCache.getFont(font), caption);
	}

	public void initializeCaption(String font, Color color) {
		this.font = font;
		this.color = color;
	}

	public void setCaption(String s) {
		caption = s;
	}
}
