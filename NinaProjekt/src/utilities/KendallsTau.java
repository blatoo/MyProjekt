package utilities;

import java.util.Collections;
import java.util.LinkedList;
import java.util.ListIterator;

public class KendallsTau {
	
	public static void main(String[] args){
		int[] ranking = {3, 0, 7, 4, 9, 1, 6, 5, 8, 2};
		
		LinkedList<Integer> numbers = new LinkedList();
		for(int i = 0; i < 10000; ++i){
			numbers.add(i);
		}
		for(int i = 0; i < 1000; ++i){
			if(i % 100 == 0)
				System.out.println();
			Collections.shuffle(numbers);
			ranking = new int[numbers.size()];
			int counter = 0;
			for(Integer j:numbers){
				ranking[counter++] = j;
			}
			
			double firstTau = computeKendallsTau(ranking, false);
			double secondTau = computeKendallsTau_naive(ranking, false); 
			if( firstTau != secondTau){
				System.out.println("Mistake!! "+firstTau+" vs "+secondTau);
				for(Integer j: ranking){
					System.out.print(j+" ");
				}
				System.out.println();
				computeKendallsTau(ranking, true);
				System.out.println();
				computeKendallsTau_naive(ranking, true);
				
				System.exit(-1);
			}else{
				System.out.print(i+" ");
			}

		}
	}
	
	
	public static double computeKendallsTau(int[] ranking, boolean show){
		double tau = 0;
		double numberOfDiscordantPairs = 0;
		
		LinkedList<Integer> Li = new LinkedList<Integer>(); //contains all values ranking[i] with ranking[i] > i (valid after completion of the i-loop)
		LinkedList<Integer> Ni = new LinkedList<Integer>(); //contains all values j < i that did not occur at index i (valid after completion of the i-loop)
		boolean[] hasBeenSeenBefore = new boolean[ranking.length];
		
		for(int i = 0; i < ranking.length; ++i){
			int current = ranking[i];
			if(show) System.out.println("---");
			if(current > i){
				//if i itself is missing
				if(!hasBeenSeenBefore[i]){
					Ni.add(i);
					//for all values that are still missing, we will have one discordant pair with the current value
					numberOfDiscordantPairs += Ni.size();
					if(show)
						for(int value: Ni)
							System.out.println(current+" "+value);
					//furthermore, since i itself  is missing, all Li-values will make a discordant pair with it
					numberOfDiscordantPairs += Li.size();
					if(show)
						for(int value: Li)System.out.println(value+" "+i);
					
				}else{
					numberOfDiscordantPairs += Ni.size();
					if(show)
						for(int value: Ni)
							System.out.println(current+" "+value);
				}
				Li.add(current);
				hasBeenSeenBefore[current] = true;
			}else{
				if(current == i){						
					//for all values that are still missing, we will have one discordant pair with the current value
					numberOfDiscordantPairs += Ni.size();
					if(show)
						for(int value: Ni)
							System.out.println(current+" "+value);
					numberOfDiscordantPairs += Li.size();
					if(show)
						for(int value: Li)System.out.println(value+" "+i);			
					
					
				}else{//current < i
					for(ListIterator<Integer> it = Ni.listIterator(); it.hasNext();){
						int value = it.next();
						if(value == current){
							it.remove();
							break;
						}
						++numberOfDiscordantPairs;
						if(show) System.out.println(current+" "+value);
					}


					if(!hasBeenSeenBefore[i]){
						Ni.add(i);
						numberOfDiscordantPairs += Li.size();
						if(show)
							for(int value: Li)System.out.println(value+" "+i);
					}
					
				}
			}
			if(hasBeenSeenBefore[i]){//i is in Li
				for(ListIterator<Integer> it = Li.listIterator(); it.hasNext();){
					int value = it.next();
					if(value == i){
						it.remove();
						break;
					}
					++numberOfDiscordantPairs;
					if(show) System.out.println(value+" "+i);
				}
				
			}
			
		}
		
//		tau = 1.0 - (2*numberOfDiscordantPairs)/(ranking.length*(ranking.length-1));
		
		return numberOfDiscordantPairs;
	}

	
	public static double computeKendallsTau_naive(int[] ranking, boolean show){
		double tau = 0;
		double numberOfDiscordantPairs = 0;
		
		for(int i = 0; i < ranking.length; ++i){
			for(int j = i+1; j < ranking.length; ++j){
				if(ranking[i] > ranking[j]){
					++numberOfDiscordantPairs;
					if(show)
						System.out.println(ranking[i]+" "+ranking[j]);
				}
			}
		}
		
		
		tau = 1.0 - (2*numberOfDiscordantPairs)/(ranking.length*(ranking.length-1));
		
		return numberOfDiscordantPairs;
	}
	
}
