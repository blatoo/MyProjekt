package utilities;

public class Cooccurrence {

	/**
	 * Computes for each pair of columns the number of 'trues' in their AND-conjunction.
	 * (i.e., if the rows represent a basket of a user and the columns represent items
	 * bought by the user, it computes for every pair of items the number of 'co-occurrence-events'
	 * in all baskets).
	 * @param adjacencyMatrix
	 * @param ofColumns
	 * @return
	 */
	public static int[][] computeCooccurrenceOfColumns(boolean[][] adjacencyMatrix){
		int[][] cooccurrences = new int[adjacencyMatrix[0].length][adjacencyMatrix[0].length];	
		
		for(int i = 0; i < adjacencyMatrix.length; ++i){
			for(int j = 0; j < adjacencyMatrix[i].length; ++j){
				if(adjacencyMatrix[i][j]){
					for(int k = 0; k < adjacencyMatrix[i].length; ++k){
						if(j!=k && adjacencyMatrix[i][k]){
							++cooccurrences[j][k];
						}
					}
				}
			}
		}
		
		return cooccurrences;
	}

	
	/**
	 * Computes for each pair of rows the number of 'trues' in their AND-conjunction.
	 * (i.e., if the rows represent a basket of a user and the columns represent items
	 * bought by the user, it computes for every pair of users the number of items that were bought
	 * by both users).
	 * @param adjacencyMatrix
	 * @param ofColumns
	 * @return
	 */
	public static int[][] computeCooccurrenceOfRows(boolean[][] adjacencyMatrix){
		int[][] cooccurrences = new int[adjacencyMatrix.length][adjacencyMatrix.length];	

		
		for(int i = 0; i < adjacencyMatrix.length; ++i){
			for(int j = 0; j < adjacencyMatrix.length; ++j){
				if(i != j){
					for(int k = 0; k < adjacencyMatrix[i].length; ++k){
						if(adjacencyMatrix[i][k] && adjacencyMatrix[j][k])
							++cooccurrences[i][j];
					}
				}
			}
		}
		
		return cooccurrences;
	}

	
}
