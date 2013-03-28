package amplified.map.entity.props;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.lwjgl.util.vector.Vector2f;
import org.newdawn.slick.Color;
import org.newdawn.slick.opengl.Texture;

import amplified.map.CollidableDrawable;
import amplified.map.Switchable;
import amplified.map.collisions.BoundingPolygon;
import amplified.map.collisions.CollisionInformation;
import amplified.map.collisions.Polygon;
import amplified.map.entity.SimpleEntity;
import amplified.map.state.Camera;
import amplified.map.state.Input;
import amplified.map.state.MapState;
import amplified.resources.TextureCache;

public class Switch extends SimpleEntity {
	private final static double DEACTIVATE_DELAY = .1;

	private final Color color;
	private final List<Switchable> switches;

	private Map<SelectableEntity,Double> hitMap;
	private boolean wasActive;

	public Switch(Color color, List<Switchable> switches) {
		super(new BoundingPolygon(new Polygon[] {
			new Polygon(new Vector2f[] {
				new Vector2f(3, 21),
				new Vector2f(34, 21),
				new Vector2f(37, 15), 
				new Vector2f(23, 0), 
				new Vector2f(14, 0), 
				new Vector2f(0, 15)
			})
		}), null);

		this.color = color;
		this.switches = switches;
		this.hitMap = new HashMap<SelectableEntity,Double>();
	}

	@Override
	public void setRotation(float rot) {
		throw new UnsupportedOperationException("Cannot rotate a Switch");
	}

	@Override
	public void preCollisionsUpdate(double tDelta, Input input, Camera camera, MapState map) {
		super.preCollisionsUpdate(tDelta, input, camera, map);

		for (Iterator<Entry<SelectableEntity, Double>> iterator = hitMap.entrySet().iterator(); iterator.hasNext();) {
			Entry<SelectableEntity,Double> entry = iterator.next();
			entry.setValue(entry.getValue() + tDelta);
			if (entry.getValue() > DEACTIVATE_DELAY)
				iterator.remove();
		}
	}

	@Override
	public void collision(CollisionInformation collisionInfo, List<CollidableDrawable> otherCollidables) {
		if (collisionInfo.getCollidedWith() instanceof SelectableEntity)
			hitMap.put((SelectableEntity) collisionInfo.getCollidedWith(), Double.valueOf(0));
		//now push the other entity out
		collisionInfo.getCollidedWith().collision(collisionInfo.complement(this), null);
	}

	@Override
	public void postCollisionsUpdate(double tDelta, Input input, Map<CollidableDrawable, Set<CollisionInformation>> log, Camera camera) {
		boolean active = !hitMap.isEmpty();
		if (active && !wasActive)
			for (Switchable s : switches)
				s.switchActivated();
		if (!active && wasActive)
			for (Switchable s : switches)
				s.switchDeactivated();
		wasActive = active;
	}

	@Override
	public int getMovabilityIndex() {
		return 2;
	}

	@Override
	public Texture getTexture() {
		return TextureCache.getTexture("switch");
	}

	@Override
	public Color getTint() {
		return color;
	}
}
