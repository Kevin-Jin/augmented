package op_lando;

import org.newdawn.slick.opengl.Texture;

public class AvatarArm extends SimpleEntity {
	@Override
	public Texture getTexture() {
		return TextureCache.getTexture("arm");
	}
}
