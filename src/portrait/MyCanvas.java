package portrait;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ByteLookupTable;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.awt.image.LookupOp;
import java.awt.image.RescaleOp;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.TreeSet;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JFrame;

import portrait.Grid;
import shapes.Line;
import shapes.Rectangle;
import shapes.RoundedRectangle;
import shapes.Ellipse;

public class MyCanvas extends Canvas {

	private static final long serialVersionUID = 1L;
	String descs[] = { "Original", "Scale", "Contrast Enhance", "Blur", "Sharpen",
			"Convolve : Sobel - horizontal gradient", "Convolve : Sobel - vertical gradient", "Greyscale : Luminance",
			"Reverse pixel intensities : LookupOp" };
	private Grid grid;
	private ArrayList<Shape> shapes;
	protected BufferedImage bi, bi2, biFiltered, greyFiltered;
	private int w, h;
	private String opStr = null;
	private byte[] hist, histr, histg, histb;
	private boolean grey = true, blurB = true, invertB = true, rescale = true, edge = true;
	private int scaleNum;
	private float rescaleNum, scaleFactor;
	private double temp = 1.0;

	public static final float[] SHARPEN3x3 = { // sharpening filter kernel
			0.f, -1.f, 0.f, -1.f, 5.f, -1.f, 0.f, -1.f, 0.f };

	public static final float[] BLUR3x3 = { 0.1f, 0.1f, 0.1f, // low-pass filter kernel
			0.1f, 0.2f, 0.1f, 0.1f, 0.1f, 0.1f };

	public static final float[] SOBELx3x3 = { 1.0f, 0.0f, -1.0f, 2.0f, 0.0f, -2.0f, 1.0f, 0.0f, -1.0f };

	public static final float[] SOBELy3x3 = { 1.0f, 2.0f, 1.0f, 0.0f, 0.0f, 0.0f, -1.0f, -2.0f, -1.0f };

	public MyCanvas(int w, int h) {
		super();
		setSize(w, h);
		setBackground(Color.white);
		grid = new Grid(0, 0, w, h);
	}

	public MyCanvas(int w, int h, ArrayList<Shape> s) {
		this(w, h);
		grid = new Grid(0, 0, w, h);
		shapes = s;
	}

	public void paint(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		filterImage();
		try {
			switch (opStr) {
			case "Scale": /* using coordinates */
				temp = (double) scaleNum / 100;
				setGrid((int) (temp * w), (int) (temp * h));
				g2.drawImage(bi, AffineTransform.getScaleInstance(temp, temp), null);
				break;
			case "Invert":
				if (invertB == false) {
					byte lut[] = new byte[256];
					for (int j = 0; j < 256; j++) {
						lut[j] = (byte) (256 - j);
					}
					ByteLookupTable blut = new ByteLookupTable(0, lut);
					LookupOp lop = new LookupOp(blut, null);
					BufferedImage test = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
					AffineTransform at = new AffineTransform();
					at.scale(temp, temp);
					AffineTransformOp scaleOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
					test = scaleOp.filter(bi, test);
					g2.drawImage(test, lop, 0, 0);
				} else {
					g.drawImage(bi, 0, 0, (int) (temp * w), (int) (temp * h), 0, 0, w, h, null);
				}
				break;
			case "Contrast Enhance":
				if (rescale == false) {
					System.out.println(scaleFactor);
					System.out.println(rescaleNum);
					RescaleOp rop = new RescaleOp(scaleFactor, rescaleNum, null);
					BufferedImage test = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
					AffineTransform at = new AffineTransform();
					at.scale(temp, temp);
					AffineTransformOp scaleOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
					test = scaleOp.filter(bi, test);
					g2.drawImage(test, rop, 0, 0);
					// g2.drawImage(bi, rop, 0, 0);
				} else {
					g.drawImage(bi, 0, 0, (int) (temp * w), (int) (temp * h), 0, 0, w, h, null);
				}

				break;
			case "Greyscale : Luminance":
				setHistogramGreyFilter();
				if (grey == true) {
					g.drawImage(bi, 0, 0, (int) (temp * w), (int) (temp * h), 0, 0, w, h, null);
				} else {
					biFiltered = greyFiltered;
					g.drawImage(biFiltered, 0, 0, (int) (temp * w), (int) (temp * h), 0, 0, w, h, null);
				}
				break;
			case "Original":
				biFiltered = bi;
				g2.drawImage(bi, 0, 0, null);
				break;
			default:
				g.drawImage(biFiltered, 0, 0, (int) (temp * w), (int) (temp * h), 0, 0, w, h, null);
				break;
			}
		} catch (NullPointerException n) {
			;
		} catch (ArithmeticException a) {
			;
		}

		try {
			for (Shape s : shapes) {
				if (s instanceof Line) {
					Line line = (Line) s;
					g2.setColor(line.getDrawColor());
					g2.draw(line);
				} else if (s instanceof Ellipse) {
					Ellipse e = (Ellipse) s;
					g2.setColor(e.getDrawColor());
					if (e.isFill()) {
						g2.fill(e);
					} else {
						g2.draw(e);
					}
				} else if (s instanceof Rectangle) {
					Rectangle r = (Rectangle) s;
					g2.setColor(r.getDrawColor());
					if (r.isFill()) {
						g2.fill(r);
					} else {
						g2.draw(r);
					}
				} else if (s instanceof RoundedRectangle) {
					RoundedRectangle rr = (RoundedRectangle) s;
					g2.setColor(rr.getDrawColor());
					if (rr.isFill()) {
						g2.fill(rr);
					} else {
						g2.draw(rr);
					}
				}
			}
		} catch (NullPointerException n) {
			;
		}

		grid.display(g2);
		setCanvasSize(w, h);
	}

