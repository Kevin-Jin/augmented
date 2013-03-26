package amplified.map.state;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.lwjgl.util.Rectangle;

import amplified.Game;
import amplified.Game.GameState;
import amplified.ScreenFiller;
import amplified.map.CollidableDrawable;
import amplified.map.Drawable;
import amplified.map.DrawableTexture;
import amplified.map.RetractablePlatform;
import amplified.map.collisions.CollisionInformation;
import amplified.map.collisions.CollisionResult;
import amplified.map.collisions.Polygon;
import amplified.map.collisions.PolygonCollision;
import amplified.map.entity.AutoTransform;
import amplified.map.entity.DrawableEntity;
import amplified.map.entity.Entity;
import amplified.map.entity.player.Player;
import amplified.map.entity.props.Box;
import amplified.map.entity.props.ExitDoor;
import amplified.map.entity.props.NBox;
import amplified.map.entity.props.RectangleBox;
import amplified.map.entity.props.Switch;
import amplified.map.physicquantity.Position;
import amplified.resources.LevelCache;
import amplified.resources.LevelLayout;
import amplified.resources.map.BoxSpawnInfo;
import amplified.resources.map.NBoxSpawnInfo;
import amplified.resources.map.OverlayInfo;
import amplified.resources.map.RectangleSpawnInfo;
import amplified.resources.map.SwitchSpawnInfo;

public class MapState extends ScreenFiller {
	private static final int FLOOR_VISIBLE_PIXELS = 20;
	private static final int CEILING_VISIBLE_PIXELS = 20;
	private static final int LEFT_WALL_VISIBLE_PIXELS = 20;
	private static final int RIGHT_WALL_VISIBLE_PIXELS = 20;
	private static final double MAP_CHANGE_DELAY = 0.5; //seconds

	private final Camera camera;
	private final Input input;
	private final List<Polygon> preCollisionPolygons;

	private LevelLayout layout;
	private final Player player;
	private final SortedMap<Byte, Entity> entities;
	private final SortedMap<Byte, ZAxisLayer> layers;
	private final List<CollidableDrawable> collidables;
	private final Map<Entity, List<AutoTransform>> autoTransforms;
	private byte nextEntityId;

	private ExitDoor door;
	private double timeLeft;

	public MapState(Camera camera, List<Polygon> preCollisionPolygons, Input input, Drawable... overlays) {
		this.camera = camera;
		this.input = input;
		this.preCollisionPolygons = preCollisionPolygons;

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
		collidables.clear();
		autoTransforms.clear();
		entities.clear();
		this.layout = layout;
		timeLeft = layout.getExpiration();
		door = (layout.isCutscene()) ? null : new ExitDoor(layout.getEndPosition());

		layers.get(ZAxisLayer.FAR_BACKGROUND).getDrawables().clear();
		if (layout.getOutsideBackground() != null)
			layers.get(ZAxisLayer.FAR_BACKGROUND).getDrawables().add(new DrawableTexture(layout.getOutsideBackground(), new Position(0, 0)));
		layers.get(ZAxisLayer.MAIN_BACKGROUND).getDrawables().clear();
		if (layout.getInsideBackground() != null)
			layers.get(ZAxisLayer.MAIN_BACKGROUND).getDrawables().add(new DrawableTexture(layout.getInsideBackground(), new Position(0, 0)));

		layers.get(ZAxisLayer.MIDGROUND).getDrawables().clear();
		layers.get(ZAxisLayer.MIDGROUND).getDrawables().addAll(layout.getPlatforms());
		collidables.addAll(layout.getPlatforms());

		nextEntityId = 0;
		if (door != null) {
			entities.put(Byte.valueOf(nextEntityId++), door);
			autoTransforms.put(door, layout.getExitAutoTransforms());
		}
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

	public void resetLevel() {
		setLayout(layout);
	}

	public Rectangle getCameraBounds() {
		return new Rectangle(-LEFT_WALL_VISIBLE_PIXELS, -FLOOR_VISIBLE_PIXELS, layout.getWidth() + LEFT_WALL_VISIBLE_PIXELS + RIGHT_WALL_VISIBLE_PIXELS, layout.getHeight() + FLOOR_VISIBLE_PIXELS + CEILING_VISIBLE_PIXELS);
	}

	public boolean isCutscene() {
		return layout.isCutscene();
	}

	public boolean shouldChangeLevel(double tDelta) {
		if (door != null && door.shouldChangeMap()) {
			//if there's also a map expiration and that is shorter than the
			//delay for the door to take effect, then use that instead
			timeLeft = Double.isInfinite(timeLeft) ? MAP_CHANGE_DELAY : Math.min(MAP_CHANGE_DELAY, timeLeft);
			door = null;
			return false;
		}
		return (!Double.isInfinite(timeLeft) && (timeLeft -= tDelta) <= 0);
	}

	public String getNextLevel() {
		return layout.getNextMap();
	}

	@Override
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

	private Map<CollidableDrawable, Set<CollisionInformation>> detectAndHandleCollisions(float tDelta) {
		//map.getCollidables() is sorted by movability ascending, y coordinate descending
		List<CollidableDrawable> collidablesList = getCollidables();
		CollidableDrawable[] collidables = collidablesList.toArray(new CollidableDrawable[collidablesList.size()]);
		Map<CollidableDrawable, Set<CollisionInformation>> log = new HashMap<CollidableDrawable, Set<CollisionInformation>>();
		CollidableDrawable a, b;
		Set<CollisionInformation> aAll, bAll;
		for (int i = 0; i < collidables.length - 1; i++) {
			a = collidables[i];
			if (a.isVisible()) {
				for (int j = i + 1; j < collidables.length; j++) {
					b = collidables[j];
					if (b.isVisible()) {
						CollisionResult result = PolygonCollision.boundingPolygonCollision(a, b, tDelta);
						if (result.collision()) {
							result.getCollisionInformation().setCollidedWith(a);
							b.collision(result.getCollisionInformation(), collidablesList);

							aAll = log.get(a);
							if (aAll == null) {
								aAll = new HashSet<CollisionInformation>();
								log.put(a, aAll);
							}
							bAll = log.get(b);
							if (bAll == null) {
								bAll = new HashSet<CollisionInformation>();
								log.put(b, bAll);
							}
							bAll.add(result.getCollisionInformation());
							aAll.add(result.getCollisionInformation().complement(b));
						}
					}
				}
			}
		}
		return log;
	}

	@Override
	public GameState update(double tDelta) {
		if (shouldChangeLevel(tDelta)){
			String next = getNextLevel();
			if (!next.equalsIgnoreCase("credits")){
				setLayout(LevelCache.getLevel(next));
				camera.setLimits(getCameraBounds());
				camera.lookAt(getPlayer().getPosition());
				input.setCutscene(isCutscene());
				return GameState.GAME;
			} else {
				return GameState.TITLE_SCREEN;
			}
		}

		for (Entity ent : getEntities()) {
			for (AutoTransform at : getAutoTransforms(ent))
				at.transform(ent, tDelta);
			ent.preCollisionsUpdate(tDelta, input, camera, this);
			if (Game.DEBUG)
				for (DrawableEntity d : ent.getDrawables())
					for (Polygon p : d.getBoundingPolygon().getPolygons())
						preCollisionPolygons.add(new Polygon(p));
		}
		Map<CollidableDrawable, Set<CollisionInformation>> collisions = detectAndHandleCollisions((float) tDelta);
		for (Entity ent : getEntities())
			ent.postCollisionsUpdate(tDelta, input, collisions, camera);
		return GameState.GAME;
	}
}
