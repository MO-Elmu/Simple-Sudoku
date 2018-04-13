package assign4;

import java.util.*;

/*
 * Encapsulates a Sudoku grid to be solved.
 * CS108 Stanford.
 */
public class Sudoku {
	
	private int[][] grid = new int[SIZE][SIZE];
	private int[][] bckpGrid = new int [SIZE][SIZE];
	private List<HashSet<Integer>> rowsList;
	private List<HashSet<Integer>> colsList;
	private List<HashSet<Integer>> subgrids;
	private List<Sudoku.Spot> emptySpots;
	private String firstSolution;
	private long elapsedTime;
	
	// Provided grid data for main/testing
	// The instance variable strategy is up to you.
	
	// Provided easy 1 6 grid
	// (can paste this text into the GUI too)
	public static final int[][] easyGrid = Sudoku.stringsToGrid(
	"1 6 4 0 0 0 0 0 2",
	"2 0 0 4 0 3 9 1 0",
	"0 0 5 0 8 0 4 0 7",
	"0 9 0 0 0 6 5 0 0",
	"5 0 0 1 0 2 0 0 8",
	"0 0 8 9 0 0 0 3 0",
	"8 0 9 0 4 0 2 0 0",
	"0 7 3 5 0 9 0 0 1",
	"4 0 0 0 0 0 6 7 9");
	
	public static final int[][] testGrid = Sudoku.stringsToGrid(
			"0 0 0 2 9 0 0 6 4",
			"9 0 0 4 0 6 7 0 3",
			"0 6 4 3 8 5 1 0 2",
			"2 0 0 0 3 0 6 0 5",
			"5 9 7 8 6 4 2 3 1",
			"6 4 3 1 0 2 0 7 8",
			"4 2 6 5 7 0 0 0 9",
			"3 5 9 6 2 8 4 1 7",
			"8 0 1 9 4 0 0 0 6");
			
	
	public static final int[][] test2Grid = Sudoku.stringsToGrid(
    "0 0 0 0 0 0 0 0 0",    
    "0 0 0 0 0 0 0 0 0",
	"0 0 0 0 0 0 0 0 0",
	"0 0 0 0 0 0 0 0 0",
	"0 0 0 0 0 0 0 0 0",
	"0 0 0 0 0 0 0 0 0",
	"0 0 0 0 0 0 0 0 0",
	"0 0 0 0 0 0 0 0 0",
	"0 0 0 0 0 0 0 0 0");
	
	public static final int[][] test1Grid = Sudoku.stringsToGrid(
			"9 0 6 0 7 0 4 0 3",
			"0 0 0 4 0 0 2 0 0",
			"0 7 0 0 2 3 0 1 0",
			"5 0 0 0 0 0 1 0 0",
			"0 4 0 2 0 8 0 6 0",
			"0 0 3 0 0 0 0 0 5",
			"0 3 0 7 0 0 0 5 0",
			"0 0 7 0 0 5 0 0 0",
			"4 0 5 0 1 0 7 0 8");
			
	
	// Provided medium 5 3 grid
	public static final int[][] mediumGrid = Sudoku.stringsToGrid(
	 "530070000",
	 "600195000",
	 "098000060",
	 "800060003",
	 "400803001",
	 "700020006",
	 "060000280",
	 "000419005",
	 "000080079");
	
	// Provided hard 3 7 grid
	// 1 solution this way, 6 solutions if the 7 is changed to 0
	public static final int[][] hardGrid = Sudoku.stringsToGrid(
	"3 7 0 0 0 0 0 8 0",
	"0 0 1 0 9 3 0 0 0",
	"0 4 0 7 8 0 0 0 3",
	"0 9 3 8 0 0 0 1 2",
	"0 0 0 0 4 0 0 0 0",
	"5 2 0 0 0 6 7 9 0",
	"6 0 0 0 2 1 0 4 0",
	"0 0 0 5 3 0 9 0 0",
	"0 3 0 0 0 0 0 5 1");
	
	
	public static final int SIZE = 9;  // size of the whole 9x9 puzzle
	public static final int PART = 3;  // size of each 3x3 part
	public static final int MAX_SOLUTIONS = 100;
	
	// Provided various static utility methods to
	// convert data formats to int[][] grid.
	
