package meyerowitz.alpha;

import java.util.ArrayList;
import java.util.Collections;

public class Solver {
	private final ArrayList<InternalShape> SHAPES;
	private ArrayList<InternalShape> mShapes;
	
	// Mimics the state of the board. A 0 represents an empty tile and a 1 
	// represents a filled tile.
	private int[][] mBoard;
	
	public Solver(Tile[][] board, Shape[] shapes) {	
		SHAPES = convertShapesToArrayList(Shape.getShapes());
		mBoard = convertBoardToIntArray(board);
		mShapes = convertShapesToArrayList(shapes);
	}
	
	public ArrayList<int[]> findBestMoves() {	
		ArrayList<int[]> bestMoves = new ArrayList<int[]>();
		int[][] boardClone = cloneBoard(mBoard);
		
		// Go through each tile and calculate the heuristic of the board when each is placed in its
		// best location. Choose the board with the best board and place that shape.
		do {
			for(InternalShape shape: mShapes)
				findOptimalPlacement(cloneBoard(boardClone), shape);
			
			if(mShapes.size() != 1)
				Collections.sort(mShapes);
			
			if(!checkShapePlaceable(cloneBoard(boardClone), mShapes.get(0), mShapes.get(0).getX(), mShapes.get(0).getY())) {
				bestMoves.add(null);
			} else {
				boardClone = placeShape(boardClone, mShapes.get(0), mShapes.get(0).getX(), mShapes.get(0).getY());
				boardClone = removeFullRowsAndColumns(boardClone);
				int[] a = {mShapes.get(0).getIndex(), mShapes.get(0).getX(), mShapes.get(0).getY()};
				bestMoves.add(a);
			}
			
			mShapes.remove(0);
		} while(!mShapes.isEmpty());
	
		return bestMoves;
	}
	
	private void findOptimalPlacement(int[][] boardClone, InternalShape shape) {
		int bestHeuristic = Integer.MAX_VALUE;
		int x = 0;
		int y = 0;
		
		if(checkShapePlaceableAnywhere(boardClone, shape)) {
			for(int i = 0; i < 10; i++) {
				for(int j = 0; j < 10; j++) {
					if(checkShapePlaceable(boardClone, shape, i, j)) {
						int[][] board = cloneBoard(boardClone);
						board =	placeShape(board, shape, i, j);	
						board = removeFullRowsAndColumns(board);
						
						// The best board has the lowest heuristic
						int boardHeuristic = calculateHeuristic(board, shape);
						
						if(boardHeuristic < bestHeuristic) {
							bestHeuristic = boardHeuristic;
							x = i;
							y = j;
						}
					}
				}
			}	
		} 
		shape.setHeuristic(bestHeuristic);	
		shape.setX(x);
		shape.setY(y);
	}
	
	private boolean checkShapePlaceableAnywhere(int[][] board, InternalShape shape) {
		for(int x = 0; x < 10; x++)
			for(int y = 0; y < 10; y++)
				if(checkShapePlaceable(board, shape, x, y))
					return true;	
		return false;
	}
	
	private boolean checkShapePlaceable(int[][] board, InternalShape shape, int x, int y) {
		for(int i = 0; i < shape.getTiles().length; i++)
			for(int j = 0; j < shape.getTiles().length; j++)
				if(shape.getTiles()[i][j] == 1 )
					if((x + i) < 10 && (y + j) < 10) {
						if(board[x + i][y + j] == 1)
							return false;
					} else  return false;
		return true;
	}
	
	private int[][] placeShape(int[][] board, InternalShape shape, int x, int y) {
		for(int i = 0; i < shape.getTiles().length; i++)
			for(int j = 0; j < shape.getTiles().length; j++)
				if(shape.getTiles()[i][j] == 1)
					board[x + i][y + j] = 1;
		
		return board;
	}
	
