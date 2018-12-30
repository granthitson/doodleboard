package shapes;

import java.awt.Color;

public class Rectangle extends java.awt.geom.Rectangle2D.Double {
	
	private static final long serialVersionUID = 1L;
	private Color c;
	private boolean fill = false;
	
	public Rectangle(int x1, int y1, int w, int h, Color color) {
		super(x1,y1,w,h);
		this.c = color;
	}
	
	public Rectangle(int x1, int y1, int w, int h, Color color, boolean fill) {
		this(x1,y1,w,h,color);
		this.fill = fill;
	}

	public Color getDrawColor() {
		return c;
	}
	
	public boolean isFill() {
		return fill;
	}
	
	public String toString() {
		return String.format("%s, %d, %d, %d, %d, %d%n", (fill ? "filledRectangle" : "rectangle"), (int) x, (int) y,
				(int) width, (int) height, c.getRGB());
	}
}