	/**
	 * Returns a 2-d grid parsed from strings, one string per row.
	 * The "..." is a Java 5 feature that essentially
	 * makes "rows" a String[] array.
	 * (provided utility)
	 * @param rows array of row strings
	 * @return grid
	 */
	public static int[][] stringsToGrid(String... rows) {
		int[][] result = new int[rows.length][];
		for (int row = 0; row<rows.length; row++) {
			result[row] = stringToInts(rows[row]);
		}
		return result;
	}
	
	
	/**
	 * Given a single string containing 81 numbers, returns a 9x9 grid.
	 * Skips all the non-numbers in the text.
	 * (provided utility)
	 * @param text string of 81 numbers
	 * @return grid
	 */
	public static int[][] textToGrid(String text) {
		int[] nums = stringToInts(text);
		if (nums.length != SIZE*SIZE) {
			throw new RuntimeException("Needed 81 numbers, but got:" + nums.length);
		}
		
		int[][] result = new int[SIZE][SIZE];
		int count = 0;
		for (int row = 0; row<SIZE; row++) {
			for (int col=0; col<SIZE; col++) {
				result[row][col] = nums[count];
				count++;
			}
		}
		return result;
	}
	
	
	/**
	 * Given a string containing digits, like "1 23 4",
	 * returns an int[] of those digits {1 2 3 4}.
	 * (provided utility)
	 * @param string string containing ints
	 * @return array of ints
	 */
	public static int[] stringToInts(String string) {
		int[] a = new int[string.length()];
		int found = 0;
		for (int i=0; i<string.length(); i++) {
			if (Character.isDigit(string.charAt(i))) {
				a[found] = Integer.parseInt(string.substring(i, i+1));
				found++;
			}
		}
		int[] result = new int[found];
		System.arraycopy(a, 0, result, 0, found);
		return result;
	}
	
	
	//Spot inner Class
	/** inner class that embodies the grid Spot (81 of them in 9x9 Sudoku) 
	 * it contains all Spot operations such as setValue getVaue possibleValues
	 * and so on. This help us conceal all the complexities of handling Spot
	 * operations by doing the dirty work in this class and give clieants
	 * easy manageable API to Spot operations and State.
	 */
	class Spot implements Comparable<Spot> {
		private int row, col, subgrid;   //col, row and subgrid where this Spot reside
		private int value;
		private Set<Integer> possibleValues = new HashSet<>(Arrays.asList(1,2,3,4,5,6,7,8,9));
		public Spot(int row, int col) throws Exception {
			if(row < 0 || row > SIZE-1 || col < 0 || col > SIZE-1) throw new Exception("SpotOutOfBounds");
			this.row = row;
			this.col = col;
			this.value = grid[row][col];
			setSubgrid((row/PART)*PART + (col/PART));
			populate();
		}
		public int getValue() {
			return value;
		}
		
		public boolean setValue(int value) {
			if(value < 0 || value > SIZE) return false;
			if(rowsList.get(this.row).contains(value) || colsList.get(this.col).contains(value) ||
					subgrids.get(this.subgrid).contains(value)) {
				
				return false;
			}
			rowsList.get(this.row).add(value); 
			colsList.get(this.col).add(value);
			subgrids.get(this.subgrid).add(value);
			grid[row][col] = value;
			this.value = value;
			
			return true;
		}
		
		//Clear the spot value by setting the value to 0 in grid and reflect that in all Sets 
		public void clear() {
			rowsList.get(this.row).remove(this.value);
			colsList.get(this.col).remove(this.value);
			subgrids.get(this.subgrid).remove(this.value);
			grid[row][col] = 0;
			this.value = 0;	
		}
		public int getSubgrid() {
			return subgrid;
		}
		private void setSubgrid(int subgrid) {
			this.subgrid = subgrid;
		}
		public boolean isEmpty() {
			return this.getValue() == 0;
		}
		private void populate() {
			possibleValues.removeAll(rowsList.get(this.row));
			possibleValues.removeAll(colsList.get(this.col));
			possibleValues.removeAll(subgrids.get(this.subgrid));

		}
		 
		@Override
		public int compareTo(Spot o) {
			return this.possibleValues.size() - o.possibleValues.size();
		}
		@Override
		public String toString() {
			String s = "(" + row + "," + col + ")" ;
			return s;
		}
	}


