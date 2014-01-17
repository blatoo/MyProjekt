package graphCreator;

public class CorrectnessTester {

	/**
	 * Tests, whether the given matrix has the given number of 'trues' in each column and row as given 
	 * by the degree distributions. 
	 * 
	 * @param matrix
	 * @param degreeDistributionRows
	 * @param degreeDistributionColumns
	 * @returns true if matrix has the prescribed row and column counts.
	 */

	public static boolean testCorrectness(boolean[][] matrix, int[] degreeDistributionRows, int[] degreeDistributionColumns){
		boolean isCorrect = true;
		//count the number of 'trues' in each column
		int[] columnCounter = new int[degreeDistributionColumns.length];
		
		//count the number of 'trues' in each row
		int rowCounter;
		for(int i = 0; i < matrix.length; ++i){
			rowCounter = 0;
			for(int j = 0; j < matrix[i].length; ++j){
				if(matrix[i][j]){
					++rowCounter;
					++columnCounter[j];
				}
			}
			if(rowCounter != degreeDistributionRows[i]){
				isCorrect = false;
				break;
			}
		}
		if(isCorrect){
			for(int i = 0; i < columnCounter.length; ++i){
				if(columnCounter[i] != degreeDistributionColumns[i]){
					isCorrect = false;
					break;
				}
			}
		}
		return isCorrect;
	}
}
