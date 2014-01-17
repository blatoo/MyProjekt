package experiments;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Random;
import java.util.StringTokenizer;
import java.io.*;

import javax.swing.JOptionPane;

import randomWalk.RandomWalk;

import utilities.AdjacencyListsToMatrix;
import utilities.MyFilenameFilter;

public class ExperimentalTesterForNetflixData_Degree {

	private static boolean debug = false;
	private static boolean notitle = false;
	private static boolean plot = true;
private static int numberOfFilms = 17770;
static int numberOfUserPerMatrix = 10000;
static int binsize = 100;

	public ExperimentalTesterForNetflixData_Degree() {
		
	}

	public static void main(String[] args){
		
		int startingTheTesting=0, numberOfTests=0;
		if(args.length > 1){
			startingTheTesting = Integer.parseInt(args[0]);
			numberOfTests = Integer.parseInt(args[1]);
		}else{
			System.err.println("Please provide at least two arguments: where should the test start and how many tests do you want to be generated?");
			System.exit(-1);
		}
		
		
		runAnalysis(numberOfUserPerMatrix,  "/home/nina/Data/Netflix/NewCoocc", startingTheTesting, numberOfTests);
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
	
	public static int runAnalysis(int numberOfUsersPerMatrix,  String outputFile, int startingTheTesting, int numberOfTests){
		//read in degrees of left
		System.out.println("now computing the experiment");
		int counter = 0;
		
		StringTokenizer st;		
		System.out.println("building adjacency matrix");
		//will contain the adjacency matrix
		boolean[][] adjacencyUsers = new boolean[numberOfUsersPerMatrix][numberOfFilms];
		File file = new File("/home/nina/Data/Netflix/UserBasedData");
		String[] filesUser = file.list(new MyFilenameFilter());
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
					if(dataSetCounter > startingTheTesting + numberOfTests){
						System.out.println("Did all tests from "+(startingTheTesting+1)+" until "+(startingTheTesting+numberOfTests));
						return 1;
					}
					
					if(dataSetCounter > startingTheTesting){
						st = new StringTokenizer(line, " ");
						while(st.hasMoreTokens()){
							adjacencyUsers[counter][Short.parseShort(st.nextToken())-1] = true;
						}
					}
					++counter;
					if(counter == numberOfUsersPerMatrix){
						if(dataSetCounter > startingTheTesting){
							System.out.println("printint the "+dataSetCounter+"th degree file");
							short[] degreeOfUsers = new short[numberOfUsersPerMatrix];
							short[] degreeOfFilms = new short[numberOfFilms];
							computeDegrees(adjacencyUsers, degreeOfUsers, degreeOfFilms);						
							printDegrees(degreeOfFilms, dataSetCounter, outputFile);
							printDegreeDistribution(degreeOfFilms, dataSetCounter, outputFile, false);
							printDegreeDistribution(degreeOfUsers, dataSetCounter, outputFile, true);
							
							//reinitialize the adjacendy matrix
							adjacencyUsers = new boolean[numberOfUsersPerMatrix][numberOfFilms];
						}
						
						++dataSetCounter;
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
	
	private static void printDegreeDistribution(short[] degrees, int numberOfDataSet, String outputDir, boolean user){
		File dir = new File(outputDir);
		if(!dir.exists()){
			dir.mkdirs();
		}
		
		
		
		Locale loc = Locale.GERMAN;
		NumberFormat nf = NumberFormat.getNumberInstance(loc);
		DecimalFormat myFormatter = (DecimalFormat)nf;
		myFormatter.applyPattern("00");
		
		String fileURL = user? outputDir+File.separator+"userDD_"+myFormatter.format(numberOfDataSet)+".txt": outputDir+File.separator+"filmDD_"+myFormatter.format(numberOfDataSet)+".txt";
		int[] distribution = user ? new int[numberOfFilms/binsize+1]: new int[numberOfUserPerMatrix/binsize+1];
		for(int i = 0; i < degrees.length; ++i){
			distribution[degrees[i]/binsize]++;
		}
		
		
		
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(new File(fileURL)));
			bw.write("#Contains the degree distribution of the numberOfDataSet-th file with "+numberOfUserPerMatrix+" users\n");
			bw.write(user ? "#user degree distribution\n":"#film degree distribution\n");
			for(int i = 0; i < distribution.length; ++i){
				bw.write((i*binsize+binsize/2)+" "+distribution[i]+"\n");
			}
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
		
	
	private static void printDegrees(short[] degrees, int numberOfDataSet, String outputDir){
		File dir = new File(outputDir);
		if(!dir.exists()){
			dir.mkdirs();
		}
		Locale loc = Locale.GERMAN;
		NumberFormat nf = NumberFormat.getNumberInstance(loc);
		DecimalFormat myFormatter = (DecimalFormat)nf;
		myFormatter.applyPattern("00");
		
		String fileURL = outputDir+File.separator+"degree_"+myFormatter.format(numberOfDataSet)+".txt";
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(new File(fileURL)));
			for(int i = 0; i < degrees.length; ++i){
				bw.write(i+" "+degrees[i]+"\n");
			}
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	
	
}
