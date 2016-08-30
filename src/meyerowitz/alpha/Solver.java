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
	
	public ArrayList<int[]> findBestMoves()
	{	
		ArrayList<int[]> bestMoves = new ArrayList<int[]>();
		int[][] boardClone = cloneBoard(mBoard);
		
		// mShapes is sorted based on how many tiles are in each Shape. Shapes with the most tiles are
		// pushed to the front.
		Collections.sort(mShapes);
		
		for(InternalShape shape: mShapes)
		{
			// First number is the heuristic value of the current board and the next two numbers are
			// the x and y coordinates where the shape should be placed
			int[] bestBoard = {Integer.MAX_VALUE, 0, 0};
			
			for(int x = 0; x < 10; x++)
			{
				for(int y = 0; y < 10; y++)
				{
					if(checkShapePlaceable(boardClone, shape, x, y))
					{
						int[][] board = cloneBoard(boardClone);
						board =	placeShape(board, shape, x, y);	
						board = removeFullRowsAndColumns(board);
						
						// The best board has the lowest heuristic
						int boardHeuristic = calculateHeuristic(board);
						
						if(boardHeuristic < bestBoard[0])
						{
							bestBoard[0] = boardHeuristic;
							bestBoard[1] = x;
							bestBoard[2] = y;
						}
					}
				}
			}
			
			boardClone = placeShape(boardClone, shape, bestBoard[1], bestBoard[2]);
			int[] temp = {shape.getIndex(), bestBoard[1], bestBoard[2]};
			bestMoves.add(temp);
		}
		//System.out.println("Best moves:");
		//printBoardArray(boardClone);
		
		return bestMoves;
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
		double filledWeight = 1;
		double groupingFilledWeight = 2;
		double groupingEmptyWeight = 3;
		double shapesPlaceableWeight = 1;
		
		return (int)
			   (+ calculateNumFilledTiles(board) * filledWeight
				+ calculateGrouping(board, 1) * groupingFilledWeight		// Calculates grouping of filled tiles
				+ calculateGrouping(board, 0) * groupingEmptyWeight			// Calculates grouping of empty tiles
				+ calculateShapesNotPlaceable(board) * shapesPlaceableWeight);
	}
	
	private int calculateGrouping(int[][] board, int tileStatus)
	{	
		//int start = (int) System.currentTimeMillis();
		int groupingHeuristic = 0;
		
		ArrayList<int[]> cachedTiles = new ArrayList<int[]>();
		int[] temp = {-1, -1};
		cachedTiles.add(temp);
		
		for(int i = 0; i < 10; i++)
			for(int j = 0; j < 10; j++)
				if(board[i][j] == tileStatus)
				{
					boolean cached = false;
					
					for(int[] a: cachedTiles)
						if(i == a[0] && j == a[1])
							cached = true;
							
					if(!cached)
					{
						ArrayList<int[]> grouping = new ArrayList<int[]>();
						int[] coords = {i, j};
						grouping.add(coords);
						
						boolean connected = false;
						
						right: if(i + 1 < 10 && board[i + 1][j] == tileStatus)
						{
							for(int[] a: cachedTiles)
								if(i + 1 == a[0] && j == a[1])
									break right;
							int[] arg = {i + 1, j};
							grouping.add(arg);
							connected = true;
							
						}
						left: if(i - 1 > -1 && board[i - 1][j] == tileStatus)
						{
							for(int[] a: cachedTiles)
								if(i - 1 == a[0] && j == a[1] )
									break left;
							int[] arg = {i - 1, j};
							grouping.add(arg);
							connected = true;
						}
						down: if(j + 1 < 10 && board[i][j + 1] == tileStatus)
						{
							for(int[] a: cachedTiles)
								if(i == a[0] && j + 1 == a[1] )
									break down;
							int[] arg = {i, j + 1};
							grouping.add(arg);
							connected = true;
						}
						up: if(j - 1 > -1 && board[i][j - 1] == tileStatus)
						{
							for(int[] a: cachedTiles)
								if(i == a[0] && j - 1 == a[1] )
									break up;
							int[] arg = {i, j - 1};
							grouping.add(arg);
							connected = true;
						}
						
						if(connected)
						{
							ArrayList<int[]> d = new ArrayList<int[]>();
							d.addAll(grouping);
							while(true)
							{
								ArrayList<int[]> b = new ArrayList<int[]>();
								
								for(int[] a : d)
								{
									right: if(a[0] + 1 < 10 && board[a[0] + 1][a[1]] == tileStatus)
									{
										for(int[] c: grouping)
											if(a[0] + 1 == c[0] && a[1] == c[1])
												break right;
										for(int[] c: b)
											if(a[0] + 1 == c[0] && a[1] == c[1])
												break right;
										
										int[] arg = {a[0] + 1, a[1]};
										b.add(arg);				
									}
									left: if(a[0] - 1 > -1 && board[a[0] - 1][a[1]] == tileStatus)
									{
										for(int[] c: grouping)
											if(a[0] - 1 == c[0] && a[1] == c[1])
												break left;
										for(int[] c: b)
											if(a[0] - 1 == c[0] && a[1] == c[1])
												break left;
										int[] arg = {a[0] - 1, a[1]};
										b.add(arg);	
									}
									down: if(a[1] + 1 < 10 && board[a[0]][a[1] + 1] == tileStatus)
									{
										for(int[] c: grouping)
											if(a[0] == c[0] && a[1] + 1 == c[1])
												break down;
										for(int[] c: b)
											if(a[0] == c[0] && a[1] + 1 == c[1])
												break down;
										int[] arg = {a[0], a[1] + 1};
										b.add(arg);
									}
									up: if(a[1] - 1 > -1 && board[a[0]][a[1] - 1] == tileStatus)
									{
										for(int[] c: grouping)
											if(a[0] == c[0] && a[1] - 1 == c[1])
												break up;
										for(int[] c: b)
											if(a[0] == c[0] && a[1] - 1 == c[1])
												break up;
										int[] arg = {a[0], a[1] - 1};
										b.add(arg);
									}
								}
								
								if(!b.isEmpty()) 
								{
									grouping.addAll(b);
									d = new ArrayList<int[]>();
									d.addAll(b);
								}
								else break;
							}		
							cachedTiles.addAll(grouping);
						}
						groupingHeuristic++;
					}
				}
		//int end = (int) System.currentTimeMillis();
		//System.out.println("Status: " + tileStatus + " Heuristic: " + groupingHeuristic + " Number Cached: " + cachedTiles.size() + " Time: " + (end-start) + "ms");
		//printBoardArray(board);
		
		return groupingHeuristic;
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
	
	private int calculateNumFilledTiles(int[][] board)
	{
		int numFilledTiles = 0;
		
		for(int i = 0; i < 10; i++)
			for(int j = 0; j < 10; j++)
				if(board[i][j] == 1)
					numFilledTiles++;
		
		return numFilledTiles;
	}
	
	private ArrayList<InternalShape> convertShapesToArrayList(Shape[] tileShapes)
	{
		ArrayList<InternalShape> shapes = new ArrayList<InternalShape>();
		
		for(int i = 0; i < tileShapes.length; i++)
		{
			if(tileShapes[i] == null) shapes.add(null);
			else
			{
				int length = tileShapes[i].getTiles().length;
				int[][] shape = new int[length][length];
				
				for(int j = 0; j < length; j++)
					for(int k = 0; k < length; k++)
						shape[j][k] = (tileShapes[i].getTiles()[j][k] != null) ? 1 : 0;
				
				shapes.add(new InternalShape(shape, i)); 
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
				clone[i][j] = board[i][j];
		
		return clone;
	}
	
	private class InternalShape implements Comparable<InternalShape>
	{
		private int[][] mTiles;
		public int[][] getTiles() { return mTiles; }
		
		private int mIndex;
		public int getIndex() { return mIndex; }
		
		public InternalShape(int[][] tiles, int index)
		{
			mTiles = tiles;
			mIndex = index;
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