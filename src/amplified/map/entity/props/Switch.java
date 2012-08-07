package amplified.map.entity.props;

import org.newdawn.slick.opengl.Texture;

import amplified.map.collisions.BoundingPolygon;
import amplified.map.entity.SimpleEntity;
import amplified.resources.EntityKinematics;

public class Switch extends SimpleEntity {
	public Switch(BoundingPolygon boundPoly, EntityKinematics quantities) {
		super(boundPoly, quantities);
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
