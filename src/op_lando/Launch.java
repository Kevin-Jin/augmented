package op_lando;

public class Launch {
	public static void main(String[] args) throws Exception {
		Game g = new Game();
		g.graphicsInit();
		g.loadContent();
		long last = System.currentTimeMillis(), now;
		while (g.nextFrame()) {
			now = System.currentTimeMillis();
			g.update((now - last) / 1000d);
			last = now;

			g.draw();
		}
		g.unloadContent();
		g.graphicsUninit();
	}
}