	private int[][] removeFullRowsAndColumns(int[][] board) {		
		ArrayList<int[]> arg = new ArrayList<int[]>();
		
		for(int i = 0; i < 10; i++)
			for(int j = 0; j < 10; j++) {
				if(board[i][j] == 1) {
					if(j == 9)
						for(int a = 0; a < 10; a++) {
							int[] index = {i, a};
							arg.add(index);
						}
				} else break; 
			}
		
		for(int i = 0; i < 10; i++)
			for(int j = 0; j < 10; j++) {
				if(board[j][i] == 1) {
					if(j == 9)
						for(int a = 0; a < 10; a++) {
							int[] index = {a , i};
							arg.add(index);
						}
				}
				else break; 
			}
		
		for(int[] i: arg)
			board[ i[0] ][ i[1] ] = 0;
		
		return board;
	}
	
	private int calculateHeuristic(int[][] board, InternalShape shape) {
		double filledWeight = 1;
		double groupingEmptyWeight = 1;
		//double groupingFilledWeight = 0;
		double shapesPlaceableWeight = 100;
		double shapesCurrentPlaceableWeight = 1000;
		
		return (int)
			   (+ calculateNumFilledTiles(board) * filledWeight
				+ calculateGrouping(board, 0) * groupingEmptyWeight				// Calculates grouping of empty tiles
				//+ calculateGrouping(board, 1) * groupingFilledWeight	
				+ calculateShapesNotPlaceable(board) * shapesPlaceableWeight
			    + calculateCurrentShapesNotPlaceable(board, shape) * shapesCurrentPlaceableWeight);
	}
	
