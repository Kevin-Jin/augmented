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
