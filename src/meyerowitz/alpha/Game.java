package meyerowitz.alpha;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.concurrent.*;
import javax.swing.*;

public class Game extends JPanel implements MouseListener {	
	private static final long serialVersionUID = 7L;
	
	private final static int WIDTH = 355;
	private final static int HEIGHT = 600;
	private final static double SCALE = 1; 
	private Tile[][] mBoard;
	private Shape[] mShapes;
	private Rectangle[][] mHitbox;
	private int mScore;
	private int mHighScore;
	private int mTotalScore;
	private int mTries;
	private boolean mGameOver;
	private Color mSolverColor;
	private int mSolverMode;
	
	
	public Game() {
		addMouseListener(this);
		
		mSolverMode = 0;
		mTotalScore = 0;
		mTries = -1;
		mSolverColor = new Color(105, 105, 105);
		
		init();
		
		Runnable runnable = new Runnable(){
			@Override
			public void run() {
				repaint();	
			}	
		};
		ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
		executor.scheduleAtFixedRate(runnable, 0, 33, TimeUnit.MILLISECONDS);	
	}
	
	private void init()
	{
		if(mScore > mHighScore)
			mHighScore = mScore;
		
		mTotalScore += mScore;
		mTries++;
		
		// Clears the shapes and the board.
		mShapes = null;
		mBoard = null;
		
		// Creates a new board and new shapes.
		mScore = 0;
		mGameOver = false;
		mBoard = new Tile[10][10];
		
		for(int i = 0; i < 10; i++)
			for(int j = 0; j < 10; j ++)
				mBoard[i][j] = new Tile();
		
		mHitbox = new Rectangle[10][10];
		for(int i = 0; i < 10; i++)
			for(int j = 0; j < 10; j++) {
				int xOffset = offsetCoords(i, mBoard[i][j]);
				int yOffset = offsetCoords(j, mBoard[i][j]) + 80;
				mHitbox[i][j] = new Rectangle(scale(xOffset), scale(yOffset), mBoard[i][j].getSize(), mBoard[i][j].getSize());
			}
		
		mShapes = new Shape[3];
		
		for(int i = 0; i < 3; i++)
			mShapes[i] = new Shape(i);
	}
	
	private boolean checkAnyPlaceable() {
		// Goes through each shape and checks if they can be placed in any position in the entire
		// board. If this is not possible for any shape in any position it returns false.
		for(Shape shape: mShapes) {
			if(shape != null) {
				for(int i = 0; i < 10; i++) {
					for(int j = 0; j < 10; j++) {				
						boolean placeable = true;
					
						for(int a = 0; a < shape.getTiles().length; a++) {
							for(int b = 0; b < shape.getTiles().length; b++) {
								if(placeable) {
									if(shape.getTiles()[a][b] != null) {
										if((i + a) < 10 & (j + b) < 10)
											placeable = mBoard[i + a][j + b].getFilled() ? false : true;
										else
											placeable = false;
									}
								}
							}
						}
						if(placeable)
							return true;
					}
				}
			}
		}
		return false;
	}
	
