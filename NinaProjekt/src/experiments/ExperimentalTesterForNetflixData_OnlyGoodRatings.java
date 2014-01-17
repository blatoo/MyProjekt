package experiments;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;
import java.util.StringTokenizer;
import java.io.*;

import javax.swing.JOptionPane;

import randomWalk.RandomWalk;

import utilities.AdjacencyListsToMatrix;
import utilities.MyFilenameFilter;

public class ExperimentalTesterForNetflixData_OnlyGoodRatings {

	private static boolean debug = false;
	private static boolean notitle = false;
	private static boolean plot = true;
	private static int numberOfFilms = 17770;
	private static int numberOfUserPerMatrix = 30000;
	private static int minNumberOfCommonUsers = 5;


	public ExperimentalTesterForNetflixData_OnlyGoodRatings() {
		
	}

	public static void main(String[] args){
		
		int startingTheTesting=0, numberOfTests=0;
		int numberOfSamples = 5000;

		if(args.length > 1){
			startingTheTesting = Integer.parseInt(args[0]);
			numberOfTests = Integer.parseInt(args[1]);
		}else{
			System.err.println("Please provide at least two arguments: where should the test start and how many tests do you want to be generated?");
			System.err.println("Each test contains the film-rating-information of "+numberOfUserPerMatrix+" many customers. In sum, we have about 480,000 customers.");
			System.err.println("The first data set is 1, the last is "+(480000/numberOfUserPerMatrix)+". Thus, the first argument must be a number in this interval and the ");
			System.err.println("second argument plus the first must be not larger than the maximum of the interval");

			System.exit(-1);
		}
		
		int lengthOfWalk = (int)(Math.log(numberOfSamples)*numberOfUserPerMatrix);
		runAnalysis(numberOfUserPerMatrix, numberOfSamples, lengthOfWalk, "/home/nina/Data/Netflix/NewCoocc/OnlyGoodRatings", startingTheTesting, numberOfTests);
	}
	
	
	/**
	 * This function controls the experiment.
	 * 
	 * @param filmDegrees: left-hand degree distribution (by definition the one for which we want to collect the co-occurrence values)
	 * @param userDegrees: right-hand degree distribution
	 * @param numberOfSamples
	 * @param lengthOfWalk
	 * @param outputFile
	 * @return
	 */
	
	public static int runAnalysis(int numberOfUsersPerMatrix, int numberOfSamples, int lengthOfWalk,  String outputFile, int startingTheTesting, int numberOfTests){
		//read in degrees of left
		System.out.println("now computing the experiment");
		int counter = 0;
		
		StringTokenizer st;		
		System.out.println("building adjacency matrix");
		//will contain the adjacency matrix
		boolean[][] adjacencyUsers = new boolean[numberOfUsersPerMatrix][numberOfFilms];
		//this is where the original data is located
		File file = new File("/home/nina/Data/Netflix/UserBasedData/OnlyGoodRating");
		String[] filesUser = file.list(new MyFilenameFilter());
		//we make sure that the files are read in in their alphabetic order
		Arrays.sort(filesUser);
		
		

		BufferedReader br;
		--startingTheTesting;
		try{
			int dataSetCounter = 1;
			for(int i = 0; i < filesUser.length-1; ++i){
				br = new BufferedReader(new FileReader(new File(file+File.separator+filesUser[i])));
				System.out.println("processing "+filesUser[i]);
				//first line is userLine 
				String line = br.readLine();
				//second is data line
				line = br.readLine();
				while(line != null){
					//we have computed all data sets that we wanted to compute
					if(dataSetCounter > startingTheTesting + numberOfTests){
						System.out.println("Did all tests from "+(startingTheTesting+1)+" until "+(startingTheTesting+numberOfTests));
						return 1;
					}
					
					if(dataSetCounter > startingTheTesting){
						st = new StringTokenizer(line, " ");
						//here we're reading in all films seen by one user
						while(st.hasMoreTokens()){
							adjacencyUsers[counter][Short.parseShort(st.nextToken())-1] = true;
						}
					}
					++counter;
					//if our adjacency matrix is full, we start the comparison against the randomwalk-graph
					if(counter == numberOfUsersPerMatrix){
						if(dataSetCounter > startingTheTesting){
							short[] degreeOfUsers = new short[numberOfUsersPerMatrix];
							short[] degreeOfFilms = new short[numberOfFilms];
							computeDegrees(adjacencyUsers, degreeOfUsers, degreeOfFilms);						

							short[][] originalCoocc = turnIntoCoocc(adjacencyUsers);
							printOutOriginal(originalCoocc, outputFile, dataSetCounter);

							//this matrix is true at x,y if the films share at least minNumberOfCommonUsers users
							boolean[][] adj = new boolean[numberOfFilms][numberOfFilms];
							for(int x = 0; x < originalCoocc.length;++x){
								for(int y = 0; y < originalCoocc[x].length; ++y){
									if(originalCoocc[x][y] >= minNumberOfCommonUsers)
										adj[x][y] = true;
								}
							}
							
							originalCoocc = null;
							

							
							int[][] coocc = new int[numberOfFilms][];
							//initialization of a lower triangle matrix
							for(int x = 1; x < coocc.length; ++x){
								//if(originalCoocc[x][x] != 0)
									coocc[x] = new int[x];
							}

							doTest2(adjacencyUsers, numberOfSamples, lengthOfWalk, coocc, degreeOfUsers, degreeOfFilms);
							printCoocc(coocc, outputFile, dataSetCounter, numberOfSamples, degreeOfFilms, adj);
							//reinitialize the adjacency matrix
							for(int x = 0; x < adjacencyUsers.length; ++x){
								for(int y = 0; y < adjacencyUsers[x].length; ++y)
									adjacencyUsers[x][y] = false;// = new boolean[numberOfUsersPerMatrix][numberOfFilms];
							}
						}
						
						
						//we are starting to do the next data set.
						++dataSetCounter;
						//which of course doesn't have any users yet
						counter = 0;
						
						
							
						
					}
					br.readLine();
					line = br.readLine();
				}
				br.close();
			}
		}catch(Exception e){
			e.printStackTrace();
		}

		return 1;
	}


	
	private static void computeDegrees(boolean[][] adjacencyUsers, short[] degreeOfUsers, short[] degreeOfFilms) {
		for(int user = 0; user < adjacencyUsers.length; ++user){
			for(int film = adjacencyUsers[user].length-1; film >= 0; --film){
				if(adjacencyUsers[user][film]){
					++degreeOfFilms[film];
					++degreeOfUsers[user];
				}
			}
		}
	}

