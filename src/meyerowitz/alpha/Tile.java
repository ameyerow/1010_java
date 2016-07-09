package meyerowitz.alpha;

import java.awt.Color;

public class Tile 
{
	private boolean filled;		
	public boolean getFilled() { return filled; }
	
	private Color color;		
	public Color getColor() { return color; }
	
	public static final Color gray = new Color(0, 0, 70);
	public static final Color lime = new Color(100, 75, 75);
	public static final Color green = new Color(150, 100, 75);
	public static final Color blue = new Color(195, 100, 75);
	public static final Color orange = new Color(35, 100, 80);
	public static final Color pink = new Color(340, 80, 80);
	public static final Color red = new Color(10, 90, 70);
	public static final Color yellow = new Color(60, 90, 80);
	public static final Color white = new Color(0, 0, 100);
	
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
