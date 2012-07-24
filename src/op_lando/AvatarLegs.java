package op_lando;

import org.newdawn.slick.opengl.Texture;

public class AvatarLegs extends SimpleEntity {
	private Animation animation;

	public AvatarLegs() {
		animation = new Animation(0.06, "legsRest", "legs1", "legs2", "legs3", "legs4", "legs5");
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
