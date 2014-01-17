package graphCreator;

import utilities.Print;

public class CanonicGraphProducer_Method3 {

	public static boolean debug = false;
	
	/**
	 * Creates a canonic bipartite graph if the degree distributions are eligible for that. 
	 * Creation method: 3 
	 * Runtime: O(m), i.e., linear in the number of edges to be created
	 * 
	 * @param leftSideDegreeDistribution (sorted non-increasingly; condition is not tested but will result in serious mistakes if not obeyed)
	 * @param rightSideDegreeDistribution (sorted non-increasingly; condition is not tested but will result in serious mistakes if not obeyed)
	 * @returns the adjacency matrix of the canonic graph, if it exists. Otherwise, it returns null.
	 */
	public static int[][] createBipartiteGraph(int[] leftSideDegreeDistribution, int[] rightSideDegreeDistribution){
		
		int[][] adjacencyMatrix = new int[leftSideDegreeDistribution.length][];
		//initialize adjacencyMatrix for the vertices on the left hand side
		for(int i = leftSideDegreeDistribution.length-1; i >= 0; --i){
			adjacencyMatrix[i] = new int[leftSideDegreeDistribution[i]];
		}
		
		//In these arrays we store the pointers to the next higher/previous lower column, i.e.,
		//at index k we store the HIGHEST index of the columns to the left that contains MORE than k trues
		//(the HIGHEST index of the columns to the right that contains the next lower number of trues).
		int[] nextLevel = new int[rightSideDegreeDistribution.length], previousLevel = new int[rightSideDegreeDistribution.length];
		for(int i = 0; i < rightSideDegreeDistribution.length; ++i){
			nextLevel[i] = previousLevel[i] = -3;
		}
		previousLevel[rightSideDegreeDistribution.length-1] = -1;
		int[] conjugateOfLeftSide = new int[rightSideDegreeDistribution.length];
		
		//this variable contains the index of the last column with a new maximum of 'trues' in it.
		int lastIndexWithPeak = rightSideDegreeDistribution.length+1;
		int currentDegree = leftSideDegreeDistribution[0];
//		int counter = 0;
		//we fill these arrays together with the conjugate vector
		for(int i = 0; i < leftSideDegreeDistribution.length; ++i){
			if(leftSideDegreeDistribution[i] < currentDegree){
				for(int j = currentDegree-1; j >= leftSideDegreeDistribution[i]; --j){
					conjugateOfLeftSide[j] = i;
				}
				
				//
				if(lastIndexWithPeak <= rightSideDegreeDistribution.length){
					//we are at some intermediate peak. This implies that the previousPeak is the one that is
					//stored in 'lastIndexWithPeak':
					previousLevel[currentDegree-1] = lastIndexWithPeak-1;
					nextLevel[lastIndexWithPeak-1] = currentDegree-1;
					lastIndexWithPeak = currentDegree;
				}else{
					//The first maximum is the column at 'currentDegree'=leftSideDegreeDistribution[0] 
					//since this is the column with the highest index that contains at least one 'true'.
					//Since it is the rightmost column, there is no previous column. We mark this by assigning a '-1' to that column
					//in the 'previousLevel' array:
					if(currentDegree == rightSideDegreeDistribution.length)
						previousLevel[currentDegree-1] = -1;
					else
						previousLevel[currentDegree-1] = rightSideDegreeDistribution.length-1;
					//Next level of the outermost column (that may be empty) is current degree.
					nextLevel[rightSideDegreeDistribution.length-1] = currentDegree-1;
					lastIndexWithPeak = currentDegree;
				}
				currentDegree = leftSideDegreeDistribution[i];
			}
		}
		if(lastIndexWithPeak > rightSideDegreeDistribution.length){
			previousLevel[currentDegree-1] = rightSideDegreeDistribution.length-1;
			nextLevel[rightSideDegreeDistribution.length-1] = currentDegree-1;
			lastIndexWithPeak = currentDegree;
			//of course, there is no 'nextLevel' after the last level, marked by a -1.
			nextLevel[lastIndexWithPeak-1] = -1;
		}else{
			previousLevel[currentDegree-1] = lastIndexWithPeak-1;
			nextLevel[lastIndexWithPeak-1] = currentDegree-1;
			lastIndexWithPeak = currentDegree;
		}
		//of course, there is no 'nextLevel' after the last level, marked by a -1.
		nextLevel[lastIndexWithPeak-1] = -1;
		
//		the last degree is never put into conjugateLeftSide, so we have to do that now.
		for(int j = currentDegree-1; j >= 0; --j){
			conjugateOfLeftSide[j] = leftSideDegreeDistribution.length;
		}

		
		if(debug){
			System.out.println("left side degree distribution is ");
			Print.printArray(leftSideDegreeDistribution);
			System.out.println("conjugate is ");
			Print.printArray(conjugateOfLeftSide);
			System.out.println("previous level is ");
			Print.printArray(previousLevel);
			System.out.println("next level is ");
			Print.printArray(nextLevel);
		}
		
		
		int[] currentDegreeOfLeftSide = new int[leftSideDegreeDistribution.length];
		int missingTrues;
		int oldHighestIndexOfNeighbor, newHighestIndexOfNeighbor;
		int currentIndexOfNeighbor;
		
		//now we fill the adjacency lists of the left hand side.
		//In this method, we will use 'conjugateOfLeftSide' to find the highest index that contains a
		//'true' in this column. This array will be updated such that for all indices j smaller than the
		//current index i it contains the highest index that contains a 'true' in that column j.
		//Remember that all the 'trues' in a column are in consecutive rows, starting from the lowest row
		for(int i = rightSideDegreeDistribution.length-1; i >= 0; --i){
			if(debug)
				System.out.println("now doing the "+i+"th column");
			//'missingTrues' contains the number of 'trues' the column finally contains
			//we can instantly correct this value by the number of 'trues' already in the column
			missingTrues = rightSideDegreeDistribution[i];
			oldHighestIndexOfNeighbor = -1;
			newHighestIndexOfNeighbor = 0;
			
			if(debug)
				System.out.println("missing trues are "+missingTrues);
			
			for(int x = conjugateOfLeftSide[i]-1; x >= 0; --x){
				if(debug)
					System.out.println("new edge between "+i+" and "+x);
				adjacencyMatrix[x][currentDegreeOfLeftSide[x]++] = i;
				--conjugateOfLeftSide[i];
				--missingTrues;
				++oldHighestIndexOfNeighbor;
			}

			
			
			int j = nextLevel[i];

			
			if(debug)
				System.out.println("still missing trues are "+missingTrues);
			
			
			
			
			while(missingTrues > 0){
				if(debug)
					System.out.println("j is "+j);
				//Note that in the column to the left of the current column i, all 'trues'
				//come consecutively from row 0 to some row k.
				
				newHighestIndexOfNeighbor = conjugateOfLeftSide[j]-1;
				//in each column we search for the highest index at which a 'true' is contained
				
				currentIndexOfNeighbor = conjugateOfLeftSide[j]-1; 
				while(currentIndexOfNeighbor > oldHighestIndexOfNeighbor && missingTrues > 0){
					if(debug)
						System.out.println("new edge between "+i+" and "+currentIndexOfNeighbor);
					adjacencyMatrix[currentIndexOfNeighbor][currentDegreeOfLeftSide[currentIndexOfNeighbor--]++] = i;
					--conjugateOfLeftSide[j];
					--missingTrues;
				}
				
				if(debug){
					System.out.println("Now, the conjugate looks like this: ");
					Print.printArray(conjugateOfLeftSide);
				}
				
				//now we have to update the level information
				//If the column is depleted to the level of the right hand column, we
				//have to remove it from the linked list represented by the
				//two array 'nextLevel/previousLevel'
//				if(currentIndexOfNeighbor == oldHighestIndexOfNeighbor){
				if(j < conjugateOfLeftSide.length-1 && conjugateOfLeftSide[j] == conjugateOfLeftSide[j+1]){
					if(debug){
						System.out.println("the column has been depleted to the level of the right hand side column");
					}
					
					//the next column to the left contains more trues than this column had before the depletion
					if(nextLevel[j] == j-1){
						//in this case, we just have to remove j from the double linked list represented
						//by the two arrays
						if(previousLevel[j] >= 0)
							nextLevel[previousLevel[j]] = nextLevel[j];
						if(nextLevel[j] >= 0)
						previousLevel[nextLevel[j]] = previousLevel[j];
					}else{
						//otherwise, j-1 has to replace j. 
						if(j > 0){
							nextLevel[j-1] = nextLevel[j];
							previousLevel[j-1]=previousLevel[j];
						}
						if(nextLevel[j] >= 0)
							previousLevel[nextLevel[j]] = j-1;
						if(previousLevel[j] >= 0)
							nextLevel[previousLevel[j]] = j-1;
					}
					int oldj = j;
					j = nextLevel[j];
					
					nextLevel[oldj] = previousLevel[oldj] = -3;
				
					if(debug){
						System.out.println("previousLevel after that");
						Print.printArray(previousLevel);
						System.out.println("nextLevel after that");
						Print.printArray(nextLevel);
					}
					
					
				}else{
					//we have introduced a new level into the game that is truely between the two levels
					//on each side
					if(nextLevel[j] < j-1){
						if(j > 0){
							nextLevel[j-1] = nextLevel[j];
							previousLevel[j-1] = j;
						}
						if(nextLevel[j] >= 0)
							previousLevel[nextLevel[j]] = j-1;
						nextLevel[j] = j-1;
						
					}
				
					j = nextLevel[j];
					if(debug){
						System.out.println("previousLevel after that");
						Print.printArray(previousLevel);
						System.out.println("nextLevel after that");
						Print.printArray(nextLevel);
					}
					
				}
				
				
				//go to the next higher level
				if(debug)
					System.out.println("j is "+j);
				
				
				if(debug)
					System.out.println("the next higher level is "+j);
				oldHighestIndexOfNeighbor = newHighestIndexOfNeighbor;
			}
//			we have now 'removed' all 'trues' from the last (active) column i
			//now, column i-1 has to replace i 
			//if(nextLevel[i] != i-1){
			if(nextLevel[i] > 0)
				previousLevel[nextLevel[i]] = i-1;

			if(i > 0){
				if(nextLevel[i-1] == -3){
					nextLevel[i-1] = nextLevel[i];
				}
				previousLevel[i-1] = previousLevel[i];
			}
			
			previousLevel[i] = -2;
			nextLevel[i] = -2;
			
			if(debug){
				System.out.println("after the column "+i+" is finished, previousLevel and nextLevel look like this:");
				Print.printArray(previousLevel);
				Print.printArray(nextLevel);
			}

		}
		return adjacencyMatrix;
	}
}
