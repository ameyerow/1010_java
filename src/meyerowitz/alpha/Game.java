package meyerowitz.alpha;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.concurrent.*;
import javax.swing.*;

public class Game extends JPanel implements MouseListener
{	
	private static final long serialVersionUID = 7L;
	
	private Solver solver;
	private Tile[][] board;
	private Shape[] shapes;
	private Rectangle[][] hitbox;
	private int score;
	private boolean gameOver;
	
	public Game()
	{
		addMouseListener(this);
		score = 0;
		gameOver = false;
		board = new Tile[10][10];
		
		for(int i = 0; i < 10; i++)
			for(int j = 0; j < 10; j ++)
				board[i][j] = new Tile();
		
		// create hitbox for the board; each tile has its respective hitbox with the 
		// same index -- board[x][y] correlates to hitbox[x][y]
		hitbox = new Rectangle[10][10];
		for(int i = 0; i < 10; i++)
			for(int j = 0; j < 10; j++)
			{
				int xOffset = offsetCoords(i, board[i][j]);
				int yOffset = offsetCoords(j, board[i][j]) + 80;
				hitbox[i][j] = new Rectangle(xOffset, yOffset, board[i][j].getSize(), board[i][j].getSize());
			}
		
		shapes = new Shape[3];
		
		for(int i = 0; i < 3; i++)
			shapes[i] = new Shape(i);
		
		// Create Solver and give it the shapes
		solver = new Solver(shapes, shapes); //INCORRECT: have to find a way to get a Shape[] of every possible shape
		
		Runnable runnable = new Runnable()
		{
			@Override
			public void run() 
			{
				repaint();	
			}	
		};
		ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
		executor.scheduleAtFixedRate(runnable, 0, 33, TimeUnit.MILLISECONDS);	
	}
	
	private boolean checkAnyPlaceable()
	{
		// Goes through each shape and checks if they can be placed in any position in the entire
		// board. If this is not possible for any shape in any position it returns false.
		for(Shape shape: shapes)
		{
			if(shape != null)
				for(int i = 0; i < 10; i++)
					for(int j = 0; j < 10; j++)
					{				
						boolean placeable = true;
						for(int a = 0; a < shape.getTiles().length; a++)
						{
							for(int b = 0; b < shape.getTiles().length; b++)
							{
								if(placeable)
									if(shape.getTiles()[a][b] != null)
									{
										if((i + a) < 10 & (j + b) < 10)
											placeable = board[i + a][j + b].getFilled() ? false : true;
										else
											placeable = false;
									}
							}
						}
						if(placeable)
							return true;
					}
		}
		return false;
	}
	
	@Override
	public void paint(Graphics g)
	{
		super.paint(g);
		g.setColor(new Color(250, 250, 255));
		g.fillRect(0, 0, this.getSize().width, this.getSize().height);
		int length = Integer.toString(score).length();
		g.setFont(new Font("Abadi MT Condensed Light", Font.PLAIN, 50));
		g.setColor(Tile.blue);
		g.drawString(Integer.toString(score), (this.getSize().width/2) - (14 * length), 80);
		
		/*Graphics2D g2D = (Graphics2D) g;
		g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2D.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
		g2D.setColor(Tile.lime);
		g2D.fillRoundRect(300, 10, 30, 30, 10, 10);
		*/
		
		for(int i = 0; i < 10; i++)
			for(int j = 0; j < 10; j++)
				paintTile(g, board[i][j], i, j);
		
		for(int i = 0; i < 3; i++)
			if(shapes[i] != null)
			{
				if(!shapes[i].getLifted())
					paintShape(g, shapes[i], i);
				else
					paintLiftedShape(g, shapes[i]);
			}
		
		if(gameOver)
			paintRestartButton(g);
	}
	
	private void paintTile(Graphics g, Tile tile, int x, int y)
	{
		Graphics2D g2D = (Graphics2D) g;
		g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2D.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
		int xOffset = offsetCoords(x, tile);
		int yOffset = offsetCoords(y, tile) + 80;
		g2D.setColor(tile.getColor());
		g2D.fillRoundRect(xOffset, yOffset, tile.getSize(), tile.getSize(), 10, 10);	
	}
	
