package amplified.resources.map;

import java.util.Collections;
import java.util.List;

import org.newdawn.slick.Color;

import amplified.map.Switchable;
import amplified.map.entity.AutoTransform;
import amplified.map.physicquantity.Position;

public class SwitchSpawnInfo {
	private final Color color;
	private final Position pos;
	private final List<Switchable> triggerFor;
	private final List<AutoTransform> autoTransforms;

	public SwitchSpawnInfo(Color color, Position pos, List<Switchable> switchables, List<AutoTransform> autoTransforms) {
		this.color = color;
		this.pos = pos;
		triggerFor = Collections.unmodifiableList(switchables);
		this.autoTransforms = Collections.unmodifiableList(autoTransforms);
	}

	public Color getColor() {
		return color;
	}

	public Position getPosition() {
		return pos;
	}

	public List<Switchable> getSwitchables() {
		return triggerFor;
	}
}
