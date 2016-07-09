package meyerowitz.alpha;

import java.awt.Color;

public class Tile 
{
	public static final Color gray = new Color(220, 220, 225);
	public static final Color lime = new Color(50, 205, 50);
	public static final Color green = new Color(0, 128, 0);
	public static final Color blue = new Color(65, 105, 225);
	public static final Color orange = new Color(255, 69, 0);
	public static final Color pink = new Color(220, 20, 60);
	public static final Color red = new Color(139, 0, 0);
	public static final Color yellow = new Color(225, 225, 0);
	public static final Color purple = new Color(72, 61, 139);
	public static final Color teal = new Color(98, 238, 214);
	
	private boolean filled;		
	public boolean getFilled() { return filled; }
	public void setFilled(boolean filled) { this.filled = filled; }
	
	private Color color;		
	public Color getColor() { return color; }
	public void setColor(Color color) { this.color = color; }
	
	private int size = 30;
	public int getSize() { return size; }
	public void setSize(int size) { this.size = size; }
	
	private int tileOffset = 2;
	public int getTileOffset() { return tileOffset; }
	public void setTileOffset(int tileOffset) { this.tileOffset = tileOffset; }
	
	private int edgeOffset = 15;
	public int getEdgeOffset() { return edgeOffset; }
	public void setEdgeOffset(int edgeOffset) { this.edgeOffset = edgeOffset; }
	
	
	// Constructor is used to easily generate empty tiles at the games instantiation.
	public Tile()
	{
		this(false, gray);
	}
	
	// Constructor for board tiles 
	public Tile(boolean filled, Color color)
	{
		this.filled = filled;
		this.color = color;
	}
	
	// Constructor for shape tiles
	public Tile(Color color)
	{
		this.color = color;
		size = 15;
		tileOffset = 1;
		edgeOffset = 0;
	}
}
