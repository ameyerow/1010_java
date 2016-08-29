package meyerowitz.alpha;

import java.util.ArrayList;
import java.util.Collections;

public class Solver 
{
	private final ArrayList<InternalShape> SHAPES;
	private ArrayList<InternalShape> mShapes;
	
	// Mimics the state of the board. A 0 represents an empty tile and a 1 
	// represents a filled tile.
	private int[][] mBoard;
	
	public Solver(Tile[][] board, Shape[] shapes)
	{	
		SHAPES = convertShapesToArrayList(Shape.getShapes());
		mBoard = convertBoardToIntArray(board);
		mShapes = convertShapesToArrayList(shapes);
		
		printShapeArray(mShapes);
		printBoardArray(mBoard);
	}
	
	public void findBestMoves()
	{	
		int[][] boardClone = cloneBoard(mBoard);
		
		// mShapes is sorted based on how many tiles are in each Shape. Shapes with the most tiles are
		// pushed to the front.
		Collections.sort(mShapes);
		
		for(InternalShape shape: mShapes)
		{
			// First number is the heuristic value of the current board and the next two numbers are
			// the x and y coordinates where the shape should be placed
			int[] bestBoard = {Integer.MIN_VALUE, 0, 0};
			
			for(int x = 0; x < 10; x++)
			{
				for(int y = 0; y < 10; y++)
				{
					if(checkShapePlaceable(boardClone, shape, x, y))
					{
						int[][] board = placeShape(cloneBoard(boardClone), shape, x, y);	
						board = removeFullRowsAndColumns(board);
						
						int boardHeuristic = calculateHeuristic(board);
						
						if(boardHeuristic > bestBoard[0])
						{
							bestBoard[0] = boardHeuristic;
							bestBoard[1] = x;
							bestBoard[2] = y;
						}
					}
				}
			}
			
			boardClone = placeShape(boardClone, shape, bestBoard[1], bestBoard[2]);
			printBoardArray(boardClone);
			System.out.println("");
		}
		
		printBoardArray(boardClone);
		System.out.println("");
	}
	
	private boolean checkShapePlaceable(int[][] board, InternalShape shape, int x, int y)
	{
		boolean placeable = true;
		
		arg: for(int i = 0; i < shape.getTiles().length; i++)
			for(int j = 0; j < shape.getTiles().length; j++)
				if(placeable)
					if(shape.getTiles()[i][j] == 1)
					{
						if((x + i) < 10 & (y + j) < 10)
							placeable = board[x + i][y + j] == 1 ? false : true;
						else
							placeable = false;
						
						if(!placeable)
							break arg;
					}	
		
		return placeable;
	}
	
	private int[][] placeShape(int[][] board, InternalShape shape, int x, int y)
	{
		for(int i = 0; i < shape.getTiles().length; i++)
			for(int j = 0; j < shape.getTiles().length; j++)
				if(shape.getTiles()[i][j] == 1)
					board[x + i][y + j] = 1;
		
		return board;
	}
	
	private int[][] removeFullRowsAndColumns(int[][] board)
	{		
		ArrayList<int[]> arg = new ArrayList<int[]>();
		
		for(int i = 0; i < 10; i++)
			for(int j = 0; j < 10; j++)
			{
				if(board[i][j] == 1)
				{
					if(j == 9)
						for(int a = 0; a < 10; a++)
						{
							int[] index = {i, a};
							arg.add(index);
						}
				}
				else { break; }
			}
		
		for(int i = 0; i < 10; i++)
			for(int j = 0; j < 10; j++)
			{
				if(board[j][i] == 1)
				{
					if(j == 9)
						for(int a = 0; a < 10; a++)
						{
							int[] index = {a , i};
							arg.add(index);
						}
				}
				else { break; }
			}
		
		for(int[] i: arg)
			board[ i[0] ][ i[1] ] = 0;
		
		
		return board;
	}
	
	private int calculateHeuristic(int[][] board)
	{
		int a = calculateGroupingFilled(board) 
				+ calculateGroupingEmpty(board) 
				- calculateShapesNotPlaceable(board);
		
		return a;
	}
	
	private int calculateGroupingFilled(int[][] board)
	{
		return 0;
	}
	
	private int calculateGroupingEmpty(int[][] board)
	{
		return 0;
	}
	
	private int calculateShapesNotPlaceable(int[][] board)
	{
		int shapesNotPlaceable = 19;
		for(InternalShape shape: SHAPES)
			arg: for(int i = 0; i < 10; i++)
				for(int j = 0; j < 10; j++)				
					if(checkShapePlaceable(board, shape, i, j))
					{
						shapesNotPlaceable--;
						break arg;
					}
		
		
		return shapesNotPlaceable;
	}
	
	private ArrayList<InternalShape> convertShapesToArrayList(Shape[] tileShapes)
	{
		ArrayList<InternalShape> shapes = new ArrayList<InternalShape>();
		
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
				
				shapes.add(new InternalShape(shape));
			}
		}	
		return shapes;
	}
	
	private int[][] convertBoardToIntArray(Tile[][] tileBoard)
	{
		int[][] board = new int[10][10];
		
		for(int i = 0; i < 10; i++)
			for(int j = 0; j < 10; j++)	
				board[i][j] = tileBoard[i][j].getFilled() ? 1 : 0;
		
		return board;
	}
	
	private void printShapeArray(ArrayList<InternalShape> shapes)
	{
		for(InternalShape shape: shapes)
		{
			for(int i = 0; i < shape.getTiles().length; i++)
			{
				for(int j = 0; j < shape.getTiles().length; j++)
					System.out.print(shape.getTiles()[j][i]);
				
				System.out.println("");
			}
			
			System.out.println("");
		}
	}
	
	private void printBoardArray(int[][] board)
	{
		for(int i = 0; i < board.length; i++)
		{
			for(int j = 0; j < board.length; j++)
				System.out.print(board[j][i]);
			
			System.out.println("");
		}
		System.out.println("");
	}
	
	private int[][] cloneBoard(int[][] board)
	{
		int[][] clone = new int[10][10];
		
		for(int i = 0; i < 10; i++)
			for(int j = 0; j < 10; j++)
				board[i][j] = board[i][j];
		
		return clone;
	}
	
	private class InternalShape implements Comparable<InternalShape>
	{
		int[][] mTiles;
		public int[][] getTiles() { return mTiles; }
		
		public InternalShape(int[][] tiles)
		{
			mTiles = tiles;
		}
		
		private int getNumFilledTiles()
		{
			int arg = 0;
			
			for(int i = 0; i < mTiles.length; i++)
				for(int j = 0; j < mTiles.length; j++)
					if(mTiles[i][j] == 1)
						arg++;
			return arg;
		}
		
		public int compareTo(InternalShape shape) 
		{
			return (this.getNumFilledTiles()  > shape.getNumFilledTiles() ? -1 :
				   (this.getNumFilledTiles() == shape.getNumFilledTiles() ?  0 : 1));
		}
	}
}