	public void filterImage() {
		if (opStr == null) {
			return;
		}
		try {
			/*
			 * Rather than directly drawing the filtered image to the destination, filter it
			 * into a new image first, then that filtered image is ready for writing out or
			 * painting.
			 */
			BufferedImageOp op = null;
			biFiltered = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
			switch (opStr) {
			case "Blur":
			case "Sharpen":
				if (blurB == false) {
					float[] data = (opStr.equals("Blur")) ? BLUR3x3 : SHARPEN3x3;
					op = new ConvolveOp(new Kernel(3, 3, data), ConvolveOp.EDGE_NO_OP, null);
					op.filter(bi, biFiltered);
				} else {
					biFiltered = bi;
				}
				break;
			case "Edge Detect H":
			case "Edge Detect V":
				if (edge == false) {
					float[] sobel = (opStr.equals("Edge Detect H")) ? SOBELx3x3 : SOBELy3x3;
					op = new ConvolveOp(new Kernel(3, 3, sobel), ConvolveOp.EDGE_NO_OP, null);
					op.filter(bi, biFiltered);
				} else {
					biFiltered = bi;
				}
				break;
			default:
				;
			}
		} catch (NullPointerException n) {
			;
		} catch (IllegalArgumentException i) {
			;
		}
	}

	public String loadPainting(String filename) {
		String name = "no file found - running demo string";
		String path = System.getProperty("user.dir") + "/" + filename;
		JFileChooser jfc = new JFileChooser(".");
		File infile = new File(path);
		if (filename != null) {
			infile = new File(path);
			name = infile.getName();
		} else {
			jfc.showOpenDialog(null);
			infile = jfc.getSelectedFile();
			name = infile.getName();
		}
		Scanner scan = null;
		try {
			scan = new Scanner(infile);
			name = infile.getName();
		} catch (FileNotFoundException | NullPointerException n) {
			scan = new Scanner("circle, 175, 600, 30, 30, -65536\r\n" + "filledCircle, 290, 520, 40, 40, -256\r\n"
					+ "circle, 375, 425, 47, 47, -16711936\r\n" + "filledCircle, 475, 325, 48, 48, -16776961\r\n"
					+ "circle, 550, 225, 47, 47, -256\r\n" + "filledCircle, 625, 100, 48, 48, -65536\r\n"
					+ "50, 50, 587, 100, -13421569\r\n" + "50, 200, 587, 100, -13421569");
		}
		if (shapes == null)
			shapes = new ArrayList<Shape>();
		int x1 = -1, x2 = -1, y1 = -1, y2 = -1, w = -1, h = -1, arcW = -1, arcH = -1;
		Color color = null;
		while (scan.hasNextLine()) {
			String line = scan.nextLine();
			String[] data = line.split(",");
			if (data.length == 5) {
				// we have a line
				x1 = Integer.parseInt(data[0].trim());
				y1 = Integer.parseInt(data[1].trim());
				x2 = Integer.parseInt(data[2].trim());
				y2 = Integer.parseInt(data[3].trim());
				color = new Color(Integer.parseInt(data[4].trim()));
				shapes.add(new Line(x1, y1, x2, y2, color));
			} else if (data[0].equals("circle")) {
				x1 = Integer.parseInt(data[1].trim());
				y1 = Integer.parseInt(data[2].trim());
				w = Integer.parseInt(data[3].trim());
				h = Integer.parseInt(data[4].trim());
				color = new Color(Integer.parseInt(data[5].trim()));
				shapes.add(new Ellipse(x1, y1, w, h, color));
			} else if (data[0].equals("filledCircle")) {
				x1 = Integer.parseInt(data[1].trim());
				y1 = Integer.parseInt(data[2].trim());
				w = Integer.parseInt(data[3].trim());
				h = Integer.parseInt(data[4].trim());
				color = new Color(Integer.parseInt(data[5].trim()));
				shapes.add(new Ellipse(x1, y1, w, h, color, true));
			} else if (data[0].equals("ellipse")) {
				x1 = Integer.parseInt(data[1].trim());
				y1 = Integer.parseInt(data[2].trim());
				w = Integer.parseInt(data[3].trim());
				h = Integer.parseInt(data[4].trim());
				color = new Color(Integer.parseInt(data[5].trim()));
				shapes.add(new Ellipse(x1, y1, w, h, color));
			} else if (data[0].equals("filledEllipse")) {
				x1 = Integer.parseInt(data[1].trim());
				y1 = Integer.parseInt(data[2].trim());
				w = Integer.parseInt(data[3].trim());
				h = Integer.parseInt(data[4].trim());
				color = new Color(Integer.parseInt(data[5].trim()));
				shapes.add(new Ellipse(x1, y1, w, h, color, true));
			} else if (data[0].equals("rectangle")) {
				x1 = Integer.parseInt(data[1].trim());
				y1 = Integer.parseInt(data[2].trim());
				w = Integer.parseInt(data[3].trim());
				h = Integer.parseInt(data[4].trim());
				color = new Color(Integer.parseInt(data[5].trim()));
				shapes.add(new Rectangle(x1, y1, w, h, color));
			} else if (data[0].equals("filledRectangle")) {
				x1 = Integer.parseInt(data[1].trim());
				y1 = Integer.parseInt(data[2].trim());
				w = Integer.parseInt(data[3].trim());
				h = Integer.parseInt(data[4].trim());
				color = new Color(Integer.parseInt(data[5].trim()));
				shapes.add(new Rectangle(x1, y1, w, h, color, true));
			} else if (data[0].equals("roundedRectangle")) {
				x1 = Integer.parseInt(data[1].trim());
				y1 = Integer.parseInt(data[2].trim());
				w = Integer.parseInt(data[3].trim());
				h = Integer.parseInt(data[4].trim());
				arcW = Integer.parseInt(data[5].trim());
				arcH = Integer.parseInt(data[6].trim());
				color = new Color(Integer.parseInt(data[7].trim()));
				shapes.add(new RoundedRectangle(x1, y1, w, h, arcW, arcH, color));
			} else if (data[0].equals("filledRoundedRectangle")) {
				x1 = Integer.parseInt(data[1].trim());
				y1 = Integer.parseInt(data[2].trim());
				w = Integer.parseInt(data[3].trim());
				h = Integer.parseInt(data[4].trim());
				arcW = Integer.parseInt(data[5].trim());
				arcH = Integer.parseInt(data[6].trim());
				color = new Color(Integer.parseInt(data[7].trim()));
				shapes.add(new RoundedRectangle(x1, y1, w, h, arcW, arcH, color, true));
			}
		}
		scan.close();
		return name;
	}

