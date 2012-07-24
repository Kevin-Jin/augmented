package op_lando.map;

import op_lando.resources.TextureCache;

import org.newdawn.slick.opengl.Texture;

public class Animation {
	private String[] textureNames;
	private double framePeriod;
	private double elapsed;
	private int index;

	public Animation(double delay, String... textures) {
		framePeriod = delay;
		textureNames = textures;
	}

	public void update(double tDelta) {
		elapsed += tDelta;

		while (elapsed >= framePeriod) {
			elapsed -= framePeriod;
			index = (index + 1) % textureNames.length;
		}
	}

	public void reset() {
		elapsed = 0;
		index = 0;
	}

	public Texture getTexture() {
		return TextureCache.getTexture(textureNames[index]);
	}
}
