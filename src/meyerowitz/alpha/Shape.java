package meyerowitz.alpha;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;

public class Shape 
{
	private Tile[][] mTiles;
	public Tile[][] getTiles() { return mTiles; }
	
	private int mValue;
	public int getValue() { return mValue; }
	
	private Rectangle[] mHitbox;
	
	private boolean mLifted = false;
	public boolean getLifted() { return mLifted; }
	public void setLifted(boolean lifted) 
	{ 
		mLifted = lifted; 
		
		for(int i = 0; i < mTiles.length; i++)
			for(int j = 0; j < mTiles.length; j++)
				if(mTiles[i][j] != null)
				{
					if(lifted == true)
					{
						mTiles[i][j].setSize(27);
						mTiles[i][j].setTileOffset(5);
						mTiles[i][j].setEdgeOffset(0);
					}
					else
					{
						mTiles[i][j].setSize(20);
						mTiles[i][j].setTileOffset(1);
						mTiles[i][j].setEdgeOffset(7);
					}
				}
	}
	
	// Once this constructor is called, a random tile is picked and created by 
	// calling the Shape(int arg) constructor.
	public Shape(int index) 
	{
		this((int)(Math.random() * 19), index); // {0,...,18}
	}
	
	public Shape(int arg, int index)
	{
		switch(arg)
		{
			// 1*1 square
			case 0:
				mTiles = new Tile[1][1];
				mTiles[0][0] = new Tile(Tile.purple);
				break;
				
			// 2*2 square
			case 1:
				mTiles = new Tile[2][2];
				for(int i = 0; i < 2; i++)
					for(int j = 0; j < 2; j++)
						mTiles[i][j] = new Tile(Tile.lime);
				break;
				
			// 3*3 square
			case 2:
				mTiles = new Tile[3][3];
				for(int i = 0; i < 3; i++)
					for(int j = 0; j < 3; j++)
						mTiles[i][j] = new Tile(Tile.teal);
				break;
				
			// 2*2 L, all four possible shapes are accounted for by randomly picking one
			// of the tiles to be set to null. The probability is kept the same by adding
			// four cases.
			case 3: case 4: case 5: case 6:
				mTiles = new Tile[2][2];
				for(int i = 0; i < 2; i++)
					for(int j = 0; j < 2; j++)
						mTiles[i][j] = new Tile(Tile.green);
				mTiles[(int)(Math.random() * 2)][(int)(Math.random() * 2)] = null; // {0,1}
				break;
				
			// 3*3 L, all four possible shapes are accounted for by setting a randomly 
			// picked 2*2 square to null.
			case 7: case 8: case 9: case 10:
				mTiles = new Tile[3][3];
				for(int i = 0; i < 3; i++)
					for(int j = 0; j < 3; j++)
						mTiles[i][j] = new Tile(Tile.blue);
				
				int[] corners = { 0, 2 };
				int x = corners[(int)(Math.random() * 2)]; // {0,1}
				int y = corners[(int)(Math.random() * 2)];
				int xModifier = (x == 0) ? 1 : -1;
				int yModifier = (y == 0) ? 1 : -1;
				
				mTiles[x][y] = null;
				mTiles[x + xModifier][y] = null;
				mTiles[x][y + yModifier] = null;
				mTiles[x + xModifier][y + yModifier] = null;
				break;
			
			// horizontal or vertical line, the length is randomly generated from 2 to 5. 
			case 11: case 12: case 13: case 14: case 15: case 16: case 17: case 18:
				int length = (int)(Math.random()* (6 - 2) + 2); // {2,...,5}
				Color color = null;
				switch(length)
				{
					case 2:
						color = Tile.yellow;
						break;
					case 3:
						color = Tile.orange;
						break;
					case 4:
						color = Tile.pink;
						break;
					case 5:
						color = Tile.red;
						break;
				}		
				
				int orientation = (int)(Math.random() * 2); // {0,1}
				mTiles = new Tile[length][length];
				
				for(int i = 0; i < length; i++)
				{
					if(orientation == 0)
						mTiles[i][0] = new Tile(color);
					else
						mTiles[0][i] = new Tile(color);
				}
				
				break;
		}
		
		// creates a hitbox for the shape
		int tileQuantity = 0;
		for(int i = 0; i < mTiles.length; i++)
			for(int j = 0; j < mTiles.length; j++)
				if(mTiles[i][j] != null)
				{
					tileQuantity++;
				}
		
		mHitbox = new Rectangle[tileQuantity];
		int a = 0;
		for(int i = 0; i < mTiles.length; i++)
			for(int j = 0; j < mTiles.length; j++)
				if(mTiles[i][j] != null)
				{
					int xOffset = offsetCoords(i, mTiles[i][j]) + (115 * index);
					int yOffset = offsetCoords(j, mTiles[i][j]) + 430;
					mHitbox[a] = new Rectangle(
							xOffset, yOffset, mTiles[i][j].getSize() + 1, mTiles[i][j].getSize() + 1);
					a++;
				}
		
		// Sets the value of the shape to the number of tiles the shape contains
		mValue = tileQuantity;
	}
	
	private int offsetCoords(int arg, Tile tile)
	{
		return arg * (tile.getSize() + tile.getTileOffset()) + tile.getEdgeOffset();
	}
	
	public Shape(Tile[][] tiles) { this.mTiles = tiles; }
	
	public static Shape[] getShapes()
	{
		Shape[] shapes = new Shape[19];
		Tile[][] tiles;
		int index = 0;
		
		// Adds all the squares and gets up to Shape[2]
		for(int i = 0; i < 3; i++)
		{
			shapes[index++] = new Shape(i, 0);
		}
		
		// Adds all the small L-shape and gets up to Shape[6] 
		for(int i = 0; i < 2; i++)
			for(int j = 0; j < 2; j++)
			{
				tiles = new Tile[2][2];
				for(int k = 0; k < 2; k++)
					for(int l = 0; l < 2; l++)
						tiles[k][l] = new Tile(null);	
				
				tiles[i][j] = null;
				shapes[index++] = new Shape(tiles);
				tiles = null;
			}
		
		// Adds all the big L-shape and gets up to Shape[10]
		for(int i = 0; i < 2; i++)
			for(int j = 0; j < 2; j++)
			{
				tiles = new Tile[3][3];
				for(int k = 0; k < 3; k++)
					for(int l = 0; l < 3; l++)
						tiles[k][l] = new Tile(null);
				
				int[] corners = { 0, 2 };
				int x = corners[i]; 
				int y = corners[j];
				int xModifier = (x == 0) ? 1 : -1;
				int yModifier = (y == 0) ? 1 : -1;
				
				tiles[x][y] = null;
				tiles[x + xModifier][y] = null;
				tiles[x][y + yModifier] = null;
				tiles[x + xModifier][y + yModifier] = null;
				
				shapes[index++] = new Shape(tiles);
				
				tiles = null;
			}
		
		//Adds all the horizontal and vertical lines and gets up to Shape[18]
		for(int i = 2; i < 6; i++)
			for(int j = 0; j < 2; j++)
			{	
				tiles = new Tile[i][i];
				
				for(int k = 0; k < i; k++)
				{
					if(j == 0)
						tiles[k][0] = new Tile(null);
					else
						tiles[0][k] = new Tile(null);
				}
				
				shapes[index++] = new Shape(tiles);
				tiles = null;
			}
				
		return shapes;
	}
	
	public boolean contains(Point arg)
	{
		for(Rectangle rectangle: mHitbox)
		{
			if(rectangle.contains(arg))
				return true;
		}
		return false;
	}
}
