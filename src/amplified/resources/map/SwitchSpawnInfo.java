package amplified.resources.map;

import java.util.List;

import org.newdawn.slick.Color;

import amplified.map.Switchable;
import amplified.map.physicquantity.Position;

public class SwitchSpawnInfo {
	private Color color;
	private Position pos;
	private List<Switchable> triggerFor;

	public SwitchSpawnInfo(Color color, Position pos, List<Switchable> switchables) {
		this.color = color;
		this.pos = pos;
		triggerFor = switchables;
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
