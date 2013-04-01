package amplified;

import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.imageio.ImageIO;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.Rectangle;
import org.lwjgl.util.vector.Matrix4f;
import org.newdawn.slick.Color;

import amplified.gui.GuiButton;
import amplified.gui.GuiButton.ButtonHandler;
import amplified.gui.GuiMainMenu;
import amplified.gui.GuiPauseMenu;
import amplified.map.AbstractCollidable;
import amplified.map.CursorOverlay;
import amplified.map.Drawable;
import amplified.map.FpsOverlay;
import amplified.map.collisions.Polygon;
import amplified.map.state.Camera;
import amplified.map.state.FrameRateState;
import amplified.map.state.Input;
import amplified.map.state.MapState;
import amplified.resources.FontCache;
import amplified.resources.LevelCache;
import amplified.resources.SoundCache;
import amplified.resources.TextureCache;

//TODO:
//Fully fix PolygonCollision.collision when dynamic SAT handling, (tEnterMax <= tDelta)
//Fix choppy player walking while on the ground
public class Game {
	public static enum GameState {
		TITLE_SCREEN, GAME, PAUSE
	}

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
	private final List<Polygon> preCollisionPolygons;
	private final GuiMainMenu titleScreen;
	private final GuiPauseMenu pauseScreen;

	private GameState state;
	private boolean screenshot, close;

	public Game() {
		matrixBuf = LowLevelUtil.createMatrixBuffer();

		input = new Input();
		camera = new Camera(WIDTH, HEIGHT);
		frameRateState = new FrameRateState(new DecimalFormat("0.0"));

		preCollisionPolygons = DEBUG ? new ArrayList<Polygon>() : null;

		FpsOverlay fps = new FpsOverlay(frameRateState, HEIGHT);
		CursorOverlay cursor = new CursorOverlay(input);
		titleScreen = new GuiMainMenu(new Rectangle(0, 0, WIDTH, HEIGHT), getTitleScreenButtons(), input, fps, cursor);
		map = new MapState(camera, preCollisionPolygons, input, fps, cursor);
		pauseScreen = new GuiPauseMenu(getPauseScreenButtons(), map, input);
		state = GameState.TITLE_SCREEN;
	}

	private List<GuiButton> getTitleScreenButtons() {
		List<GuiButton> buttons = new ArrayList<GuiButton>();
		buttons.add(new GuiButton("New Game", new Rectangle((WIDTH - 200) / 2, HEIGHT / 2, 200, 50), new ButtonHandler() {
			public void clicked() {
				newGame();
			}
		}));
		return buttons;
	}

	private List<GuiButton> getPauseScreenButtons() {
		List<GuiButton> buttons = new ArrayList<GuiButton>();
		buttons.add(new GuiButton("New Game", new Rectangle((WIDTH - 200) / 2,365, 200, 50), new ButtonHandler() {
			public void clicked() {
				newGame();
			}
		}));
		buttons.add(new GuiButton("Restart Level", new Rectangle((WIDTH - 200) / 2,305, 200, 50), new ButtonHandler() {
			public void clicked() {
				map.suspend();
				map.resetLevel();
				camera.setLimits(map.getCameraBounds());
				camera.lookAt(map.getPlayer().getPosition());
				input.setCutscene(map.isCutscene());
				map.resume();
				state = GameState.GAME;
			}
		}));
		buttons.add(new GuiButton("Main Menu", new Rectangle((WIDTH - 200) / 2,245, 200, 50), new ButtonHandler() {
			public void clicked() {
				state = GameState.TITLE_SCREEN;
			}
		}));
		buttons.add(new GuiButton("Back to Game", new Rectangle((WIDTH - 200) / 2,185, 200, 50), new ButtonHandler() {
			public void clicked() {
				input.setCutscene(map.isCutscene());
				map.resume();
				state = GameState.GAME;
			}
		}));
		return buttons;
	}

	private void newGame() {
		map.suspend();
		map.setLayout(LevelCache.getLevel("level1"));
		camera.setLimits(map.getCameraBounds());
		camera.lookAt(map.getPlayer().getPosition());
		input.setCutscene(map.isCutscene());
		map.resume();
		state = GameState.GAME;
	}

