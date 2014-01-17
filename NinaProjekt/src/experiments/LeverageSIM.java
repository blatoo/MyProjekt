package experiments;

import java.io.*;
import java.util.StringTokenizer;

import utilities.MyFilenameFilter;


public class LeverageSIM {
    static int numberOfFilms = 17770;
    static int numberOfUsers = 10000;
    
    public static void main(String[] args){
        String parDirURL = "D:\\Daten\\Netflix"; 
        	//"/home/nina/Data/Netflix/NewCoocc/20000";
       
        File parDir = new File(parDirURL);
        if(!parDir.exists()){
            System.err.println("The parent dir does not exist!");
        }
       
        String[] files = parDir.list(new MyFilenameFilter("original", ".txt"));
        String degreeFileURL;
        File orig, degree;
        for(String originalFileURL : files){
            degreeFileURL = originalFileURL.replaceAll("original", "degree");
           
            System.out.println("originalFileURL is "+originalFileURL+", degreeFileURL is "+degreeFileURL);
            orig = new File(parDirURL+File.separator+originalFileURL);
            degree = new File(parDirURL+File.separator+degreeFileURL);
           
            int[] degrees = new int[numberOfFilms];
            
            double[][] simCooc = new double[numberOfFilms][];
            for(int i = 0; i < simCooc.length; ++i){
                simCooc[i] = new double[i];
            }
            
            int filmID, deg;
            BufferedReader br;
            String line;
            StringTokenizer st;
           
            System.out.println("Trying to read in degrees");
            int lineCounter = 0;
            try {
                br = new BufferedReader (new FileReader(degree));
                line = br.readLine();
               
                while(line != null){
                   
                    if(++lineCounter % 10000000 == 0)
                        System.out.println("reading in the "+lineCounter+"th line");
                   
                    st = new StringTokenizer(line);
                    filmID = Integer.parseInt(st.nextToken());
                    deg = Integer.parseInt(st.nextToken());
                   
                    degrees[filmID] = deg;
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
           
            System.out.println("Done reading the degrees, constructing SIM-cooc");
            
            for (int i = 0; i < numberOfFilms; i++)
            	for (int j = 0; j < i; j++) {
            		simCooc[i][j] = degrees[i] * degrees[j] / numberOfUsers;
            	}
            System.out.println("Done constructing the SIM-cooc-Matrix, reading original, calculating the leverage and constructing LeverageSIM");
            
            lineCounter = 0;
            int x,y;
            double value;
            try {
            	br = new BufferedReader(new FileReader(orig));
                String outputURL = originalFileURL.replaceAll("original", "leverageSIM");
               
                BufferedWriter bw = new BufferedWriter(new FileWriter(parDirURL+File.separator+outputURL));
                bw.write("#This file contains the SIM leverage for the films, regarding the original file described in \n");
                bw.write("#"+originalFileURL+"\n");
                bw.write("#it is given as a simple edge list, where the numbers denote the Netflix filmIDs\n");
                bw.write("#\n");
               
                line = br.readLine();
                double SIMLeverage;
                while(line != null){
                    if(++lineCounter % 10000000 == 0)
                        System.out.println("reading in the "+lineCounter+"th line");
                   
                    st = new StringTokenizer(line);
                    x = Integer.parseInt(st.nextToken());
                    y = Integer.parseInt(st.nextToken());
                    value = Double.parseDouble(st.nextToken());
                    SIMLeverage = (value-simCooc[x][y]);
                   
                    //if(SIMLeverage > 1)
                        bw.write(x+" "+y+" "+(int)SIMLeverage+"\n");
                   
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