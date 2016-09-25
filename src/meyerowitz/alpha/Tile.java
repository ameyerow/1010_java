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
	public static final Color yellow = new Color(255, 215, 0);
	public static final Color purple = new Color(72, 61, 139);
	public static final Color teal = new Color(87, 176, 168);
	
	private boolean mFilled;		
	public boolean getFilled() { return mFilled; }
	public void setFilled(boolean filled) { mFilled = filled; }
	
	private Color mColor;		
	public Color getColor() { return mColor; }
	public void setColor(Color color) { mColor = color; }
	
	private int mSize;
	public int getSize() { return mSize; }
	public void setSize(int size) { mSize = size; }
	
	private int mTileOffset;
	public int getTileOffset() { return mTileOffset; }
	public void setTileOffset(int tileOffset) { mTileOffset = tileOffset; }
	
	private int mEdgeOffset;
	public int getEdgeOffset() { return mEdgeOffset; }
	public void setEdgeOffset(int edgeOffset) { mEdgeOffset = edgeOffset; }
	
	// Constructor is used to easily generate empty tiles at the games instantiation.
	public Tile() {
		this(false, gray);
	}
	
	// Constructor for board tiles 
	public Tile(boolean filled, Color color) {
		this.mFilled = filled;
		this.mColor = color;
		mSize = 30;
		mTileOffset = 2;
		mEdgeOffset = 15;
	}
	
	// Constructor for shape tiles
	public Tile(Color color) {
		this.mColor = color;
		mSize = 20;
		mTileOffset = 1;
		mEdgeOffset = 7;
	}
}
