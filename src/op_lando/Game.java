package op_lando;

import java.awt.Font;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import op_lando.map.AbstractCollidable;
import op_lando.map.CollidableDrawable;
import op_lando.map.CursorOverlay;
import op_lando.map.Drawable;
import op_lando.map.FpsOverlay;
import op_lando.map.collisions.CollisionInformation;
import op_lando.map.collisions.CollisionResult;
import op_lando.map.collisions.PolygonCollision;
import op_lando.map.entity.Entity;
import op_lando.map.state.Camera;
import op_lando.map.state.FrameRateState;
import op_lando.map.state.Input;
import op_lando.map.state.MapState;
import op_lando.resources.FontCache;
import op_lando.resources.LevelCache;
import op_lando.resources.SoundCache;
import op_lando.resources.TextureCache;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Matrix4f;

public class Game {
	public static final boolean DEBUG = true;
	private static final boolean FULLSCREEN = false;
	private static final boolean VSYNC = true;
	private static final int TARGET_FPS = 1200; //VSYNC must be false for this to be higher than the monitor refresh rate
	private static final int WIDTH = 800, HEIGHT = 600;

	private final FloatBuffer matrixBuf;

	private final Input input;
	private final Camera camera;
	private final FrameRateState frameRateState;
	private final MapState map;

	public Game() {
		matrixBuf = LowLevelUtil.createMatrixBuffer();

		input = new Input();
		camera = new Camera(WIDTH, HEIGHT);
		frameRateState = new FrameRateState(new DecimalFormat("0.0"));

		map = new MapState(new FpsOverlay(frameRateState, HEIGHT), new CursorOverlay(input));
	}

	public void graphicsInit() throws Exception {
		LowLevelUtil.setUpWindow(FULLSCREEN, WIDTH, HEIGHT, VSYNC);
		LowLevelUtil.setUp2dCanvas(100, 149, 237, WIDTH, HEIGHT);
	}

	public void loadContent() throws IOException {
		TextureCache.setTexture("mouse", LowLevelUtil.loadPng("resources/cursor2"));
		TextureCache.setTexture("spacer", LowLevelUtil.loadGif("resources/spacer"));
		TextureCache.setTexture("platform", LowLevelUtil.loadPng("resources/platform"));
		TextureCache.setTexture("body", LowLevelUtil.loadPng("resources/body"));
		TextureCache.setTexture("arm", LowLevelUtil.loadPng("resources/arm"));
		TextureCache.setTexture("legsRest", LowLevelUtil.loadPng("resources/anim_legs/legsRest"));
		TextureCache.setTexture("legs1", LowLevelUtil.loadPng("resources/anim_legs/legs1"));
		TextureCache.setTexture("legs2", LowLevelUtil.loadPng("resources/anim_legs/legs2"));
		TextureCache.setTexture("legs3", LowLevelUtil.loadPng("resources/anim_legs/legs3"));
		TextureCache.setTexture("legs4", LowLevelUtil.loadPng("resources/anim_legs/legs4"));
		TextureCache.setTexture("legs5", LowLevelUtil.loadPng("resources/anim_legs/legs5"));
		TextureCache.setTexture("flame1", LowLevelUtil.loadPng("resources/anim_flame/flame1"));
		TextureCache.setTexture("flame2", LowLevelUtil.loadPng("resources/anim_flame/flame2"));
		TextureCache.setTexture("flame3", LowLevelUtil.loadPng("resources/anim_flame/flame3"));
		TextureCache.setTexture("flame4", LowLevelUtil.loadPng("resources/anim_flame/flame4"));
		TextureCache.setTexture("beam", LowLevelUtil.loadPng("resources/beam"));
		TextureCache.setTexture("box", LowLevelUtil.loadPng("resources/crate"));
		TextureCache.setTexture("scrollingWindowBg", LowLevelUtil.loadPng("resources/scrollingBg"));
		TextureCache.setTexture("mainBg", LowLevelUtil.loadPng("resources/mainBg"));

		SoundCache.setSound("beam", LowLevelUtil.loadWav("resources/BeamSound"));
		SoundCache.setSound("jetpack", LowLevelUtil.loadWav("resources/Jetpack"));
		SoundCache.setSound("bgm", LowLevelUtil.loadOgg("resources/bgm"));

		FontCache.setFont("fps", LowLevelUtil.loadFont(new Font("Arial", Font.PLAIN, 14)));

		LevelCache.initialize();

		SoundCache.getSound("bgm").playAsMusic(0.5f, 1, true);
		map.setLayout(LevelCache.getLevel("tutorial"));
		camera.setLimits(map.getCameraBounds());
	}

	public boolean nextFrame() {
		return !LowLevelUtil.windowClosed() && !input.releasedKeys().contains(Keyboard.KEY_ESCAPE);
	}

	private Map<CollidableDrawable, Set<CollisionInformation>> detectAndHandleCollisions() {
		//map.getCollidables() is sorted by movability ascending, y coordinate descending
		List<CollidableDrawable> collidablesList = map.getCollidables();
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
						CollisionResult result = PolygonCollision.boundingPolygonCollision(a.getBoundingPolygon(), b.getBoundingPolygon());
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

	public void update(double tDelta) {
		frameRateState.addFrame();
		if (frameRateState.getElapsedSecondsSinceLastReset() > 1)
			frameRateState.reset();

		input.update();
		for (Entity ent : map.getEntities())
			ent.preCollisionsUpdate(tDelta, input, camera, map);
		Map<CollidableDrawable, Set<CollisionInformation>> collisions = detectAndHandleCollisions();
		for (Entity ent : map.getEntities())
			ent.postCollisionsUpdate(tDelta, input, collisions, camera);

		LowLevelUtil.advanceAudioFrame();
	}

	private void drawGame() {
		for (MapState.ZAxisLayer layer : map.getLayers().values()) {
			Matrix4f viewMatrix = camera.getViewMatrix(layer.getParallaxFactor());

			for (Drawable drawable : layer.getDrawables()) {
				LowLevelUtil.drawSprite(matrixBuf, viewMatrix, drawable);

				if (DEBUG && drawable instanceof AbstractCollidable)
					LowLevelUtil.drawTransformedWireframe(matrixBuf, viewMatrix, (AbstractCollidable) drawable);
			}
		}
	}

	public void draw() {
		LowLevelUtil.clearCanvas();
		drawGame();
		LowLevelUtil.flipBackBuffer(TARGET_FPS);
	}

	public void unloadContent() {
		TextureCache.flush();
		SoundCache.flush();
	}

	public void graphicsUninit() {
		LowLevelUtil.releaseWindow();
	}
}