	private void removeFullRowsAndColumns() {
		ArrayList<Tile> tiles = new ArrayList<Tile>();
		// Checks every column to see if one is full and stores the tiles to be set to empty
		// after the rows are checked. They need to be kept full in case both a row and column
		// with overlapping tiles are both full.
		for(int i = 0; i < 10; i++) {
			for(int j = 0; j < 10; j++) {
				if(mBoard[i][j].getFilled()) {
					if(j == 9)
						for(int a = 0; a < 10; a++)
							tiles.add(mBoard[i][a]);
				} else break; 
			}
		}
		// Checks every row to see if one is full and stores the tiles to be set to empty.
		for(int i = 0; i < 10; i++) {
			for(int j = 0; j < 10; j++) {
				if(mBoard[j][i].getFilled()) {
					if(j == 9)
						for(int a = 0; a < 10; a++)
							tiles.add(mBoard[a][i]);
				} else break;
			}
		}
		// Removes every full row and column.
		for(Tile tile: tiles) {
			if(tile.getFilled())
				mScore++;
			tile.setColor(Tile.gray);
			tile.setFilled(false);
		}
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		g.setColor(new Color(250, 250, 255));
		g.fillRect(0, 0, scale(WIDTH), scale(HEIGHT));
		
		Graphics2D g2D = (Graphics2D) g;
		g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2D.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
		
		int length = Integer.toString(mScore).length();
		g2D.setFont(new Font("Abadi MT Condensed Light", Font.PLAIN, scale(50)));
		g2D.setColor(Tile.blue);
		g2D.drawString(Integer.toString(mScore), scale((WIDTH/4) - (14 * length) + 16), scale(80));
		
		length = Integer.toString(mHighScore).length();
		g2D.setColor(Tile.lime);
		g2D.drawString(Integer.toString(mHighScore), scale((WIDTH/4)*3 - (14 * length)) , scale(80));
		
		int average = mTries != 0 ? mTotalScore / mTries : 0;
		g2D.setColor(Tile.blue);
		g2D.setFont(new Font("Abadi MT Condensed Light", Font.PLAIN, scale(25)));
		g2D.drawString(Integer.toString(average), scale(20), scale(HEIGHT) - scale(40));
		
		
		paintSolverButton(g);
		
		for(int i = 0; i < 10; i++)
			for(int j = 0; j < 10; j++)
				paintTile(g, mBoard[i][j], i, j);
		
		for(int i = 0; i < 3; i++)
			if(mShapes[i] != null) {
				if(!mShapes[i].getLifted())
					paintShape(g, mShapes[i], i);
				else
					paintLiftedShape(g, mShapes[i]);
			}
		
		if(mGameOver)
			paintRestartButton(g);
	}
	
	private void paintSolverButton(Graphics g) {
		Graphics2D g2D = (Graphics2D) g;
		g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2D.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
		g2D.setColor(mSolverColor);
		g2D.fillRoundRect(scale(322), scale(27), scale(10), scale(25), scale(10), scale(10));
		g2D.fillOval(scale(322), scale(15), scale(10), scale(10));
	}
	
	private void paintTile(Graphics g, Tile tile, int x, int y) {
		Graphics2D g2D = (Graphics2D) g;
		g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2D.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
		int xOffset = offsetCoords(x, tile);
		int yOffset = offsetCoords(y, tile) + 80;
		g2D.setColor(tile.getColor());
		g2D.fillRoundRect(scale(xOffset), scale(yOffset), scale(tile.getSize()), scale(tile.getSize()), scale(10), scale(10));	
	}
	
	private void paintShape(Graphics g, Shape shape, int index) {
		Graphics2D g2D = (Graphics2D) g;
		g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2D.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
		Tile[][] tiles = shape.getTiles();
		for(int i = 0; i < tiles.length; i++)
			for(int j = 0; j < tiles.length; j++)
				if(tiles[i][j] != null) {
					int xOffset = offsetCoords(i, tiles[i][j]) + (115 * index);
					int yOffset = offsetCoords(j, tiles[i][j]) + 430;
					g2D.setColor(tiles[i][j].getColor());
					g2D.fillRoundRect(scale(xOffset), scale(yOffset), scale(tiles[i][j].getSize()), scale(tiles[i][j].getSize()), scale(8), scale(8));
				}			
	}
	
	private void paintLiftedShape(Graphics g, Shape shape) {
		Graphics2D g2D = (Graphics2D) g;
		g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2D.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
		Tile[][] tiles = shape.getTiles();
		for(int i = 0; i < tiles.length; i++)
			for(int j = 0; j < tiles.length; j++)
				if(tiles[i][j] != null) {
					int xOffset = (int) (offsetCoordsLifted(i, tiles[i][j]) + 
							(MouseInfo.getPointerInfo().getLocation().getX()) - this.getLocationOnScreen().getX()) - 10;
					int yOffset = (int) (offsetCoordsLifted(j, tiles[i][j]) + 
							(MouseInfo.getPointerInfo().getLocation().getY()) - this.getLocationOnScreen().getY()) - 10;
					g2D.setColor(tiles[i][j].getColor());
					g2D.fillRoundRect(xOffset, yOffset, scale(tiles[i][j].getSize()), scale(tiles[i][j].getSize()), scale(10), scale(10));
				}			
	}
	
