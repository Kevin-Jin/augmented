package op_lando;

import java.io.IOException;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.Point;
import org.newdawn.slick.openal.Audio;
import org.newdawn.slick.openal.AudioLoader;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

public class Launch {
	private static final int WIDTH = 800, HEIGHT = 600;

	private final Input input;

	public Launch() {
		input = new Input();
	}

	public void glInit() throws LWJGLException {
		Display.setDisplayMode(new DisplayMode(WIDTH, HEIGHT));
		Display.create();
		Display.setVSyncEnabled(true);
		//Mouse.setGrabbed(true);

		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_CULL_FACE);

		GL11.glClearColor(100f / 255, 149f / 255, 237f / 255, 255f / 255); //cornflower blue
		GL11.glEnable(GL11.GL_BLEND); //enable alpha blending
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		GL11.glViewport(0, 0, WIDTH, HEIGHT);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);

		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GL11.glOrtho(0, WIDTH, HEIGHT, 0, -1, 1);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
	}

	private Texture loadPng(String name) throws IOException {
		return TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream(name + ".png"));
	}

	private Audio loadWav(String name) throws IOException {
		return AudioLoader.getAudio("WAV", ResourceLoader.getResourceAsStream(name + ".wav"));
	}

	public void loadContent() throws IOException {
		TextureCache.setTexture("mouse", loadPng("resources/cursor2"));

		SoundCache.setSound("beam", loadWav("resources/BeamSound"));
	}

	public void unloadContent() {
		TextureCache.flush();
		SoundCache.flush();
	}

	private void drawMouse() {
		Texture texture = TextureCache.getTexture("mouse");
		int width = texture.getImageWidth();
		int height = texture.getImageHeight();
		texture.bind();
		Point pt = input.cursorPosition();
		int x = pt.getX() - width / 2;
		int y = HEIGHT - pt.getY() - height / 2;
		GL11.glBegin(GL11.GL_QUADS);
		{
			GL11.glTexCoord2f(0, 0);
			GL11.glVertex2f(x, y);
			GL11.glTexCoord2f(texture.getWidth(), 0);
			GL11.glVertex2f(x + width, y);
			GL11.glTexCoord2f(texture.getWidth(), texture.getHeight());
			GL11.glVertex2f(x + width, y + height);
			GL11.glTexCoord2f(0, texture.getHeight());
			GL11.glVertex2f(x, y + height);
		}
		GL11.glEnd();
	}

	public void update(double tDelta) {
		input.update();
		if (input.pressedButtons().contains(Integer.valueOf(Input.MOUSE_LEFT_CLICK)))
			SoundCache.getSound("beam").playAsSoundEffect(1, 1, true);
		if (input.releasedButtons().contains(Integer.valueOf(Input.MOUSE_LEFT_CLICK)))
			SoundCache.getSound("beam").stop();
	}

	public void draw() {
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
		drawMouse();
	}

	public static void main(String[] args) throws IOException, LWJGLException {
		Launch g = new Launch();
		g.glInit();
		g.loadContent();
		long last = System.currentTimeMillis(), now;
		while (!Display.isCloseRequested()) {
			now = System.currentTimeMillis();
			g.update((last - now) / 1000d);
			last = now;

			g.draw();

			Display.update();
			Display.sync(60);
		}
		g.unloadContent();
		Display.destroy();
	}
}