	public String loadImage(String filename) {
		String path = System.getProperty("user.dir") + "/" + filename;
		String name = null;
		JFileChooser jfc = new JFileChooser(".");
		File infile = new File(path);
		if (filename != null) {
			if (infile.exists()) {
				infile = new File(path);
				name = infile.getName();
			} else {
				path = System.getProperty("user.dir") + "/TestImages/" + filename;
				infile = new File(path);
				name = infile.getName();
			}
		} else {
			try {
				jfc.showOpenDialog(null);
				infile = jfc.getSelectedFile();
				path = jfc.getSelectedFile().getAbsolutePath().replace('\\', '/');
				name = infile.getName();
			} catch (NullPointerException e) {
				;
			}
			
		}
		
		try {
			bi = ImageIO.read(infile);
			w = bi.getWidth(null);
			h = bi.getHeight(null);
			grid.setWidth(w);
			grid.setHeight(h);
			
			if (bi.getType() != BufferedImage.TYPE_INT_RGB) {
				BufferedImage bi2 = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
				Graphics big = bi2.createGraphics();
				big.drawImage(bi, 0, 0, null);
				biFiltered = bi = bi2;
				paint(big);
				big.dispose();
			} else if (bi.getType() != BufferedImage.TYPE_INT_ARGB) {
				BufferedImage bi2 = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
				Graphics big = bi2.createGraphics();
				big.drawImage(bi, 0, 0, null);
				biFiltered = bi = bi2;
				paint(big);
				big.dispose();
			}
		} catch (IOException e) {
			//System.out.println("Image could not be read");
			;
		} catch (IllegalArgumentException e) {
			//System.out.println("Image could not be read");
			;
		} catch (NullPointerException e) {
			//System.out.println("Image could not be read");
			;
		}
		//setCanvasSize(w, h);
		return name;
	}

