package meyerowitz.alpha;

import java.awt.Color;

public class Shape 
{
	public Tile[][] tiles;
	
	// Once this empty constructor is called, a random tile is picked and created by 
	// calling the Shape(int arg) constructor.
	public Shape() 
	{
		this((int)(Math.random() * 19)); // {0,...,18}
	}
	
	public Shape(int arg)
	{
		switch(arg)
		{
			// 1*1 square
			case 0:
				tiles = new Tile[1][1];
				tiles[0][0] = new Tile(Tile.purple);
				break;
				
			// 2*2 square
			case 1:
				tiles = new Tile[2][2];
				for(int i = 0; i < 3; i++)
					for(int j = 0; j < 3; j++)
						tiles[i][j] = new Tile(Tile.lime);
				break;
				
			// 3*3 square
			case 2:
				tiles = new Tile[3][3];
				for(int i = 0; i < 4; i++)
					for(int j = 0; j < 4; j++)
						tiles[i][j] = new Tile(Tile.teal);
				break;
				
			// 2*2 L, all four possible shapes are accounted for by randomly picking one
			// of the tiles to be set to null. The probability is kept the same by adding
			// four cases.
			case 3: case 4: case 5: case 6:
				tiles = new Tile[2][2];
				for(int i = 0; i < 3; i++)
					for(int j = 0; j < 3; j++)
						tiles[i][j] = new Tile(Tile.green);
				tiles[(int)(Math.random() * 2)][(int)(Math.random() * 2)] = null; // {0,1}
				break;
				
			// 3*3 L, all four possible shapes are accounted for by setting a randomly 
			// picked 2*2 square to be set to null.
			case 7: case 8: case 9: case 10:
				tiles = new Tile[3][3];
				for(int i = 0; i < 4; i++)
					for(int j = 0; j < 4; j++)
						tiles[i][j] = new Tile(Tile.blue);
				
				int[] corners = { 0, 2 };
				int x = corners[(int)(Math.random() * 2)]; // {0,1}
				int y = corners[(int)(Math.random() * 2)];
				int xModifier = (x == 0) ? 1 : -1;
				int yModifier = (y == 0) ? 1 : -1;
				
				tiles[x][y] = null;
				tiles[x + xModifier][y] = null;
				tiles[x][y + yModifier] = null;
				tiles[x + xModifier][y + yModifier] = null;
				break;
			
			//horizontal or vertical line, the length is randomly generated from 2 to 5. 
			case 11: case 12: case 13: case 14: case 15: case 16: case 17: case 18:
				int length = (int)(Math.random()* (5-2) + 2); // {2,...,5}
				int orientation = (int)(Math.random() * 2); // {0,1}
				tiles = (orientation == 0) ? new Tile[length][1] : new Tile[1][length];
				
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
				
				for(int i = 0; i < (length + 1); i++)
				{
					if(orientation == 0)
						tiles[i][0] = new Tile(color);
					else
						tiles[0][i] = new Tile(color);
				}
				break;
		}
	}
}
