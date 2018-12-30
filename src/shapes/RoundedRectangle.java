package shapes;

import java.awt.Color;

public class RoundedRectangle extends java.awt.geom.RoundRectangle2D.Double {
	
	private static final long serialVersionUID = 1L;
	private Color c;
	private boolean fill = false;
	
	public RoundedRectangle(int x1, int y1, int w, int h, int arcW, int arcH, Color color) {
		super(x1,y1,w,h,arcW,arcH);
		this.c = color;
	}
	
	public RoundedRectangle(int x1, int y1, int w, int h, int arcW, int arcH, Color color, boolean fill) {
		this(x1,y1,w,h,arcW,arcH,color);
		this.fill = fill;
	}

	public Color getDrawColor() {
		return c;
	}
	
	public boolean isFill() {
		return fill;
	}
	
	public String toString() {
		return String.format("%s, %d, %d, %d, %d, %d, %d, %d%n", (fill ? "filledRoundedRectangle" : "roundedRectangle"), (int) x, (int) y,
				(int) width, (int) height, (int) arcwidth, (int) arcwidth, c.getRGB());
	}
}