	private void paintRestartButton(Graphics g) {
		Graphics2D g2D = (Graphics2D) g;
		g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2D.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
		g2D.setColor(new Color(250, 250, 255, 150));
		g2D.fillRect(0, 0, scale(WIDTH), scale(HEIGHT));
		g2D.setColor(Tile.lime);
		g2D.fillRoundRect(scale(100), scale(175), scale(150), scale(150), scale(30), scale(30));
		Color white = new Color(250, 250, 255);
		g2D.setColor(white);
		g2D.fillOval(scale(138), scale(213), scale(74), scale(74));
		g2D.setColor(Tile.lime);
		g2D.fillOval(scale(145), scale(220), scale(60), scale(60));
		int[] xpoints = {scale(141), scale(161), scale(170), scale(169), scale(131)};
		int[] ypoints = {scale(278), scale(258), scale(257), scale(306), scale(305)};
		Polygon polygon = new Polygon(xpoints, ypoints, 5);
		g2D.fillPolygon(polygon);
		int[] xpoints2 = {scale(161), scale(175), scale(175)};
		int[] ypoints2 = {scale(283), scale(272), scale(294)};
		Polygon triangle = new Polygon(xpoints2, ypoints2, 3);
		g2D.setColor(white);
		g2D.fillPolygon(triangle);
	}
	
	private int offsetCoords(int arg, Tile tile) {
		return arg * (tile.getSize() + tile.getTileOffset()) + tile.getEdgeOffset();
	}

	private int offsetCoordsLifted(int arg, Tile tile) {
		return arg * (scale(tile.getSize()) + scale(tile.getTileOffset())) + scale(tile.getEdgeOffset());
	}
	
	@Override
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	
	public void mouseClicked(MouseEvent e) {
		Point point = new Point(e.getX(), e.getY());
		if(!mGameOver) {
			Rectangle solverHitbox = new Rectangle(scale(322), scale(15), scale(10), scale(37));
			if(solverHitbox.contains(point)) {
				Runnable runnable = new Runnable() {
					@Override
					public void run() {
						while(mSolverMode!= 0) {
							// A solver is generated with the board and the current shapes. It uses these 
							// to find the "best moves" which it transmits as a int[][]
							Solver solver = new Solver(mBoard, mShapes);
							ArrayList<int[]> moves = solver.findBestMoves();
							
							for(int[] move : moves) {
								if(move != null) {
									for(int a = 0; a < mShapes[move[0]].getTiles().length; a++)
										for(int b = 0; b < mShapes[move[0]].getTiles().length; b++)
											if(mShapes[move[0]].getTiles()[a][b] != null) {
												mBoard[move[1] + a][move[2] + b].setColor(mShapes[move[0]].getTiles()[a][b].getColor());
												mBoard[move[1] + a][move[2] + b].setFilled(true);
											}
									mScore += mShapes[move[0]].getValue();
									mShapes[move[0]] = null;
									
									if(mSolverMode == 1) 
										try {
										    Thread.sleep(250);              
										} catch(InterruptedException e) {
										    Thread.currentThread().interrupt();
										}
									
									removeFullRowsAndColumns();
								}
								
								if(mSolverMode == 1)
									try {
									    Thread.sleep(400);              
									} catch(InterruptedException e) {
									    Thread.currentThread().interrupt();
									}
							}
							
							boolean tilesRemaining = false;
							for(int j = 0; j < 3; j++)
								if(mShapes[j] != null)
									tilesRemaining = true;
							
							if(!tilesRemaining)
								for(int i = 0; i < 3; i++)
									mShapes[i] = new Shape(i);
							
							if(!checkAnyPlaceable()) 
								if(mSolverMode == 2) {
									init();
								} else {
									mGameOver = true;
									mSolverMode = 0;
									mSolverColor = Tile.darkgray;
								}
							
							
							if(mSolverMode == 1)
								try {
								    Thread.sleep(400);              
								} catch(InterruptedException e) {
								    Thread.currentThread().interrupt();
								}
						}
					}
				};
				Thread thread = new Thread(runnable);
				
				if(mSolverMode == 0) {
					mSolverColor = Tile.turquois;
					mSolverMode = 1;
					thread.start();
				} else if(mSolverMode == 1){
					mSolverColor = Tile.red;
					mSolverMode = 2;
				} else {
					mSolverColor = Tile.darkgray;
					mSolverMode = 0;
				}
			}
		} else {
			Rectangle restartHitbox = new Rectangle(scale(100), scale(175), scale(150), scale(150));
			if(restartHitbox.contains(point)) {
				init();
			}
		}
	}
	
