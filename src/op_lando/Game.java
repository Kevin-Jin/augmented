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
import op_lando.map.DrawableOverlayText;
import op_lando.map.FpsOverlay;
import op_lando.map.collisions.CollisionInformation;
import op_lando.map.collisions.CollisionResult;
import op_lando.map.collisions.Polygon;
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

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.Point;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.newdawn.slick.Color;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.openal.Audio;
import org.newdawn.slick.openal.AudioLoader;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

@SuppressWarnings("deprecation")
public class Game {
	public static final boolean DEBUG = true;
	private static final boolean FULLSCREEN = false;
	private static final boolean VSYNC = true;
	public static final int TARGET_FPS = 1200; //VSYNC must be false for this to be higher than the monitor refresh rate
	private static final int WIDTH = 800, HEIGHT = 600;

	private final FloatBuffer matrixBuf;

	private final Input input;
	private final Camera camera;
	private final FrameRateState frameRateState;
	private final MapState map;

	public Game() {
		matrixBuf = BufferUtils.createFloatBuffer(16);

		input = new Input();
		camera = new Camera(WIDTH, HEIGHT);
		frameRateState = new FrameRateState(new DecimalFormat("0.0"));

		map = new MapState(new FpsOverlay(frameRateState, HEIGHT), new CursorOverlay(input));
	}

	public void glInit() throws LWJGLException {
		DisplayMode[] modes = Display.getAvailableDisplayModes();
		DisplayMode mode = null;
		if (FULLSCREEN) {
			int freq = Display.getDesktopDisplayMode().getFrequency();
			int colorDepth = Display.getDesktopDisplayMode().getBitsPerPixel();
			for (int i = 0; i < modes.length && mode == null; i++) {
				mode = modes[i];
				if (mode.getWidth() == WIDTH && mode.getHeight() == HEIGHT && mode.getFrequency() == freq && mode.getBitsPerPixel() == colorDepth)
					Display.setFullscreen(true);
				else
					mode = null;
			}
		}
		if (mode == null)
			mode = new DisplayMode(WIDTH, HEIGHT);
		Display.setDisplayMode(mode);
		Display.create();
		Display.setVSyncEnabled(VSYNC);

		Mouse.setGrabbed(true);

		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glClearColor(100f / 255, 149f / 255, 237f / 255, 255f / 255); //cornflower blue
		GL11.glEnable(GL11.GL_BLEND); //enable alpha blending
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glViewport(0, 0, WIDTH, HEIGHT);

		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GL11.glOrtho(0, WIDTH, 0, HEIGHT, -1, 1);

		GL11.glMatrixMode(GL11.GL_MODELVIEW);
	}