	private static void printCoocc(int[][] coocc, String outputDir,
			int numberOfDataSet, int numberOfSamples, short[] degreesOfFilms, boolean[][] adj) {

		File dir = new File(outputDir);
		if(!dir.exists()){
			dir.mkdirs();
		}
		String fileURL = outputDir+File.separator+"sim_numbOfusers"+numberOfUserPerMatrix+"_"+numberOfDataSet+".txt";
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(new File(fileURL)));
//			int[] degreesOfFilms = new int[numberOfFilms];
//			for(int i = 0; i < coocc.length; ++i){
//				degreesOfFilms[i] = coocc[i][i];
//			}
			
			for(int i = 0; i < coocc.length; ++i){
//			for(int i = 0; i < 1000; ++i){
				if(degreesOfFilms[i] != 0){
					//System.out.println("i is "+i);
					for(int j = 0; j < i; ++j){
						if(adj[i][j])
							bw.write(i+" "+j+" "+(int)((double)coocc[i][j]/(double)numberOfSamples +0.5)+"\n");
					}
				}
			}
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

	private static void printOutOriginal(short[][] originalCoocc, String outputDir, int numberOfDataSet) {
		// TODO Auto-generated method stub
		File dir = new File(outputDir);
		if(!dir.exists()){
			dir.mkdirs();
		}
		String fileURL = outputDir+File.separator+"original_NumbUsers"+numberOfUserPerMatrix+"_"+numberOfDataSet+".txt"; 
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(new File(fileURL)));
			bw.write("#Contains the number of common users for all pairs of films with at least "+minNumberOfCommonUsers+"\n");
			bw.write("#Number of users: "+numberOfUserPerMatrix+"\n");
			for(int i = 1; i < originalCoocc.length; ++i){
//			for(int i = 0; i < 1000; ++i){
				if(originalCoocc[i][i] != 0){
//					System.out.println("i is "+i);
					for(int j = 0; j < i; ++j){
//					for(int j = 0; j < 1000; ++j){
						if(originalCoocc[i][j] >= minNumberOfCommonUsers)
							bw.write(i+" "+j+" "+originalCoocc[i][j]+"\n");
					}
				}
			}
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Turn a boolean adjacency matrix into a matrix of co-occurrence
	 * of the vertices on the COLUMNS of the matrix.
	 * 
	 * @param adjacencyUsers
	 * @return
	 */
	private static short[][] turnIntoCoocc(boolean[][] adjacencyUsers) {
		short[][] coocc = new short[numberOfFilms][];
		for(int film = 0; film < coocc.length; ++film)
			coocc[film] = new short[film+1];
		
		for(int user = adjacencyUsers.length-1; user >= 0; --user){
			int degree = 0;
			for(int film = 0; film < adjacencyUsers[user].length; ++film){
				if(adjacencyUsers[user][film]) ++degree;
			}
			short[] filmsOfUser = new short[degree];
			degree = 0;
			for(int film = 0; film < adjacencyUsers[user].length; ++film){
				if(adjacencyUsers[user][film]) filmsOfUser[degree++] = (short)film;
			}
			
			for(short firstIndex = 0; firstIndex < filmsOfUser.length; ++firstIndex)
				for(short secondIndex = 0; secondIndex <= firstIndex; ++secondIndex)
					coocc[filmsOfUser[firstIndex]][filmsOfUser[secondIndex]] += 1.0;
		}
		return coocc;
	}


