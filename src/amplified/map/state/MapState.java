package amplified.map.state;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.lwjgl.util.Rectangle;

import amplified.map.CollidableDrawable;
import amplified.map.Drawable;
import amplified.map.DrawableTexture;
import amplified.map.RetractablePlatform;
import amplified.map.entity.AutoTransform;
import amplified.map.entity.Entity;
import amplified.map.entity.player.Player;
import amplified.map.entity.props.Box;
import amplified.map.entity.props.NBox;
import amplified.map.entity.props.RectangleBox;
import amplified.map.entity.props.Switch;
import amplified.map.physicquantity.Position;
import amplified.resources.LevelLayout;
import amplified.resources.map.BoxSpawnInfo;
import amplified.resources.map.NBoxSpawnInfo;
import amplified.resources.map.OverlayInfo;
import amplified.resources.map.RectangleSpawnInfo;
import amplified.resources.map.SwitchSpawnInfo;

public class MapState {
	public static class ZAxisLayer {
		public static final Byte
			FAR_BACKGROUND = Byte.valueOf((byte) 0),
			MAIN_BACKGROUND = Byte.valueOf((byte) 1),
			MIDGROUND = Byte.valueOf((byte) 2),
			FOREGROUND = Byte.valueOf((byte) 3),
			OVERLAY = Byte.valueOf((byte) 4)
		;

		private float parallax;
		private List<Drawable> drawables;

		public ZAxisLayer(float parallax) {
			this.parallax = parallax;
			this.drawables = new ArrayList<Drawable>();
		}

		public float getParallaxFactor() {
			return parallax;
		}

		public List<Drawable> getDrawables() {
			return drawables;
		}
	}

	private static final int FLOOR_VISIBLE_PIXELS = 20;
	private static final int CEILING_VISIBLE_PIXELS = 20;
	private static final int LEFT_WALL_VISIBLE_PIXELS = 20;
	private static final int RIGHT_WALL_VISIBLE_PIXELS = 20;

	private LevelLayout layout;
	private final Player player;
	private final SortedMap<Byte, Entity> entities;
	private final SortedMap<Byte, ZAxisLayer> layers;
	private final List<CollidableDrawable> collidables;
	private final Map<Entity, List<AutoTransform>> autoTransforms;
	private byte nextEntityId;

	public MapState(Drawable... overlays) {
		player = new Player();

		entities = new TreeMap<Byte, Entity>();
		layers = new TreeMap<Byte, ZAxisLayer>();
		collidables = new ArrayList<CollidableDrawable>();
		autoTransforms = new HashMap<Entity, List<AutoTransform>>();

		layers.put(ZAxisLayer.FAR_BACKGROUND, new ZAxisLayer(0.25f));
		layers.put(ZAxisLayer.MAIN_BACKGROUND, new ZAxisLayer(0.5f));
		layers.put(ZAxisLayer.MIDGROUND, new ZAxisLayer(1));
		layers.put(ZAxisLayer.FOREGROUND, new ZAxisLayer(2));
		layers.put(ZAxisLayer.OVERLAY, new ZAxisLayer(0));

		for (Drawable overlay : overlays)
			layers.get(ZAxisLayer.OVERLAY).getDrawables().add(overlay);
	}