	private void paintShape(Graphics g, Shape shape, int index)
	{
		Graphics2D g2D = (Graphics2D) g;
		g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2D.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
		Tile[][] tiles = shape.getTiles();
		for(int i = 0; i < tiles.length; i++)
			for(int j = 0; j < tiles.length; j++)
				if(tiles[i][j] != null)
				{
					int xOffset = offsetCoords(i, tiles[i][j]) + (115 * index);
					int yOffset = offsetCoords(j, tiles[i][j]) + 430;
					g2D.setColor(tiles[i][j].getColor());
					g2D.fillRoundRect(xOffset, yOffset, tiles[i][j].getSize(), tiles[i][j].getSize(), 8, 8);
				}			
	}
	
	private void paintLiftedShape(Graphics g, Shape shape)
	{
		Graphics2D g2D = (Graphics2D) g;
		g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2D.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
		Tile[][] tiles = shape.getTiles();
		for(int i = 0; i < tiles.length; i++)
			for(int j = 0; j < tiles.length; j++)
				if(tiles[i][j] != null)
				{
					int xOffset = (int) (offsetCoords(i, tiles[i][j]) + 
							(MouseInfo.getPointerInfo().getLocation().getX()) - this.getLocationOnScreen().getX()) - 10;
					int yOffset = (int) (offsetCoords(j, tiles[i][j]) + 
							(MouseInfo.getPointerInfo().getLocation().getY()) - this.getLocationOnScreen().getY()) - 10;
					g2D.setColor(tiles[i][j].getColor());
					g2D.fillRoundRect(xOffset, yOffset, tiles[i][j].getSize(), tiles[i][j].getSize(), 10, 10);
				}			
	}
	
	private void paintRestartButton(Graphics g)
	{
		Graphics2D g2D = (Graphics2D) g;
		g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2D.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
		g2D.setColor(new Color(250, 250, 255, 150));
		g2D.fillRect(0, 0, this.getSize().width, this.getSize().height);
		g2D.setColor(Tile.lime);
		g2D.fillRoundRect(100, 175, 150, 150, 30, 30);
		Color white = new Color(250, 250, 255);
		g2D.setColor(white);
		g2D.fillOval(138, 213, 74, 74);
		g2D.setColor(Tile.lime);
		g2D.fillOval(145, 220, 60, 60);
		int[] xpoints = {141, 161, 170, 169, 131};
		int[] ypoints = {278, 258, 257, 306, 305};
		Polygon polygon = new Polygon(xpoints, ypoints, 5);
		g2D.fillPolygon(polygon);
		int[] xpoints2 = {161, 175, 175};
		int[] ypoints2 = {283, 272, 294};
		Polygon triangle = new Polygon(xpoints2, ypoints2, 3);
		g2D.setColor(white);
		g2D.fillPolygon(triangle);
	}
	
	private int offsetCoords(int arg, Tile tile)
	{
		return arg * (tile.getSize() + tile.getTileOffset()) + tile.getEdgeOffset();
	}

	@Override
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	
	public void mouseClicked(MouseEvent e) 
	{
		if(gameOver)
		{
			Point point = new Point(e.getX(), e.getY());
			Rectangle restartHitbox = new Rectangle(100, 175, 150, 150);
			if(restartHitbox.contains(point))
			{
				// Clears the shapes and the board.
				shapes = null;
				board = null;
				
				// Creates a new board and new shapes.
				score = 0;
				gameOver = false;
				board = new Tile[10][10];
				
				for(int i = 0; i < 10; i++)
					for(int j = 0; j < 10; j ++)
						board[i][j] = new Tile();
				
				hitbox = new Rectangle[10][10];
				for(int i = 0; i < 10; i++)
					for(int j = 0; j < 10; j++)
					{
						int xOffset = offsetCoords(i, board[i][j]);
						int yOffset = offsetCoords(j, board[i][j]) + 80;
						hitbox[i][j] = new Rectangle(xOffset, yOffset, board[i][j].getSize(), board[i][j].getSize());
					}
				
				shapes = new Shape[3];
				
				for(int i = 0; i < 3; i++)
					shapes[i] = new Shape(i);
			}
		}
	}
	
