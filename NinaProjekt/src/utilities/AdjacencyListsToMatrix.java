package utilities;

public class AdjacencyListsToMatrix {

	public static boolean[][] transformAdjacencyListIntoCompleteMatrix(int[][] adjacencyLists, int numberOfVerticesOnLeftSide, int numberOfVerticesOnRightSide){
		boolean[][] adjacency = new boolean[numberOfVerticesOnLeftSide][numberOfVerticesOnRightSide];
		
		for(int i = 0; i < adjacencyLists.length; ++i){
			for(int j = 0; j < adjacencyLists[i].length; ++j){
				adjacency[i][adjacencyLists[i][j]] = true;
			}
		}
		
		return adjacency;
	}
	
	public static  boolean satisfiesDegreeDistributions(boolean[][] adj1, int[] leftSide, int[] rightSide) {
		int[] degreeOfColumns = new int[rightSide.length];
		int rowCounter = 0;
		for(int i = 0; i < adj1.length; ++i){
			rowCounter = 0;
			for(int j = 0; j < adj1[i].length; ++j){
				if(adj1[i][j]){
					rowCounter++;
					degreeOfColumns[j]++;
				}
			}
			if(leftSide[i] != rowCounter)
				return false;
		}
		for(int i = 0; i < rightSide.length; ++i){
			if(degreeOfColumns[i] != rightSide[i])
				return false;
		}
		
		return true;
	}
}
