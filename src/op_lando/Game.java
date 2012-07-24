package op_lando;

import java.awt.Font;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.text.DecimalFormat;

import op_lando.map.CollidableDrawable;
import op_lando.map.CursorEntity;
import op_lando.map.Drawable;
import op_lando.map.DrawableOverlayText;
import op_lando.map.FpsEntity;
import op_lando.map.collisions.Polygon;
import op_lando.map.entity.Entity;
import op_lando.map.physicquantity.Position;
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

public class Game {
	private static final boolean DEBUG = true;
	private static final boolean FULLSCREEN = false;
	private static final boolean VSYNC = true;
	public static final int TARGET_FPS = 1200; //VSYNC must be false for this to have an effect
	private static final int WIDTH = 800, HEIGHT = 600;

	private final Input input;
	private final Camera camera;
	private final FrameRateState frameRateState;
	private final MapState map;

	public Game() {
		input = new Input();
		camera = new Camera(WIDTH, HEIGHT);
		frameRateState = new FrameRateState(new DecimalFormat("0.0"));

		map = new MapState(new FpsEntity(frameRateState, HEIGHT), new CursorEntity(input));
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

		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_CULL_FACE);

		GL11.glClearColor(100f / 255, 149f / 255, 237f / 255, 255f / 255); //cornflower blue
		GL11.glEnable(GL11.GL_BLEND); //enable alpha blending
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		GL11.glViewport(0, 0, WIDTH, HEIGHT);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);

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

		SoundCache.setSound("beam", loadWav("resources/BeamSound"));
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

	public void update(double tDelta) {
		frameRateState.addFrame();
		if (frameRateState.getElapsedSecondsSinceLastReset() > 1)
			frameRateState.reset();

		input.update();
		if (input.pressedButtons().contains(Integer.valueOf(Input.MOUSE_LEFT_CLICK)))
			SoundCache.getSound("beam").playAsSoundEffect(1, 1, true);
		if (input.releasedButtons().contains(Integer.valueOf(Input.MOUSE_LEFT_CLICK)))
			SoundCache.getSound("beam").stop();
		map.getPlayer().lookAt(camera.mouseToWorld(input.cursorPosition().getX(), input.cursorPosition().getY()));
		for (Entity ent : map.getEntities())
			ent.update(tDelta);
		camera.lookAt(new Position(0, 0));

		AudioLoader.update();
	}

	private void drawGame() {
		FloatBuffer buf = BufferUtils.createFloatBuffer(16);

		// draw world
		for (MapState.ZAxisLayer layer : map.getLayers().values()) {
			Matrix4f viewMatrix = camera.getViewMatrix(layer.getParallaxFactor());

			for (Drawable ent : layer.getDrawables()) {
				Texture texture = ent.getTexture();

				buf.clear();
				Matrix4f.mul(viewMatrix, ent.getTransformationMatrix(), null).store(buf);
				buf.flip();

				ent.getTint().bind();
				texture.bind();

				GL11.glPushMatrix();
				{
					GL11.glLoadMatrix(buf);
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

					if (DEBUG && ent instanceof CollidableDrawable) {
						GL11.glDisable(GL11.GL_TEXTURE_2D);
						Color.green.bind();
						for (Polygon p : ((CollidableDrawable) ent).getUntransformedBoundingPolygon().getPolygons()) {
							GL11.glBegin(GL11.GL_LINE_LOOP);
							Vector2f[] vertices = p.getVertices();
							for (int i = 0; i < vertices.length; i++)
								GL11.glVertex2f(vertices[i].getX(), vertices[i].getY());
							GL11.glEnd();
						}
					}

					DrawableOverlayText caption = ent.getCaption();
					if (caption != null) {
						Point pos = caption.getRelativePosition();
						caption.getFont().drawString(pos.getX(), pos.getY(), caption.getMessage(), ent.getTint());
					}
				}
				GL11.glPopMatrix();
			}
		}
	}

	public void draw() {
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
		drawGame();
	}
}
