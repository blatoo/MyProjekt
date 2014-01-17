package netflixSampleAnalyzer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Locale;
import java.util.StringTokenizer;

import edges.EdgeWithIDAndWeight;

public class Analyzer_Clustering {

	public static int numberOfFilms = 17770;
	public static int numberOfSamples = 5000;
	private static Locale loc = Locale.GERMAN;
	private static NumberFormat nf = NumberFormat.getNumberInstance(loc);
	private static DecimalFormat myFormatter = (DecimalFormat)nf;
	
	
	
	public static void main(String[] args){
		myFormatter.applyPattern("0.00000E0");
		new Analyzer_Clustering().doAnalysis();
	}
	
	
	/**
	 * Note that this class will only work if maxDegree and maxCoocc are below 32767 and can thus be represented as a short
	 */
	public void doAnalysis(){
		int numberOfBaskets = 10000;
		int numberOfBestRankings_global = 1000;
		int numberOfBestRankings_local = 100;
		String parDirFile = "/home/nina/Data/Netflix/NewCoocc";
		String outDirURL_global = "/home/nina/Data/Netflix/ForClusteringResults_Globally";
		String outDirURL_local = "/home/nina/Data/Netflix/ForClusteringResults_Locally";
		String[] files = {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36"};

		for(String file: files){
			//degrees[A]/numberOfBaskets = pA
			short[] degrees = readInDegrees(parDirFile+File.separator+"degree_"+file+".txt");
			String[] titles = readInMovieTitles("/home/nina/Data/Netflix/movie_titles.txt");
						
			String originalFileURL = parDirFile+File.separator+"original_"+file+".txt";
			String simFileURL = parDirFile+File.separator+"sim_"+file+".txt";
			
			String line;
			StringTokenizer st;
			int firstFilm, secondFilm, currentCooccs = 0;
			double[] simCooccs = new double[numberOfFilms];
			double value;
						
			LinkedList<EdgeWithIDAndWeight<Integer>> best_newLeverage = new LinkedList<EdgeWithIDAndWeight<Integer>>();
			LinkedList<EdgeWithIDAndWeight<Integer>>[] best_local_newLeverage = new LinkedList[numberOfFilms];
			for(int i = 0; i < numberOfFilms; ++i) best_local_newLeverage[i] = new LinkedList<EdgeWithIDAndWeight<Integer>>();
			//for a test, we will check whether all partners of the film 15623 are listed
			int filmOfInterest = 15623;
			boolean[] testWhetherAllPartnersAreIn = new boolean[numberOfFilms];
			
			EdgeWithIDAndWeight<Integer> newEdge;
			try {
				BufferedReader br = new BufferedReader(new FileReader(new File(originalFileURL)));
				BufferedReader brSim = new BufferedReader(new FileReader(new File(simFileURL)));
				line = br.readLine();
				String lineSim = brSim.readLine();
				int counter = 0; 
				int id = 0;
				
				while(line != null ){
					++counter;
					st = new StringTokenizer(line);
					firstFilm = Integer.parseInt(st.nextToken());
					if(firstFilm > currentCooccs){
						lineSim = readInCoocsLine(brSim, firstFilm, lineSim, simCooccs);
						if(simCooccs == null)System.out.println("sim cooccs is null");
						currentCooccs = firstFilm;
					}
					
					secondFilm = Integer.parseInt(st.nextToken());
					value = Double.parseDouble(st.nextToken()); 
					
//					System.out.println(titles[firstFilm]+" (degree = "+degrees[firstFilm]+") has been corented with "+titles[secondFilm]+" (degree = "+degrees[secondFilm]+ ") "+value+" times (sim: "+simCooccs[secondFilm]+")");
					
					double pAB = value/(double)numberOfBaskets;
					double pAB_sim = simCooccs[secondFilm]/(double)numberOfBaskets;
					//leverage(A,B) = p(A,B)-(p(A)*p(B))
					double newLeverage = (pAB-pAB_sim)*numberOfBaskets;
			
					newEdge = new EdgeWithIDAndWeight<Integer>(firstFilm, secondFilm, id, newLeverage);
					if(newLeverage > 0){
						addIntoAscendingList(best_newLeverage, newEdge , numberOfBestRankings_global);
						addIntoAscendingList(best_local_newLeverage[firstFilm], newEdge, numberOfBestRankings_local);
						addIntoAscendingList(best_local_newLeverage[secondFilm], newEdge, numberOfBestRankings_local);
					}
					
					line = br.readLine();
				}
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			try {
				String fileURL = outDirURL_global+File.separator+"rankings_"+file+".txt";
				BufferedWriter bw = new BufferedWriter(new FileWriter(new File(fileURL)));
				bw.write("#Contains the "+numberOfBestRankings_global+" rankings that are globally the best \n");
				bw.write("#\n");
				bw.write("#First column: film1 (filmID)\n");
				bw.write("#Second column: film1.degree \n");
				bw.write("#Third column: film2 (filmID)\n");
				bw.write("#Fourth column: film2.degree\n");
				bw.write("#fifth column: leverage \n");
				
				printOutBest_Globally(bw, best_newLeverage, degrees, titles);
				
				bw.close();
				
				fileURL = outDirURL_local+File.separator+"rankings_"+file+".txt";
				bw = new BufferedWriter(new FileWriter(new File(fileURL)));
				bw.write("#Contains the "+numberOfBestRankings_local+" rankings that are locally the best \n");
				bw.write("#Each row is preceeded by the films ID, followed by a list of at most "+numberOfBestRankings_local+" other filmIDs\n");
				bw.write("#Following each neighbor's filmID is the leverage after a colon (e.g.: 3:4.23)\n");
				
				printOutBest_Locally(bw, best_local_newLeverage, degrees, titles);
				
				bw.close();
				
				
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			//end of scanning all files
		}
	}


	private void printOutBest_Locally(BufferedWriter bw, LinkedList<EdgeWithIDAndWeight<Integer>>[] bestNewLeverage, short[] degrees, String[] titles) {
		// TODO Auto-generated method stub
		EdgeWithIDAndWeight<Integer> edge;
		try {
			for(int i = 0; i < bestNewLeverage.length; ++i){
				LinkedList<EdgeWithIDAndWeight<Integer>> list = bestNewLeverage[i];
				Collections.reverse(list);
				bw.write((i+1)+" ");
				for(ListIterator<EdgeWithIDAndWeight<Integer>> li = list.listIterator(); li.hasNext();){
					edge = li.next();
					if(edge.node1 == i)
						bw.write((edge.node2+1)+":"+nf.format(edge.value)+" ");
					else
						bw.write((edge.node1+1)+":"+nf.format(edge.value)+" ");
				}
				bw.write("\n");
			}
			
			bw.write("\n");
			bw.write("\n");
			bw.write("\n");
			
			for(int i = 0; i < bestNewLeverage.length; ++i){
				LinkedList<EdgeWithIDAndWeight<Integer>> list = bestNewLeverage[i];
				String st = titles[i];
				bw.write(st +":\n");
				for(ListIterator<EdgeWithIDAndWeight<Integer>> li = list.listIterator(); li.hasNext();){
					edge = li.next();
					
					if(edge.node1 == i){
						st = titles[edge.node2];
					}else{
						st = titles[edge.node1];

					}
					
					bw.write("   "+st+"\n");

				}
//				bw.write("\n");
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	private void printOutBest_Globally(BufferedWriter bw, LinkedList<EdgeWithIDAndWeight<Integer>> best_newMeasure,  short[] degrees, String[] titles) {
		Collections.reverse(best_newMeasure);
		EdgeWithIDAndWeight<Integer> edge2;

		try{
			int edgeCounter = 1;
			for(ListIterator<EdgeWithIDAndWeight<Integer>> liNew = best_newMeasure.listIterator(); liNew.hasNext();){
				edge2 = liNew.hasNext() ? liNew.next() : null;
				bw.write(edgeCounter+++"\t"+(edge2.node1+1)+"\t"+degrees[edge2.node1]+"\t"+(edge2.node2+1)+"\t"+degrees[edge2.node2]+"\t"+nf.format(edge2.value)+"\t"
						                +titles[edge2.node1]+ "\t"+titles[edge2.node2]+"\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}		
	}


	private void addIntoAscendingList(LinkedList<EdgeWithIDAndWeight<Integer>> sortedList, EdgeWithIDAndWeight<Integer> edge, int lengthOfList) {
		if(sortedList.size() == 0){
			sortedList.add(edge);
			return;
		}
		double currentDouble;
		boolean hasBeenAdded = false;
		ListIterator<EdgeWithIDAndWeight<Integer>> it = sortedList.listIterator(); 
		while(it.hasNext()){
			currentDouble = it.next().value;
			if(currentDouble > edge.value){
				it.previous();
				it.add(edge);
				hasBeenAdded = true;
				break;
			}
		}
		
		if(!hasBeenAdded)
			it.add(edge);
		
		if(sortedList.size() > lengthOfList) sortedList.removeFirst();
		
//		System.out.println("list is now: ");
//		for(EdgeWithIDAndWeight<Integer> edgy: sortedList){
//			System.out.print(edgy.value+" ");
//		}
//		System.out.println();
//		
	}


	private String readInCoocsLine(BufferedReader br, int wantedFirstFilm, String line, double[] simCoocs) {
		if(line == null)
			return null;
		for(int i = 0; i < simCoocs.length; ++i) simCoocs[i] = 0;

		StringTokenizer st = new StringTokenizer(line);
		int firstFilm = Integer.parseInt(st.nextToken());
		
		try {
			//find correct lines in the file
			while(line != null && firstFilm < wantedFirstFilm){
				line = br.readLine();
				st = new StringTokenizer(line);
				firstFilm = Integer.parseInt(st.nextToken());
			}
			
			int secondFilm;
			double value;
			while(line != null && firstFilm == wantedFirstFilm){
				secondFilm = Integer.parseInt(st.nextToken());
				value = Double.parseDouble(st.nextToken());
				simCoocs[secondFilm] = value;
				
				line = br.readLine();
				if(line != null){
					st = new StringTokenizer(line);
					firstFilm = Integer.parseInt(st.nextToken());
				}
			}
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(-1);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(-1);
		}
		
		

		return line;
	}


	private ArrayList<EdgeWithIDAndWeight> computeBestPairsRegardingLeverage(
			int i) {
		// TODO Auto-generated method stub
		return null;
	}


	private ArrayList<EdgeWithIDAndWeight> computeBestPairsRegardingLift(int i) {
		// TODO Auto-generated method stub
		return null;
	}


	private String[] readInMovieTitles(String fileURL) {
		String[] movieTitles = new String[numberOfFilms];
		try {
			BufferedReader br = new BufferedReader(new FileReader(new File(fileURL)));
			String line = br.readLine();
			StringTokenizer st;
			int stringCounter = 0;
			int index;
			while(line != null){
				index = line.indexOf(',');
				index = line.indexOf(',', index+1);
				
				movieTitles[stringCounter++] = line.substring(index+1);
				
				line = br.readLine();
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
		
		
		return movieTitles;
	}



	private short[] readInDegrees(String fileURL) {
		short[] filmDegrees = new short[numberOfFilms];
		try {
			BufferedReader br = new BufferedReader(new FileReader(new File(fileURL)));
			String line = br.readLine();
			StringTokenizer st;
			int film, degree;
			while(line != null){
				st = new StringTokenizer(line);
				filmDegrees[Integer.parseInt(st.nextToken())] = Short.parseShort(st.nextToken());
				
				line = br.readLine();
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
		
		return filmDegrees;
	}
	

}
