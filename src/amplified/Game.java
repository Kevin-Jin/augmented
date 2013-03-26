package amplified;

import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.Rectangle;
import org.lwjgl.util.vector.Matrix4f;
import org.newdawn.slick.Color;

import amplified.gui.GuiButton;
import amplified.gui.GuiButton.ButtonHandler;
import amplified.gui.GuiMainMenu;
import amplified.gui.GuiScreen;
import amplified.map.AbstractCollidable;
import amplified.map.CollidableDrawable;
import amplified.map.CursorOverlay;
import amplified.map.Drawable;
import amplified.map.FpsOverlay;
import amplified.map.collisions.CollisionInformation;
import amplified.map.collisions.CollisionResult;
import amplified.map.collisions.Polygon;
import amplified.map.collisions.PolygonCollision;
import amplified.map.entity.AutoTransform;
import amplified.map.entity.DrawableEntity;
import amplified.map.entity.Entity;
import amplified.map.state.Camera;
import amplified.map.state.FrameRateState;
import amplified.map.state.Input;
import amplified.map.state.MapState;
import amplified.resources.FontCache;
import amplified.resources.LevelCache;
import amplified.resources.SoundCache;
import amplified.resources.TextureCache;

//TODO:
//Implement AutoTransform.Scale
//Fully fix PolygonCollision.collision when (tEnterMax <= tDelta)
//Fix box being stuck to platform when dragged up, even when they no longer collide?
public class Game {

	public static enum GameState
	{
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
	private List<Polygon> preCollisionPolygons;
	private final GuiScreen titleScreen, pauseScreen;

	private GameState state;
	private boolean screenshot, close;

	public Game() {
		matrixBuf = LowLevelUtil.createMatrixBuffer();

		input = new Input();
		camera = new Camera(WIDTH, HEIGHT);
		frameRateState = new FrameRateState(new DecimalFormat("0.0"));

		titleScreen = new GuiMainMenu(new Rectangle(0,0,WIDTH,HEIGHT));
		pauseScreen = new GuiScreen(new Rectangle(0,0,WIDTH,HEIGHT));

		map = new MapState(new FpsOverlay(frameRateState, HEIGHT), new CursorOverlay(input));
		state = GameState.TITLE_SCREEN;

		initGuis();
	}
	private void initGuis(){
		titleScreen.getButtons().add(new GuiButton("New Game", new Rectangle((WIDTH - 200) / 2, HEIGHT / 2, 200, 50), new ButtonHandler()
		{
			public void clicked(){
				newGame();
			}
		}));


		pauseScreen.getButtons().add(new GuiButton("New Game", new Rectangle((WIDTH - 200) / 2,50, 200, 50), new ButtonHandler()
		{
			public void clicked(){
				newGame();
			}
		}));
		pauseScreen.getButtons().add(new GuiButton("Restart Level", new Rectangle((WIDTH - 200) / 2,110, 200, 50), new ButtonHandler()
		{
			public void clicked(){
				map.resetLevel();
				camera.setLimits(map.getCameraBounds());
				camera.lookAt(map.getPlayer().getPosition());
				input.setCutscene(map.isCutscene());
				state = GameState.GAME;
			}
		}));
		pauseScreen.getButtons().add(new GuiButton("Main Menu", new Rectangle((WIDTH - 200) / 2,170, 200, 50), new ButtonHandler()
		{
			public void clicked(){
				state = GameState.TITLE_SCREEN;
			}
		}));
		pauseScreen.getButtons().add(new GuiButton("Back to Game", new Rectangle((WIDTH - 200) / 2,230, 200, 50), new ButtonHandler()
		{
			public void clicked(){
				input.setCutscene(map.isCutscene());
				state = GameState.GAME;
			}
		}));
	}

