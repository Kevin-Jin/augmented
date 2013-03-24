package amplified.map.entity.props;

import java.util.List;

import org.lwjgl.util.vector.Vector2f;
import org.newdawn.slick.Color;
import org.newdawn.slick.opengl.Texture;

import amplified.map.Switchable;
import amplified.map.collisions.BoundingPolygon;
import amplified.map.collisions.Polygon;
import amplified.map.entity.SimpleEntity;
import amplified.resources.TextureCache;

public class Switch extends SimpleEntity {
	public Switch(Color color, List<Switchable> list) {
		super(new BoundingPolygon(new Polygon[] {
				new Polygon(new Vector2f[] {
						new Vector2f(3, 0),
						new Vector2f(34, 0),
						new Vector2f(37, 6), 
						new Vector2f(23, 21), 
						new Vector2f(14, 21), 
						new Vector2f(0, 6)
				})
		}), null);
	}

	@Override
	public void setRotation(float rot) {
		throw new UnsupportedOperationException("Cannot rotate a Switch");
	}

	@Override
	public int getMovabilityIndex() {
		return 0;
	}

	@Override
	public Texture getTexture() {
		return TextureCache.getTexture("switch");
	}
}
