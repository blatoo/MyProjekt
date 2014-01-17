package randomWalk;

import java.util.Random;

public class RandomWalk {
	public static Random random = new Random(356);

	/**
	 * Performs a random walk through A(R,S) as represented by the given adjacency matrix. 
	 * 
	 * @param numberOfSteps performed in the random walk
	 * @param adjacencyMatrix on which the random walk is done
	 */
	public static void doTheWalk(int numberOfSteps, boolean[][] adjacencyMatrix){
		int numberOfEdges = 0;
		for(int i = adjacencyMatrix.length-1; i >= 0; --i){ 
			for(int j = adjacencyMatrix[i].length-1; j >= 0; --j){
				if(adjacencyMatrix[i][j])
					++numberOfEdges;
			}
		}
		
		int[] allEdges = new int[2*numberOfEdges];
		int edgeCounter = 0;
		for(int i = adjacencyMatrix.length-1; i >= 0; --i){ 
			for(int j = adjacencyMatrix[i].length-1; j >= 0; --j){
				if(adjacencyMatrix[i][j]){
					allEdges[edgeCounter++] = i;
					allEdges[edgeCounter++] = j;
				}
			}
		}
		
		int index1, index2, source1, source2, target1, target2;
		for(int i = numberOfSteps; i > 0; --i){
//			if(i % 1000 == 0)
//				System.out.println("doing the "+i+"th step in the RW");
			index1 = random.nextInt(numberOfEdges);
			source1 = allEdges[2*index1];
			target1 = allEdges[2*index1+1];
			
			index2 = random.nextInt(numberOfEdges);
			source2 = allEdges[2*index2];
			target2 = allEdges[2*index2+1];
			
			if(!adjacencyMatrix[source1][target2] && !adjacencyMatrix[source2][target1]){
				adjacencyMatrix[source1][target1] = false;
				adjacencyMatrix[source2][target2] = false;
				adjacencyMatrix[source1][target2] = true;
				adjacencyMatrix[source2][target1] = true;
				allEdges[2*index1+1] = target2;
				allEdges[2*index2+1] = target1;
			}
			
		}
		
		
		
	}
}