	public void setLayout(LevelLayout layout) {
		this.layout = layout;

		layers.get(ZAxisLayer.FAR_BACKGROUND).getDrawables().clear();
		if (layout.getOutsideBackground() != null)
			layers.get(ZAxisLayer.FAR_BACKGROUND).getDrawables().add(new DrawableTexture(layout.getOutsideBackground(), new Position(0, 0)));
		layers.get(ZAxisLayer.MAIN_BACKGROUND).getDrawables().clear();
		if (layout.getInsideBackground() != null)
			layers.get(ZAxisLayer.MAIN_BACKGROUND).getDrawables().add(new DrawableTexture(layout.getInsideBackground(), new Position(0, 0)));

		layers.get(ZAxisLayer.MIDGROUND).getDrawables().clear();
		layers.get(ZAxisLayer.MIDGROUND).getDrawables().addAll(layout.getPlatforms());
		collidables.addAll(layout.getPlatforms());

		entities.clear();
		nextEntityId = 0;
		entities.put(Byte.valueOf(nextEntityId++), player);
		autoTransforms.put(player, layout.getAvatarAutoTransforms());
		autoTransforms.put(player.getBeam(), layout.getBeamAutoTransforms());
		for (AutoTransform at : layout.getAvatarAutoTransforms())
			at.reset();
		for (AutoTransform at : layout.getBeamAutoTransforms())
			at.reset();
		for (BoxSpawnInfo box : layout.getBoxes()) {
			Box b = new Box(box.getMinimumScale(), box.getMaximumScale());
			b.setPosition(box.getPosition());
			entities.put(Byte.valueOf(nextEntityId++), b);
			autoTransforms.put(b, box.getAutoTransforms());
			for (AutoTransform at : box.getAutoTransforms())
				at.reset();
		}
		for (RectangleSpawnInfo rect : layout.getRectangles()) {
			RectangleBox r = new RectangleBox(rect.getMinimumScale(), rect.getMaximumScale());
			r.setPosition(rect.getPosition());
			entities.put(Byte.valueOf(nextEntityId++), r);
			autoTransforms.put(r, rect.getAutoTransforms());
			for (AutoTransform at : rect.getAutoTransforms())
				at.reset();
		}
		for (NBoxSpawnInfo nbox : layout.getNBoxes()) {
			NBox n = new NBox();
			n.setPosition(nbox.getPosition());
			entities.put(Byte.valueOf(nextEntityId++), n);
			autoTransforms.put(n, nbox.getAutoTransforms());
			for (AutoTransform at : nbox.getAutoTransforms())
				at.reset();
		}
		for (SwitchSpawnInfo sw : layout.getSwitches()) {
			Switch s = new Switch(sw.getColor(), sw.getSwitchables());
			s.setPosition(sw.getPosition());
			entities.put(Byte.valueOf(nextEntityId++), s);
			autoTransforms.put(s, sw.getAutoTransforms());
			for (AutoTransform at : sw.getAutoTransforms())
				at.reset();
		}
		for (RetractablePlatform plat : layout.getDoors())
			plat.reset();
		for (Entity ent : entities.values()) {
			layers.get(ZAxisLayer.MIDGROUND).getDrawables().addAll(ent.getDrawables());
			collidables.addAll(ent.getDrawables());
		}

		layers.get(ZAxisLayer.FOREGROUND).getDrawables().clear();
		for (OverlayInfo ol : layout.getTips())
			layers.get(ZAxisLayer.FOREGROUND).getDrawables().add(new DrawableTexture(ol.getWidth(), ol.getHeight(), ol.getImageName(), ol.getPosition()));

		player.setPosition(layout.getStartPosition());
	}
	
	public void resetLevel(){
		setLayout(layout);
	}

	public Rectangle getCameraBounds() {
		return new Rectangle(-LEFT_WALL_VISIBLE_PIXELS, -FLOOR_VISIBLE_PIXELS, layout.getWidth() + LEFT_WALL_VISIBLE_PIXELS + RIGHT_WALL_VISIBLE_PIXELS, layout.getHeight() + FLOOR_VISIBLE_PIXELS + CEILING_VISIBLE_PIXELS);
	}

	public boolean isCutscene() {
		return layout.isCutscene();
	}

	public SortedMap<Byte, ZAxisLayer> getLayers() {
		return layers;
	}

	public Collection<Entity> getEntities() {
		return entities.values();
	}

	public List<CollidableDrawable> getCollidables() {
		Collections.sort(collidables, new Comparator<CollidableDrawable>() {
			@Override
			public int compare(CollidableDrawable a, CollidableDrawable b) {
				int delta = a.getMovabilityIndex() - b.getMovabilityIndex();
				if (delta == 0)
					delta = Double.compare(a.getPosition().getY(), b.getPosition().getY());
				return delta;
			}
		});
		return collidables;
	}

	public Player getPlayer() {
		return player;
	}

	public double getGravitationalFieldStrength() {
		return layout.getGravitationalFieldStrength();
	}

	public double getTerminalVelocity() {
		return layout.getTerminalVelocity();
	}

	public List<AutoTransform> getAutoTransforms(Entity ent) {
		return autoTransforms.get(ent);
	}
}
