package experiments;


import utilities.AdjacencyListsToMatrix;
import utilities.Print;
import graphCreator.CanonicGraphProducer_Method2;
import graphCreator.CanonicGraphProducer_Method3;
import graphCreator.EligibilityTester;
import degreeDistributionCreator.DegreeDistributionGenerator;

public class TestRuntimeOfMethods {

	public static boolean debug = false;
	
	public static void main(String[] args){
		for(int i = 4; i <= 100; ++i){
			System.out.println("number of edges is "+(25*300*i));
			new TestRuntimeOfMethods().testTheResults(25,300*i,25,300*i, 10, 10);
		}
	}
	
	public void testTheResults(int degreeLeft, int numberLeft, int degreeRight, int numberRight, int numberOfTests, int lengthOfRandomWalk) {
		int[] leftSide = DegreeDistributionGenerator.uniform(degreeLeft, numberLeft);
		int[] rightSide = DegreeDistributionGenerator.uniform(degreeRight, numberRight);
		
		Integer[] leftSideInt = new Integer[leftSide.length];
		for(int i = 0; i < leftSide.length; ++i) leftSideInt[i] = new Integer(leftSide[i]);
		Integer[] rightSideInt = new Integer[rightSide.length];
		for(int i = 0; i < rightSide.length; ++i) rightSideInt[i] = new Integer(rightSide[i]);
		
		if(debug){
			System.out.println("Starting degree distribution left ");
			Print.printArray(leftSide);
			System.out.println("right");
			Print.printArray(rightSide);
		}
			
		long aggregateTime1 = 0, aggregateTime2 = 0, aggregateTime3 = 0;
		long timeBefore, timeAfter;
		int numberOfTestsCompleted=0;

		for(int i = 0; i < numberOfTests && EligibilityTester.testEligibility(leftSideInt, rightSideInt); ++i){
			++numberOfTestsCompleted;
			
//			timeBefore = System.currentTimeMillis();
//			boolean[][] adj1 = CanonicGraphProducer_Method1.createBipartiteGraph(leftSide, rightSide);
//			timeAfter = System.currentTimeMillis();
//			aggregateTime1 += timeAfter-timeBefore;
//			
//			if(debug){
//				System.out.println("finished first method. Produced adjacency matrix:");
//				Print.printAdjacencyMatrix(adj1);
//			}
//
//			if(!satisfiesDegreeDistributions(adj1, leftSide, rightSide)){
//				System.err.println("The matrix does NOT satisfy the degree distributions");
//				System.exit(-1);
//			}
			
			timeBefore = System.currentTimeMillis();
			boolean[][] adj2 = CanonicGraphProducer_Method2.createBipartiteGraph(leftSide, rightSide);
			timeAfter = System.currentTimeMillis();
			aggregateTime2 += timeAfter-timeBefore;
	
			if(!satisfiesDegreeDistributions(adj2, leftSide, rightSide)){
				System.err.println("The matrix does NOT satisfy the degree distributions");
				System.exit(-1);
			}
			
//			if(!compareTwoAdjacencyMatrices(adj1, adj2)){
//				System.err.println("First and second matrix don't match");
//				System.err.println("First adjacency matrix is: ");
//				Print.printAdjacencyMatrix(adj1);
//				System.err.println("Second adjacency matrix is: ");
//				Print.printAdjacencyMatrix(adj2);
//				System.exit(-1);
//			}
			
			timeBefore= System.currentTimeMillis();
			int[][] adjList3 = CanonicGraphProducer_Method3.createBipartiteGraph(leftSide, rightSide);
			timeAfter = System.currentTimeMillis();
			aggregateTime3 += timeAfter-timeBefore;
			
			boolean[][] adj3 = AdjacencyListsToMatrix.transformAdjacencyListIntoCompleteMatrix(adjList3, leftSide.length, rightSide.length);
			
			if(!compareTwoAdjacencyMatrices(adj2, adj3)){
				System.err.println("Second and third matrix don't match");
				System.err.println("Second adjacency matrix is: ");
				Print.printAdjacencyMatrix(adj2);
				System.err.println("Third adjacency matrix is: ");
				Print.printAdjacencyMatrix(adj3);
				System.exit(-1);
			}
			
			DegreeDistributionGenerator.randomWalkWithMaximum(leftSide, lengthOfRandomWalk, rightSide.length);
			/**
			if(debug){
				System.out.println("Left side degree distribution after random walk");
				Print.printArray(leftSide);
			}*/
				
			DegreeDistributionGenerator.randomWalkWithMaximum(rightSide, lengthOfRandomWalk, leftSide.length);
			/**
			if(debug){
				System.out.println("Right side degree distribution after random walk");
				Print.printArray(rightSide);
			}*/

		}
//		System.out.println("method 1 took "+(aggregateTime1/((double)numberOfTestsCompleted*1000.0))+" seconds");
		System.out.println("method 2 took "+(aggregateTime2/((double)numberOfTestsCompleted*1000.0))+" seconds");
		System.out.println("method 3 took "+(aggregateTime3/((double)numberOfTestsCompleted*1000.0))+" seconds");
	}

	private boolean satisfiesDegreeDistributions(boolean[][] adj1, int[] leftSide, int[] rightSide) {
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

	private void printAdjMatrix(boolean[][] adj1) {
		for(int i = 0; i < adj1.length; ++i){
			for(int j = 0; j < adj1[i].length; ++j){
				System.out.print(adj1[i][j]+" ");
			}
			System.out.println();
		}
		System.out.println();
	}

	public void runAnalysis(){
		
		
		
	}
	
	
	
	public boolean compareTwoAdjacencyMatrices(boolean[][] matrix1, boolean[][] matrix2){
		
		if(matrix1.length != matrix2.length)
			return false;
		
		for(int i = matrix1.length -1; i >= 0; --i){
			if(matrix1[i].length != matrix2[i].length)
				return false;
			for(int j = matrix1[i].length-1; j >= 0; --j){
				if(matrix1[i][j] != matrix2[i][j])
					return false;
			}
		}
		
		return true;
	}
	
}
