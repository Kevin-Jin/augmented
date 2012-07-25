package op_lando.map.entity.player;

import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import op_lando.map.CollidableDrawable;
import op_lando.map.collisions.BoundingPolygon;
import op_lando.map.collisions.CollisionInformation;
import op_lando.map.collisions.Polygon;
import op_lando.map.entity.BodyEntity;
import op_lando.map.entity.SimpleEntity;
import op_lando.map.physicquantity.Position;
import op_lando.map.state.Camera;
import op_lando.map.state.Input;
import op_lando.resources.TextureCache;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.ReadableVector2f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector4f;
import org.newdawn.slick.opengl.Texture;

public class AvatarBody extends SimpleEntity implements BodyEntity<PlayerPart> {
	private final Player parent;
	private final Map<PlayerPart, Vector2f> baseAttachPoints;
	private final Map<PlayerPart, Vector2f> transformedAttachPoints;
	private double rot;
	private boolean flipHorizontally;
	private BoundingPolygon parentBoundPoly;

	public AvatarBody(Player parent) {
		super(new BoundingPolygon(new Polygon[] {
			new Polygon(new Vector2f[] {
				new Vector2f(30, 12),
				new Vector2f(18, 22),
				new Vector2f(9, 42),
				new Vector2f(9, 58),
				new Vector2f(16, 76),
				new Vector2f(27, 86),
				new Vector2f(38, 86),
				new Vector2f(50, 75),
				new Vector2f(56, 57),
				new Vector2f(56, 42),
				new Vector2f(49, 24),
				new Vector2f(35, 12)
			}), new Polygon(new Vector2f[] {
				new Vector2f(30, 0),
				new Vector2f(30, 11),
				new Vector2f(35, 11),
				new Vector2f(35, 0)
			}), new Polygon(new Vector2f[] {
				new Vector2f(1, 36),
				new Vector2f(9, 36),
				new Vector2f(9, 65),
				new Vector2f(1, 65)
			})
		}));

		this.parent = parent;

		EnumMap<PlayerPart, Vector2f> attachPoints = new EnumMap<PlayerPart, Vector2f>(PlayerPart.class);
		attachPoints.put(PlayerPart.ARM, new Vector2f(26, 55));
		attachPoints.put(PlayerPart.LEGS, new Vector2f(8, 132));
		attachPoints.put(PlayerPart.FIRE, new Vector2f(5, 64));
		baseAttachPoints = Collections.unmodifiableMap(attachPoints);
		transformedAttachPoints = new EnumMap<PlayerPart, Vector2f>(PlayerPart.class);
		for (Map.Entry<PlayerPart, Vector2f> entry : attachPoints.entrySet())
			transformedAttachPoints.put(entry.getKey(), new Vector2f(entry.getValue()));

		parentBoundPoly = getSelfBoundingPolygon();
	}

	@Override
	public boolean collision(CollisionInformation collisionInfo, List<CollidableDrawable> otherCollidables) {
		//TODO: fix collision fighting between legs polygon and body (self) polygon
		//when a thin CollidableDrawable goes between body and legs. Basically can't
		//have any horizontal platforms that are thinner than the legs are high.
		//also fix player being able to be inside a platform for a frame when
		//moving into it when rotation is not zero
		boolean movedSelf = super.collision(collisionInfo, otherCollidables);
		if (movedSelf)
			parent.updateChildPositionsAndPolygons(collisionInfo.getMinimumTranslationVector());
		return movedSelf;
	}

	@Override
	public int getMovabilityIndex() {
		return 1;
	}

	@Override
	public Vector2f getOrigin() {
		return new Vector2f(getTexture().getImageWidth() / 2f, baseAttachPoints.get(PlayerPart.ARM).getY() + parent.getArm().getUntransformedBeamAttachPoint().getY());
	}

	@Override
	public float getRotation() {
		return (float) rot;
	}

	public float lookAt(Position pos) {
		ReadableVector2f rotateAbout = Matrix4f.transform(getTransformationMatrix(), new Vector4f(getOrigin().getX(), getOrigin().getY(), 1, 1), null);
		double y = pos.getY() - rotateAbout.getY();
		double x = pos.getX() - rotateAbout.getX();
		double realRot = Math.atan2(y, x);
		if (flipHorizontally)
			realRot -= Math.PI;
		if (!flipHorizontally && x < 0 || flipHorizontally && x > 0) {
			flipHorizontally = !flipHorizontally;
			realRot -= Math.PI;
		}

		rot = Math.min(realRot, Math.PI / 4);
		if (flipHorizontally && rot < -Math.PI / 4 && rot > -Math.PI / 2)
			// uhh, kludgy check but it works!
			rot = -Math.PI / 4;
		return (float) realRot;
	}

	@Override
	public boolean flipHorizontally() {
		return flipHorizontally;
	}

	@Override
	public BoundingPolygon getBoundingPolygon() {
		return parentBoundPoly;
	}

	@Override
	public void setBoundingPolygon(BoundingPolygon parent) {
		parentBoundPoly = parent;
	}

	@Override
	public BoundingPolygon getSelfBoundingPolygon() {
		return super.getBoundingPolygon();
	}

	@Override
	public void update(double tDelta, Input input, Camera camera) {
		if (input.downKeys().contains(Integer.valueOf(Keyboard.KEY_A)))
			getPosition().setX(getPosition().getX() - 10);
		if (input.downKeys().contains(Integer.valueOf(Keyboard.KEY_D)))
			getPosition().setX(getPosition().getX() + 10);
		if (input.downKeys().contains(Integer.valueOf(Keyboard.KEY_S)))
			getPosition().setY(getPosition().getY() - 10);
		if (input.downKeys().contains(Integer.valueOf(Keyboard.KEY_W)))
			getPosition().setY(getPosition().getY() + 10);
		camera.lookAt(parent.getPosition());
		parent.lookAt(camera.mouseToWorld(input.cursorPosition().getX(), input.cursorPosition().getY()));

		for (Map.Entry<PlayerPart, Vector2f> entry : transformedAttachPoints.entrySet()) {
			Vector2f base = baseAttachPoints.get(entry.getKey());
			entry.getValue().set(Matrix4f.transform(getTransformationMatrix(), new Vector4f(base.getX(), base.getY(), 1, 1), null));
		}

		double lastRot = rot;
		rot = 0;
		try {
			Vector2f coord = new Vector2f(baseAttachPoints.get(PlayerPart.LEGS));
			if (flipHorizontally)
				coord.setX(coord.getX() + 48);
			transformedAttachPoints.get(PlayerPart.LEGS).set(Matrix4f.transform(getTransformationMatrix(), new Vector4f(coord.getX(), coord.getY(), 1, 1), null));
		} finally {
			rot = lastRot;
		}

		super.update(tDelta, input, camera);
	}

	@Override
	public Texture getTexture() {
		return TextureCache.getTexture("body");
	}

	@Override
	public Vector2f getAttachPoint(PlayerPart part) {
		switch (part) {
			case BEAM:
				return parent.getArm().getBeamAttachPoint();
			default:
				return transformedAttachPoints.get(part);
		}
	}
}
