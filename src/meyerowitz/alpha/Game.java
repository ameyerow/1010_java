package meyerowitz.alpha;

import java.awt.*;
import java.awt.event.*;
import java.util.concurrent.*;
import javax.swing.*;

public class Game extends JPanel implements MouseListener
{	
	private static final long serialVersionUID = 7L;
	
	private Tile[][] board;
	private Shape[] shapes;
	private Rectangle[][] hitbox;
	
	public Game()
	{
		addMouseListener(this);
		board = new Tile[10][10];
		
		for(int i = 0; i < 10; i++)
			for(int j = 0; j < 10; j ++)
				board[i][j] = new Tile();
		
		// create hitbox for the board (one for every tile)
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
	
	@Override
	public void paint(Graphics g)
	{
		super.paint(g);
		g.setColor(new Color(250, 250, 255));
		g.fillRect(0, 0, this.getSize().width, this.getSize().height);
		
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
	
	private int offsetCoords(int arg, Tile tile)
	{
		return arg * (tile.getSize() + tile.getTileOffset()) + tile.getEdgeOffset();
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

	@Override
	public void mouseClicked(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	
	public void mousePressed(MouseEvent e) 
	{
		Point point = new Point(e.getX(), e.getY());
		
		for(Shape shape: shapes)
			if(shape != null && shape.contains(point))
			{
				shape.setLifted(true);	
			}
	}

	public void mouseReleased(MouseEvent e) 
	{
		Point point = new Point(e.getX(), e.getY());
		for(int i = 0; i < 10; i++)
			for(int j = 0; j < 10; j++)
				if(hitbox[i][j].contains(point))
				{
					int index = 0;
					for(int a = 0; a < 3; a++)
						if(shapes[a] != null && shapes[a].getLifted())
							index = a;
					
					boolean placeable = false;
					for(int a = 0; a < shapes[index].getTiles().length; a++)
						for(int b = 0; b < shapes[index].getTiles().length; b++)
							if(shapes[index].getTiles()[a][b] != null & ((i + a) < 10 & (j + b) < 10))
								placeable = board[i + a][j + b].getFilled() ? false : true;
					
					if(placeable)
					{
						for(int a = 0; a < shapes[index].getTiles().length; a++)
							for(int b = 0; b < shapes[index].getTiles().length; b++)
								if(shapes[index].getTiles()[a][b] != null)
								{
									board[i + a][j + b].setColor(shapes[index].getTiles()[a][b].getColor());
									board[i + a][j + b].setFilled(true);
								}
						shapes[index] = null;
					}
				}
		
		for(Shape shape: shapes)
			if(shape != null && shape.getLifted())
				shape.setLifted(false);
		
		boolean noShapes = true;
		for(Shape shape: shapes)
			if(shape != null)
				noShapes = false;
		if(noShapes)
			for(int i = 0; i < 3; i++)
				shapes[i] = new Shape(i);
		}
}
