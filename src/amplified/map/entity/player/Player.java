package amplified.map.entity.player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import amplified.map.entity.AuxiliaryEntity;
import amplified.map.entity.CompoundEntity;
import amplified.map.physicquantity.Position;

public class Player extends CompoundEntity<PlayerPart> {
	private final List<AuxiliaryEntity<PlayerPart>> allAuxiliaries;
	private AvatarBody body;
	private AvatarLegs legs;
	private AvatarArm arm;
	private JetpackFire flame;
	private TractorBeam beam;

	public Player() {
		body = new AvatarBody(this);
		legs = new AvatarLegs(this);
		arm = new AvatarArm();
		flame = new JetpackFire(this);
		beam = new TractorBeam(this);

		List<AuxiliaryEntity<PlayerPart>> parts = new ArrayList<AuxiliaryEntity<PlayerPart>>(4);
		parts.add(legs);
		parts.add(arm);
		parts.add(flame);
		parts.add(beam);
		allAuxiliaries = Collections.unmodifiableList(parts);

		partsConstructed();
	}

	@Override
	public List<? extends AuxiliaryEntity<PlayerPart>> getAuxiliaries() {
		return allAuxiliaries;
	}

	@Override
	public AvatarBody getBody() {
		return body;
	}

	public AvatarLegs getLegs() {
		return legs;
	}

	public AvatarArm getArm() {
		return arm;
	}

	public JetpackFire getFlame() {
		return flame;
	}

	public TractorBeam getBeam() {
		return beam;
	}

	@Override
	public void setRotation(float rot) {
		float realRot = body.rotate(rot, body.flipHorizontally() ^ Math.cos(rot) < 0);
		arm.setRotation(realRot);
		beam.setRotation(realRot);
		flame.setRotation(body.getRotation());
		for (AuxiliaryEntity<PlayerPart> child : getAuxiliaries()) {
			child.setFlip(getBody().flipHorizontally());
			child.markStartPosition();
			child.setPosition(new Position(getBody().getAttachPoint(child.getType())));
		}
	}

	public void lookAt(Position pos) {
		float realRot = body.lookAt(pos);
		arm.setRotation(realRot);
		beam.setRotation(realRot);
		flame.setRotation(body.getRotation());
	}

	public void reset() {
		body.reset();
		legs.reset();
		flame.reset();
		beam.reset();
		setRotation(0);
	}
}
