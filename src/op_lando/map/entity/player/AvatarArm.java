package op_lando.map.entity.player;

import op_lando.map.entity.SimpleEntity;
import op_lando.resources.TextureCache;

import org.newdawn.slick.opengl.Texture;

public class AvatarArm extends SimpleEntity {
	@Override
	public Texture getTexture() {
		return TextureCache.getTexture("arm");
	}
}