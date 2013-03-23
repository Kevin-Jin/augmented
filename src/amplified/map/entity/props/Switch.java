package amplified.map.entity.props;

import java.util.List;

import org.newdawn.slick.Color;
import org.newdawn.slick.opengl.Texture;

import amplified.map.Switchable;
import amplified.map.entity.SimpleEntity;

public class Switch extends SimpleEntity {
	public Switch(Color color, List<Switchable> list) {
		super(null, null);
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
		return null;
	}
}
