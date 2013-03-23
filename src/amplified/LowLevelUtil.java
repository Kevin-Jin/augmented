package amplified;

import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.input.Mouse;
import org.lwjgl.openal.AL;
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
import org.newdawn.slick.opengl.TextureImpl;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

import amplified.map.Drawable;
import amplified.map.DrawableOverlayText;
import amplified.map.collisions.Polygon;

@SuppressWarnings("deprecation")
public class LowLevelUtil {
	public static FloatBuffer createMatrixBuffer() {
		return BufferUtils.createFloatBuffer(16);
	}

	public static void setUpWindow(boolean fullScreen, int width, int height, boolean vsync) throws Exception {
		DisplayMode mode = null;
		if (fullScreen) {
			DisplayMode[] modes = Display.getAvailableDisplayModes();
			int freq = Display.getDesktopDisplayMode().getFrequency();
			int colorDepth = Display.getDesktopDisplayMode().getBitsPerPixel();
			for (int i = 0; i < modes.length && mode == null; i++) {
				mode = modes[i];
				if (mode.getWidth() == width && mode.getHeight() == height && mode.getFrequency() == freq && mode.getBitsPerPixel() == colorDepth)
					Display.setFullscreen(true);
				else
					mode = null;
			}
		}
		if (mode == null)
			mode = new DisplayMode(width, height);
		Display.setDisplayMode(mode);
		Display.create();
		Display.setVSyncEnabled(vsync);

		Mouse.setGrabbed(true); //allows cursor to move outside of desktop resolution, which makes beam control less awkward
	}

	public static void setUp2dCanvas(int clearR, int clearG, int clearB, int width, int height) {
		GL11.glDisable(GL11.GL_CULL_FACE); //prevents flipped textures becoming invisible - should be disabled by default, but just in case
		GL11.glClearColor(clearR / 255f, clearG / 255f, clearB / 255f, 255 / 255f);
		GL11.glEnable(GL11.GL_BLEND); //enable alpha blending
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glViewport(0, 0, width, height);

		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GL11.glOrtho(0, width, 0, height, -1, 1); //sets origin to bottom left corner of screen - beware that textures still have to be flipped vertically

		GL11.glMatrixMode(GL11.GL_MODELVIEW); //we never change back to projection matrix, so switch matrix mode here instead of in drawSprite for every Drawable
	}

	public static Texture loadPng(String name) throws IOException {
		return TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream(name + ".png"));
	}

	public static Texture loadGif(String name) throws IOException {
		return TextureLoader.getTexture("GIF", ResourceLoader.getResourceAsStream(name + ".gif"));
	}

	public static Audio loadWav(String name) throws IOException {
		return AudioLoader.getAudio("WAV", ResourceLoader.getResourceAsStream(name + ".wav"));
	}

	public static Audio loadOgg(String name) throws IOException {
		return AudioLoader.getAudio("OGG", ResourceLoader.getResourceAsStream(name + ".ogg"));
	}

	public static TrueTypeFont loadFont(Font awtFont) {
		return new TrueTypeFont(awtFont, true);
	}

	public static boolean windowClosed() {
		return Display.isCloseRequested();
	}

	public static void advanceAudioFrame() {
		AudioLoader.update();
	}

	public static void clearCanvas() {
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
	}

	public static void drawSprite(FloatBuffer modelViewMatrix, Matrix4f viewMatrix, Drawable drawable) {
		Texture texture = drawable.getTexture();

		modelViewMatrix.clear();
		Matrix4f.mul(viewMatrix, drawable.getWorldMatrix(), null).store(modelViewMatrix);
		modelViewMatrix.flip();

		drawable.getTint().bind();
		texture.bind();

		GL11.glPushMatrix();
		{
			GL11.glLoadMatrix(modelViewMatrix);

			//OpenGL ES 1.1 compatible code equivalent...
			/*FloatBuffer box = BufferUtils.createFloatBuffer(8);
			FloatBuffer tex = BufferUtils.createFloatBuffer(8);
			GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
			GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
			box.clear();
			box.put(new float[] {
				0, 0,
				texture.getImageWidth(), 0,
				texture.getImageWidth(), texture.getImageHeight(),
				0, texture.getImageHeight()
			});
			box.flip();
			GL11.glVertexPointer(2, 0, box);
			tex.clear();
			tex.put(new float[] {
				0, 0,
				texture.getWidth(), 0,
				texture.getWidth(), texture.getHeight(),
				0, texture.getHeight()
			});
			tex.flip();
			GL11.glTexCoordPointer(2, 0, tex);
			GL11.glDrawArrays(GL11.GL_QUADS, 0, 4);
			GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
			GL11.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);*/

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

		TextureImpl.unbind();
	}

	public static void drawTransformedWireframe(FloatBuffer buf, Matrix4f viewMatrix, Color color, Polygon[] polygons) {
		buf.clear();
		viewMatrix.store(buf);
		buf.flip();
		GL11.glPushMatrix();
		{
			GL11.glLoadMatrix(buf);
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			color.bind();
			for (Polygon p : polygons) {
				GL11.glBegin(GL11.GL_LINE_LOOP);
				Vector2f[] vertices = p.getVertices();
				for (int i = 0; i < vertices.length; i++)
					GL11.glVertex2f(vertices[i].getX(), vertices[i].getY());
				GL11.glEnd();
			}
		}
	}

	public static void flipBackBuffer() {
		Display.update();
	}

	public static BufferedImage pngScreenshot() {
		GL11.glReadBuffer(GL11.GL_FRONT);
		int width = Display.getDisplayMode().getWidth();
		int height= Display.getDisplayMode().getHeight();
		int bpp = 4; //assuming color depth of 32 bits
		ByteBuffer buffer = BufferUtils.createByteBuffer(width * height * bpp);
		GL11.glReadPixels(0, 0, width, height, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);

		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB); //PNG does NOT support premultiplied alpha
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				int i = (x + (width * y)) * bpp;
				int r = buffer.get(i) & 0xFF;
				int g = buffer.get(i + 1) & 0xFF;
				int b = buffer.get(i + 2) & 0xFF;
				int a = buffer.get(i + 3) & 0xFF;
				//OpenGL has alpha channel last (RGBA), BufferedImage has alpha channel first (ARGB)
				image.setRGB(x, height - (y + 1), (a << 24) | (r << 16) | (g << 8) | b);
			}
		}
		return image;
	}

	public static void waitForNextFrame(int fps) {
		Display.sync(fps);
	}

	public static void releaseWindow() {
		Display.destroy();
	}

	public static void releaseAudio() {
		AL.destroy();
	}
}
