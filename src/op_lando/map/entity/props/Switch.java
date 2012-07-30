package op_lando.map.entity.props;

import op_lando.map.collisions.BoundingPolygon;
import op_lando.map.entity.SimpleEntity;
import op_lando.resources.EntityKinematics;

import org.newdawn.slick.opengl.Texture;

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
