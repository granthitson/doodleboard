package shapes;

import java.awt.Color;

public class Line extends java.awt.geom.Line2D.Double {

	private static final long serialVersionUID = 1L;
	private Color c;
	
	public Line(int x1, int y1, int x2, int y2, Color c) {
		super(x1,y1,x2,y2);
		this.c = c;
	}
	
	public Color getDrawColor() {
		return c;
	}
	
	public String toString() {
		return String.format("%d, %d, %d, %d, %d%n", (int) x1, (int) y1,
				(int) x2, (int) y2, c.getRGB());
	}
}