	private void newGame(){
		map.setLayout(LevelCache.getLevel("debug"));
		state = GameState.GAME;
		camera.setLimits(map.getCameraBounds());
		camera.lookAt(map.getPlayer().getPosition());
		input.setCutscene(map.isCutscene());
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
		TextureCache.setTexture("button", LowLevelUtil.loadPng("resources/buttons/buttonRegular"));
		TextureCache.setTexture("buttonHover", LowLevelUtil.loadPng("resources/buttons/buttonHovering"));
		TextureCache.setTexture("buttonPressed", LowLevelUtil.loadPng("resources/buttons/buttonPressed"));
		TextureCache.setTexture("beam", LowLevelUtil.loadPng("resources/beam"));
		TextureCache.setTexture("box", LowLevelUtil.loadPng("resources/crate"));
		TextureCache.setTexture("rect", LowLevelUtil.loadPng("resources/rect"));

		TextureCache.setTexture("scrollingWindowBg", LowLevelUtil.loadPng("resources/scrollingBg"));
		TextureCache.setTexture("mainBg", LowLevelUtil.loadPng("resources/mainBg"));
		TextureCache.setTexture("door", LowLevelUtil.loadPng("resources/door"));
		TextureCache.setTexture("openDoor", LowLevelUtil.loadPng("resources/opendoor"));
		TextureCache.setTexture("switch", LowLevelUtil.loadPng("resources/switch"));

		TextureCache.setTexture("jetpackOverlay",LowLevelUtil.loadPng("resources/overlays/overlayJetpack"));
		TextureCache.setTexture("switchOverlay",LowLevelUtil.loadPng("resources/overlays/buttonOverlay"));
		TextureCache.setTexture("growOverlay", LowLevelUtil.loadPng("resources/overlays/growBeamOverlay"));
		TextureCache.setTexture("shrinkOverlay", LowLevelUtil.loadPng("resources/overlays/shrinkBeamOverlay"));
		TextureCache.setTexture("beamOverlay", LowLevelUtil.loadPng("resources/overlays/tractorBeamOverlay"));
		TextureCache.setTexture("rotateOverlay", LowLevelUtil.loadPng("resources/overlays/rotationOverlay"));

		SoundCache.setSound("beam", LowLevelUtil.loadWav("resources/BeamSound"));
		SoundCache.setSound("jetpack", LowLevelUtil.loadWav("resources/Jetpack"));
		SoundCache.setSound("bgm", LowLevelUtil.loadOgg("resources/bgm"));

		FontCache.setFont("fps", LowLevelUtil.loadFont(new Font("Arial", Font.PLAIN, 14)));
		FontCache.setFont("button",LowLevelUtil.loadFont(new Font("Arial", Font.BOLD, 16)));

		LevelCache.setLevel("intro1", LevelCache.loadXml("resources/intro1", WIDTH, HEIGHT));
		LevelCache.setLevel("intro2", LevelCache.loadXml("resources/intro2", WIDTH, HEIGHT));
		LevelCache.setLevel("intro3", LevelCache.loadXml("resources/intro3", WIDTH, HEIGHT));
		LevelCache.setLevel("tutorial", LevelCache.loadXml("resources/tutorial", WIDTH, HEIGHT));
		LevelCache.setLevel("mid1", LevelCache.loadXml("resources/mid1", WIDTH, HEIGHT));
		LevelCache.setLevel("level1", LevelCache.loadXml("resources/level1", WIDTH, HEIGHT));
		LevelCache.setLevel("level2", LevelCache.loadXml("resources/level2", WIDTH, HEIGHT));
		LevelCache.setLevel("level3", LevelCache.loadXml("resources/level3", WIDTH, HEIGHT));
		LevelCache.setLevel("mid2", LevelCache.loadXml("resources/mid2", WIDTH, HEIGHT));
		LevelCache.setLevel("level4", LevelCache.loadXml("resources/level4", WIDTH, HEIGHT));
		LevelCache.setLevel("end1", LevelCache.loadXml("resources/end1", WIDTH, HEIGHT));
		LevelCache.setLevel("debug", LevelCache.loadXml("resources/debugCutscene", WIDTH, HEIGHT));

		SoundCache.getSound("bgm").playAsMusic(0.5f, 1, true);
	}

	public boolean nextFrame() {
		return !LowLevelUtil.windowClosed() && !close;
	}

