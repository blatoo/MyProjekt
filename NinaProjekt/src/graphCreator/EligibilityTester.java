package graphCreator;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

public class EligibilityTester {

	public static boolean debug = false;
	
	/**
	 * Function tests whether the two degree distributions give rise to at least one bipartite graph.
	 * Test as described by Gale and Ryser.
	 * 
	 * @param leftSideDegreeDistribution (sorted non-increasingly (this condition is not tested but will lead to serious mistakes if not obeyed))
	 * @param rightSideDegreeDistribution (sorted non-increasingly (this condition is not tested but will lead to serious mistakes if not obeyed))
	 * @returns true if eligible, else false;
	 */
	public static boolean testEligibility(Integer[] leftSideDegreeDistribution, Integer[] rightSideDegreeDistribution){
		//make sure arrays are sorted non-increasingly:
		Arrays.sort(leftSideDegreeDistribution, Collections.reverseOrder());
		Arrays.sort(rightSideDegreeDistribution, Collections.reverseOrder());
		
		
		//The test is done in two phases:
		//1. Compute the conjugate of one of the degree distributions;
		//2. Test whether the other degree distribution is majorized by the conjugate
		
		//conjugateLeftSide contains at index i the number of entries in the leftSideDegreeDistribution
		//that are greater than or equal to i.
		int[] conjugateLeftSide = computeConjugate(leftSideDegreeDistribution, rightSideDegreeDistribution.length);
		
		//now, the conjugate vector is finished and we will test for majorization.
		//A vector of numbers V_1 majorizes another vector of numbers V_2 iff
		//the sum of the first k numbers of V_1 is always at least as high as the 
		//sum of the first k numbers of V_2 for all k and with equality for k = V_2.length
		//(The latter constraint implies that V_1 cannot be longer than V_2 (where 'length' is defined as the
		//highest index with a non-zero-element.).
		
		int tempSumConjugate=0, tempSumRightSide=0;
		boolean isMajorized = true;
		//Remember that conjugateLeftSide has the same length as rightSideDegreeDistribution
		for(int i = 0; i < conjugateLeftSide.length; ++i){
			tempSumConjugate += conjugateLeftSide[i];
			tempSumRightSide += rightSideDegreeDistribution[i];
			if(tempSumRightSide > tempSumConjugate){
				if(debug)
					System.out.println("the right side is not majorized by the conjugate vector");
				isMajorized = false;
				break;
			}
		}
		if(tempSumConjugate != tempSumRightSide){
			if(debug)
				System.out.println("The sums do not match - not eligible");
			isMajorized = false;
		}
		
		return isMajorized;
	}

	/**
	 * Computes the conjugate vector of a given integer array, i.e., at index
	 * i-1 we find the number of elements at least as large as i. Depending on the
	 * number of vertices on the right hand side (i.e., the maximal element in the vector)
	 * the
	 *  
	 * Example: ({6 6 4 3 3 2 1 1 1}, 8)
	 * gives: 9 6 5 3 2 2 0 0 
	 * (3 elements of at least 1,
	 *  4 elements of at least 2,
	 *  6 elements of at least 3,
	 *  7 elements of at least 4,
	 *  7 elements of at least ,
	 *  9 elements of at least 1.)
	 * 
	 * @param leftSideDegreeDistribution
	 * @param rightSideDegreeDistribution
	 * @return
	 */
	public static int[] computeConjugate(Integer[] leftSideDegreeDistribution, int maximalElement) {
		int[] conjugateLeftSide = new int[maximalElement];
		
		int currentDegree = leftSideDegreeDistribution[0];
		for(int i = 0; i < leftSideDegreeDistribution.length; ++i){
			if(leftSideDegreeDistribution[i] < currentDegree){
				for(int j = currentDegree; j >= leftSideDegreeDistribution[i]; --j){
					conjugateLeftSide[j-1] = i;
				}
				currentDegree = leftSideDegreeDistribution[i];
			}
		}
		//the last degree is never put into conjugateLeftSide, so we have to do that now.
		for(int j = currentDegree; j > 0; --j){
			conjugateLeftSide[j-1] = leftSideDegreeDistribution.length;
		}
		
		if(debug){
			System.out.println("Left side degree distribution is ");
			for(int i : leftSideDegreeDistribution)
				System.out.print(i+" ");
			System.out.println();
			System.out.println("Its conjugate vector is ");
			for(int i : conjugateLeftSide)
				System.out.print(i+" ");
			System.out.println();
		}
		
		return conjugateLeftSide;
	}
	
}
