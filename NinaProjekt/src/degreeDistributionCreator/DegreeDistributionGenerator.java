package degreeDistributionCreator;

import java.util.Random;

import utilities.Print;

public class DegreeDistributionGenerator {
	public static Random random = new Random(313);
	public static boolean debug = false;
	
	public DegreeDistributionGenerator(int randomSeed){
		random = new Random(randomSeed);
	}

	/**
	 * Returns an array of ints that contains 'numberOfVertices' times the degree.
	 * 
	 *  Example: uniform(3, 5) returns '3 3 3 3 3'
	 * 
	 * @param degree
	 * @param numberOfVertices
	 * @return
	 */
	public static int[] uniform(int degree, int numberOfVertices){
		int[] result = new int[numberOfVertices];
		for(int i = numberOfVertices-1; i >= 0; --i){
			result[i] = degree;
		}
		return result;
	}
	
	/**
	 * Performs the following random walk on the given degree distribution:
	 * In every step, choose two degrees at random and increase the higher and decrease the smaller by one.
	 * The algorithm guarantees that no degree drops to zero (if all but one degrees are zero, the algorithm will
	 * run forever).
	 * Returns the new degree distribution (also sorted non-increasingly if they were sorted non-increasingly).
	 * 
	 * @param oldDegreeDistribution (sorted non-increasingly)
	 * @param numberOfRandomWalkSteps (sorted  non-increasingl)
	 * @return
	 */
	public static void randomWalk(int[] oldDegreeDistribution, int numberOfRandomWalkSteps){
		int index1, index2, temp;
		
		for(int i = numberOfRandomWalkSteps; i > 0; --i){
			index1 = random.nextInt(oldDegreeDistribution.length);
			index2 = random.nextInt(oldDegreeDistribution.length);
			while(index1 == index2 || oldDegreeDistribution[index2] <= 1){
				index1 = random.nextInt(oldDegreeDistribution.length);
				index2 = random.nextInt(oldDegreeDistribution.length);
			}
			//the degree at index1 should be the larger degree. If it is not, we swap the indices
			if(oldDegreeDistribution[index1] < oldDegreeDistribution[index2]){
				temp = index2;
				index2 = index1;
				index1 = temp;
			}
			int index = index1-1;
			//if the next entry to the left (smaller index) has a higher value,
			//we can immediately increase the value at index1
			if(index1 > 0 || oldDegreeDistribution[index1] < oldDegreeDistribution[index]){
				++oldDegreeDistribution[index1];
			}else{
				//otherwise we will search for the left most index in the array with the same
				//value and increase that one
				while(index >= 0 && oldDegreeDistribution[index--] == oldDegreeDistribution[index1]);
				++oldDegreeDistribution[index+1];
			}

			index = index2+1;
			//analogously
			if(index2 == oldDegreeDistribution.length-1 || oldDegreeDistribution[index2] < oldDegreeDistribution[index]){
				--oldDegreeDistribution[index2];
			}else{
				while(index < oldDegreeDistribution.length && oldDegreeDistribution[index++] == oldDegreeDistribution[index2]);
				++oldDegreeDistribution[index-1];
			}
		}
	}

	
	/**
	 * Performs the following random walk on the given degree distribution:
	 * In every step, choose two degrees at random and increase the higher and decrease the smaller by one.
	 * If the higher degree cannot be increased without violating the maximum, nothing is done and the degree
	 * distribution stays the same.
	 * 
	 * Returns the number of changes made.
	 * 
	 * if the degree distribution is given in non-increasing order, this method will maintain the order.
	 * @param oldDegreeDistribution
	 * @param numberOfRandomWalkSteps
	 * @return
	 */
	public static int randomWalkWithMaximum(int[] oldDegreeDistribution, int numberOfRandomWalkSteps, int maximum){
		int index1, index2, temp, numberOfChanges = 0;
		
		for(int i = numberOfRandomWalkSteps; i > 0; --i){
			index1 = random.nextInt(oldDegreeDistribution.length);
			index2 = random.nextInt(oldDegreeDistribution.length);
			while(index1 == index2){
				index1 = random.nextInt(oldDegreeDistribution.length);
				index2 = random.nextInt(oldDegreeDistribution.length);
			}
			//the degree at index1 is supposed to be the larger degree. If it is not, we swap the indices
			if(oldDegreeDistribution[index1] < oldDegreeDistribution[index2]){
				temp = index2;
				index2 = index1;
				index1 = temp;
			}
			if(oldDegreeDistribution[index1] < maximum && oldDegreeDistribution[index2] > 1){
				int index = index1-1;
				//if the next entry to the left (smaller index) has a higher value,
				//we can immediately increase the value at index1
				if(index1 == 0 || oldDegreeDistribution[index1] < oldDegreeDistribution[index]){
					++oldDegreeDistribution[index1];
				}else{
					//otherwise we will search for the left most index in the array with the same
					//value and increase that one. This method maintains the non-increasing sorting of the degrees
					//within the array.
					while(index >= 0 && oldDegreeDistribution[index] == oldDegreeDistribution[index1])
						--index;
					++oldDegreeDistribution[index+1];
				}

				if(debug){
					System.out.println("After the "+(numberOfChanges+1)+"th change: Increase value at "+index1);
					Print.printArray(oldDegreeDistribution);
				}
				
				index = index2+1;
				//analogously
				if(index2 == oldDegreeDistribution.length-1 || oldDegreeDistribution[index2] > oldDegreeDistribution[index]){
					--oldDegreeDistribution[index2];
				}else{
					while(index < oldDegreeDistribution.length && oldDegreeDistribution[index] == oldDegreeDistribution[index2])
						++index;
					--oldDegreeDistribution[index-1];
				}

				if(debug){
					System.out.println("Decrease value at "+index2);
					Print.printArray(oldDegreeDistribution);
				}

				
				++numberOfChanges;
			}
		}
		return numberOfChanges;
	}

}