	public void graphicsInit() throws Exception {
		LowLevelUtil.setUpWindow(FULLSCREEN, WIDTH, HEIGHT, VSYNC);
		LowLevelUtil.setUp2dCanvas(0, 0, 0, WIDTH, HEIGHT);
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
		TextureCache.setTexture("button", LowLevelUtil.loadPng("resources/buttons/buttonRegular"));
		TextureCache.setTexture("buttonHover", LowLevelUtil.loadPng("resources/buttons/buttonHovering"));
		TextureCache.setTexture("buttonPressed", LowLevelUtil.loadPng("resources/buttons/buttonPressed"));
		TextureCache.setTexture("beam", LowLevelUtil.loadPng("resources/beam"));
		TextureCache.setTexture("box", LowLevelUtil.loadPng("resources/crate"));
		TextureCache.setTexture("rect", LowLevelUtil.loadPng("resources/rect"));
		TextureCache.setTexture("door", LowLevelUtil.loadPng("resources/door"));
		TextureCache.setTexture("openDoor", LowLevelUtil.loadPng("resources/opendoor"));
		TextureCache.setTexture("switch", LowLevelUtil.loadPng("resources/switch"));
		TextureCache.setTexture("diamond", LowLevelUtil.loadPng("resources/diamond"));
		TextureCache.setTexture("conveyor", LowLevelUtil.loadPng("resources/conveyor"));

		TextureCache.setTexture("intro1", LowLevelUtil.loadPng("resources/intro1"));
		TextureCache.setTexture("scrollingWindowBg", LowLevelUtil.loadPng("resources/scrollingBg"));
		TextureCache.setTexture("mainBg", LowLevelUtil.loadPng("resources/mainBg"));

		TextureCache.setTexture("jetpackOverlay",LowLevelUtil.loadPng("resources/overlays/overlayJetpack"));
		TextureCache.setTexture("switchOverlay",LowLevelUtil.loadPng("resources/overlays/buttonOverlay"));
		TextureCache.setTexture("growOverlay", LowLevelUtil.loadPng("resources/overlays/growBeamOverlay"));
		TextureCache.setTexture("shrinkOverlay", LowLevelUtil.loadPng("resources/overlays/shrinkBeamOverlay"));
		TextureCache.setTexture("beamOverlay", LowLevelUtil.loadPng("resources/overlays/tractorBeamOverlay"));
		TextureCache.setTexture("rotateOverlay", LowLevelUtil.loadPng("resources/overlays/rotationOverlay"));

		SoundCache.setSound("beam", LowLevelUtil.loadWav("resources/BeamSound"));
		SoundCache.setSound("jetpack", LowLevelUtil.loadWav("resources/Jetpack"));
		SoundCache.setSound("newBGM", LowLevelUtil.loadWav("resources/tsa"));
		SoundCache.setSound("bgm", LowLevelUtil.loadOgg("resources/bgm"));

		FontCache.setFont("fps", LowLevelUtil.loadFont(new Font("Arial", Font.PLAIN, 14)));
		FontCache.setFont("button", LowLevelUtil.loadFont(new Font("Arial", Font.PLAIN, 24)));
		FontCache.setFont("rain", LowLevelUtil.loadFont(new Font("Courier New", Font.PLAIN, 12)));

		LevelCache.setLevel("intro1", LevelCache.loadXml("resources/intro1", WIDTH, HEIGHT));
		LevelCache.setLevel("intro2", LevelCache.loadXml("resources/intro2", WIDTH, HEIGHT));
		LevelCache.setLevel("intro3", LevelCache.loadXml("resources/intro3", WIDTH, HEIGHT));
		LevelCache.setLevel("intro4", LevelCache.loadXml("resources/intro4", WIDTH, HEIGHT));
		LevelCache.setLevel("intro5", LevelCache.loadXml("resources/intro5", WIDTH, HEIGHT));
		LevelCache.setLevel("intro6", LevelCache.loadXml("resources/intro6", WIDTH, HEIGHT));
		LevelCache.setLevel("tutorial", LevelCache.loadXml("resources/tutorial", WIDTH, HEIGHT));
		LevelCache.setLevel("mid1", LevelCache.loadXml("resources/mid1", WIDTH, HEIGHT));
		LevelCache.setLevel("level1", LevelCache.loadXml("resources/level1", WIDTH, HEIGHT));
		LevelCache.setLevel("level2", LevelCache.loadXml("resources/level2", WIDTH, HEIGHT));
		LevelCache.setLevel("level3", LevelCache.loadXml("resources/level3", WIDTH, HEIGHT));
		LevelCache.setLevel("mid2", LevelCache.loadXml("resources/mid2", WIDTH, HEIGHT));
		LevelCache.setLevel("level4", LevelCache.loadXml("resources/level4", WIDTH, HEIGHT));
		LevelCache.setLevel("end1", LevelCache.loadXml("resources/end1", WIDTH, HEIGHT));
		LevelCache.setLevel("debug", LevelCache.loadXml("resources/debug", WIDTH, HEIGHT));
		LevelCache.setLevel("debugCutscene", LevelCache.loadXml("resources/debugCutscene", WIDTH, HEIGHT));

		SoundCache.getSound("newBGM").playAsMusic(1, 1, true);
	}

