package utilities;

import java.io.BufferedWriter;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class Print {


	public static void printAdjacencyMatrix(boolean[][] adjMatrix){
		for(int i = 0; i < adjMatrix.length; ++i){
			for(int j = 0; j < adjMatrix[i].length; ++j){
				if(adjMatrix[i][j]){
					System.out.print("X ");
				}else{
					System.out.print("  ");
				}
			}
			System.out.println("|");
		}
	}
	
	public static void printArray(int[] array){
		for(int i = 0; i < array.length; ++i){
			System.out.print(array[i]+" ");
		}
		System.out.println();
	}

	public static void printCooccMatrix(double[][] coocc, int[] sideOfInterest,
			int[] sideDeterminingNumberOfCooccEvents, BufferedWriter bw) {
		
		try {
			bw.write("#\n");

			bw.write("#degree distribution of side of interest: ");
			for(int x = sideOfInterest.length-1; x >= 0; --x){
				bw.write(x+":"+sideOfInterest[x]+" ");
			}
			bw.write("\n");

			bw.write("#degree distribution of other side: ");
			for(int x = sideDeterminingNumberOfCooccEvents.length-1; x >= 0; --x){
				bw.write(sideDeterminingNumberOfCooccEvents[x]+" ");
			}
			bw.write("\n");

			DecimalFormat myFormatter = new DecimalFormat("#0.0000E0");


			int numberOfAllCooccEvents = 0;
			for(int i = sideDeterminingNumberOfCooccEvents.length-1; i >= 0; --i){
				int cooccEvents = sideDeterminingNumberOfCooccEvents[i] % 2 == 0 ? (sideDeterminingNumberOfCooccEvents[i]>>1)*(sideDeterminingNumberOfCooccEvents[i]-1) :sideDeterminingNumberOfCooccEvents[i]*((sideDeterminingNumberOfCooccEvents[i]-1)>>1); 
				numberOfAllCooccEvents += cooccEvents;
			}
			bw.write("\n+ ");
			double testNumberOfAllCooccEvents = 0.0;
			for(int x = sideOfInterest.length-1; x >= 0; --x){
				bw.write(sideOfInterest[x]+" ");
			}
			bw.write("\n");
			for(int x = coocc.length-1; x >= 0; --x){
				bw.write(sideOfInterest[x]+" ");
				for(int y = coocc[x].length-1; y >= 0; --y){
					testNumberOfAllCooccEvents += coocc[x][y];
					if(x != y){
						bw.write(myFormatter.format(coocc[x][y])+" ");
					}else{
						bw.write("+ ");
					}
					
				}
				bw.write("\n");
			}
			
			System.out.println("number of Coocc events "+numberOfAllCooccEvents+" and test gives "+testNumberOfAllCooccEvents);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

	public static void printAvgAdjMatrix(double[][] adj, int[] rightSide, int[] leftSide, BufferedWriter bw) {
		// TODO Auto-generated method stub

		
		
		
		try {
			bw.write("#\n");

			bw.write("#degree distribution of left side: (row:degree)");
			for(int x = leftSide.length-1; x >= 0; --x){
				bw.write((leftSide.length+1-x)+":"+leftSide[x]+" ");
			}
			bw.write("\n");

			bw.write("#degree distribution of right side: (column:degree)");
			for(int x = rightSide.length-1; x >= 0; --x){
				bw.write((rightSide.length+1-x)+":"+rightSide[x]+" ");
			}
			bw.write("\n");

			Locale loc = new Locale("en");
			NumberFormat nf = NumberFormat.getNumberInstance(loc);

			DecimalFormat myFormatter = (DecimalFormat)nf;
			myFormatter.applyPattern("#0.0000E0");

			bw.write("\n# ");
			for(int x = rightSide.length-1; x >= 0; --x){
				bw.write(rightSide[x]+" ");

			}
			
			//Old code for adjacency matrices produced by random walk!
			bw.write("\n");
			for(int x = adj.length-1; x >= 0; --x){
				bw.write(leftSide[x]+" ");
				for(int y = adj[x].length-1; y >= 0; --y){
					bw.write(myFormatter.format(adj[x][y])+" ");
				}
				bw.write("\n");
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		
	}

	public static void printAvgAdjMatrixBD(BigDecimal[][] adj, int[] rightSide, int[] leftSide, BufferedWriter bw) {
		// TODO Auto-generated method stub
		try {
			bw.write("#\n");

			bw.write("#degree distribution of left side: (row:degree)");
			for(int x = leftSide.length-1; x >= 0; --x){
				bw.write((leftSide.length+1-x)+":"+leftSide[x]+" ");
			}
			bw.write("\n");

			bw.write("#degree distribution of right side: (column:degree)");
			for(int x = rightSide.length-1; x >= 0; --x){
				bw.write((rightSide.length+1-x)+":"+rightSide[x]+" ");
			}
			bw.write("\n");

			Locale loc = new Locale("en");
			NumberFormat nf = NumberFormat.getNumberInstance(loc);

			DecimalFormat myFormatter = (DecimalFormat)nf;
			myFormatter.applyPattern("0.00000E0");

			bw.write("\n# ");
			for(int x = rightSide.length-1; x >= 0; --x){
				bw.write(rightSide[x]+" ");

			}
			
			//Old code for adjacency matrices produced by random walk!
			bw.write("\n");
			for(int x = adj.length-1; x >= 0; --x){
				bw.write(leftSide[x]+" ");
				for(int y = adj[x].length-1; y >= 0; --y){
					bw.write(myFormatter.format(adj[x][y])+" ");
//					bw.write(adj[x][y].toString()+" ");
				}
				bw.write("\n");
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		
	}
	


	/**
	 * Takes in a double valued adjacency matrix, the degree distribution on the columns (second index) and the one on the rows (first index).
	 * Writes it to the provided bw.
	 * @param adj
	 * @param rightSide
	 * @param leftSide
	 * @param bw
	 */
	
	public static void printAvgAdjMatrix_simple(double[][] adj, int[] rightSide, int[] leftSide, BufferedWriter bw) {
		// TODO Auto-generated method stub

		
		
		
		try {
			bw.write("#\n");
			//the first column will be held by the degree of the respective row. 
			bw.write("#degree distribution of left side: (row:degree)");
			for(int x = 0; x < leftSide.length; ++x){
				bw.write((x+1)+":"+leftSide[x]+" ");
			}
			bw.write("\n");

			bw.write("#degree distribution of right side: (column:degree)");
			for(int x = 0; x < rightSide.length; ++x){
				bw.write((x+2)+":"+rightSide[x]+" ");
			}
			bw.write("\n");

			Locale loc = new Locale("en");
			NumberFormat nf = NumberFormat.getNumberInstance(loc);

			DecimalFormat myFormatter = (DecimalFormat)nf;
			myFormatter.applyPattern("#0.0000E0");

			bw.write("\n# ");
			for(int x = 0; x < rightSide.length; ++x){
				bw.write(rightSide[x]+" ");

			}
			
			bw.write("\n");
			for(int x = 0; x < adj.length; ++x){
				bw.write(leftSide[x]+" ");
				for(int y = 0; y < adj[x].length; ++y){
					bw.write(myFormatter.format(adj[x][y])+" ");
				}
				bw.write("\n");
			}
			

			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		
	}

	
}
