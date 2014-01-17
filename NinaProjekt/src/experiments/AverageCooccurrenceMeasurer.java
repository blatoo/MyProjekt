package experiments;

import graphCreator.CanonicGraphProducer_Method3;
import graphCreator.CorrectSamplingFromJH;
import graphCreator.SamplingFromGLR_Holmes_Jones;

import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;

import randomWalk.RandomWalk;

import utilities.*;

import degreeDistributionCreator.DegreeDistributionGenerator;

public class AverageCooccurrenceMeasurer {
	static int numberOfTests = 1;
	static int numberOfInstancesPerTest = 20000;
	static int numberOfNodesLeft = 50;
	static int numberOfNodesRight = 50;
	static int degreeLeft = 20;
	static int degreeRight = 20;
	static int numberOfEdges = numberOfNodesLeft*degreeLeft;
	static int numberOfSkewingEvents =numberOfEdges/4;
	static int numberOfStepsInRandomWalk = (int)(Math.log(numberOfEdges)*numberOfEdges);
	
	
	static String parentDir;
	
	static boolean debug = false;
	static boolean cooccIsComputed = false;
	
	public static void main(String[] args){
		//file to store results in
		parentDir = "D:\\Daten\\Projects2010\\ALENEX2010\\BipartiteGraphs\\experiments";
		File file = new File(parentDir);
		if(!file.exists()){
			file.mkdirs();
		}

		//new AverageCooccurrenceMeasurer().doTest_withRandomWalk();
		new AverageCooccurrenceMeasurer().doTest_withHolmes();
	}

	long time1 = System.currentTimeMillis(), time2;
	
