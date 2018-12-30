package portrait;

import java.awt.Color;
import java.awt.Graphics2D;


public class Grid {

	private int w = 1200;
	private int h = 1000;
	private int x;
	private int y;
	private boolean showGrid = false;

	public Grid(int x, int y, int w, int h) {
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;

	}

	public void toggleGrid() {
		showGrid = !showGrid;
	}
	
	public boolean getToggleGrid() {
		return showGrid;
	}
	
	public void setWidth(int width) {
		w = width;
	}
	
	public void setHeight(int height) {
		h = height;
	}

	public void display(Graphics2D g2) {
		if (showGrid == true) {
			g2.setColor(new Color(0xf0f8ff)); // aliceblue
			for (int x1 = x; x1 <= w; x1 += 10) {
				g2.drawLine(x1, 0, x1, h);
				if (x1 % 50 == 0 && x1 > 0) {
					g2.setColor(Color.black);
					g2.drawString(String.format("%3d", x1), x1, 15);
					g2.setColor(new Color(0xf0f8ff));
				}
			}
			for (int y1 = y; y1 <= h; y1 += 10) {
				g2.drawLine(0, y1, w, y1);
				if (y1 % 50 == 0) {
					g2.setColor(Color.black);
					g2.drawString(String.format("%3d", y1), 5, y1);
					g2.setColor(new Color(0xf0f8ff));
				}
			}
		}

	}

}
