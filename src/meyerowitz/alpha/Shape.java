package meyerowitz.alpha;

import java.awt.Color;

public class Shape 
{
	//Once this empty constructor is called, a random tile is picked and created by 
	//calling the Shape(int arg) constructor. The statement generates a number in
	//the interval [0,6].
	public Shape() 
	{
		this((int)(Math.random() * 7));
	}
	
	public Shape(int arg)
	{
		switch(arg)
		{
			case 0:
				break;
			case 1:
				break;
			case 2:
				break;
			case 3:
				break;
			case 4:
				break;
			case 5:
				break;
			case 6:
				break;
		}
	}
	
	private class ShapeTile
	{
		private boolean filled;		
		public boolean getFilled() { return filled; }
		public void setFilled(boolean filled) { this.filled = filled; } 
		
		private Color color;		
		public Color getColor() { return color; }
		public void setColor(Color color) { this.color = color; }
		
		private int size;
		public int getSize() { return size; }
		public void setSize(int size) { this.size = size; }
		
		private int tileOffset;
		public int getTileOffset() { return tileOffset; }
		public void setTileOffset(int tileOffset) { this.tileOffset = tileOffset; }
		
		public ShapeTile()
		{
			
		}
	}
}