	private int calculateGrouping(int[][] board, int tileStatus) {	
		int groupingHeuristic = 0;
		
		ArrayList<int[]> cachedTiles = new ArrayList<int[]>();
		
		for(int i = 0; i < 10; i++)
			for(int j = 0; j < 10; j++)
				if(board[i][j] == tileStatus) {
					boolean cached = false;
					
					for(int[] a: cachedTiles)
						if(i == a[0] && j == a[1])
							cached = true;
							
					if(!cached) {
						ArrayList<int[]> grouping = new ArrayList<int[]>();
						int[] coords = {i, j};
						grouping.add(coords);
						
						ArrayList<int[]> d = new ArrayList<int[]>();
						d.addAll(grouping);
						while(true) {
							ArrayList<int[]> b = new ArrayList<int[]>();
							
							for(int[] a : d) {
								right: if(a[0] + 1 < 10 && board[a[0] + 1][a[1]] == tileStatus) {
									for(int[] c: grouping)
										if(a[0] + 1 == c[0] && a[1] == c[1])
											break right;
									for(int[] c: b)
										if(a[0] + 1 == c[0] && a[1] == c[1])
											break right;
										
									int[] arg = {a[0] + 1, a[1]};
									b.add(arg);				
								}
								left: if(a[0] - 1 > -1 && board[a[0] - 1][a[1]] == tileStatus) {
									for(int[] c: grouping)
										if(a[0] - 1 == c[0] && a[1] == c[1])
											break left;
									for(int[] c: b)
										if(a[0] - 1 == c[0] && a[1] == c[1])
											break left;
									int[] arg = {a[0] - 1, a[1]};
									b.add(arg);	
								}
								down: if(a[1] + 1 < 10 && board[a[0]][a[1] + 1] == tileStatus) {
									for(int[] c: grouping)
										if(a[0] == c[0] && a[1] + 1 == c[1])
											break down;
									for(int[] c: b)
										if(a[0] == c[0] && a[1] + 1 == c[1])
											break down;
									int[] arg = {a[0], a[1] + 1};
									b.add(arg);
								}
								up: if(a[1] - 1 > -1 && board[a[0]][a[1] - 1] == tileStatus) {
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
								
							if(!b.isEmpty()) {
								grouping.addAll(b);
								d = new ArrayList<int[]>();
								d.addAll(b);
							}
							else break;
						}		
						cachedTiles.addAll(grouping);
						groupingHeuristic++;
					}
				}
		//Adds the total perimeter of the groupings to the groupingHeuristic
		if(tileStatus == 0) {
			int totalPerimeter = 0;
			
			for(int[] tile: cachedTiles) {
				int a = tileStatus == 0 ? 1 : 0;
				if(tile[0] + 1 < 10 && board[tile[0] + 1][tile[1]] == a) totalPerimeter++; //right
				if(tile[0] - 1 > -1 && board[tile[0] - 1][tile[1]] == a) totalPerimeter++; //left
				if(tile[1] + 1 < 10 && board[tile[0]][tile[1] + 1] == a) totalPerimeter++; //down
				if(tile[1] - 1 > -1 && board[tile[0]][tile[1] - 1] == a) totalPerimeter++; //up
			}
			
			return groupingHeuristic * totalPerimeter;
		} else {
			return groupingHeuristic;
		}
	}
	
	private int calculateShapesNotPlaceable(int[][] board) {
		int shapesNotPlaceable = 19;
		for(InternalShape shape: SHAPES)
			arg: for(int i = 0; i < 10; i++)
				for(int j = 0; j < 10; j++)				
					if(checkShapePlaceable(board, shape, i, j)) {
						shapesNotPlaceable--;
						break arg;
					}
		
		return shapesNotPlaceable;
	}
	
	private int calculateCurrentShapesNotPlaceable(int[][] board, InternalShape shape) {
		int currentShapesNotPlaceable = 0;
		for(InternalShape a: mShapes)
			if(a != shape)
				if(!checkShapePlaceableAnywhere(board, a))
					currentShapesNotPlaceable++;	
		return currentShapesNotPlaceable;
		
	}
	
	private int calculateNumFilledTiles(int[][] board) {
		int numFilledTiles = 0;
		
		for(int i = 0; i < 10; i++)
			for(int j = 0; j < 10; j++)
				if(board[i][j] == 1)
					numFilledTiles++;
		
		return numFilledTiles;
	}
	
	private ArrayList<InternalShape> convertShapesToArrayList(Shape[] tileShapes) {
		ArrayList<InternalShape> shapes = new ArrayList<InternalShape>();
		
		for(int i = 0; i < tileShapes.length; i++) {
			if(tileShapes[i] == null) {
				shapes.add(null); 
			} else {
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
	
	private int[][] convertBoardToIntArray(Tile[][] tileBoard) {
		int[][] board = new int[10][10];
		
		for(int i = 0; i < 10; i++)
			for(int j = 0; j < 10; j++)	
				board[i][j] = tileBoard[i][j].getFilled() ? 1 : 0;
		
		return board;
	}
	
	@SuppressWarnings("unused")
	private void printShapeArray(InternalShape shape) {
		for(int i = 0; i < shape.getTiles().length; i++) {
			for(int j = 0; j < shape.getTiles().length; j++) {
				System.out.print(shape.getTiles()[j][i]);
			}
			System.out.println("");
		}		
	}
	
	@SuppressWarnings("unused")
	private void printBoardArray(int[][] board) {
		for(int i = 0; i < board.length; i++) {
			for(int j = 0; j < board.length; j++)
				System.out.print(board[j][i]);
			
			System.out.println("");
		}
		System.out.println("");
	}
	
	private int[][] cloneBoard(int[][] board) {
		int[][] clone = new int[10][10];
		
		for(int i = 0; i < 10; i++)
			for(int j = 0; j < 10; j++)
				clone[i][j] = board[i][j];
		
		return clone;
	}
	
	private class InternalShape implements Comparable<InternalShape>, Cloneable {
		private int[][] mTiles;
		public int[][] getTiles() { return mTiles; }
		
		private int mIndex;
		public int getIndex() { return mIndex; }
		
		private int mHeuristic;
		public int getHeuristic() { return mHeuristic; }
		public void setHeuristic(int heuristic) { mHeuristic = heuristic; }
		
		private int mX, mY;
		public int getX() { return mX; }
		public void setX(int x) { mX = x; }
		public int getY() { return mY; }
		public void setY(int y) { mY = y; }
		
		public InternalShape(int[][] tiles, int index) {
			mTiles = tiles;
			mIndex = index;
		}
		
		@Override
		protected InternalShape clone() throws CloneNotSupportedException {
			int[][] tiles = new int[mTiles.length][mTiles.length];
			for(int i = 0; i < mTiles.length; i++)
	    	   for(int j = 0; j < mTiles.length; j++)
	    		   tiles[i][j] = mTiles[i][j];
			
	        InternalShape shape = new InternalShape(tiles, getIndex());
	        
			return shape;
	    }
		
		@Override
		public int compareTo(InternalShape shape) {
			return (this.getHeuristic()  < shape.getHeuristic() ? -1 :
				   (this.getHeuristic() == shape.getHeuristic() ?  0 : 1));
		}
	}
}