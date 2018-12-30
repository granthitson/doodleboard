package portrait;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import shapes.Rectangle;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import pong.Pong;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent; //128 ctrl, 512 alt, 0 no mask
import java.awt.Shape;

import shapes.Ellipse;
import shapes.Line;
import shapes.RoundedRectangle;

public class Doodle extends JFrame implements ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	{
		try {
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private JLabel coords;
	private JButton clearBtn, colorBtn, lineBtn, circleBtn, filledCircleBtn, smoothLineBtn, ellipseBtn,
	filledEllipseBtn, rectangleBtn, filledRectangleBtn, roundedRectangleBtn, filledRoundedRectangleBtn,
	scaleBtn, sharpenBtn, blurBtn, edgeDetectBtn, edgeDetectBtn2, luminanceBtn, contrastEnhanceBtn, invertBtn, originalBtn;
	private JTextField sizeField1, sizeField2, sizeField3, sizeField4, scaleField, scaleMenuField, contrastField, contrastField2, contrastMenuField, contrastMenuField2;
	private JFileChooser fileChooser;

	private JPanel shapesPanel, controlPanel, filtersPanel;
	private ArrayList<Shape> shapes;
	private Color drawColor = Color.black;
	private MyCanvas sketchpad;
	private int x1, x2, y1, y2, w, h, arcW = 20, arcH = 20;
	private int drawType;
	private String name = null, lastName = null;
	private Pong pong;

	/**
	 * Sets up title of the board, the flowlayout, and registers menu options such
	 * as Open, Save, Save As. Also adds actionlisteners, mousemotionlisteners, and
	 * mouselisteners. Register the listeners to make buttons.
	 */
	public Doodle() {
		String name = "Grant Hitson";
		setTitle("By " + name);
		Container contentPane = getContentPane();
		// set the content pane properties
		contentPane.setLayout(new BorderLayout());
		contentPane.setBackground(Color.white);

		// create MenuBar, major menubar menus and mneumonics
		JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu = new JMenu("File");
		fileMenu.setMnemonic(KeyEvent.VK_F);

		JMenu gameMenu = new JMenu("Pong");

		JMenu controlsMenu = new JMenu("Controls");
		controlsMenu.setMnemonic(KeyEvent.VK_C);
		JMenu shapesMenu = new JMenu("Shapes");
		shapesMenu.setMnemonic(KeyEvent.VK_S);

		JMenu filtersMenu = new JMenu("Filters");
		filtersMenu.setMnemonic(KeyEvent.VK_F);

		// add menu items
		JMenu openMenu = new JMenu("Open");
		addMenuItem("Last Saved Image", openMenu, KeyEvent.VK_L, InputEvent.ALT_DOWN_MASK, null);
		addMenuItem("Image (.txt)", openMenu, KeyEvent.VK_I, InputEvent.ALT_DOWN_MASK, null);
		addMenuItem("Image", openMenu, KeyEvent.VK_I, InputEvent.ALT_DOWN_MASK, null);
		addMenuItem("New Painting", openMenu, KeyEvent.VK_D, InputEvent.ALT_DOWN_MASK, null);
		fileMenu.add(openMenu);

		addMenuItem("Play Game", gameMenu, -1, -1, null);
		addMenuItem("Leaderboard", gameMenu, -1, -1, null);

		addMenuItem("Save", fileMenu, KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK, null);
		addMenuItem("Save As", fileMenu, KeyEvent.VK_A, InputEvent.CTRL_DOWN_MASK, null);
		addMenuItem("Exit", fileMenu, KeyEvent.VK_X, InputEvent.ALT_DOWN_MASK, null);
		addMenuItem("Save Image and Exit", fileMenu, -1, -1, null);

		addMenuItem("Show Control Panel", controlsMenu, KeyEvent.VK_F2, 0, null);
		addMenuItem("Hide Control Panel", controlsMenu, KeyEvent.VK_F3, 0, null);
		addMenuItem("Color", controlsMenu, KeyEvent.VK_C, 0, null);
		addMenuItem("Clear", controlsMenu, KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK, null);
		addMenuItem("Set Size", controlsMenu, KeyEvent.VK_S, 0, null);
		sizeField3 = new JTextField("1200", 8);
		sizeField3.addActionListener(new MyActionListener());
		controlsMenu.add(sizeField3);
		sizeField3.setMaximumSize(sizeField3.getPreferredSize());
		
		sizeField4 = new JTextField("1000", 8);
		sizeField4.addActionListener(new MyActionListener());
		controlsMenu.add(sizeField4);
		sizeField4.setMaximumSize(sizeField4.getPreferredSize());
		addMenuItem("Toggle Grid", controlsMenu, KeyEvent.VK_G, InputEvent.CTRL_DOWN_MASK, null);

		addMenuItem("Show Shapes Panel", shapesMenu, KeyEvent.VK_F4, 0, null);
		addMenuItem("Hide Shapes Panel", shapesMenu, KeyEvent.VK_F5, 0, null);
		addMenuItem("Line", shapesMenu, KeyEvent.VK_L, InputEvent.CTRL_DOWN_MASK, "images/line.png");
		addMenuItem("Smooth Line", shapesMenu, KeyEvent.VK_K, InputEvent.CTRL_DOWN_MASK, "images/curvedLine.png");
		addMenuItem("Circle", shapesMenu, KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK, "images/circle.png");
		addMenuItem("Filled Circle", shapesMenu, KeyEvent.VK_C, InputEvent.SHIFT_DOWN_MASK, "images/filledCircle.png");
		addMenuItem("Ellipse", shapesMenu, KeyEvent.VK_E, InputEvent.CTRL_DOWN_MASK, "images/ellipse.png");
		addMenuItem("Filled Ellipse", shapesMenu, KeyEvent.VK_E, InputEvent.SHIFT_DOWN_MASK,
				"images/filledEllipse.png");
		addMenuItem("Rectangle", shapesMenu, KeyEvent.VK_R, InputEvent.CTRL_DOWN_MASK, "images/rectangle.png");
		addMenuItem("Filled Rectangle", shapesMenu, KeyEvent.VK_R, InputEvent.SHIFT_DOWN_MASK,
				"images/filledRectangle.png");
		addMenuItem("Round Rectangle", shapesMenu, KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK,
				"images/roundRectangle.png");
		addMenuItem("Filled Round Rectangle", shapesMenu, KeyEvent.VK_O, InputEvent.SHIFT_DOWN_MASK,
				"images/filledRoundRectangle.png");
		addMenuItem("Polygon", shapesMenu, KeyEvent.VK_L, InputEvent.CTRL_DOWN_MASK, "images/polygon.png");

		addMenuItem("Show Filters Panel", filtersMenu, KeyEvent.VK_F6, 0, null);
		addMenuItem("Hide Filters Panel", filtersMenu, KeyEvent.VK_F2, 0, null);
		JMenu scaleMenu = new JMenu("Scale");
		addMenuItem("Scale 25%", scaleMenu, KeyEvent.VK_F7, InputEvent.SHIFT_DOWN_MASK, null);
		addMenuItem("Scale 50%", scaleMenu, KeyEvent.VK_F8, InputEvent.SHIFT_DOWN_MASK, null);
		addMenuItem("Scale 75%", scaleMenu, KeyEvent.VK_F9, InputEvent.SHIFT_DOWN_MASK, null);
		addMenuItem("Scale 100%", scaleMenu, KeyEvent.VK_F10, InputEvent.SHIFT_DOWN_MASK, null);
		addMenuItem("Scale 200%", scaleMenu, KeyEvent.VK_F11, InputEvent.SHIFT_DOWN_MASK, null);
		addMenuItem("Scale __%", scaleMenu, KeyEvent.VK_F12, InputEvent.SHIFT_DOWN_MASK, null);
		scaleMenu.add(scaleMenuField = new JTextField("100", 5));
		scaleMenuField.addActionListener(new MyActionListener());
		scaleMenuField.setMaximumSize(scaleMenuField.getPreferredSize());

		filtersMenu.add(scaleMenu);

		addMenuItem("Original", filtersMenu, KeyEvent.VK_F6, InputEvent.SHIFT_DOWN_MASK, null);
		addMenuItem("Sharpen", filtersMenu, KeyEvent.VK_S, InputEvent.SHIFT_DOWN_MASK, null);
		addMenuItem("Blur", filtersMenu, KeyEvent.VK_B, InputEvent.SHIFT_DOWN_MASK, null);
		addMenuItem("Edge Detect H", filtersMenu, KeyEvent.VK_H, InputEvent.SHIFT_DOWN_MASK, null);
		addMenuItem("Edge Detect V", filtersMenu, KeyEvent.VK_H, InputEvent.SHIFT_DOWN_MASK, null);
		addMenuItem("Greyscale : Luminance", filtersMenu, KeyEvent.VK_L, InputEvent.SHIFT_DOWN_MASK, null);
		JMenu contrastMenu = new JMenu("Contrast Enhance");
		addMenuItem("Contrast Enhance", contrastMenu, KeyEvent.VK_F12, InputEvent.SHIFT_DOWN_MASK, null);
		contrastMenu.add(contrastMenuField = new JTextField("1.2", 5));
		contrastMenu.add(contrastMenuField2 = new JTextField("20.0", 5));
		contrastMenuField.addActionListener(new MyActionListener());
		contrastMenuField.setMaximumSize(contrastMenuField.getPreferredSize());
		contrastMenuField2.addActionListener(new MyActionListener());
		contrastMenuField2.setMaximumSize(contrastMenuField2.getPreferredSize());
		filtersMenu.add(contrastMenu);

		JPanel pNorth = new JPanel();

		shapesPanel = new JPanel();
		controlPanel = new JPanel();
		filtersPanel = new JPanel();
		coords = new JLabel("(x, y)          ");
		controlPanel.add(coords);

		colorBtn = new JButton("Color");
		colorBtn.addActionListener(new MyActionListener());
		controlPanel.add(colorBtn);

		clearBtn = new JButton("Clear");
		clearBtn.addActionListener(new MyActionListener());
		controlPanel.add(clearBtn);

		lineBtn = new JButton("Line");
		lineBtn.addActionListener(new MyActionListener());
		shapesPanel.add(lineBtn);

		smoothLineBtn = new JButton("Smooth Line");
		smoothLineBtn.addActionListener(new MyActionListener());
		shapesPanel.add(smoothLineBtn);

		circleBtn = new JButton("Circle");
		circleBtn.addActionListener(new MyActionListener());
		shapesPanel.add(circleBtn);

		filledCircleBtn = new JButton("Filled Circle");
		filledCircleBtn.addActionListener(new MyActionListener());
		shapesPanel.add(filledCircleBtn);

		ellipseBtn = new JButton("Ellipse");
		ellipseBtn.addActionListener(new MyActionListener());
		shapesPanel.add(ellipseBtn);

		filledEllipseBtn = new JButton("Filled Ellipse");
		filledEllipseBtn.addActionListener(new MyActionListener());
		shapesPanel.add(filledEllipseBtn);

		rectangleBtn = new JButton("Rectangle");
		rectangleBtn.addActionListener(new MyActionListener());
		shapesPanel.add(rectangleBtn);

		filledRectangleBtn = new JButton("Filled Rectangle");
		filledRectangleBtn.addActionListener(new MyActionListener());
		shapesPanel.add(filledRectangleBtn);

		roundedRectangleBtn = new JButton("Rounded Rectangle");
		roundedRectangleBtn.addActionListener(new MyActionListener());
		shapesPanel.add(roundedRectangleBtn);

		filledRoundedRectangleBtn = new JButton("Filled Rounded Rectangle");
		filledRoundedRectangleBtn.addActionListener(new MyActionListener());
		shapesPanel.add(filledRoundedRectangleBtn);

		originalBtn = new JButton("Original");
		originalBtn.addActionListener(new MyActionListener());
		filtersPanel.add(originalBtn);
		
		scaleBtn = new JButton("Scale");
		scaleBtn.addActionListener(new MyActionListener());
		filtersPanel.add(scaleBtn);

		scaleField = new JTextField("100", 4);
		scaleField.addActionListener(new MyActionListener());
		filtersPanel.add(scaleField);
		scaleField.setMaximumSize(scaleField.getPreferredSize());

		sharpenBtn = new JButton("Sharpen");
		sharpenBtn.addActionListener(new MyActionListener());
		filtersPanel.add(sharpenBtn);

		blurBtn = new JButton("Blur");
		blurBtn.addActionListener(new MyActionListener());
		filtersPanel.add(blurBtn);

		edgeDetectBtn = new JButton("Edge Detect H");
		edgeDetectBtn.addActionListener(new MyActionListener());
		filtersPanel.add(edgeDetectBtn);
		
		edgeDetectBtn2 = new JButton("Edge Detect V");
		edgeDetectBtn2.addActionListener(new MyActionListener());
		filtersPanel.add(edgeDetectBtn2);

		luminanceBtn = new JButton("Greyscale : Luminance");
		luminanceBtn.addActionListener(new MyActionListener());
		filtersPanel.add(luminanceBtn);

		contrastEnhanceBtn = new JButton("Contrast Enhance");
		contrastEnhanceBtn.addActionListener(new MyActionListener());
		filtersPanel.add(contrastEnhanceBtn);

		contrastField = new JTextField("1.2", 4);
		contrastField.addActionListener(new MyActionListener());
		filtersPanel.add(contrastField);
		contrastField.setMaximumSize(contrastField.getPreferredSize());

		contrastField2 = new JTextField("20.0", 4);
		contrastField2.addActionListener(new MyActionListener());
		filtersPanel.add(contrastField2);
		contrastField2.setMaximumSize(contrastField2.getPreferredSize());

		invertBtn = new JButton("Invert");
		invertBtn.addActionListener(new MyActionListener());
		filtersPanel.add(invertBtn);
		
		add(pNorth, BorderLayout.PAGE_START);
		add(shapesPanel, BorderLayout.PAGE_END);
		add(controlPanel, BorderLayout.LINE_START);
		add(filtersPanel, BorderLayout.LINE_END);

		controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
		filtersPanel.setLayout(new FlowLayout());
		
		filtersPanel.setPreferredSize(new Dimension(175,h));

		menuBar.add(fileMenu);
		menuBar.add(gameMenu);
		menuBar.add(controlsMenu);
		menuBar.add(shapesMenu);
		menuBar.add(filtersMenu);
		filtersMenu.add(scaleMenu);
		setJMenuBar(menuBar);

		sketchpad = new MyCanvas(1400, 800);
		add(sketchpad, BorderLayout.CENTER);
		
		sizeField3.setText(""+ sketchpad.width());
		sizeField4.setText(""+ sketchpad.height());
		
		shapesPanel.setBackground(Color.gray);
		controlPanel.setBackground(Color.gray);
		filtersPanel.setBackground(Color.gray);		
		pNorth.setBackground(Color.gray);
		
		shapes = new ArrayList<Shape>();

		sketchpad.addMouseListener(new MyMouseListener());
		sketchpad.addMouseMotionListener(new MyMouseMotionListener());

		setPreferredSize(new Dimension(sketchpad.getWidth(),sketchpad.getHeight()));
		
		pong = new Pong();
		pong.setVisible(false);
		

		//shapes.add(new Rectangle(0, 0, w, h, Color.white, true));

		// register EXIT_ON_CLOSE as default close operation
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		// make window maximized when opened
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		// setResizable(false);
		pack();
		setVisible(true);

	}

	/**
	 * Adds menu options for each function.
	 */
	private void addMenuItem(String label, JMenu menu, int key, int mask, String iconPath) {
		JMenuItem item;
		if (iconPath != null)
			item = new JMenuItem(label, new ImageIcon(iconPath));
		else
			item = new JMenuItem(label);
		if (key != -1)
			item.setAccelerator(KeyStroke.getKeyStroke(key, mask));
		item.addActionListener(this);
		menu.add(item);
	}

	/**
	 * Switches for each menu option. If a menu option is not selected the default
	 * statemenet is called. Also contains if statements that invokes various menu
	 * options.
	 * 
	 * @param event
	 *            ActionEvent
	 */
	public void actionPerformed(ActionEvent event) {
		String action = event.getActionCommand();
		System.out.println(action);
		if (event.getSource() instanceof JMenuItem) {
			switch (action) {
			case "Line":
				drawType = 1;
				break;
			case "Smooth Line":
				drawType = 2;
				break;
			case "Circle":
				drawType = 3;
				break;
			case "Filled Circle":
				drawType = 4;
				break;
			case "Ellipse":
				drawType = 5;
				break;
			case "Filled Ellipse":
				drawType = 6;
				break;
			case "Rectangle":
				drawType = 7;
				break;
			case "Filled Rectangle":
				drawType = 8;
				break;
			case "Round Rectangle":
				drawType = 9;
				break;
			case "Filled Round Rectangle":
				drawType = 10;
				break;
			case "Save":
				save();
				break;
			case "Save As":
				saveAs();
				break;
			case "Save Image and Exit":
				save();
				exit();
				break;
			case "Last Saved Image":
				clear();
				if (name.contains(".txt")) {
					name = sketchpad.loadPainting(lastName);
				} else {
					loadImage(lastName);
				}
				lastName = name;
				repaint();
				break;
			case "New Painting":
				clear();
				//name = null;
				break;
			case "Image (.txt)":
				clear();
				lastName = null;
				name = sketchpad.loadPainting(lastName);
				lastName = name;
				repaint();
				break;
			case "Image":
				clear();
				lastName = null;
				loadImage(lastName);
				lastName = name;
				break;
			case "Color":
				selectDrawColor();
				break;
			case "Clear":
				clear();
				break;
			case "Set Size":
				int sizew = Integer.parseInt(sizeField3.getText());
				int sizeh = Integer.parseInt(sizeField4.getText());
				sketchpad.setCanvasSize(sizew,sizeh);
				sketchpad.setSize(new Dimension(sizew,sizeh));
				sketchpad.setGrid(sizew, sizeh);
				repaint();
				break;
			case "Toggle Grid":
				toggleGrid();
				break;
			case "Show Shapes Panel":
				showShapesPanel();
				break;
			case "Hide Shapes Panel":
				hideShapesPanel();
				break;
			case "Show Control Panel":
				showControlPanel();
				break;
			case "Hide Control Panel":
				hideControlPanel();
				break;
			case "Show Filters Panel":
				showFiltersPanel();
				break;
			case "Hide Filters Panel":
				hideFiltersPanel();
				break;
			case "Original":
				sketchpad.setOp("Original");
				sketchpad.setAllTrue();
				repaint();
				break;
			case "Scale 25%":
				scale(25);
				repaint();
				break;
			case "Scale 50%":
				scale(50);
				repaint();
				break;
			case "Scale 75%":
				scale(75);
				repaint();
				break;
			case "Scale 100%":
				scale(100);
				repaint();
				break;
			case "Scale 200%":
				scale(200);
				repaint();
				break;
			case "Scale __%":
				int scalePercent = Integer.parseInt(scaleMenuField.getText());
				scale(scalePercent);
				repaint();
				break;
			case "Blur":
				sketchpad.toggleBlur();
				sketchpad.setOp("Blur");
				repaint();
				break;
			case "Sharpen":
				sketchpad.toggleBlur();
				sketchpad.setOp("Sharpen");
				repaint();
				break;
			case "Edge Detect H":
				sketchpad.toggleEdgeDetect();
				sketchpad.setOp("Edge Detect H");
				repaint();
				break;
			case "Edge Detect V":
				sketchpad.toggleEdgeDetect();
				sketchpad.setOp("Edge Detect V");
				repaint();
				break;
			case "Contrast Enhance":
				contrastEnhance();
				repaint();
				break;
			case "Greyscale : Luminance":
				sketchpad.toggleGrey();
				sketchpad.setOp("Greyscale : Luminance");
				repaint();
				break;
			case "Invert":
				sketchpad.toggleInvert();
				sketchpad.setOp("Invert");
				repaint();
				break;
			case "Play Game":
				// add open game method here
				pong.setVisible(true);
				break;
			case "Leaderboard":
				pong.showLeaderboard();
				break;
			default:
				System.out.printf("Action: %s not found", action);
			}
		}
	}

	class MyActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String label = e.getActionCommand();
			if (label.equals("Clear")) {
				clear();
			} else if (label.equals("Color")) {
				selectDrawColor();
			} else if (label.equals("Set Size")) {
				int sizew = Integer.parseInt(sizeField1.getText());
				int sizeh = Integer.parseInt(sizeField2.getText());
				sketchpad.setCanvasSize(sizew,sizeh);
				sketchpad.setGrid(sizew, sizeh);
				repaint();
			}  else if (label.equals("Line")) {
				drawType = 1;
			} else if (label.equals("Smooth Line")) {
				drawType = 2;
			} else if (label.equals("Circle")) {
				drawType = 3;
			} else if (label.equals("Filled Circle")) {
				drawType = 4;
			} else if (label.equals("Ellipse")) {
				drawType = 5;
			} else if (label.equals("Filled Ellipse")) {
				drawType = 6;
			} else if (label.equals("Rectangle")) {
				drawType = 7;
			} else if (label.equals("Filled Rectangle")) {
				drawType = 8;
			} else if (label.equals("Rounded Rectangle")) {
				drawType = 9;
			} else if (label.equals("Filled Rounded Rectangle")) {
				drawType = 10;
			} else if (label.equals("Original")) {
				sketchpad.setOp("Original");
				sketchpad.setAllTrue();
				repaint();
			} else if (label.equals("Scale")) {
				int scalePercent = Integer.parseInt(scaleField.getText());
				scale(scalePercent);
				repaint();
			} else if (label.equals("Sharpen")) {
				sketchpad.toggleBlur();
				sketchpad.setOp("Sharpen");
				repaint();
			} else if (label.equals("Blur")) {
				sketchpad.toggleBlur();
				sketchpad.setOp("Blur");
				repaint();
			} else if (label.equals("Edge Detect H")) {
				sketchpad.toggleEdgeDetect();
				sketchpad.setOp("Edge Detect H");
				repaint();
			} else if (label.equals("Edge Detect V")) {
				sketchpad.toggleEdgeDetect();
				sketchpad.setOp("Edge Detect V");
				repaint();
			} else if (label.equals("Greyscale : Luminance")) {
				sketchpad.toggleGrey();
				sketchpad.setOp("Greyscale : Luminance");
				repaint();
			} else if (label.equals("Contrast Enhance")) {
				contrastEnhance();
				repaint();
			} else if (label.equals("Invert")) {
				sketchpad.toggleInvert();
				sketchpad.setOp("Invert");
				repaint();
			}

		}
	}

	/**
	 * Keeps track of mouse events.
	 */
	class MyMouseListener implements MouseListener {

		/**
		 * Keeps track of the number of times the mouse is pressed.
		 */
		public void mousePressed(MouseEvent e) {
			x1 = e.getX();
			y1 = e.getY();
		}

		public void mouseClicked(MouseEvent e) {

		}

		public void mouseEntered(MouseEvent e) {

		}

		public void mouseExited(MouseEvent e) {

		}

		public void mouseReleased(MouseEvent e) {
			x2 = e.getX();
			y2 = e.getY();

			w = Math.abs(x1 - x2);
			h = Math.abs(y1 - y2);
			int newx = Math.min(x1, x2);
			int newy = Math.min(y1, y2);
			switch (drawType) {
			case 1:
				shapes.add(new Line(x1, y1, x2, y2, drawColor));
				break;
			case 2:
				shapes.add(new Line(x1, y1, x2, y2, drawColor));
				break;
			case 3:
				shapes.add(new Ellipse(newx, newy, w, w, drawColor));
				break;
			case 4:
				shapes.add(new Ellipse(newx, newy, w, w, drawColor, true));
				break;
			case 5:
				shapes.add(new Ellipse(newx, newy, w, h, drawColor));
				break;
			case 6:
				shapes.add(new Ellipse(newx, newy, w, h, drawColor, true));
				break;
			case 7:
				shapes.add(new Rectangle(newx, newy, w, h, drawColor));
				break;
			case 8:
				shapes.add(new Rectangle(newx, newy, w, h, drawColor, true));
				break;
			case 9:
				shapes.add(new RoundedRectangle(newx, newy, w, h, arcW, arcH, drawColor));
				break;
			case 10:
				shapes.add(new RoundedRectangle(newx, newy, w, h, arcW, arcH, drawColor, true));
				break;

			}
			sketchpad.setShape(shapes);
			sketchpad.repaint();
		}
	}

	/**
	 * private listener classes keeps track of mouse motion events
	 */
	class MyMouseMotionListener implements MouseMotionListener {
		/**
		 * initializes x2 and y2, adds a new MyLine object to the ArrayList field,
		 * invokes doodlingArea's repaint method, assign x2 and y2 to x1 and y1
		 * respectively
		 */
		public void mouseDragged(MouseEvent e) {
			if (drawType == 2) {
				x2 = e.getX();
				y2 = e.getY();
				shapes.add(new Line(x1, y1, x2, y2, drawColor));
				sketchpad.repaint();
				x1 = x2;
				y1 = y2;
			}
		}

		public void mouseMoved(MouseEvent e) {
			coords.setText("(" + e.getX() + ", " + e.getY() + ")");
		}
	}

	/**
	 * Functionality for the menu option "Save".
	 * Saves the current file.
	 */
	public void save() {
		if (name == null) {
			saveAs();
		} else {
			lastName = name;
			PrintWriter out = null;
			try {
				out = new PrintWriter(new FileWriter(name));
				for (Shape s : shapes) {
					out.printf(s.toString()); // use object's string method to save to .txt file
				}
				System.out.println("Saved as \"" + name + "\" ");
			} catch (IndexOutOfBoundsException e) {
				System.err.println("Caught IndexOutOfBoundsException: " + e.getMessage());
			} catch (IOException e) {
				System.err.println("Caught IOException: " + e.getMessage());
			} catch (NullPointerException e) {
				System.err.println("Caught NullPointerException: " + e.getMessage());
			} finally {
				if (out != null) {
					System.out.println("Closing PrintWriter");
					out.close();
				} else {
					System.out.println("PrintWriter not open");
				}
			}
		}
	}

	/**
	 * Functionality for the menu option "Save As". Can save images with different
	 * file extensions.
	 */
	public void saveAs() {
		fileChooser = new JFileChooser(".");
		FileFilter pngFilter = new FileTypeFilter(".png", "PNG Images");
		FileFilter jpgFilter = new FileTypeFilter(".jpg", "JPG Images");
		FileFilter bmpFilter = new FileTypeFilter(".bmp", "BMP Images");
		FileFilter gifFilter = new FileTypeFilter(".gif", "GIF Images");
		FileFilter imgFilter = new FileNameExtensionFilter("Images", "jpg", "png", "gif", "bmp");
		fileChooser.addChoosableFileFilter(pngFilter);
		fileChooser.addChoosableFileFilter(jpgFilter);
		fileChooser.addChoosableFileFilter(gifFilter);
		fileChooser.addChoosableFileFilter(bmpFilter);
		fileChooser.setAcceptAllFileFilterUsed(true);
		int ret = fileChooser.showSaveDialog(null);
		if (ret == JFileChooser.APPROVE_OPTION) {
			if (fileChooser.getFileFilter() == pngFilter) {
				saveFileType("png");
				name = fileChooser.getSelectedFile().getName() + ".png";
				lastName = name;
			} else if (fileChooser.getFileFilter() == jpgFilter) {
				saveFileType("jpg");
				name = fileChooser.getSelectedFile().getName() + ".jpg";
				lastName = name;
			} else if (fileChooser.getFileFilter() == bmpFilter) {
				saveFileType("bmp");
				name = fileChooser.getSelectedFile().getName() + ".bmp";
				lastName = name;
			} else if (fileChooser.getFileFilter() == gifFilter) {
				saveFileType("gif");
				name = fileChooser.getSelectedFile().getName() + ".gif";
				lastName = name;
			} else {
				try {
					FileWriter fw = new FileWriter(fileChooser.getSelectedFile() + ".txt");
					for (Shape s : shapes) {
						fw.write(s.toString());
					}

					fw.close();
					name = fileChooser.getSelectedFile().getName() + ".txt";
					lastName = name;
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}

		}
		if (ret == JFileChooser.CANCEL_OPTION) {
			;
		}
	}

	/**
	 * Saves different types of files.
	 * 
	 * @param type
	 */
	public void saveFileType(String type) {
		if (type == "jpg" || type == "bmp") {
			try {
				BufferedImage img = new BufferedImage(sketchpad.getWidth(), sketchpad.getHeight(),
						BufferedImage.TYPE_INT_RGB);
				Graphics2D graphics = img.createGraphics();
				sketchpad.paint(graphics);
				graphics.dispose();
				File ext = new File(fileChooser.getSelectedFile().getName() + "." + type);
				ImageIO.write(img, type, ext);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (type == "png" || type == "gif") {
			try {
				BufferedImage img = new BufferedImage(sketchpad.getWidth(), sketchpad.getHeight(),
						BufferedImage.TYPE_INT_ARGB);
				Graphics2D graphics = img.createGraphics();
				sketchpad.paint(graphics);
				graphics.dispose();
				File ext = new File(fileChooser.getSelectedFile().getName() + "." + type);
				ImageIO.write(img, type, ext);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Class handles different files types and
	 *makes sure they are valid file types.
	 */
	public class FileTypeFilter extends FileFilter {
		private String extension;
		private String description;

		public FileTypeFilter(String extension, String description) {
			this.extension = extension;
			this.description = description;
		}

		public boolean accept(File file) {
			if (file.isDirectory()) {
				return true;
			}
			return file.getName().endsWith(extension);
		}

		public String getDescription() {
			return description + String.format(" (*%s)", extension);
		}
	}

	/**
	 * Loads an image onto the Doodle board.
	 */
	public void loadImage(String s) {
		boolean check = sketchpad.getToggleGrid();
		if (check == true) {
			sketchpad.toggleGrid();
			name = sketchpad.loadImage(s);
			sketchpad.setOp("Original");
			repaint();
			sketchpad.toggleGrid();
			repaint();
			System.out.println(check);
		} else {
			name = sketchpad.loadImage(s);
			sketchpad.setOp("Original");
			repaint();
		}
	}
	
	public void scale(int i) {
		sketchpad.setOp("Scale");
		sketchpad.setScale(i);
	}
	
	public void contrastEnhance() {
		float rescaleNum = Float.parseFloat(contrastField.getText());
		float rescaleFactor = Float.parseFloat(contrastField2.getText());
		
		if (contrastField.getText().equals("") && contrastField2.getText().equals("")) {
			;
		} else {
			if (sketchpad.getRescaleOp() == true) {
				sketchpad.toggleRescaleOp();
				sketchpad.setOp("Contrast Enhance");
				sketchpad.setRescaleOp(rescaleFactor, rescaleNum);
			} else {
				sketchpad.toggleRescaleOp();
			}
		}
	}

	public void selectDrawColor() {
		drawColor = JColorChooser.showDialog(null, " Color Pallette", Color.cyan);
	}

	public void toggleGrid() {
		sketchpad.toggleGrid();
		repaint();
	}

	public void repaint() {
		sketchpad.repaint();
		sizeField3.setText(""+ sketchpad.getWidth());
		sizeField4.setText(""+ sketchpad.getHeight());
	}

	public void showShapesPanel() {
		shapesPanel.setVisible(true);
	}

	public void hideShapesPanel() {
		shapesPanel.setVisible(false);
	}

	public void showControlPanel() {
		controlPanel.setVisible(true);
	}

	public void hideControlPanel() {
		controlPanel.setVisible(false);
	}

	public void showFiltersPanel() {
		filtersPanel.setVisible(true);
	}

	public void hideFiltersPanel() {
		filtersPanel.setVisible(false);
	}

	public void clear() {
		try {
			sketchpad.clear();
			repaint();
		} catch (NullPointerException n) {
			System.out.println("Already clear.");
		}
	}

	/**
	 * Exits program.
	 */
	private void exit() {
		System.exit(1);
	}

	public static void main(String[] args) {
		new Doodle();
	}
	
}