	/**
	 * Samples numberOfSamples from glr by a random walk with lengthOfWalk steps.
	 * 
	 * @param adjacencyUsers
	 * @param numberOfSamples
	 * @param lengthOfWalk
	 */
	private static void doTest(boolean[][] adjacencyUsers, int numberOfSamples, int lengthOfWalk, int[][] cooccurrence) {
		for(int i = numberOfSamples; i > 0; --i){
//			if(i % 100 == 0)
				System.out.println("doing "+(numberOfSamples-i+1)+"th walk");
			//do the walk
			RandomWalk.doTheWalk(lengthOfWalk, adjacencyUsers);
		
			//for each user we convert his adjvector into an adjlist and
			//compute all pairs of films that he has rented together
			for(int user = 0; user < adjacencyUsers.length; ++user){
				//convert adjacency matrix into adjacency lists
				int degree = 0;
				for(int film = adjacencyUsers[user].length-1; film>= 0; --film){
					if(adjacencyUsers[user][film]) ++degree;
				}
				//build user's adjacency list
				short[] list = new short[degree];
				int counter =0;
				for(int film = 0; film < adjacencyUsers[user].length; ++film){
					if(adjacencyUsers[user][film]) list[counter++] = (short)film;
				}
				//for each pair of films, add one to the co-occurrence matrix
				for(int firstIndex = 0; firstIndex < list.length; ++firstIndex)
					for(int secondIndex = 0; secondIndex < firstIndex; ++secondIndex)
						cooccurrence[list[firstIndex]][list[secondIndex]] += 1.0;
			}
			//end of sampling
		}
	}

	/**
	 * Samples numberOfSamples from glr by a random walk with lengthOfWalk steps.
	 * 
	 * @param adjacencyUsers
	 * @param numberOfSamples
	 * @param lengthOfWalk
	 */
	private static void doTest2(boolean[][] adjacencyUsers, int numberOfSamples, int lengthOfWalk, int[][] cooccurrence, short[] degreeOfUsers, short[] degreeOfFilms) {
		for(int i = numberOfSamples; i > 0; --i){
//			if(i % 100 == 0)
				System.out.println("doing "+(numberOfSamples-i+1)+"th walk");
			//do the walk
			long time = System.currentTimeMillis();
			RandomWalk.doTheWalk(lengthOfWalk, adjacencyUsers);
			System.out.println("   took "+(System.currentTimeMillis()-time)+" ms to do the random walk");
			
			time = System.currentTimeMillis();
			//we turn the adjmatrix into two adj-lists. 
			short[][] adjListsUsers = new short[numberOfUserPerMatrix][];
			short[][] adjListsFilms = new short[numberOfFilms][];
			short[] currentDegreeOfFilm = new short[numberOfFilms];
			
			for(int user = 0; user < adjacencyUsers.length; ++user) adjListsUsers[user] = new short[degreeOfUsers[user]];
			for(int film = 0; film < numberOfFilms; ++film) adjListsFilms[film] = new short[degreeOfFilms[film]];
			System.out.println("   took "+(System.currentTimeMillis()-time)+"ms to initialize all data structures ");
			
			time = System.currentTimeMillis();
			for(int user = 0; user < adjacencyUsers.length; ++user){			
				//build user's adjacency list
				int counter =0;
				for(int film = 0; film < adjacencyUsers[user].length; ++film){
					if(adjacencyUsers[user][film]){
						adjListsUsers[user][counter++] = (short)film;
						adjListsFilms[film][currentDegreeOfFilm[film]++] = (short)user;
					}
				}
			}
			System.out.println("   took "+(System.currentTimeMillis()-time)+"ms to build the adjacency lists");
			
			time = System.currentTimeMillis();
			//for each film go to all neighbors of this film and add all other films to its co-occurrence
			for(int film = 0; film < degreeOfFilms.length; ++film){
				for(short user: adjListsFilms[film]){
					for(short otherFilm: adjListsUsers[user]){
						if(otherFilm >= film)
							break;
						++cooccurrence[film][otherFilm];
					}
				}
			}
			System.out.println("   took "+(System.currentTimeMillis()-time+"ms to compute the cooccs"));
		
		//end of sampling
		}
	}
	
