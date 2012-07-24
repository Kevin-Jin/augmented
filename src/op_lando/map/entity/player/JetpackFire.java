package op_lando.map.entity.player;

import op_lando.map.Animation;
import op_lando.map.collisions.BoundingPolygon;
import op_lando.map.collisions.Polygon;
import op_lando.map.entity.AuxiliaryEntity;
import op_lando.map.entity.SimpleEntity;
import op_lando.map.state.Input;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector2f;
import org.newdawn.slick.opengl.Texture;

public class JetpackFire extends SimpleEntity implements AuxiliaryEntity<PlayerPart> {
	private final Animation animation;
	private float rot;
	private boolean flipHorizontally;
	private boolean visible;

	public JetpackFire() {
		super(new BoundingPolygon(new Polygon[] {
			new Polygon(new Vector2f[] {
				new Vector2f(0, 0),
				new Vector2f(8, 0),
				new Vector2f(8, 19),
				new Vector2f(0, 19)
			})
		}));
		animation = new Animation(0.2, "flame1", "flame2", "flame3", "flame4");
	}

	@Override
	public Vector2f getOrigin() {
		return new Vector2f(getTexture().getImageWidth() / 2f, 0);
	}

	@Override
	public Vector2f getDrawPosition() {
		return getPosition().asVector();
	}

	@Override
	public float getWidth() {
		return visible ? super.getWidth() : 0;
	}

	@Override
	public float getHeight() {
		return visible ? super.getHeight() : 0;
	}

	@Override
	public float getRotation() {
		return rot;
	}

	public void setRotation(float rot) {
		this.rot = rot;
	}

	@Override
	public boolean flipHorizontally() {
		return flipHorizontally;
	}

	@Override
	public void setFlip(boolean flip) {
		flipHorizontally = flip;
	}

	private boolean isEmpty() {
		//TODO: implement
		return false;
	}

	@Override
	public void update(double tDelta, Input input) {
		super.update(tDelta, input);
		visible = input.downKeys().contains(Integer.valueOf(Keyboard.KEY_W)) && !isEmpty();
		if (visible)
			animation.update(tDelta);
	}

	@Override
	public Texture getTexture() {
		return animation.getTexture();
	}

	@Override
	public PlayerPart getType() {
		return PlayerPart.FIRE;
	}
}
