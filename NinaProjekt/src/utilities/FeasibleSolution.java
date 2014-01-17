package utilities;

import java.math.BigInteger;

public class FeasibleSolution implements Comparable<FeasibleSolution> {
	BigInteger[] lineNumbers;
	int length;
	boolean debug = false;
	public FeasibleSolution(BigInteger[] _lineNumbers){
		lineNumbers = _lineNumbers;
		length = lineNumbers.length;
	}
	
	public int compareTo(FeasibleSolution other) throws ClassCastException{
		if(this.length != other.length)
			throw new ClassCastException("The two solutions are not comparable");
		
		int counter = 0; 
		while(counter < length-1 && this.lineNumbers[counter].compareTo(other.lineNumbers[counter]) == 0){
			++counter;
		}
		if(debug){
			switch(this.lineNumbers[counter].compareTo(other.lineNumbers[counter])){
			case 0:
				for(BigInteger bi: this.lineNumbers) System.out.print(bi+" ");
				System.out.print(" is equal to ");
				for(BigInteger bi: other.lineNumbers) System.out.print(bi+" ");
				System.out.println("");
				break;
			case -1:
				for(BigInteger bi: this.lineNumbers) System.out.print(bi+" ");
				System.out.print(" is smaller than ");
				for(BigInteger bi: other.lineNumbers) System.out.print(bi+" ");
				System.out.println("");
				break;
			case 1:
				for(BigInteger bi: this.lineNumbers) System.out.print(bi+" ");
				System.out.print(" is larger than ");
				for(BigInteger bi: other.lineNumbers) System.out.print(bi+" ");
				System.out.println("");
				break;
				
			}
		}
		return -this.lineNumbers[counter].compareTo(other.lineNumbers[counter]);
		
	}
	
	public String toString(){
		String string = "";
		for(BigInteger bi: lineNumbers) string = string + bi+" ";
		return string;
	}

}
