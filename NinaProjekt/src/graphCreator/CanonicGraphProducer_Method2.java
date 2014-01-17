package graphCreator;


public class CanonicGraphProducer_Method2 {

	public static boolean debug = false;
	
	/**
	 * Creates a canonic bipartite graph if the degree distributions are eligible for that. 
	 * Creation method: 2 
	 * Runtime; O(l*r+r^2) where l denotes the number of vertices on the left and r the number of 
	 * vertices on the right.
	 * 
	 * @param leftSideDegreeDistribution (sorted non-increasingly; condition is not tested but will result in serious mistakes if not obeyed)
	 * @param rightSideDegreeDistribution (sorted non-increasingly; condition is not tested but will result in serious mistakes if not obeyed)
	 * @returns the adjacency matrix of the canonic graph, if it exists. Otherwise, it returns null.
	 */
	public static boolean[][] createBipartiteGraph(int[] leftSideDegreeDistribution, int[] rightSideDegreeDistribution){
		
		Integer[] leftSideInt = new Integer[leftSideDegreeDistribution.length];
		for(int i = 0; i < leftSideInt.length; ++i) leftSideInt[i] = new Integer(leftSideDegreeDistribution[i]);
		
		boolean[][] adjacencyMatrix = new boolean[leftSideDegreeDistribution.length][rightSideDegreeDistribution.length];
		int[] conjugateOfLeftSide = EligibilityTester.computeConjugate(leftSideInt, rightSideDegreeDistribution.length);
		//initialize the matrix
		for(int i = 0; i < leftSideDegreeDistribution.length; ++i){
			for(int j = leftSideDegreeDistribution[i]-1; j >= 0; --j){
				adjacencyMatrix[i][j] = true;
			}
		}
		
		int missingTrues;
		
		//now we fill the columns (if it is possible)
		//In this method, we will use 'conjugateOfLeftSide' to find the highest index that contains a
		//'true' in this column. This array will be updated such that for all indices j smaller than the
		//current index i it contains the highest index that contains a 'true' in that column j.
		//Remember that all the 'trues' in a column are in consecutive rows, starting from the lowest row
		for(int i = rightSideDegreeDistribution.length-1; i >= 0; --i){
			//'missingTrues' contains the number of 'trues' the column finally contains
			//we can instantly correct this value by the number of 'trues' already in the column
			missingTrues = rightSideDegreeDistribution[i]-conjugateOfLeftSide[i];
			
			//Note that in the column to the left of the current column i, all 'trues'
			//come consecutively from row 0 to some row k.
			for(int j = i-1; j >= 0 && missingTrues > 0; j--){
				//in each column we search for the highest index at which a 'true' is contained
				//The initialization of x makes the main difference between method 1 and method 2
				for(int x = conjugateOfLeftSide[j]-1; x >= 0 && missingTrues > 0; --x){
					//if that index is already true in the i-th column, there is no
					//other 'true' in column j that we can transfer. We can thus stop.
					if(adjacencyMatrix[x][i])
						break;
					adjacencyMatrix[x][j] = false;
					adjacencyMatrix[x][i] = true;
					
					--conjugateOfLeftSide[j];
					--missingTrues;
				}
			}
			
		}
		return adjacencyMatrix;
	}
}
