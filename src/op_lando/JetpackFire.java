package op_lando;

import org.newdawn.slick.opengl.Texture;

public class JetpackFire extends SimpleEntity {
	private Animation animation;

	public JetpackFire() {
		animation = new Animation(0.2, "flame1", "flame2", "flame3", "flame4");
	}

	@Override
	public void update(double tDelta) {
		super.update(tDelta);
		animation.update(tDelta);
	}

	@Override
	public Texture getTexture() {
		return animation.getTexture();
	}
}
