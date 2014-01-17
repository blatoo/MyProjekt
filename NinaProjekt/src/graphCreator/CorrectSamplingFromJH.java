package graphCreator;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import utilities.FeasibleSolution;

public class CorrectSamplingFromJH {
	BigInteger[][] binomialCoefficients;
	Integer[] left;
	Integer[] right; 
	boolean debug = false;
	
	/**
	 * We assume that neither entry in _left and _right is 0.
	 * @param _left
	 * @param _right
	 */
	public CorrectSamplingFromJH(Integer[] _left, Integer[] _right){
		left = _left;
		right = _right;
		//we will use this table to produce the weights
		binomialCoefficients = createBinomialCoefficientsTable(right.length+1);
			
	}
	
	
	
	private BigInteger[][] createBinomialCoefficientsTable(int length) {

		BigInteger[][] binCoeff = new BigInteger[length][];


		for(int i = 0; i < binCoeff.length; ++i){
			binCoeff[i] = new BigInteger[i+1];
			binCoeff[i][0] = BigInteger.ONE;
			for(int j = 1; j < i; ++j){
				binCoeff[i][j] = binCoeff[i-1][j-1].add(binCoeff[i-1][j]);
			}
			binCoeff[i][i] = BigInteger.ONE;	
		}


			if(debug){
				for(int i = 0; i < binCoeff.length; ++i){
					for(int j = 0; j < binCoeff[i].length; ++j){
						System.out.print(binCoeff[i][j]+" ");
					}
					System.out.println();
				}
			}
		return binCoeff;
	}



	public static void main(String[] args){
//		Integer[] leftSide = {3,2,1};
//		Integer[] rightSide = {2,2,1,1};
		Integer[] leftSide = {3,2,2,1};
		Integer[] rightSide = {2,2,2,1,1};

//		Integer[] leftSide = {7,6,5,5,5,4,4,3,1};
//		Integer[] rightSide = {1,3,3,4,4,5,6,6,8};
		
		int numberOfTests = 1000000;
		ArrayList<FeasibleSolution> solutions = new ArrayList<FeasibleSolution>(100);
		int numberOfSolutions = 0;
		
		if(!EligibilityTester.testEligibility(leftSide, rightSide)){
			System.err.println("The given degree distributions are not eligible");
			System.exit(-1);
		}

		
		CorrectSamplingFromJH sampling = new CorrectSamplingFromJH(leftSide, rightSide);
		BigInteger[][] adjMatrix = null;
		BigInteger[][] addedAdjMatrix = new BigInteger[leftSide.length][rightSide.length];
		for(int i =0; i < leftSide.length; ++i){
			for(int j =0; j < rightSide.length; ++j){
				addedAdjMatrix[i][j] = BigInteger.ZERO;
			}
		}
		BigDecimal sumOfWeights = BigDecimal.ZERO;
		
		for(int i = numberOfTests; i > 0; --i){
			adjMatrix = sampling.buildAdjacency_improvedWay();
			int counterOfDrawings = 1;
			while(adjMatrix == null){			
				adjMatrix = sampling.buildAdjacency_improvedWay();		
				++counterOfDrawings;
			}
			
			for(int x = 0; x < adjMatrix[0].length; ++x){ 
				if(adjMatrix[0][x] != BigInteger.ZERO){ 
					sumOfWeights = sumOfWeights.add(new BigDecimal(adjMatrix[0][x]));
					break;
				}
			}

			BigInteger[] numberOfLines = new BigInteger[leftSide.length];
			BigInteger numberOfLine;

			for(int x = 0; x < adjMatrix.length; ++x){
				numberOfLine = BigInteger.ZERO;
				for(int j = 0; j < adjMatrix[x].length; ++j){
					addedAdjMatrix[x][j] = addedAdjMatrix[x][j].add(adjMatrix[x][j]);
					numberOfLine = numberOfLine.shiftLeft(1);
					if(adjMatrix[x][j] != BigInteger.ZERO)
						numberOfLine = numberOfLine.add(BigInteger.ONE);
					//System.out.print(adjMatrix[x][j]+" ");
				}
//				System.out.print(numberOfLine+" ");
				numberOfLines[x] = numberOfLine;
			}
//			System.out.println();
			
			
			
			FeasibleSolution solution = new FeasibleSolution(numberOfLines);
			int index = Collections.binarySearch(solutions, solution);
			
			if(index < 0){
				solutions.add(-index-1, solution);
				System.out.println("number of solutions after "+ (numberOfTests-i+1)+" tests is now "+(++numberOfSolutions));
			}
			

			//System.out.println("it took "+counterOfDrawings+" to produce the adjacency list");
		}
		
		for(FeasibleSolution solution: solutions){
			System.out.println(solution.toString());
		}
		
		double[][] realProbs = computeRealProbs(solutions);
		
		
		BigDecimal maxWeight = new BigDecimal(BigInteger.ZERO);
		BigDecimal[][] scaled = new BigDecimal[leftSide.length][rightSide.length];
		for(int i =0; i < leftSide.length; ++i){
			for(int j =0; j < rightSide.length; ++j){
				scaled[i][j] = BigDecimal.ZERO;
			}
		}
		BigDecimal current;
		System.out.println("Added Adjacencies");
		for(int i =0; i < leftSide.length; ++i){
			for(int j =0; j < rightSide.length; ++j){
				current = new BigDecimal(addedAdjMatrix[i][j], 0);
				System.out.print(addedAdjMatrix[i][j]+":"+current+"  ");
				scaled[i][j] = current;
				maxWeight = maxWeight.compareTo(current) < 0 ? current : maxWeight;
			}
			System.out.println();
		}
		System.out.println();
		System.out.println("Scaling down by "+sumOfWeights);
		for(int i =0; i < leftSide.length; ++i){
			for(int j =0; j < rightSide.length; ++j){
//				System.out.println("dividing "+scaled[i][j].toString()+" by "+sumOfWeights.toString());
				scaled[i][j] = scaled[i][j].divide(sumOfWeights, 5, BigDecimal.ROUND_HALF_DOWN);
				System.out.print(scaled[i][j].toString()+" ");
			}
			System.out.println();
		}
	
		
		
		
	}