	public void mousePressed(MouseEvent e) {
		if(!mGameOver) {
			Point point = new Point(e.getX(), e.getY());
			
			for(Shape shape: mShapes)
				if(shape != null && shape.contains(point)) {
					shape.setLifted(true);	
				}
		}
	}

	public void mouseReleased(MouseEvent e) {
		if(!mGameOver) {
			// Checks to see if the lifted shape is placeable in the current location. If
			// not, it returns to its original location and size.
			Point point = new Point(e.getX(), e.getY());
			boolean arg = false;
			for(Shape shape: mShapes)
				if(shape != null & !arg)
					arg = shape.getLifted();
			if(arg)
				for(int i = 0; i < 10; i++)
					for(int j = 0; j < 10; j++)
						if(mHitbox[i][j].contains(point)) {
							int index = 0;
							for(int a = 0; a < 3; a++)
								if(mShapes[a] != null && mShapes[a].getLifted())
									index = a;
							
							boolean placeable = true;
							for(int a = 0; a < mShapes[index].getTiles().length; a++)
								for(int b = 0; b < mShapes[index].getTiles().length; b++)
									if(placeable)
										if(mShapes[index].getTiles()[a][b] != null) {
											if((i + a) < 10 & (j + b) < 10)
												placeable = mBoard[i + a][j + b].getFilled() ? false : true;
											else
												placeable = false;
										}
										
							if(placeable) {
								for(int a = 0; a < mShapes[index].getTiles().length; a++)
									for(int b = 0; b < mShapes[index].getTiles().length; b++)
										if(mShapes[index].getTiles()[a][b] != null) {
											mBoard[i + a][j + b].setColor(mShapes[index].getTiles()[a][b].getColor());
											mBoard[i + a][j + b].setFilled(true);
										}
								mScore += mShapes[index].getValue();
								mShapes[index] = null;
							}
						}	
			
			for(Shape shape: mShapes)
				if(shape != null && shape.getLifted())
					shape.setLifted(false);

			removeFullRowsAndColumns();
			
			// Generates new shapes if all shapes are null -- if there are no remaining shapes
			boolean noShapes = true;
				for(Shape shape: mShapes)
					if(shape != null)
						noShapes = false;
				if(noShapes)
					for(int i = 0; i < 3; i++)
						mShapes[i] = new Shape(i);
										
			
			// Ends the game if you can't place any tiles
			if(!checkAnyPlaceable())
				mGameOver = true;
		}
	}
	
	public static int scale(int arg)
	{
		return (int)(arg * SCALE);
	}
	
	public static void main(String[] args) {
		JFrame game = new JFrame();
		game.setTitle("1010!");
		game.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		game.setSize(scale(WIDTH), scale(HEIGHT));
		game.setResizable(false);
		game.add(new Game());
		game.setLocationRelativeTo(null);
		game.setVisible(true); 
	}
}