	public void setGrid(int width, int height) {
		grid.setWidth(width);
		grid.setHeight(height);
	}

	public void setShape(ArrayList<Shape> s) {
		shapes = s;
	}

	public void setHistogramGreyFilter() {
		greyFiltered = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);
		histr = new byte[256];
		histg = new byte[256];
		histb = new byte[256];
		hist = new byte[256];
		for (int i = 0; i < w; i++)
			for (int j = 0; j < h; j++) {
				int rgb = bi.getRGB(i, j); // AAAAAAAA|RRRRRRRR|GGGGGGGG|BBBBBBBBB
				int b = rgb & 0xff; // BBBBBBBBB
				int g = rgb >> 8 & 0xff; // GGGGGGGG
				int r = rgb >> 16 & 0xff; // RRRRRRRR
				int luma = (int) (r * .2989 + g * .587 + b * .114);

				hist[luma]++;
				histr[r]++;
				histg[g]++;
				histb[b]++;
			}
		Graphics g = greyFiltered.createGraphics();
		g.drawImage(bi, 0, 0, null); // better performance than the pixel by pixel conversion
		g.dispose();
	}

	public Dimension getPreferredSize() {
		return new Dimension(w, h);
	}

	String[] getDescriptions() {
		return descs;
	}

	public void setScale(int n) {
		scaleNum = n;
	}

	public void setRescaleOp(float n, float s) {
		rescaleNum = n;
		scaleFactor = s;
	}

	void setOp(String i) {
		opStr = i;
	}

	public void toggleGrid() {
		grid.toggleGrid();
	}

	public void toggleInvert() {
		invertB = !invertB;
	}

	public void toggleBlur() {
		blurB = !blurB;
	}

	public void toggleGrey() {
		grey = !grey;
	}

	public void toggleRescaleOp() {
		rescale = !rescale;
	}

	public boolean getRescaleOp() {
		return rescale;
	}

	public void toggleEdgeDetect() {
		edge = !edge;
	}

	public boolean getToggleGrid() {
		return grid.getToggleGrid();
	}
	
	public void setAllTrue() {
		grey = blurB = invertB = rescale = edge = true;
		temp = 1;
		setGrid((int) (temp * w), (int) (temp * h));
	}

	public void clear() {
		try {
			shapes.clear();
		} catch (NullPointerException e) {
			;
		}
		repaint();
		bi = null;
		biFiltered = null;
		bi2 = null;
		greyFiltered = null;
		opStr = null;
	}

	public void setCanvasSize(int width, int height) {
		w = width;
		h = height;
	}

	public int height() {
		return h;
	}

	public int width() {
		return w;
	}

	public BufferedImage getMyImage() {
		return bi2;
	}

}