	//toDo
private static double[][] computeRealProbs(ArrayList<FeasibleSolution> solutions) {
	
	
	return null;
	}



/**
 * 
 * @param leftSide
 * @param rightSide
 * @return
 */

	public BigInteger[][] buildAdjacency_improvedWay() {			

		
			//we sample until we are done or until we are forced to reject the matrix
			boolean noRejection = true;
			BigInteger[][] weightedAdjMatrix = null;
			try {
				boolean[][] adj = null;
				//we need to make copies because we will sort and alter the degree distributions
				ArrayList<Integer> copyOfLeft = new ArrayList<Integer>(left.length);
				for(int i = left.length-1; i >= 0; --i) copyOfLeft.add(left[i]);
				double copyOfRight[] = new double[right.length];
				int numberOfVerticesOnRightWithDegreeOverZero = 0;
				for(int i = right.length-1; i >= 0; --i){
					copyOfRight[i] = right[i];
					if(right[i] > 0) ++numberOfVerticesOnRightWithDegreeOverZero;
				}

				Arrays.sort(copyOfRight);
				int[] invertedCopyOfRight = new int[copyOfRight.length];
				for(int x = 0; x < copyOfRight.length; ++x) invertedCopyOfRight[copyOfRight.length-1-x]=(int)copyOfRight[x];

				//sort the degrees of the left hand side non-increasingly.
				Collections.sort(copyOfLeft, Collections.reverseOrder());
				
				//count number of edges
				int noe = 0;
				for(int degree: left) noe += degree;

				//we need a list of all nodes that we can choose from. This will be updated and only contain
				//vertices from the right side with a strictly positive degree
				ArrayList<Integer> indicesOfNodesOnTheRight = new ArrayList<Integer>(copyOfRight.length);
				for(int i = 0; i < copyOfRight.length; ++i) if(copyOfRight[i] > 0)	indicesOfNodesOnTheRight.add(i);
				
				adj = new boolean[copyOfLeft.size()][copyOfRight.length];
				int nodeOnRightSide;

				//this weight contains the relative weight of the produced matrix as described in the article.
				BigInteger weight = BigInteger.ONE;
				
				//for each node on the left...
				//for(int i = copyOfLeft.size()-1; i >= 0; --i){
				for(int i = 0; i < copyOfLeft.size(); ++i){
					//if he wants copyOfLeft[i] neighbors but the current list provides less than this number of 
					//nodes with a current strictly-positive degree we have to reject the construction
					if(indicesOfNodesOnTheRight.size() < copyOfLeft.get(i)){
						noRejection = false;
						if(debug)
							System.out.println("rejected at node "+i);
						break;
					}else{
						//the weight is proportional to k_i \choose d_i, i.e., to indicesOfNodesOnTheRight.size() \choose copyOfLeft[i]
						weight = weight.multiply(binomialCoefficients[indicesOfNodesOnTheRight.size()][copyOfLeft.get(i)]);
						
						//otherwise, pick deg(i)=copyOfLeft[i] nodes u.a.r. without replacement.  
						Collections.shuffle(indicesOfNodesOnTheRight);
						for(int j = copyOfLeft.get(i)-1; j >= 0; --j){
							nodeOnRightSide = (int)indicesOfNodesOnTheRight.get(j); 
							//make an edge between them in the adjacency matrix
							adj[i][nodeOnRightSide] = true;
							//decrease degree of the nodes on the right accordingly.
							invertedCopyOfRight[nodeOnRightSide]--;
							if(invertedCopyOfRight[nodeOnRightSide] == 0){
//							System.out.print(nodeOnRightSide+" ");
								--numberOfVerticesOnRightWithDegreeOverZero;
								indicesOfNodesOnTheRight.remove(j);
							}
						}
					}
				}
//			System.out.println();
				
				if(debug && noRejection){
					System.out.print("weight "+weight+": ");
					int numberOfLine = 0;
					for(int i = 0; i < adj.length; ++i){
						numberOfLine = 0;
						for(int j = 0; j < adj[i].length; ++j){
							numberOfLine = numberOfLine << 1;
							if(adj[i][j])  numberOfLine++; 
//							System.out.print((adj[i][j] ? "1 " : "0 "));
						}
						System.out.print(numberOfLine+" ");
					}
					System.out.println();
				}				
				
				
				weightedAdjMatrix = new BigInteger[adj.length][adj[0].length];
				for(int i = 0; i < adj.length; ++i){
					for(int j = 0; j < adj[i].length; ++j){
						weightedAdjMatrix[i][j] = adj[i][j] ? weight : BigInteger.ZERO;
					}
				}
				

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println("Some error occurred");
			}
			return noRejection ? weightedAdjMatrix : null;
			
	}

}
