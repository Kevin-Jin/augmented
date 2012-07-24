package op_lando.map.entity.player;

import java.util.Arrays;
import java.util.Collection;

import op_lando.map.entity.CompoundEntity;
import op_lando.map.entity.Direction;
import op_lando.map.entity.SimpleEntity;

public class Player extends CompoundEntity {
	private AvatarBody body;
	private AvatarLegs legs;
	private AvatarArm arm;
	private JetpackFire flame;
	private TractorBeam beam;

	public Player() {
		body = new AvatarBody();
		legs = new AvatarLegs();
		arm = new AvatarArm();
		flame = new JetpackFire();
		beam = new TractorBeam();
	}

	@Override
	public Collection<SimpleEntity> getDrawables() {
		return Arrays.asList(body, legs, arm, flame, beam);
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

	public void move(Direction dir) {
		
	}
}
