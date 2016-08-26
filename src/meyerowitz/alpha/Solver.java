package meyerowitz.alpha;

import java.util.ArrayList;

public class Solver 
{
	private ArrayList<int[][]> shapes;
	public void setShapes(Shape[] shapes) { this.shapes = convertShapesToIntArrays(shapes); }
	
	// Mimics the initial state of the empty board. A 0 represents an empty tile
	// and a 1 represents a filled tile.
	private int[][] board;
	
	public Solver(Shape[] tileShapes)
	{
		// Creates empty board
		for(int i = 0; i < 10; i++)
			for(int j = 0; j < 10; j++)
				board[i][j] = 0;
		
		this.shapes = convertShapesToIntArrays(tileShapes);
	}
	
	private ArrayList<int[][]> convertShapesToIntArrays(Shape[] tileShapes)
	{
		ArrayList<int[][]> shapes = null;
		
		for(Shape tileShape: tileShapes)
		{
			if(tileShape == null) shapes.add(null);
			else
			{
				int length = tileShape.getTiles().length;
				int[][] shape = new int[length][length];
				
				for(int i = 0; i < length; i++)
					for(int j = 0; j < length; j++)
						shape[i][j] = (tileShape.getTiles()[i][j].getFilled()) ? 1 : 0;
				
				shapes.add(shape);
			}
		}	
		return shapes;
	}
}
