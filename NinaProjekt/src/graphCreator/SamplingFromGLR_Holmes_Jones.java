package graphCreator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Random;


public class SamplingFromGLR_Holmes_Jones {
	static boolean debug = false;
	
	public static void main (String[] args){
		
		Integer[] left = {5,4,3,3,3,2,1};
		Integer[] right = {5,4,3,3,3,2,1};

		
		if(!EligibilityTester.testEligibility(left, right)){
			System.err.println("The given degree distributions are not eligible");
			System.exit(-1);
		}
		
		Arrays.sort(left);
		for(int deg:left)System.out.print(deg+" ");
		System.out.println();
		Arrays.sort(right);
		SamplingFromGLR_Holmes_Jones sampling = new SamplingFromGLR_Holmes_Jones();

		int[][] adjList = sampling.buildAdjacency_improvedWay(left, right);
//		int[][] adjList = sampling.buildAdjacency_strictWay(left, right);
		int counterOfDrawings = 1;
		while(adjList == null){			
			adjList = sampling.buildAdjacency_improvedWay(left, right);
//			adjList = sampling.buildAdjacency_strictWay(left, right);

			++counterOfDrawings;
		}
		System.out.println("it took "+counterOfDrawings+" to produce the adjacency list");
//		int[] testOfRightDegree = new int[right.length];
//		for(int i = 0; i < adjList.length; ++i){
//			System.out.println("node "+i+" with degree "+left[i]+" has neighbors: \t ");
//			for(int j = 0; j < adjList[i].length; ++j){
//				System.out.print(adjList[i][j]+" ");
//				++testOfRightDegree[adjList[i][j]];
//			}	
//			System.out.println();
//		}
//
//		System.out.println("testing the degree of the vertices on the right");
//		for(int j = 0; j < testOfRightDegree.length; ++j){
//			System.out.println("vertex "+j+" on right side has degree "+testOfRightDegree[j]+" and should have "+right[j]);
//		}
	}



	public static int[][] buildAdjacency_strictWay(Integer[] leftSideDegreeDistribution, Integer[] rightSideDegreeDistribution){
//		if(!EligibilityTester.testEligibility(leftSideDegreeDistribution, rightSideDegreeDistribution)){
//			System.err.println("The given degree distributions are not eligible");
//			System.exit(-1);
//		}

		boolean[][] adj = null;
		int copyOfLeft[] = new int[leftSideDegreeDistribution.length];
		for(int i = leftSideDegreeDistribution.length-1; i >= 0; --i) copyOfLeft[i] = leftSideDegreeDistribution[i];
		double copyOfRight[] = new double[rightSideDegreeDistribution.length];
		int numberOfVerticesOnRightWithDegreeOverZero = 0;

		for(int i = rightSideDegreeDistribution.length-1; i >= 0; --i){
			copyOfRight[i] = rightSideDegreeDistribution[i];
			if(rightSideDegreeDistribution[i] > 0) ++numberOfVerticesOnRightWithDegreeOverZero;
		}

		Random random = new Random();
		//sort the degrees of the left hand side non-decreasingly. 
		Arrays.sort(copyOfLeft);
		int noe = 0;
		for(int degree: copyOfLeft){
			noe += degree;
			//System.out.print(" "+degree);
		}
		//		System.out.println();
		ArrayList<Integer> indicesOfNodesOnTheRight = new ArrayList<Integer>(rightSideDegreeDistribution.length);
		for(int i = 0; i < rightSideDegreeDistribution.length; ++i){
			indicesOfNodesOnTheRight.add(i);
		}

		boolean noRejection = true;
		adj = new boolean[leftSideDegreeDistribution.length][rightSideDegreeDistribution.length];
		int nodeOnRightSide;
		//for each node on the left
		for(int i = copyOfLeft.length-1; i >= 0 && noRejection; --i){
			//pick deg(i) nodes u.a.r. without replacement.  If a node has degree 0 it cannot be chosen anymore.
			Collections.shuffle(indicesOfNodesOnTheRight);
			for(int j = copyOfLeft[i]-1; j >= 0; --j){
				nodeOnRightSide = (int)indicesOfNodesOnTheRight.get(j); 
				//make an edge between them in the adjacency matrix
				adj[i][nodeOnRightSide] = true;
				//decrease degree of the nodes on the right accordingly.
				copyOfRight[nodeOnRightSide]--;
				if(copyOfRight[nodeOnRightSide] < 0){
					if(debug) System.out.println("rejection after the "+i+"th node");
					noRejection = false;
					break;
				}
			}
		}


		if(noRejection){
			int[][] adjList = new int[copyOfLeft.length][];
			int counter = 0;
			for(int i = 0; i < copyOfLeft.length; ++i){
				adjList[i] = new int[copyOfLeft[i]];
				counter = 0; 
				for(int j = 0; j < adj[i].length; ++j){
					if(adj[i][j]){
						adjList[i][counter++] = j;
					}
				}
			}
			return adjList;
		}else{
			return null;
		}
	}
	
	
	
	

