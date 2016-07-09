package meyerowitz.alpha;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

public class Game extends JPanel
{	
	private static final long serialVersionUID = 7L;
	
	private Tile[][] board;
	private Shape[] shapes;
	
	public Game()
	{
		board = new Tile[10][10];
		
		for(int i = 0; i < 10; i++)
			for(int j = 0; j < 10; j ++)
				board[i][j] = new Tile();
		
		shapes = new Shape[3];
		
		for(int i = 0; i < 3; i++)
			shapes[i] = new Shape();
		
		repaint();
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
				paintShape(g, shapes[i], i);
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
		{
			for(int j = 0; j < tiles.length; j++)
			{
				if(tiles[i][j] != null)
				{
					int xOffset = offsetCoords(i, tiles[i][j]) + (115 * index);
					int yOffset = offsetCoords(j, tiles[i][j]) + 430;
					g2D.setColor(tiles[i][j].getColor());
					g2D.fillRoundRect(xOffset, yOffset, tiles[i][j].getSize(), tiles[i][j].getSize(), 8, 8);
				}
			}
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
}
