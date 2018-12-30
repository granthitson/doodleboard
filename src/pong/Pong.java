package pong;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.UIManager;

public class Pong extends JFrame {
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

	protected static final int WIDTH = 700;
	protected final static int HEIGHT = 500;
	public static PongPanel panel;
	public static Pong pong;

	private int score1, score2;
	protected int scoreLimit = 10, playerWon;
	private Pong game;
	private Ball ball;
	private Racket player1, player2;

	public boolean w, s, up, down;
	public boolean computer = false;
	private int gameStatus = 0;// 0 = stopped, 1 = Paused, 2 = Playing
	protected ArrayList<String> leaderboard;
	protected String wName;

	/**
	 * Sets up the frame of the board along with the title. Also makes the rackets,
	 * ball, and score board visible.
	 */
	public Pong() {
		setSize(WIDTH, HEIGHT + 30);
		setTitle("Pong");
		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());
		contentPane.setBackground(Color.black);

		panel = new PongPanel(this);
		contentPane.add(panel);

//		contentPane.setLocation(200, -200);
		setResizable(false);
		setVisible(true);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	}

	public void showLeaderboard() {
		panel.showLeaderboard();
	}

	public static void main(String[] args) {
		pong = new Pong();
	}

	public PongPanel getPanel() {
		return panel;
	}

	public void paint(Graphics g) {
		super.paint(g);
		g.setColor(Color.black);
		g.fillRect(0, 0, WIDTH, HEIGHT);
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////

	public class PongPanel extends JPanel implements ActionListener, KeyListener {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private Pong game;
		private Leaderboard board;
		public String wName;

		{
			try {
				UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		public PongPanel(Pong game) {
			setBackground(Color.black);

			this.game = game;

			startGame();
			Timer timer = new Timer(5, this);
			timer.start();

			board = new Leaderboard();
			board.displayScore();
			board.setVisible(false);

			addKeyListener(this);
			setFocusable(true);
		}

		public void showLeaderboard() {
			board.setVisible(true);
		}

		public void hideLeaderboard() {
			board.setVisible(false);
		}

		public void startGame() {
			player1 = new Racket(game, 1);
			player2 = new Racket(game, 2);
			ball = new Ball(game);
		}

		protected void increaseScore(int playerNo) {
			if (playerNo == 1)
				score1++;
			else
				score2++;
		}

		protected int getScore(int playerNo) {
			if (playerNo == 1)
				return score1;
			else
				return score2;
		}

		public void actionPerformed(ActionEvent e) {
			if (gameStatus == 1) {
				update();
			}
			repaint();
		}

		/**
		 * Updates player 1 and player 2's movements. Resets the score back to 0 for
		 * each new game. Displays the winner once player 1 or 2 wins.
		 */
		protected void update() {
			ball.update(player1, player2);
			if (w) {
				player1.move(true);
			}

			if (s) {
				player1.move(false);
			}

			if (up) {
				player2.move(true);
			}

			if (down) {
				player2.move(false);
			}

			if (player1.score >= scoreLimit) {
				playerWon = 1;
			}

			if (player2.score >= scoreLimit) {
				playerWon = 2;
			}

			if (player1.score == 2) {
				player1.score = 0;
				player2.score = 0;
				JOptionPane.showMessageDialog(null, "Player 1 is the winner!", "Pong", JOptionPane.PLAIN_MESSAGE);
				wName = JOptionPane.showInputDialog("Enter Winners Name: ");
				leaderboard = new ArrayList<String>();
				leaderboard.add(wName);

				board.saveScore(wName);
				board.displayScore();
				showLeaderboard();

				gameStatus = 0;
				System.out.println(leaderboard);
			}

			else if (player2.score == 2) {
				player1.score = 0;
				player2.score = 0;
				JOptionPane.showMessageDialog(null, "Player 2 is the winner!", "Pong", JOptionPane.PLAIN_MESSAGE);
				wName = JOptionPane.showInputDialog("Enter Winners Name: ");

				leaderboard = new ArrayList<String>();
				leaderboard.add(wName);

				board.saveScore(wName);
				board.displayScore();
				showLeaderboard();

				gameStatus = 0;
				System.out.println(leaderboard);
			}
		}

		/**
		 * Keeps track of the keys pressed to move the rackets up and down the screen.
		 */
		public void keyPressed(KeyEvent e) {
			int id = e.getKeyCode();
			if (id == KeyEvent.VK_W) {
				w = true;
			}

			if (id == KeyEvent.VK_S) {
				s = true;
			}

			if (id == KeyEvent.VK_UP) {
				up = true;
			}

			if (id == KeyEvent.VK_DOWN) {
				down = true;
			}

			if (id == KeyEvent.VK_SPACE) {
				if (gameStatus == 0) {
					gameStatus = 2;
				}
				if (gameStatus == 1) {
					gameStatus = 2;
				} else if (gameStatus == 2) {
					gameStatus = 1;
				}
			}

			if (id == KeyEvent.VK_SHIFT) {
				// game option here

				computer = true;
			}
		}

		public void keyReleased(KeyEvent e) {
			int id = e.getKeyCode();
			if (id == KeyEvent.VK_W) {
				w = false;
			}

			if (id == KeyEvent.VK_S) {
				s = false;
			}

			if (id == KeyEvent.VK_UP) {
				up = false;
			}

			if (id == KeyEvent.VK_DOWN) {
				down = false;
			}
		}

		public void keyTyped(KeyEvent e) {
			;
		}

		public void paintComponent(Graphics g) {
			super.paintComponent(g);

			if (gameStatus == 0) {
				g.setColor(Color.WHITE);
				g.setFont(new Font("Arial", 1, 25));
				g.drawString("PONG", Pong.WIDTH / 2 - 60, Pong.HEIGHT / 2);
				g.drawString("Press Space to Play", Pong.WIDTH / 2 - 125, Pong.HEIGHT / 2 + 50);
				startGame();
			}
			if (gameStatus == 2) {
				g.setColor(Color.WHITE);
				g.setFont(new Font("Arial", 1, 50));
				g.drawString("Paused", Pong.WIDTH / 2 - 90, Pong.HEIGHT / 2 - 50);
			}
			if (gameStatus == 1 || gameStatus == 2) {
				g.setColor(Color.WHITE);
				g.drawLine(700 / 2, 0, 700 / 2, 550);
				g.drawOval(WIDTH / 2 + 200, HEIGHT / 2 + 100, 300, 300);

				player1.paint(g);
				player2.paint(g);
				ball.paint(g);

				g.setFont(new Font("Arial", 1, 25));
				g.setColor(Color.WHITE);
				g.drawString("Player 1: " + String.valueOf(player1.score), WIDTH / 2 + 175, 40);
				g.drawString("Player 2: " + String.valueOf(player2.score), WIDTH / 2 + 400, 40);
			}
		}

		public ArrayList<String> getwName() {
			return leaderboard;
		}

		///////////////////////////////////////////////////////////////////////////////////////////////////////
		///////////////////////////////////////////////////////////////////////////////////////////////////////
		///////////////////////////////////////////////////////////////////////////////////////////////////////
		///////////////////////////////////////////////////////////////////////////////////////////////////////
		///////////////////////////////////////////////////////////////////////////////////////////////////////
		///////////////////////////////////////////////////////////////////////////////////////////////////////
		///////////////////////////////////////////////////////////////////////////////////////////////////////
		///////////////////////////////////////////////////////////////////////////////////////////////////////
		///////////////////////////////////////////////////////////////////////////////////////////////////////
		///////////////////////////////////////////////////////////////////////////////////////////////////////

		public class Leaderboard extends JFrame {
			// keep track of the number of wins per player
			// needs to PERMANENTLY save the scores in a separate text
			// file from PongPanel
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

			protected static final int WIDTH = 700;
			protected final static int HEIGHT = 500;

			protected String label, fileName;
			protected Leaderboard lb;
			protected DisplayBoard display;
			protected Racket player1, player2;
			protected int bp = 0;
			protected int gh = 0;
			protected int jl = 0;
			protected int jj = 0;
			protected int jm = 0;
			protected int c = 0;

			public Leaderboard() {
				setSize(WIDTH, HEIGHT);
				setTitle("Leaderboard");
				Container contentPane = getContentPane();
				contentPane.setLayout(new BorderLayout());
				contentPane.setBackground(Color.black);

				display = new DisplayBoard();
				contentPane.add(display);

				// contentPane.setLocation(500, -200);
				setResizable(false);
				setVisible(true);
				setDefaultCloseOperation(DISPOSE_ON_CLOSE);
				
			}

			public Leaderboard(int bp, int gh, int jl, int jj, int jm, int c) {
				this.bp = bp;
				this.gh = gh;
				this.jl = jl;
				this.jj = jj;
				this.jm = jm;
				this.c = c;
			}

			public void main(String[] args) {
				lb = new Leaderboard();
			}

			public File saveScore(String fileName) {
				File scores = null;
				String line = null;
				int lineNumber = 0;
				PrintWriter saveOut = null;
				try {
					BufferedReader br = new BufferedReader(new FileReader("src/Wins.txt"));
					//read line by line
					while((line = br.readLine()) != null) {
						lineNumber++;
						//System.out.println("Line " + lineNumber + " : " + line);
					}
					scores = new File("src/Wins.txt");
					FileWriter fw = new FileWriter(scores, true);//want to add to file
					BufferedWriter bw = new BufferedWriter(fw);
					saveOut = new PrintWriter(bw);
					
					for (int i = 0; i < leaderboard.size(); i++) {
						wName = leaderboard.get(i);// turns into string
						saveOut.println("Player: " + panel.wName);
						
						//saveOut.printf("Winner's Name: " + "%d%n", wName);
					}
					br.close();
					bw.close();
					fw.close();
					saveOut.close();// can't write after this; data flushed
					System.out.println("Done");
					
					
				}

				catch (IOException f) {
					System.err.println("File not found" + f + "\n");
				}
				return scores;
			}

			public void displayScore() {
				BufferedReader br = null;
				String line = null;
				
				// reads in lines here
				// iterate over names to get leaderboard count
				try {
					br = new BufferedReader(new FileReader("src/Wins.txt"));
					while ((line = br.readLine()) != null) {
						if(line.contains("Brittany")) {
							bp++;
						}
						
						else if(line.contains("Grant")) {
							gh++;
						}
						
						else if(line.contains("Jamie")) {
							jl++;
						}
						else if(line.contains("Josh J")) {
							jj++;
						}
						
						else if(line.contains("Josh M")) {
							jm++;
						}
						
						else if(line.contains("Computer") || line.contains("computer")) {
							c++;
						}
						continue;
					}
					br.close();
				}

				catch (IOException io) {
					System.err.println(io + "Error reading file");
				} finally {
					repaint();
				}
			}

			class DisplayBoard extends JPanel {
				public DisplayBoard() {
					setBackground(Color.black);
				}

				protected void update() {
					//lb.displayScore("Wins.txt");
					displayScore();
				}

				public void paintComponent(Graphics g) {
					super.paintComponent(g);
					
					//displayScore();

					g.setColor(Color.WHITE);

					g.setFont(new Font("Arial", 1, 30));
					g.setColor(Color.WHITE);
					g.drawString("Player Stats ", WIDTH / 2 + 250, HEIGHT / 2 + 50);
					
					g.setFont(new Font("Arial", 1, 20));
					g.setColor(Color.WHITE);
					g.drawString("Brittany's Wins: " + String.valueOf(bp), WIDTH / 2 + 15, HEIGHT / 2 + 100);
					g.drawString("Grant's Wins: " + String.valueOf(gh), WIDTH / 2 + 15, HEIGHT / 2 + 200);
					g.drawString("Jaime's Wins: " + String.valueOf(jl), WIDTH / 2 + 15, HEIGHT / 2 + 300);
					g.drawString("Josh J's Wins: " + String.valueOf(jj), WIDTH / 2 + 15, HEIGHT / 2 + 400);
					g.drawString("Josh M's Wins: " + String.valueOf(jm), WIDTH / 2 + 15, HEIGHT / 2 + 500);
					g.drawString("Computer's Wins: " + String.valueOf(c), WIDTH / 2 + 15, HEIGHT / 2 + 600);
				}
			}
		}
	}
}