	/**
	 * Given a boolean matrix which states which films have a positive cooccurrence with which other film, 
	 * we turn it into a film-adjacency list matrix in which each film lists all other films (with smaller index) 
	 * with which they have a positive co-occurrence
	 * 
	 * @param adj
	 * @return
	 */
	public static short[][] computeFilmToFilm(boolean[][] adj){
		short[][] filmToFilm = new short[numberOfFilms][];
		
		//first we need to know with how many other films a film has a coocc of at least 1.
		short[] degreeOfFilmFilm = new short[numberOfFilms];
		for(int x = 0; x < adj.length; ++x){
			for(int y = 0; y < x; ++y){
				if(adj[x][y])
					++degreeOfFilmFilm[x];
			}
		}
		
		//init
		for(short x= 0; x < filmToFilm.length; ++x){
			filmToFilm[x] = new short[degreeOfFilmFilm[x]];
		}
		
		short counter; 
		for(int x = 0; x < adj.length; ++x){
			counter = 0; 
			for(short y = 0; y < x; ++y){
				if(adj[x][y])
					filmToFilm[x][counter++] = y;
			}
		}
		
		
		return filmToFilm;
		
	}


	/**
	 * Samples numberOfSamples from glr by a random walk with lengthOfWalk steps.
	 * 
	 * @param adjacencyUsers
	 * @param numberOfSamples
	 * @param lengthOfWalk
	 */
	private static void doTest3(boolean[][] adjacencyUsers, int numberOfSamples, int lengthOfWalk, int[][] cooccurrence, short[] degreeOfUsers, short[] degreeOfFilms, short[][] filmToFilm) {
		for(int i = numberOfSamples; i > 0; --i){
//			if(i % 100 == 0)
				System.out.println("doing "+(numberOfSamples-i+1)+"th walk");
			//do the walk
			long time = System.currentTimeMillis();
			RandomWalk.doTheWalk(lengthOfWalk, adjacencyUsers);
			System.out.println("   took "+(System.currentTimeMillis()-time)+" ms to do the random walk");
			
			time = System.currentTimeMillis();
			//we turn the adjmatrix into an adj-lists for the films, i.e., they know their users 			
			short[][] adjListsFilms = new short[numberOfFilms][];
			short[] currentDegreeOfFilm = new short[numberOfFilms];
			
			for(int film = 0; film < numberOfFilms; ++film) adjListsFilms[film] = new short[degreeOfFilms[film]];
			System.out.println("   took "+(System.currentTimeMillis()-time)+"ms to initialize all data structures ");
			
			time = System.currentTimeMillis();
			for(int user = 0; user < adjacencyUsers.length; ++user){			
				for(int film = 0; film < adjacencyUsers[user].length; ++film){
					if(adjacencyUsers[user][film]){
						adjListsFilms[film][currentDegreeOfFilm[film]++] = (short)user;
					}
				}
			}
			
			System.out.println("   took "+(System.currentTimeMillis()-time)+"ms to build the adjacency list");
			
			time = System.currentTimeMillis();
			//for each film go to all neighbors of this film and add all other films to its co-occurrence
			for(int film = 0; film < degreeOfFilms.length; ++film){
			//	System.out.println("film is "+film);
				boolean[] isUserOfFilm = new boolean[numberOfUserPerMatrix];
				for(short user: adjListsFilms[film]) isUserOfFilm[user] = true;
				for(short otherFilm: filmToFilm[film]){
			//		System.out.println("other film is "+otherFilm);
					for(short otherUser: adjListsFilms[otherFilm]){
						if(isUserOfFilm[otherUser])
							++cooccurrence[film][otherFilm];
					}
				}
			}
			System.out.println("   took "+(System.currentTimeMillis()-time+"ms to compute the cooccs"));
		
		//end of sampling
		}
	}

	
	
	private static void normalize(double[][] cooccurrence, int numberOfSamples) {

		for(int i = 0; i < cooccurrence.length; ++i){
			for(int j = 0; j < cooccurrence.length; ++j){
				cooccurrence[i][j] /= (double)numberOfSamples;
			}
		}
	}