	private Texture loadPng(String name) throws IOException {
		return TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream(name + ".png"));
	}

	private Texture loadGif(String name) throws IOException {
		return TextureLoader.getTexture("GIF", ResourceLoader.getResourceAsStream(name + ".gif"));
	}

	private Audio loadWav(String name) throws IOException {
		return AudioLoader.getAudio("WAV", ResourceLoader.getResourceAsStream(name + ".wav"));
	}

	private Audio loadOgg(String name) throws IOException {
		return AudioLoader.getAudio("OGG", ResourceLoader.getResourceAsStream(name + ".ogg"));
	}

	public void loadContent() throws IOException {
		TextureCache.setTexture("mouse", loadPng("resources/cursor2"));
		TextureCache.setTexture("spacer", loadGif("resources/spacer"));
		TextureCache.setTexture("platform", loadPng("resources/platform"));
		TextureCache.setTexture("body", loadPng("resources/body"));
		TextureCache.setTexture("arm", loadPng("resources/arm"));
		TextureCache.setTexture("legsRest", loadPng("resources/anim_legs/legsRest"));
		TextureCache.setTexture("legs1", loadPng("resources/anim_legs/legs1"));
		TextureCache.setTexture("legs2", loadPng("resources/anim_legs/legs2"));
		TextureCache.setTexture("legs3", loadPng("resources/anim_legs/legs3"));
		TextureCache.setTexture("legs4", loadPng("resources/anim_legs/legs4"));
		TextureCache.setTexture("legs5", loadPng("resources/anim_legs/legs5"));
		TextureCache.setTexture("flame1", loadPng("resources/anim_flame/flame1"));
		TextureCache.setTexture("flame2", loadPng("resources/anim_flame/flame2"));
		TextureCache.setTexture("flame3", loadPng("resources/anim_flame/flame3"));
		TextureCache.setTexture("flame4", loadPng("resources/anim_flame/flame4"));
		TextureCache.setTexture("beam", loadPng("resources/beam"));
		TextureCache.setTexture("box", loadPng("resources/crate"));
		TextureCache.setTexture("scrollingWindowBg", loadPng("resources/scrollingBg"));
		TextureCache.setTexture("mainBg", loadPng("resources/mainBg"));

		SoundCache.setSound("beam", loadWav("resources/BeamSound"));
		SoundCache.setSound("jetpack", loadWav("resources/Jetpack"));
		SoundCache.setSound("bgm", loadOgg("resources/bgm"));

		FontCache.setFont("fps", new TrueTypeFont(new Font("Arial", Font.PLAIN, 14), true));

		LevelCache.initialize();

		SoundCache.getSound("bgm").playAsMusic(0.5f, 1, true);
		map.setLayout(LevelCache.getLevel("tutorial"));
		camera.setLimits(map.getCameraBounds());
	}

	public void unloadContent() {
		TextureCache.flush();
		SoundCache.flush();
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

		AudioLoader.update();
	}

	private void drawGame() {
		// draw world
		for (MapState.ZAxisLayer layer : map.getLayers().values()) {
			Matrix4f viewMatrix = camera.getViewMatrix(layer.getParallaxFactor());

			for (Drawable drawable : layer.getDrawables()) {
				Texture texture = drawable.getTexture();

				matrixBuf.clear();
				//multiply view matrix and world matrix to get modelview matrix
				Matrix4f.mul(viewMatrix, drawable.getWorldMatrix(), null).store(matrixBuf);
				matrixBuf.flip();

				drawable.getTint().bind();
				texture.bind();

				GL11.glPushMatrix();
				{
					GL11.glLoadMatrix(matrixBuf);
					GL11.glBegin(GL11.GL_QUADS);
					{
						GL11.glTexCoord2f(0, 0);
						GL11.glVertex2f(0, 0);
						GL11.glTexCoord2f(texture.getWidth(), 0);
						GL11.glVertex2f(texture.getImageWidth(), 0);
						GL11.glTexCoord2f(texture.getWidth(), texture.getHeight());
						GL11.glVertex2f(texture.getImageWidth(), texture.getImageHeight());
						GL11.glTexCoord2f(0, texture.getHeight());
						GL11.glVertex2f(0, texture.getImageHeight());
					}
					GL11.glEnd();

					DrawableOverlayText caption = drawable.getCaption();
					if (caption != null) {
						Point pos = caption.getRelativePosition();
						caption.getFont().drawString(pos.getX(), pos.getY(), caption.getMessage(), drawable.getTint());
					}
				}
				GL11.glPopMatrix();

				if (DEBUG && drawable instanceof AbstractCollidable) {
					matrixBuf.clear();
					viewMatrix.store(matrixBuf);
					matrixBuf.flip();
					GL11.glPushMatrix();
					{
						GL11.glLoadMatrix(matrixBuf);
						GL11.glDisable(GL11.GL_TEXTURE_2D);
						Color.green.bind();
						for (Polygon p : ((AbstractCollidable) drawable).getBoundingPolygon().getPolygons()) {
							GL11.glBegin(GL11.GL_LINE_LOOP);
							Vector2f[] vertices = p.getVertices();
							for (int i = 0; i < vertices.length; i++)
								GL11.glVertex2f(vertices[i].getX(), vertices[i].getY());
							GL11.glEnd();
						}
					}
				}
			}
		}
	}

	public void draw() {
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
		drawGame();
	}
}
