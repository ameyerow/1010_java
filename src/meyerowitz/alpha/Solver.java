package meyerowitz.alpha;

import java.util.ArrayList;

public class Solver 
{
	private final ArrayList<int[][]> SHAPES;
	private ArrayList<int[][]> currentShapes;
	public void setShapes(Shape[] currentTileShapes) { this.currentShapes = convertShapesToIntArrays(currentTileShapes); }
	
	// Mimics the initial state of the empty board. A 0 represents an empty tile
	// and a 1 represents a filled tile.
	private int[][] board;
	
	public Solver(Shape[] currentTileShapes, Shape[] SHAPES)
	{	
		// Creates empty board and fills it with empty tiles 
		board = new int[10][10];
		
		for(int i = 0; i < 10; i++)
			for(int j = 0; j < 10; j++)
				board[i][j] = 0;
		
		this.currentShapes = convertShapesToIntArrays(currentTileShapes);
		this.SHAPES = convertShapesToIntArrays(SHAPES);
		
		printShapeArrays(this.SHAPES);
	}
	
	private ArrayList<int[][]> convertShapesToIntArrays(Shape[] tileShapes)
	{
		ArrayList<int[][]> shapes = new ArrayList<int[][]>();
		
		for(Shape tileShape: tileShapes)
		{
			if(tileShape == null) shapes.add(null);
			else
			{
				int length = tileShape.getTiles().length;
				int[][] shape = new int[length][length];
				
				for(int i = 0; i < length; i++)
					for(int j = 0; j < length; j++)
							shape[i][j] = (tileShape.getTiles()[i][j] != null) ? 1 : 0;
				
				shapes.add(shape);
			}
		}	
		return shapes;
	}
	
	public void printShapeArrays(ArrayList<int[][]> shapes)
	{
		for(int[][] shape: shapes)
		{
			for(int i = 0; i < shape.length; i++)
			{
				for(int j = 0; j < shape.length; j++)
					System.out.print(shape[i][j]);
				
				System.out.println("");
			}
			
			System.out.println("");
		}
	}
}
