package experiments;

import java.io.*;
import java.util.StringTokenizer;

import utilities.MyFilenameFilter;

public class LeverageSimilarityForGergely {
	static int numberOfFilms = 17770;

	
	public static void main(String[] args){
		String parDirURL = "/home/nina/Data/Netflix/NewCoocc/20000";
		
		File parDir = new File(parDirURL);
		if(!parDir.exists()){
			System.err.println("The parent dir does not exist!");
		}
		
		String[] files = parDir.list(new MyFilenameFilter("original", ".txt"));
		String simFileURL;
		File orig, sim;
		for(String originalFileURL : files){
			simFileURL = originalFileURL.replaceAll("original", "sim");
			
			System.out.println("originalFileURL is "+originalFileURL+", simFileURL is "+simFileURL);
			orig = new File(parDirURL+File.separator+originalFileURL);
			sim = new File(parDirURL+File.separator+simFileURL);
			
			double[][] simCooc = new double[numberOfFilms][];
			for(int i = 0; i < simCooc.length; ++i){
				simCooc[i] = new double[i];
			}
			int x,y;
			double value;
			BufferedReader br;
			String line;
			StringTokenizer st;
			
			System.out.println("Trying to read in sim-Matrix");
			int lineCounter = 0;
			try {
				br = new BufferedReader (new FileReader(sim));
				line = br.readLine();
				
				
				while(line != null){
					
					if(++lineCounter % 10000000 == 0)
						System.out.println("reading in the "+lineCounter+"th line");
					
					st = new StringTokenizer(line);
					x = Integer.parseInt(st.nextToken());
					y = Integer.parseInt(st.nextToken());
					value = Double.parseDouble(st.nextToken());
					
					simCooc[x][y] = value;
					
					
					
					line = br.readLine();
				}
				br.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			System.out.println("Done reading the sim-Matrix, reading in origCoocc");
		
			lineCounter = 0;
			try {
				br = new BufferedReader(new FileReader(orig));
				String outputURL = simFileURL.replaceAll("sim", "newLeverageInt");
				
				BufferedWriter bw = new BufferedWriter(new FileWriter(parDirURL+File.separator+outputURL));
				bw.write("#This file contains the modified leverage for the films, regarding the original file described in \n");
				bw.write("#"+originalFileURL+"\n");
				bw.write("#it is given as a simple edge list, where the numbers denote the Netflix filmIDs -1 (i.e, 0 is 1, 1 is 2, etc.)\n");
				bw.write("#\n");
				
				line = br.readLine();
				double modifiedLeverage;
				while(line != null){
					if(++lineCounter % 10000000 == 0)
						System.out.println("reading in the "+lineCounter+"th line");
					
					st = new StringTokenizer(line);
					x = Integer.parseInt(st.nextToken());
					y = Integer.parseInt(st.nextToken());
					value = Double.parseDouble(st.nextToken());
					modifiedLeverage = (value-simCooc[x][y]);
					
					if(modifiedLeverage > 1)
						bw.write(x+" "+y+" "+(int)modifiedLeverage+"\n");
					
					line = br.readLine();
				}
				br.close();
				bw.close();
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
			
			
		}
	}
}
