package op_lando.map.state;

import java.text.NumberFormat;

public class FrameRateState {
	private final NumberFormat formatter;
	private int count;
	private long start;
	private double lastFps;

	public FrameRateState(NumberFormat formatter) {
		this.formatter = formatter;
	}

	public void addFrame() {
		count++;
	}

	public double getElapsedSecondsSinceLastReset() {
		return (System.currentTimeMillis() - start) / 1000d;
	}

	public double getLastCalculatedFps() {
		return lastFps;
	}

	public String getDisplayFps() {
		return formatter.format(getLastCalculatedFps());
	}

	public void reset() {
		lastFps = count / getElapsedSecondsSinceLastReset();
		count = 0;
		start = System.currentTimeMillis();
	}
}