	public static int[][] buildAdjacency_improvedWay(Integer[] leftSideDegreeDistribution, Integer[] rightSideDegreeDistribution){
		

		boolean[][] adj = null;
		ArrayList<Integer> left = new ArrayList<Integer>(leftSideDegreeDistribution.length);
		for(int i: leftSideDegreeDistribution) left.add(i);
		ArrayList<Integer> right = new ArrayList<Integer>(rightSideDegreeDistribution.length);
		for(int i : rightSideDegreeDistribution) right.add(i);
		
		Collections.shuffle(left);
		Collections.shuffle(right);
		
		int copyOfLeft[] = new int[left.size()];
		for(int i = left.size()-1; i >= 0; --i) copyOfLeft[i] = left.get(i);
		double copyOfRight[] = new double[right.size()];
		int numberOfVerticesOnRightWithDegreeOverZero = 0;

		for(int i = right.size()-1; i >= 0; --i){
			copyOfRight[i] = right.get(i);
			if(right.get(i) > 0) ++numberOfVerticesOnRightWithDegreeOverZero;
		}

		//sort the degrees of the left hand side non-decreasingly.
		Arrays.sort(copyOfRight);
		Arrays.sort(copyOfLeft);
		int noe = 0;
		for(int degree: leftSideDegreeDistribution){
			noe += degree;
			//System.out.print(" "+degree);
		}
		//		System.out.println();
		ArrayList<Integer> indicesOfNodesOnTheRight = new ArrayList<Integer>(rightSideDegreeDistribution.length);
		for(int i = 0; i < rightSideDegreeDistribution.length; ++i){
			indicesOfNodesOnTheRight.add(i);
		}

		boolean noRejection = true;
		adj = new boolean[leftSideDegreeDistribution.length][rightSideDegreeDistribution.length];
		int nodeOnRightSide;
//		System.out.print("removed: ");
		//for each node on the left
		for(int i = copyOfLeft.length-1; i >= 0; --i){
			if(indicesOfNodesOnTheRight.size() < copyOfLeft[i]){
				noRejection = false;
				if(debug)System.out.println("rejected at node "+i);
				break;
			}else{

				//pick deg(i) nodes u.a.r. without replacement.  If a node has degree 0 it cannot be chosen anymore.
				Collections.shuffle(indicesOfNodesOnTheRight);
				for(int j = copyOfLeft[i]-1; j >= 0; --j){
					nodeOnRightSide = (int)indicesOfNodesOnTheRight.get(j); 
					//make an edge between them in the adjacency matrix
					adj[i][nodeOnRightSide] = true;
					//decrease degree of the nodes on the right accordingly.
					copyOfRight[nodeOnRightSide]--;
					if(copyOfRight[nodeOnRightSide] == 0){
//						System.out.print(nodeOnRightSide+" ");
						--numberOfVerticesOnRightWithDegreeOverZero;
						indicesOfNodesOnTheRight.remove(j);

					}
				}
			}
		}
//		System.out.println();

		if(noRejection){
			int[][] adjList = new int[copyOfLeft.length][];
			int counter = 0;
			for(int i = 0; i < copyOfLeft.length; ++i){
				adjList[i] = new int[copyOfLeft[i]];
				counter = 0; 
				for(int j = 0; j < adj[i].length; ++j){
					if(adj[i][j]){
						adjList[i][counter++] = j;
					}
				}
			}
			return adjList;
		}else{
			return null;
		}
	}

public static boolean[][] buildAdjacency_improvedWay_Matrix(Integer[] leftSideDegreeDistribution, Integer[] rightSideDegreeDistribution){
		

		boolean[][] adj = null;
		
		int copyOfLeft[] = new int[leftSideDegreeDistribution.length];
		for(int i = leftSideDegreeDistribution.length-1; i >= 0; --i) copyOfLeft[i] = leftSideDegreeDistribution[i];
		double copyOfRight[] = new double[rightSideDegreeDistribution.length];
		int numberOfVerticesOnRightWithDegreeOverZero = 0;

		for(int i = rightSideDegreeDistribution.length-1; i >= 0; --i){
			copyOfRight[i] = rightSideDegreeDistribution[i];
			if(rightSideDegreeDistribution[i] > 0) ++numberOfVerticesOnRightWithDegreeOverZero;
		}

		//sort the degrees of the left hand side non-decreasingly.
		Arrays.sort(copyOfRight);
		Arrays.sort(copyOfLeft);
		int noe = 0;
		for(int degree: leftSideDegreeDistribution){
			noe += degree;
			//System.out.print(" "+degree);
		}
		//		System.out.println();
		ArrayList<Integer> indicesOfNodesOnTheRight = new ArrayList<Integer>(rightSideDegreeDistribution.length);
		for(int i = 0; i < rightSideDegreeDistribution.length; ++i){
			indicesOfNodesOnTheRight.add(i);
		}

		boolean noRejection = true;
		adj = new boolean[leftSideDegreeDistribution.length][rightSideDegreeDistribution.length];
		int nodeOnRightSide;
//		System.out.print("removed: ");
		//for each node on the left
		for(int i = copyOfLeft.length-1; i >= 0; --i){
			if(indicesOfNodesOnTheRight.size() < copyOfLeft[i]){
				noRejection = false;
				if(debug)System.out.println("rejected at node "+i);
				break;
			}else{
				//pick deg(i) nodes u.a.r. without replacement.  If a node has degree 0 it cannot be chosen anymore.
				Collections.shuffle(indicesOfNodesOnTheRight);
				for(int j = copyOfLeft[i]-1; j >= 0; --j){
					nodeOnRightSide = (int)indicesOfNodesOnTheRight.get(j); 
					//make an edge between them in the adjacency matrix
					adj[i][nodeOnRightSide] = true;
					//decrease degree of the nodes on the right accordingly.
					copyOfRight[nodeOnRightSide]--;
					if(copyOfRight[nodeOnRightSide] == 0){
//						System.out.print(nodeOnRightSide+" ");
						--numberOfVerticesOnRightWithDegreeOverZero;
						indicesOfNodesOnTheRight.remove(j);

					}
				}
			}
		}
//		System.out.println();

		return noRejection ? adj : null;
		
	}

	
	
/**
 * We sample from the space of all bipartite graphs with the given degree distribution by the method proposed by Holmes and Jones which we slightly modified.  
 * 
 * @param leftSideDegreeDistribution
 * @param rightSideDegreeDistribution
 * @return
 */
	public static int[][] buildAdjacency_improvedWay_ImprovedSpace(Integer[] leftSideDegreeDistribution, Integer[] rightSideDegreeDistribution){
		
//		boolean[][] adj = null;
		//left-hand side is copied to sort it
		int copyOfLeft[] = new int[leftSideDegreeDistribution.length];
		for(int i = leftSideDegreeDistribution.length-1; i >= 0; --i) copyOfLeft[i] = leftSideDegreeDistribution[i];
		double copyOfRight[] = new double[rightSideDegreeDistribution.length];
		int numberOfVerticesOnRightWithDegreeOverZero = 0;

		for(int i = rightSideDegreeDistribution.length-1; i >= 0; --i){
			copyOfRight[i] = rightSideDegreeDistribution[i];
			if(rightSideDegreeDistribution[i] > 0) ++numberOfVerticesOnRightWithDegreeOverZero;
		}

		//sort the degrees of the left hand side non-decreasingly. 
		Arrays.sort(copyOfLeft);
		int noe = 0;
		for(int degree: leftSideDegreeDistribution){
			noe += degree;
			//System.out.print(" "+degree);
		}
		//		System.out.println();
		ArrayList<Integer> indicesOfNodesOnTheRight = new ArrayList<Integer>(rightSideDegreeDistribution.length);
		for(int i = 0; i < rightSideDegreeDistribution.length; ++i){
			indicesOfNodesOnTheRight.add(i);
		}

		boolean noRejection = true;
		
		int[][] adjList = new int[copyOfLeft.length][];
		int counter = 0;
		for(int i = 0; i < copyOfLeft.length; ++i){
			adjList[i] = new int[copyOfLeft[i]];
		}
		
//		adj = new boolean[leftSideDegreeDistribution.length][rightSideDegreeDistribution.length];
		int nodeOnRightSide;
		//for each node on the left
		for(int i = copyOfLeft.length-1; i >= 0; --i){
			if(indicesOfNodesOnTheRight.size() < copyOfLeft[i]){
				noRejection = false;
				if(debug) System.out.println("rejected at node "+i);
				break;
			}else{

				//pick deg(i) nodes u.a.r. without replacement.  If a node has degree 0 it cannot be chosen anymore.
				Collections.shuffle(indicesOfNodesOnTheRight);
				for(int j = copyOfLeft[i]-1; j >= 0; --j){
					nodeOnRightSide = (int)indicesOfNodesOnTheRight.get(j); 
					//make an edge between them in the adjacency matrix
//					adj[i][nodeOnRightSide] = true;
					adjList[i][j] = nodeOnRightSide;
					//decrease degree of the nodes on the right accordingly.
					copyOfRight[nodeOnRightSide]--;
					if(copyOfRight[nodeOnRightSide] == 0){
						--numberOfVerticesOnRightWithDegreeOverZero;
						indicesOfNodesOnTheRight.remove(j);

					}
				}
			}
		}


		return noRejection ? adjList : null;
	}

	


}
