package op_lando;

import org.newdawn.slick.opengl.Texture;

public class AvatarBody extends SimpleEntity {
	@Override
	public Texture getTexture() {
		return TextureCache.getTexture("body");
	}
}
