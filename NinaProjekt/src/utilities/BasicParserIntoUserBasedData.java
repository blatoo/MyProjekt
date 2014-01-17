package utilities;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.StringTokenizer;


/**
 * This class reads in the data file written by Sebastian Burg (data3user.txt)
 * into user based data files. For every 10,000 users we make one data file that contains
 * all the films seen by the one user (without the evaluation). 
 * 
 * 
 * @author Nina
 *
 */
public class BasicParserIntoUserBasedData {
	
	public static int numberOfUsersPerRound = 10000;
	public static boolean onlyGoodRating = true;
	public static int minimalGoodRating = 4;

	public BasicParserIntoUserBasedData() {
	
	}
	
	/**
	 * This class takes in the data3user.txt file and compiles digestible files from it which 
	 * contain numberOfUsersPerRound users, each
	 * 
	 * 
	 * 
	 * @param args
	 */
	
	public static void main(String[] args){
		File file = new File("/home/rootm/Netflix/data3user.txt");
		try{
			BufferedWriter bw;
			
			int filmID, userID, value;
			
			StringTokenizer st;
			
			//I expect that users in this textfile are consecutively listed (I have no idea whether this is true, but the test below indicates it.)
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line = br.readLine();
			
			//round counter
			int j = 0;
			int counter1 = 0;
			int counter2 = 1;
			int numberOfDifferentUsers = 0;
			
			
			//we know the highest userID is below 2,650,000
			boolean[] seenThisUserID = new boolean[2650000];
			
			while(line != null){
				//in each round we include all users into one textfile with IDs between j*numberOfUsersPerRound and smaller
				//than (j+1)*numberOfUsersPerRound
				int lowerBound = j*numberOfUsersPerRound;
				int higherBound = (j+1)*numberOfUsersPerRound;
				
				//this is a bipartite adjacency matrix of the data,
				//only for the numberOfUsersPerRound (otherwise it wouldn't fit within 1 GB)
				boolean[][] userFilm = new boolean[numberOfUsersPerRound][18000];

//				long time1 = System.currentTimeMillis();
				while(line != null){
					//just to show some progress
					if(++counter1 % 1000000 == 0){
						counter1 = 0;
						System.out.println(counter2+ " million lines have been read");
						counter2++;
						if(counter2 % 1000000 == 0){
							System.out.println("A billion lines have been read");
							counter2 = 0;
						}
					}
					
					//parse the line to find out which user and which film
					st = new StringTokenizer(line, ",");
					//System.out.println(i +"-th line is: "+line);
					filmID = Integer.parseInt(st.nextToken());
					userID = Integer.parseInt(st.nextToken());
					if(!seenThisUserID[userID]){
						seenThisUserID[userID] = true;
						++numberOfDifferentUsers;
					}
					//parse evaluation
					value = Integer.parseInt(st.nextToken());
					//System.out.println("film is "+filmID+", user is "+userID+", value "+value);

					
					if(lowerBound <= userID && userID < higherBound)
						if(onlyGoodRating && value >= minimalGoodRating)
						userFilm[userID%numberOfUsersPerRound][filmID] = true;
					if(userID < lowerBound){
						System.out.println("!!!!!Assumption is wrong!!!!!");
					}
					//Once the first higher userID is encountered, jump out of the loop  
					if(userID >= higherBound){
						System.out.println("userID is "+userID);
						break;
					}

					line = br.readLine();

				}
				
				//prepare for output
				String parFileName = "/home/rootm/Netflix/UserBasedData/OnlyGoodRating";
				File parfile = new File(parFileName);
				if(!parfile.exists()){
					parfile.mkdirs();
				}
				
				
				//formatting
				Locale loc = new Locale("en");
				NumberFormat nf = NumberFormat.getNumberInstance(loc);
				DecimalFormat myFormatter = (DecimalFormat)nf;
				myFormatter.applyPattern("0000000");

				
				String filename = onlyGoodRating ? parFileName+File.separator+nf.format(j*numberOfUsersPerRound)+"_minRating_"+minimalGoodRating+".txt":  parFileName+File.separator+nf.format(j*numberOfUsersPerRound)+".txt";
				File toWrite = new File(filename);
				bw = new BufferedWriter(new FileWriter(toWrite));

				//for each user in this turn
				for(int i = 0; i < numberOfUsersPerRound; ++i){
					String theLine = null;
					//write down all the films she has rented 
					for(int z = 0; z < 18000; ++z){
						if(userFilm[i][z]){
							if(theLine == null)
								theLine = z + " ";
							else
								theLine= theLine + z + " ";
						}
					}
					if(theLine != null){
						bw.write((j*numberOfUsersPerRound+i)+":\n");
						bw.write(theLine + "\n");
					}
				}
				bw.close();

				j++;
			}
			br.close();
			
		
		}catch(Exception e){
			System.err.println("Something went wrong");
			e.printStackTrace();
			System.exit(-1);
		}
		
		
		
	}

}

