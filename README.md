##Introduction
Sudokure is a sudoku solver with a very fast human-like algorithm

##Algorithm
###Input
Matrix 9x9 with initial numbers
###Algorithm:
1. Start with a 9x9 grid with initial (given) numbers and 0 for missing numbers
2. An element is a row, a column, or a 3x3 block
3. A minimal element is defined as the element with least 0 (missing number)
4. Loop until the grid is full
	* Find a minimum element
	* For each of 0 squares, fill with one of the remaining numbers
	* Check related elements, continue
5. End

##Info
* @author	Son N. Han
* @date	2005/09/15
* Hanoi University of Technology
