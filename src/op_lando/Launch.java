package op_lando;

import java.io.IOException;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;

public class Launch {
	public static void main(String[] args) throws IOException, LWJGLException {
		Game g = new Game();
		g.glInit();
		g.loadContent();
		long last = System.currentTimeMillis(), now;
		while (!Display.isCloseRequested()) {
			now = System.currentTimeMillis();
			g.update((now - last) / 1000d);
			last = now;

			g.draw();

			Display.update();
			Display.sync(Game.TARGET_FPS);
		}
		g.unloadContent();
		Display.destroy();
	}
}
