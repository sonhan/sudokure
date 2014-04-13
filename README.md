Sodokure
========

/**
 * Sudoku solver
 * A very fast human-like algorithm
 * Input: file for initial numbers
 * 
 * Algorithm:
 * - Start with a 9x9 grid with initial (given) numbers and 0 for missing numbers
 * - An element is a row, a column, or a 3x3 block
 * - A minimal element is defined as the element with least 0 (missing number)
 * - Loop until the grid is full
 * 		+ Find a minimum element
 * 		+ For each of 0 squares, fill with one of the remaining numbers
 * 		+ Check related elements, continue
 * - End
 * @author	Son N. Han
 * @date	2005/09/15
 * 			Hanoi University of Technology
 */