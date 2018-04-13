# Simple-Sudoku
I will build a simple code to solve Sudoku puzzles. The approach will concentrate on OOP and API design.

### Sudoku Strategy
There are many ways to solve Sudoku. According to Stanford CS108 assignment requirements I have to use the following approach which is a sort of OOP interpretation of classic recursive backtracking search. Call each square in the puzzle a "spot". We want to do a recursive backtracking search for a solution, assigning numbers to spots to find a combination that works.
-  When assigning a number to a spot, never assign a number that, at that moment, conflicts with the spot's row, column, or square. We are up-front careful about assigning legal numbers to a spot, rather than assigning any number 1..9 and finding the problem later in the recursion. Assume that the initial grid is all legal, and make only legal spot assignments thereafter.
-  There are 81 spots in the 9x9 game. You could try making assignments to the blank spots in any order. However, for our solution, first sort the spots into order by the size of their set of assignable numbers, with the smallest set (most constrained) spots first. Follow that order in the recursive search, assigning the most constrained spots first. Do not re-sort the spots during the search. It works well enough to just sort once at the start and then keep that ordering. The sorting is just a heuristic, but it's easy and effective. 
-  We will set a max number of solutions of 100 -- if the recursive search gets to a point where it has 100 or more solutions, it should stop looking and just return how many have been found so far.

### Sudoku OOP Design
For this project given by the professor, the starter code has some routine code and data, and the rest of the design is up to me. My goal is to design classes and APIs so that the solve() method (below) is clean expression of the strategy described above.

I took an OOP approach to the search by treating each spot as its own capable little object. A Sudoku class is created that encapsulates a Sudoku game and give it a "Spot" inner class that represents a single spot in the game. Constant factor efficiency is not a big concern -- we're going for correctness, clarity, and a reasonably smart strategy.

Concentrate on OOP design around the Spot class -- push complexity into the Spot, making things easy for clients of the Spot. For example, the Spot has its own access to the grid (remember, it's an inner class of Sudoku).
##### Design Requirements
-  Sudoku(int[][] grid) -- constructor takes the initial grid state, and assumed that the client passes us a legal grid. Empty spots are represented by 0 in the grid. it's assumed that the grid is 9x9. 
-  Sudoku(String text) -- takes in puzzle in text form -- 81 numbers.
-  String toString() -- override toString() to return a String made of 9 lines that shows the rows of the grid, with each number preceded by a space. For example, here is the toString() of the "medium 5 3" puzzle...

        5 3 0 0 7 0 0 0 0 
        6 0 0 1 9 5 0 0 0 
        0 9 8 0 0 0 0 6 0 
        8 0 0 0 6 0 0 0 3 
        4 0 0 8 0 3 0 0 1 
        7 0 0 0 2 0 0 0 6 
        0 6 0 0 0 0 2 8 0 
        0 0 0 4 1 9 0 0 5 
        0 0 0 0 8 0 0 7 9 

-  int solve() -- tries to solve the puzzle using the strategy described above. Returns the number of solutions and sets the state for getSolutionText() and getElapsed() (below). The original grid of the sudoku should not be changed by the solution (i.e. toString() is still the original problem).
-  String getSolutionText() -- after a solve, if there was one or more solutions, this is the text form of the first one found (otherwise the empty string). Which solution is found first may vary, depending on quirks of your solve() implementation.
-  long getElapsed() -- after a solve, returns the elapsed time spent in the solve measured in milliseconds(System.currentTimeMillis()).

### Deliverable main()

> public static void main(String[] args) {<br>
Sudoku sudoku;<br>
sudoku = new Sudoku(hardGrid);<br>
System.out.println(sudoku); // print the raw problem<br>
int count = sudoku.solve();<br>
System.out.println("solutions:" + count);<br>
System.out.println("elapsed:" + sudoku.getElapsed() + "ms");<br>
System.out.println(sudoku.getSolutionText());<br>
}<br>
