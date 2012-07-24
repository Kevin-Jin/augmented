package op_lando;

import java.util.Arrays;
import java.util.Collection;

public class Player extends CompoundEntity {
	private AvatarBody body;
	private AvatarLegs legs;
	private AvatarArm arm;
	private JetpackFire flame;
	private TractorBeam beam;

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