	private Map<CollidableDrawable, Set<CollisionInformation>> detectAndHandleCollisions(float tDelta) {
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

	public void update(double tDelta) {
		frameRateState.addFrame();
		if (frameRateState.getElapsedSecondsSinceLastReset() > 1)
			frameRateState.reset();

		input.update();
		if (input.pressedKeys().contains(Keyboard.KEY_F2)){
			screenshot = true;
		}
		
		if (input.pressedKeys().contains(Keyboard.KEY_ESCAPE)){
			switch (state)
			{
			case GAME:
				state = GameState.PAUSE;
				input.setCutscene(false);
				break;
			case PAUSE:
				state = GameState.GAME;
				input.setCutscene(map.isCutscene());
				break;
			case TITLE_SCREEN:
				close = true;
				break;
			}
		}
		
		switch(state){
		case TITLE_SCREEN:
			titleScreen.updateState(tDelta, input);
			break;
		case PAUSE:
			pauseScreen.updateState(tDelta, input);
			break;
		case GAME:
			updateGame(tDelta);
			break;
		}

		LowLevelUtil.advanceAudioFrame();
	}
	private void updateGame(double tDelta){
		if (map.shouldChangeLevel(tDelta)){
			String next = map.getNextLevel();
			if (!next.equalsIgnoreCase("credits")){
				map.setLayout(LevelCache.getLevel(next));
				camera.setLimits(map.getCameraBounds());
				camera.lookAt(map.getPlayer().getPosition());
				input.setCutscene(map.isCutscene());
			}
			else
				state = GameState.TITLE_SCREEN;
			return;
		}
			
		
		if (DEBUG)
			preCollisionPolygons = new ArrayList<Polygon>();
		for (Entity ent : map.getEntities()) {
			for (AutoTransform at : map.getAutoTransforms(ent))
				at.transform(ent, tDelta);
			ent.preCollisionsUpdate(tDelta, input, camera, map);
			if (DEBUG)
				for (DrawableEntity d : ent.getDrawables())
					for (Polygon p : d.getBoundingPolygon().getPolygons())
						preCollisionPolygons.add(new Polygon(p));
		}
		Map<CollidableDrawable, Set<CollisionInformation>> collisions = detectAndHandleCollisions((float) tDelta);
		for (Entity ent : map.getEntities())
			ent.postCollisionsUpdate(tDelta, input, collisions, camera);
	}


	private void drawGame() {
		for (MapState.ZAxisLayer layer : map.getLayers().values()) {
			Matrix4f viewMatrix = camera.getViewMatrix(layer.getParallaxFactor());

			for (Drawable drawable : layer.getDrawables()) {
				LowLevelUtil.drawSprite(matrixBuf, viewMatrix, drawable);

				if (DEBUG && drawable instanceof AbstractCollidable)
					LowLevelUtil.drawTransformedWireframe(matrixBuf, viewMatrix, Color.green, ((AbstractCollidable) drawable).getBoundingPolygon().getPolygons());
			}
		}
		if (DEBUG && preCollisionPolygons != null)
			LowLevelUtil.drawTransformedWireframe(matrixBuf, camera.getViewMatrix(map.getLayers().get(MapState.ZAxisLayer.MIDGROUND).getParallaxFactor()), Color.red, preCollisionPolygons.toArray(new Polygon[preCollisionPolygons.size()]));
	}

	private void drawOverlays(){
		MapState.ZAxisLayer layer = map.getLayers().get(MapState.ZAxisLayer.OVERLAY);
		Matrix4f viewMatrix = camera.getViewMatrix(layer.getParallaxFactor());
		for (Drawable drawable : layer.getDrawables()) {
			LowLevelUtil.drawSprite(matrixBuf, viewMatrix, drawable);
		}
	}

	public void draw() {
		LowLevelUtil.clearCanvas();
		switch(state){
		case TITLE_SCREEN:
			titleScreen.draw();
			drawOverlays();
			break;
		default:
			drawGame();
			if (state == GameState.PAUSE){
				pauseScreen.draw();
				drawOverlays();
			}
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
