package graphCreator;

import utilities.Print;

public class CanonicGraphProducer_Method1 {

	public static boolean debug = true;
	
	/**
	 * Creates a canonic bipartite graph if the degree distributions are eligible for that. 
	 * 
	 * @param leftSideDegreeDistribution (sorted non-increasingly)
	 * @param rightSideDegreeDistribution (sorted non-increasingly)
	 * @returns the adjacency matrix of the canonic graph, if it exists. Otherwise, it returns null.
	 */
	public static boolean[][] createBipartiteGraph(int[] leftSideDegreeDistribution, int[] rightSideDegreeDistribution){
		
		
		boolean[][] adjacencyMatrix = new boolean[leftSideDegreeDistribution.length][rightSideDegreeDistribution.length];
		//initialize the matrix with the degrees of the left hand side (in rows)
		for(int i = 0; i < leftSideDegreeDistribution.length; ++i){
			for(int j = leftSideDegreeDistribution[i]-1; j >= 0; --j){
				adjacencyMatrix[i][j] = true;
			}
		}

		
		int missingTrues, tempCounter;
		//now we fill the columns (if it is possible)
		for(int i = rightSideDegreeDistribution.length-1; i >= 0; --i){
			//'missingTrues' contains the number of 'trues' the column finally contains
			missingTrues = rightSideDegreeDistribution[i];
			//we'll first count the number of 'trues' that are already in the column.
			tempCounter = 0;
			while(tempCounter < adjacencyMatrix.length && adjacencyMatrix[tempCounter++][i]){
				--missingTrues;
			}
			
			//Note that in the column to the left of the current column i, all 'trues'
			//come consecutively from row 0 to some row k.
			for(int j = i-1; j >= 0 && missingTrues > 0; --j){
				//in each column we search for the highest index at which a 'true' is contained
				for(int x = adjacencyMatrix.length-1; x >= 0 && missingTrues > 0; --x){
					//if that index is already true in the i-th column, there is no
					//other 'true' in column j that we can transfer. We can thus stop.
					if(adjacencyMatrix[x][i])
						break;
					if(adjacencyMatrix[x][j] == true){
						adjacencyMatrix[x][j] = false;
						adjacencyMatrix[x][i] = true;
						--missingTrues;
					}
				}
			}
		}
		return adjacencyMatrix;
	}
}
