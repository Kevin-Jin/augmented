package amplified;

public class Launch {
	public static void main(String[] args) throws Exception {
		Game g = new Game();
		g.graphicsInit();
		g.loadContent();
		long last = System.nanoTime(), now;
		while (g.nextFrame()) {
			now = System.nanoTime();
			g.update((now - last) / 1e9d);
			last = now;

			g.draw();
		}
		g.unloadContent();
		g.graphicsUninit();
	}
}