	public boolean nextFrame() {
		return !LowLevelUtil.windowClosed() && !close;
	}

	public void update(double tDelta) {
		if (DEBUG)
			preCollisionPolygons.clear();

		frameRateState.addFrame();
		if (frameRateState.getElapsedSecondsSinceLastReset() > 1)
			frameRateState.reset();

		input.update();
		if (input.pressedKeys().contains(Keyboard.KEY_F2))
			screenshot = true;

		if (input.pressedKeys().contains(Keyboard.KEY_ESCAPE)) {
			switch (state) {
				case GAME:
					state = GameState.PAUSE;
					input.setCutscene(false);
					map.suspend();
					break;
				case PAUSE:
					state = GameState.GAME;
					input.setCutscene(map.isCutscene());
					map.resume();
					break;
				case TITLE_SCREEN:
					close = true;
					break;
			}
		}

		switch (state) {
			case TITLE_SCREEN:
				titleScreen.update(tDelta);
				break;
			case PAUSE:
				pauseScreen.update(tDelta);
				break;
			case GAME:
				state = map.update(tDelta);
				break;
		}

		LowLevelUtil.advanceAudioFrame();
	}

	private void fillScreen(ScreenFiller screen) {
		for (MapState.ZAxisLayer layer : screen.getLayers().values()) {
			Matrix4f viewMatrix = camera.getViewMatrix(layer.getParallaxFactor());

			for (Drawable drawable : layer.getDrawables()) {
				LowLevelUtil.drawSprite(matrixBuf, viewMatrix, drawable);

				if (DEBUG && drawable instanceof AbstractCollidable)
					LowLevelUtil.drawTransformedWireframe(matrixBuf, viewMatrix, Color.green, ((AbstractCollidable) drawable).getBoundingPolygon().getPolygons());
			}
		}
		if (DEBUG && !preCollisionPolygons.isEmpty())
			LowLevelUtil.drawTransformedWireframe(matrixBuf, camera.getViewMatrix(screen.getLayers().get(MapState.ZAxisLayer.MIDGROUND).getParallaxFactor()), Color.red, preCollisionPolygons.toArray(new Polygon[preCollisionPolygons.size()]));
	}

	public void draw() {
		LowLevelUtil.clearCanvas();
		switch (state) {
			case TITLE_SCREEN:
				titleScreen.drawBackground();
				fillScreen(titleScreen);
				break;
			case PAUSE:
				fillScreen(pauseScreen);
				break;
			default:
				fillScreen(map);
				break;
		}
		LowLevelUtil.flipBackBuffer();
		if (screenshot) {
			screenshot = false;
			String fileName = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss.SSS").format(Calendar.getInstance().getTime()) + ".png";
			try {
				ImageIO.write(LowLevelUtil.pngScreenshot(), "png", new File(fileName));
			} catch (IOException e) {
				System.err.println("Could not save screenshot " + fileName);
				e.printStackTrace();
			}
		}
		LowLevelUtil.waitForNextFrame(TARGET_FPS);
	}

	public void unloadContent() {
		TextureCache.flush();
		SoundCache.flush();
	}

	public void graphicsUninit() {
		LowLevelUtil.releaseWindow();
	}
}