	// Provided -- the deliverable main().
	// You can edit to do easier cases, but turn in
	// solving hardGrid.
	public static void main(String[] args) {
		Sudoku sudoku;
		sudoku = new Sudoku(hardGrid);
		
		System.out.println(sudoku); // print the raw problem
		int count = sudoku.solve();
		System.out.println("solutions:" + count);
		System.out.println("elapsed:" + sudoku.getElapsed() + "ms");
		System.out.println(sudoku.getSolutionText());
		
	}
	
	
	

	/**
	 * Sets up based on the given ints.
	 */
	public Sudoku(int[][] ints) {
		for(int i=0; i<SIZE; i++) {
			System.arraycopy(ints[i], 0, this.grid[i], 0, SIZE);
			System.arraycopy(ints[i], 0, this.bckpGrid[i], 0, SIZE);
		}
		rowsList = new ArrayList<HashSet<Integer>>();
		colsList = new ArrayList<HashSet<Integer>>();
		subgrids = new ArrayList<HashSet<Integer>>();
		emptySpots = new ArrayList<Spot>();
		for(int i=0; i<SIZE; i++) {
			HashSet<Integer> rowSet = new HashSet<Integer>();
			HashSet<Integer> colSet = new HashSet<Integer>();
			for(int j=0; j<SIZE; j++) {
				if(ints[i][j] != 0) rowSet.add(ints[i][j]);
				if(ints[j][i] != 0) colSet.add(ints[j][i]);	
			}
			rowsList.add(rowSet);
			colsList.add(colSet);
		}
		for(int i=0; i<SIZE; i+=PART) {
			for(int j=0; j<SIZE; j+=PART) {
				HashSet<Integer> part = new HashSet<Integer>();
				for(int k=0; k<PART; k++) {
					for(int n=0; n<PART; n++) {
					
						if(ints[i+k][j+n] != 0) part.add(ints[i+k][j+n]);
					}
				}
				subgrids.add(part);
			}
		}
		this.populateEmptySpots();
	}
	
	private void populateEmptySpots() {
		for(int i=0; i<SIZE; i++) {
			for(int j=0; j<SIZE; j++) {
				try {
				if(grid[i][j] == 0) emptySpots.add(new Spot(i,j));
				}catch (Exception e) { 
					System.out.println("Spot out of Bounds");
					System.exit(-1); 
				}
			}
		}
	}
	
	public Sudoku(String text) {
		this(textToGrid(text));	
	}
	
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		for(int i=0; i<SIZE; i++) {
			for(int j=0; j<SIZE; j++) {
				result.append(" ");
				result.append(grid[i][j]);
			}
			result.append("\n");
		}
		return result.toString();
	}
	
	private void sortEmptySpots() {
		Collections.sort(this.emptySpots);
	}
	
	private class SolCount{
		int count = 0;
	}
	private void countSolutions (int index, int len, SolCount solCount) throws Exception {
		if(index == len) {
			solCount.count++;
			if(solCount.count == 1) {
				firstSolution = new String(this.toString());
			}
			if(solCount.count >= MAX_SOLUTIONS) {
				throw new Exception("HundredSolutionsException");
			}
			return;
		}
		Iterator<Integer> it = emptySpots.get(index).possibleValues.iterator();
		while(it.hasNext()) {
			if(emptySpots.get(index).setValue(it.next())) {
				countSolutions(index+1, len, solCount);
				emptySpots.get(index).clear();
			}
		}
		return;
	}
	/**
	 * Solves the puzzle, invoking the underlying recursive search.
	 */
	public int solve() {
		long startTime = System.currentTimeMillis();
		int len = emptySpots.size();
		if(len <= 0) return 0;
		sortEmptySpots();
		SolCount solCount = new SolCount();
		int index = 0;
		try {
			countSolutions(index, len, solCount);
		}catch (Exception e) {
			for(int i=0; i<SIZE; i++) {
				System.arraycopy(this.bckpGrid[i], 0, this.grid[i], 0, SIZE);
			}
			
		}
		long endTime = System.currentTimeMillis();
		elapsedTime = endTime - startTime;
		return  solCount.count;
	}
	
	public String getSolutionText() {
		if(firstSolution == null) {
			firstSolution = new String("");
			return firstSolution;
		}
		return firstSolution; 
	}
	
	public long getElapsed() {
		return elapsedTime; // YOUR CODE HERE
	}

}
