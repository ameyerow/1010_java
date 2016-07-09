package meyerowitz.alpha;

import java.awt.Color;

public class Tile 
{
	private boolean filled;		
	public boolean getFilled() { return filled; }
	
	private Color color;		
	public Color getColor() { return color; }
	
	public static final Color gray = new Color(220, 220, 225);
	public static final Color lime = new Color(50, 205, 50);
	public static final Color green = new Color(0, 128, 0);
	public static final Color blue = new Color(65, 105, 225);
	public static final Color orange = new Color(255, 69, 0);
	public static final Color pink = new Color(220, 20, 60);
	public static final Color red = new Color(139, 0, 0);
	public static final Color yellow = new Color(225, 225, 0);
	
	public static final int size = 30;
	public static final int tileOffset = 2;
	public static final int edgeOffset = 15;
	
	public Tile()
	{
		this(false, gray);
	}
	
	public Tile(boolean filled, Color color)
	{
		this.filled = filled;
		this.color = color;
	}
}