	public void mousePressed(MouseEvent e) 
	{
		if(!gameOver)
		{
			Point point = new Point(e.getX(), e.getY());
			
			for(Shape shape: shapes)
				if(shape != null && shape.contains(point))
				{
					shape.setLifted(true);	
				}
		}
	}

	public void mouseReleased(MouseEvent e) 
	{
		if(!gameOver)
		{
			// Checks to see if the lifted shape is placeable in the current location. If
			// not, it returns to its original location and size.
			Point point = new Point(e.getX(), e.getY());
			boolean arg = false;
			for(Shape shape: shapes)
				if(shape != null & !arg)
					arg = shape.getLifted();
			if(arg)
				for(int i = 0; i < 10; i++)
					for(int j = 0; j < 10; j++)
						if(hitbox[i][j].contains(point))
						{
							int index = 0;
							for(int a = 0; a < 3; a++)
								if(shapes[a] != null && shapes[a].getLifted())
									index = a;
							
							boolean placeable = true;
							for(int a = 0; a < shapes[index].getTiles().length; a++)
								for(int b = 0; b < shapes[index].getTiles().length; b++)
									if(placeable)
										if(shapes[index].getTiles()[a][b] != null)
										{
											if((i + a) < 10 & (j + b) < 10)
												placeable = board[i + a][j + b].getFilled() ? false : true;
											else
												placeable = false;
										}
										
							if(placeable)
							{
								for(int a = 0; a < shapes[index].getTiles().length; a++)
									for(int b = 0; b < shapes[index].getTiles().length; b++)
										if(shapes[index].getTiles()[a][b] != null)
										{
											board[i + a][j + b].setColor(shapes[index].getTiles()[a][b].getColor());
											board[i + a][j + b].setFilled(true);
										}
								score += shapes[index].getValue();
								shapes[index] = null;
							}
						}	
			
			for(Shape shape: shapes)
				if(shape != null && shape.getLifted())
					shape.setLifted(false);
			
			// Generates new shapes if all shapes are null -- if there are no remaining shapes
			boolean noShapes = true;
			for(Shape shape: shapes)
				if(shape != null)
					noShapes = false;
			if(noShapes)
				for(int i = 0; i < 3; i++)
					shapes[i] = new Shape(i);
			
			ArrayList<Tile> tiles = new ArrayList<Tile>();
			// Checks every column to see if one is full and stores the tiles to be set to empty
			// after the rows are checked. They need to be kept full in case both a row and column
			// with overlaping tiles are both full.
			for(int i = 0; i < 10; i++)
				for(int j = 0; j < 10; j++)
				{
					if(board[i][j].getFilled())
					{
						if(j == 9)
							for(int a = 0; a < 10; a++)
								tiles.add(board[i][a]);
					}
					else { break; }
				}
			// Checks every row to see if one is full and stores the tiles to be set to empty.
			for(int i = 0; i < 10; i++)
				for(int j = 0; j < 10; j++)
				{
					if(board[j][i].getFilled())
					{
						if(j == 9)
							for(int a = 0; a < 10; a++)
								tiles.add(board[a][i]);
					}
					else { break; }
				}
			// Removes every full row and column.
			for(Tile tile: tiles)
			{
				if(tile.getFilled())
					score ++;
				tile.setColor(Tile.gray);
				tile.setFilled(false);
			}
			
			// Ends the game if you can't place any tiles
			if(!checkAnyPlaceable())
				gameOver = true;
		}
	}
	
	public static void main(String[] args)
	{
		JFrame game = new JFrame();
		game.setTitle("1010!");
		game.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		game.setSize(355, 600);
		game.setResizable(false);
		game.add(new Game());
		game.setLocationRelativeTo(null);
		game.setVisible(true); 
	}
}