	private void doTest_withHolmes() {
		for(int i = 0; i < numberOfTests; ++i){		
			DegreeDistributionGenerator ddGen = new DegreeDistributionGenerator(1247);
			
			System.out.println("number of skewing events was: "+numberOfSkewingEvents);
			double[][] avgCoocc = new double[numberOfNodesRight][numberOfNodesRight];
			
			
			//create right and left degree distribution. They will be uniform at first, i.e., 
			//they have the same length and everyone has the same degree
			int[] leftSide = DegreeDistributionGenerator.uniform(degreeLeft, numberOfNodesLeft);			
			int[] rightSide = DegreeDistributionGenerator.uniform(degreeRight, numberOfNodesRight);
			
			int[] rightDegree = new int[rightSide.length];
			int[] leftDegree = new int[leftSide.length];
			
			
			//Now we skew the degree distribution
			ddGen.randomWalkWithMaximum(leftSide, numberOfSkewingEvents, rightSide.length);
			if(debug) System.out.println("skewed left side");
			ddGen.randomWalkWithMaximum(rightSide, numberOfSkewingEvents, leftSide.length);
			if(debug) System.out.println("skewed right side");
			if(debug){System.out.println("Arrays look like this: ");Print.printArray(leftSide); Print.printArray(rightSide);}

//			int[][] adjacencyList = null;
			BigInteger[][] adjacencyMatrix = null;
			
			//in this experiment we want to find out about the prob that two vertices with deg i and deg j are connected
			BigInteger[][] addedAdjacencies = new BigInteger[leftSide.length][rightSide.length];
			for(int c = 0; c < addedAdjacencies.length; ++c){
				for(int d = 0; d < addedAdjacencies[c].length; ++d){
					addedAdjacencies[c][d] = BigInteger.ZERO;
				}
			}
			
			//we need to copy the skewed degree distributions into an INTEGER array.
			Integer[] left = new Integer[leftSide.length];
			int counter = 0;
			for(int x: leftSide) left[counter++] = x;
			Integer[] right = new Integer[rightSide.length];
			counter=0;
			for(int x: rightSide) right[counter++] = x;

			CorrectSamplingFromJH sampling = new CorrectSamplingFromJH(left, right);
			
			
			//count number of tries to construct the adjaceny matrix
			int countOfTries = 0;

			for(int j = numberOfInstancesPerTest; j > 0; --j){
				
				adjacencyMatrix = sampling.buildAdjacency_improvedWay();
				++countOfTries;
				while(adjacencyMatrix == null){
					adjacencyMatrix = sampling.buildAdjacency_improvedWay();
					++countOfTries;
				}
				
				//since the method changes the order of the vertices, we have to find out which degree is where
				leftDegree = new int[leftSide.length];
				rightDegree = new int[rightSide.length];
				for(int li = 0; li < adjacencyMatrix.length; li++){
					for(int ri = 0; ri < adjacencyMatrix[li].length; ++ri){
						if(debug)
							System.out.print(adjacencyMatrix[li][ri]+" ");
						if(adjacencyMatrix[li][ri] != BigInteger.ZERO){
							++rightDegree[ri];
							++leftDegree[li];
						}
						addedAdjacencies[li][ri] = addedAdjacencies[li][ri].add(adjacencyMatrix[li][ri]);
					}
					//System.out.println();
				}
				
				
				
				if( j % 100 == 0){
					System.out.println("computed "+j+"th sample");
//					if(numberOfInstancesPerTest-j > 0)
					System.out.println("average number of tries to create the graphs: "+(countOfTries/(numberOfInstancesPerTest-j+1)));
					System.out.println("Absolute time= "+(System.currentTimeMillis()-time1)/60000.0+" min");
					
				}
										
				
				int[][] coocc = null;
//				if(cooccIsComputed){					
//					Cooccurrence.computeCooccurrenceOfColumns(adjacencyMatrix);
//				}

//				if(cooccIsComputed){
//					for(int x = coocc.length-1; x >= 0; --x){
//						for(int y = coocc[x].length-1; y >= 0; --y){
//							avgCoocc[x][y] += coocc[x][y];
//						}
//					}
//				}
			}

			//here starts the output			
			BufferedWriter bw = null;
//			if(cooccIsComputed){
//				for(int x = avgCoocc.length-1; x >= 0; --x){
//					for(int y = avgCoocc[x].length-1; y >= 0; --y){
//						avgCoocc[x][y] /=  (double)numberOfInstancesPerTest;
//						//avgCoocc[x][y] /=  (double)(rightSide[x]*rightSide[y]);
//					}
//				}
//
//				try {
//					bw = new BufferedWriter(new FileWriter(new File(parentDir+File.separator+"experiments_Test_JH_numberOfSkewingEvents_"+numberOfSkewingEvents+"_"+i+".txt")));
//					bw.write("#Starting from a graph with "+numberOfNodesLeft+" on the left, and "+numberOfNodesRight+" on the right, each with \n");
//					bw.write("#degree "+degreeLeft+" (left) and "+degreeRight+" (right), we produced a more skewed graph, by choosing two \n");
//					bw.write("#degrees from the same side, increasing the larger one and decreasing the smaller by one, each. Each side underwent \n");
//					bw.write("#"+numberOfSkewingEvents+" skewing events. For the resulting DD we built a bipartite graph by the improved Holmes and Jones method.\n");
//					bw.write("#We computed the average co-occurrence for each pair over"+numberOfInstancesPerTest+" bipartite graphs with the given DD. \n");
//					bw.write("#\n");
//					bw.write("#The file contains left- and right-hand side DD plus the average co-occurrence matrix. The nodes are non-decreasingly sorted by\n");
//					bw.write("#degree. First number in line gives the degree of the node. The matrix is symmetric. Co-occurrence of the node with itself is \n");
//					bw.write("#not defined and thus replaced by '-'\n");
//					bw.write("#\n");
//
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//
//				utilities.Print.printCooccMatrix(avgCoocc, rightSide, leftSide,  bw);
//
//				try {
//					bw.flush();
//					bw.close();
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}

			BigDecimal maxWeight = new BigDecimal(BigInteger.ZERO);
			BigDecimal[][] scaledWeight = new BigDecimal[addedAdjacencies.length][addedAdjacencies[0].length];
			BigDecimal currentWeight;
			//System.out.println("added Adjacency is");
			for(int x = addedAdjacencies.length-1; x >= 0; --x){
				for(int y = addedAdjacencies[x].length-1; y >= 0; --y){
					currentWeight = new BigDecimal(addedAdjacencies[x][y], 0);
					System.out.print(addedAdjacencies[x][y]+":"+currentWeight+" ");

					maxWeight = maxWeight.compareTo(currentWeight) < 0 ? currentWeight : maxWeight;	
					scaledWeight[x][y] = currentWeight;
				}
				System.out.println();
			}
			System.out.println();
			System.out.println("maxWeight is "+maxWeight);

			for(int x = scaledWeight.length-1; x >= 0; --x){
				for(int y = scaledWeight[x].length-1; y >= 0; --y){
					
					scaledWeight[x][y] = scaledWeight[x][y].divide(maxWeight, 100, BigDecimal.ROUND_HALF_UP);				
				}
			}

			
			
			String newParDirURL = "D:\\Daten\\Publications2010\\KDD2010\\experiments";
			File file2 = new File(newParDirURL); 
			if(! file2.exists()){
				file2.mkdirs();
			}
			try {
				bw = new BufferedWriter(new FileWriter(new File(newParDirURL+File.separator+"DD_left_right_JH_Prob_numberOfSkewingEvents_"+numberOfSkewingEvents+"_"+i+".txt")));
				bw.write("#Starting from a graph with "+numberOfNodesLeft+" on the left, and "+numberOfNodesRight+" on the right, each with \n");
				bw.write("#degree "+degreeLeft+" (left) and "+degreeRight+" (right), we produced a more skewed graph, by choosing two \n");
				bw.write("#degrees from the same side, increasing the larger one and decreasing the smaller by one, each. Each side underwent \n");
				bw.write("#"+numberOfSkewingEvents+" skewing events. For the resulting DD we built a bipartite graph.\n");
				bw.write("#We computed the average prob of adjacency for each pair over"+numberOfInstancesPerTest+" bipartite graphs with the given DD. \n");
				bw.write("#\n");
				bw.write("#This file contains left- and right-hand side DD plus the average probability that the node from the left was connected to the node on the right. " +
						"The nodes are non-decreasingly sorted by\n");
				bw.write("#degree. First number in line gives the degree of the node. The matrix is in general not symmetric.\n");
				bw.write("#\n");
				bw.write("#\n");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
			utilities.Print.printAvgAdjMatrixBD(scaledWeight, rightDegree, leftDegree,  bw);
				
			
			try {
				bw.flush();
				bw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		numberOfSkewingEvents += numberOfEdges;
	}

	
	
	/**
	 * toDo: Install back the random walk method!
	 */
	private void doTest_withRandomWalk() {
		int numberOfRoundsOfRandomWalk = 5*(int)(Math.log(numberOfEdges)*(double)numberOfEdges);

		
		for(int i = 0; i < numberOfTests; ++i){		
			//numberOfSkewingEvents = (i+1)*numberOfEdges/2;
			DegreeDistributionGenerator ddGen = new DegreeDistributionGenerator(1247);

			
			
			System.out.println("number of skewing events was: "+numberOfSkewingEvents);
			double[][] avgCoocc = new double[numberOfNodesRight][numberOfNodesRight];
			
			
			//create right and left degree distribution. They will be uniform at first, i.e., 
			//they have the same length and everyone has the same degree
			int[] leftSide = DegreeDistributionGenerator.uniform(degreeLeft, numberOfNodesLeft);			
			int[] rightSide = DegreeDistributionGenerator.uniform(degreeRight, numberOfNodesRight);
			
			int[] rightDegree = new int[rightSide.length];
			int[] leftDegree = new int[leftSide.length];
			
			
			//Now we skew the degree distribution
			ddGen.randomWalkWithMaximum(leftSide, numberOfSkewingEvents, rightSide.length);
			if(debug) System.out.println("skewed left side");
			
			int numberOfAllCooccEvents = 0;
			for(int z = leftSide.length-1; z >= 0; --z){
				int cooccEvents = leftSide[z] % 2 == 0 ? 
						(leftSide[z]>>1)*(leftSide[z]-1):
							leftSide[z]*((leftSide[z]-1)>>1); 
				numberOfAllCooccEvents += cooccEvents;
			}
			System.out.println("m events is "+numberOfAllCooccEvents);
			
			
			ddGen.randomWalkWithMaximum(rightSide, numberOfSkewingEvents, leftSide.length);
			if(debug) System.out.println("skewed right side");

			
			
			if(debug){System.out.println("Arrays look like this: ");Print.printArray(leftSide); Print.printArray(rightSide);}

			int[][] adjacencyList = CanonicGraphProducer_Method3.createBipartiteGraph(leftSide, rightSide);
			if(debug) System.out.println("build adjacency lists");
			
			boolean[][] adjacencyMatrix = AdjacencyListsToMatrix.transformAdjacencyListIntoCompleteMatrix(adjacencyList, leftSide.length, rightSide.length);
			if(debug) System.out.println("built matrix");
			
			//since the method changes the order of the vertices, we have to find out which degree is where
			rightDegree = new int[rightSide.length];
			for(int li = 0; li < adjacencyList.length; li++){
				leftDegree[li] = adjacencyList[li].length;
				for(int ri = 0; ri < adjacencyList[li].length; ++ri){
					++rightDegree[adjacencyList[li][ri]];	
				}
			}
			
			
			
			double[][] addedAdjacencies = new double[leftSide.length][rightSide.length];
			
			Integer[] left = new Integer[leftSide.length];
			int counter = 0;
			for(int x: leftSide) left[counter++] = x;
			
			Integer[] right = new Integer[rightSide.length];
			counter=0;
			for(int x: rightSide) right[counter++] = x;
			
			//for each adjacency matrix (that is in the beginning in the canonic form)
			//we make numberOfInstancesPerTest many random walks and average the 
			//resulting co-occurrences for all pairs of vertices.
			
			for(int j = numberOfInstancesPerTest; j > 0; --j){
				long time1 = System.currentTimeMillis();
				RandomWalk.doTheWalk(numberOfStepsInRandomWalk, adjacencyMatrix);
				
				if( j % 100 == 0){
					System.out.println("did "+j+"th random walk");
					System.out.println("average time to create the graphs: "+((System.currentTimeMillis()-time1)/(1000.0)));
				}
				
		
				for(int leftI = 0; leftI < adjacencyMatrix.length; ++leftI){
					for(int rightI = 0; rightI < adjacencyMatrix[leftI].length; ++rightI){
						if(adjacencyMatrix[leftI][rightI])
							++addedAdjacencies[leftI][rightI];
					}
				}
				
				
				int[][] coocc = null;
				if(cooccIsComputed){					
					Cooccurrence.computeCooccurrenceOfColumns(adjacencyMatrix);
				}

				if(cooccIsComputed){
					for(int x = coocc.length-1; x >= 0; --x){
						for(int y = coocc[x].length-1; y >= 0; --y){
							avgCoocc[x][y] += coocc[x][y];
						}
					}
				}
			}
			
//			System.out.println();
			BufferedWriter bw = null;
			
			if(cooccIsComputed){

				for(int x = avgCoocc.length-1; x >= 0; --x){
					for(int y = avgCoocc[x].length-1; y >= 0; --y){
						avgCoocc[x][y] /=  (double)numberOfInstancesPerTest;
						//avgCoocc[x][y] /=  (double)(rightSide[x]*rightSide[y]);

					}
				}

				try {
					bw = new BufferedWriter(new FileWriter(new File(parentDir+File.separator+"experiments_Test_RW_"+numberOfRoundsOfRandomWalk+"_numberOfSkewingEvents_"+numberOfSkewingEvents+"_"+i+".txt")));
					bw.write("#Starting from a graph with "+numberOfNodesLeft+" on the left, and "+numberOfNodesRight+" on the right, each with \n");
					bw.write("#degree "+degreeLeft+" (left) and "+degreeRight+" (right), we produced a more skewed graph, by choosing two \n");
					bw.write("#degrees from the same side, increasing the larger one and decreasing the smaller by one, each. Each side underwent \n");
					bw.write("#"+numberOfSkewingEvents+" skewing events. For the resulting DD we built a bipartite graph, and performed a random walk on it\n");
					bw.write("# with "+numberOfRoundsOfRandomWalk+" steps in the Markov chain. We computed the average co-occurrence for each pair over\n");
					bw.write("#"+numberOfInstancesPerTest+" bipartite graphs with the given DD. \n");
					bw.write("#\n");
					bw.write("#The file contains left- and right-hand side DD plus the average co-occurrence matrix. The nodes are non-decreasingly sorted by\n");
					bw.write("#degree. First number in line gives the degree of the node. The matrix is symmetric. Co-occurrence of the node with itself is \n");
					bw.write("#not defined and thus replaced by '-'\n");
					bw.write("#\n");

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				utilities.Print.printCooccMatrix(avgCoocc, rightSide, leftSide,  bw);

				try {
					bw.flush();
					bw.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			for(int x = addedAdjacencies.length-1; x >= 0; --x){
				for(int y = addedAdjacencies[x].length-1; y >= 0; --y){
					addedAdjacencies[x][y] /=  (double)numberOfInstancesPerTest;
				}
			}
	
			
			String newParDirURL = "D:\\Daten\\Publications2010\\KDD2010\\experiments";
			File file2 = new File(newParDirURL); 
			if(! file2.exists()){
				file2.mkdirs();
			}
			try {
				bw = new BufferedWriter(new FileWriter(new File(newParDirURL+File.separator+"DD_left_right_Prob_RW_numberOfSkewingEvents_"+numberOfSkewingEvents+"_"+i+".txt")));
				bw.write("#Starting from a graph with "+numberOfNodesLeft+" on the left, and "+numberOfNodesRight+" on the right, each with \n");
				bw.write("#degree "+degreeLeft+" (left) and "+degreeRight+" (right), we produced a more skewed graph, by choosing two \n");
				bw.write("#degrees from the same side, increasing the larger one and decreasing the smaller by one, each. Each side underwent \n");
				bw.write("#"+numberOfSkewingEvents+" skewing events. For the resulting DD we built a bipartite graph, and performed a random walk on it\n");
				bw.write("# with "+numberOfRoundsOfRandomWalk+" steps in the Markov chain. We computed the average prob of adjacency for each pair over\n");
				bw.write("#"+numberOfInstancesPerTest+" bipartite graphs with the given DD. \n");
				bw.write("#\n");
				bw.write("#This file contains left- and right-hand side DD plus the average probability that the node from the left was connected to the node on the right. " +
						"The nodes are non-decreasingly sorted by\n");
				bw.write("#degree. First number in line gives the degree of the node. The matrix is in general not symmetric.\n");
				bw.write("#\n");
				bw.write("#\n");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
			utilities.Print.printAvgAdjMatrix(addedAdjacencies, rightDegree, leftDegree,  bw);
				
			
			try {
				bw.flush();
				bw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		numberOfSkewingEvents += numberOfEdges;
	}

	
}