	private void computeCooccurrence(int[][] adjacency, double[][] cooccurrence) {
		int s, t;
		for(int index1 = adjacency.length-1; index1 >= 0; --index1){
			
			for(int index2 = adjacency[index1].length-1; index2 >= 0; --index2){
				s = adjacency[index1][index2];
				for(int index3 = index2-1; index3 >= 0; --index3){
					t = adjacency[index1][index3];
					++cooccurrence[s][t];
					++cooccurrence[t][s];
				}
			}
		}
	}

	private void computeCooccurrence(short[][] adjacency, short[][] cooccurrence) {
		System.out.println("starting cooccurrence computation");
		int s, t;
		for(int index1 = adjacency.length-1; index1 >= 0; --index1){
			if(index1 % 100000 == 0)
				System.out.println("doing the "+(500000-index1)+"th film");
			if(adjacency[index1] != null){
				for(int index2 = adjacency[index1].length-1; index2 >= 0; --index2){
					s = adjacency[index1][index2];
					for(int index3 = index2-1; index3 >= 0; --index3){
						t = adjacency[index1][index3];
						++cooccurrence[s][t];
						++cooccurrence[t][s];
					}
				}
			}
		}
		System.out.println("done with computing cooccurrence");
	}


	private boolean test(int[][] adjacencyR, LinkedList<Integer> S) {
		int[] degreeDisOfS = new int[S.size()];
		for(int i = 0; i < adjacencyR.length; ++i){
			boolean[] alreadyInThere = new boolean[S.size()];
			for(int j = 0; j < adjacencyR[i].length; ++j){
				if(!alreadyInThere[adjacencyR[i][j]]){
					alreadyInThere[adjacencyR[i][j]] = true;
					++degreeDisOfS[adjacencyR[i][j]];
				}else{
					System.out.println("there is a double neighbor in line "+i+":");
					for(int j1=0; j1 < adjacencyR[i].length; ++j1){
						System.out.print(" "+adjacencyR[i][j1]);
					}
					System.out.println();
					return false;
				}
			}
		}

		Iterator<Integer> it = S.iterator();
		int tempCounter = 0;
		while(it.hasNext()){
			if(degreeDisOfS[tempCounter++] != it.next())
				return false;
		}
		
		return true;
	}

	private boolean test(short[][] adjacencyR, LinkedList<Integer> S) {
		int[] degreeDisOfS = new int[S.size()];
		for(int i = 0; i < adjacencyR.length; ++i){
			boolean[] alreadyInThere = new boolean[S.size()];
			for(int j = 0; j < adjacencyR[i].length; ++j){
				if(!alreadyInThere[adjacencyR[i][j]]){
					alreadyInThere[adjacencyR[i][j]] = true;
					++degreeDisOfS[adjacencyR[i][j]];
				}else{
					System.out.println("there is a double neighbor in line "+i+":");
					for(int j1=0; j1 < adjacencyR[i].length; ++j1){
						System.out.print(" "+adjacencyR[i][j1]);
					}
					System.out.println();
					return false;
				}
			}
		}

		Iterator<Integer> it = S.iterator();
		int tempCounter = 0;
		while(it.hasNext()){
			if(degreeDisOfS[tempCounter++] != it.next())
				return false;
		}
		
		return true;
	}


	/**
	 * We get a list of integers
	 * 
	 * 
	 * @param R
	 * @param S
	 * @return
	 */
	private int areDegreeDistributionsEligible(LinkedList<Integer> R, LinkedList<Integer> S) {
		boolean SMajorizedByR = true, RMajorizedByS = true;
		
		int tempSumR=0, tempSumS=0;
		Iterator<Integer> lR= R.iterator(), lS = S.iterator();
		while(lR.hasNext() && lS.hasNext()){
			tempSumR += lR.next();
			tempSumS += lS.next();
			if(tempSumR > tempSumS)
				RMajorizedByS = false;
			if(tempSumS > tempSumR)
				SMajorizedByR = false;
		}
		
		while(lR.hasNext())
			tempSumR += lR.next();
		while(lS.hasNext())
			tempSumS += lS.next();
		if(tempSumR != tempSumS){
			
			JOptionPane.showMessageDialog(null, "The degrees do not sum up! R-S = "+(tempSumR-tempSumS), "Error", JOptionPane.ERROR_MESSAGE);
			return 0;
		}
		
		if(SMajorizedByR || RMajorizedByS){
			return SMajorizedByR ? 1 : -1;
		}else{
			return 0;
		}
	}

}
