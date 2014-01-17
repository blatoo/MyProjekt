package utilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;

public class YingsTestClass {
	
	public static void main(String[] args){
		
		File file = new File("/home/rootm/Netflix/data3user.txt");
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line = br.readLine();
			int userID, maxUserID = 0;
			int counter1 =0, counter2 = 1;
			
			while(line != null){
				
				if(++counter1 % 1000000 == 0){
					counter1 = 0;
					System.out.println(counter2+ " million lines have been read");
					counter2++;
					if(counter2 % 1000000 == 0){
						System.out.println("   A milliard lines have been read");
						counter2 = 0;
					}
				}
				
				
				
				StringTokenizer st = new StringTokenizer(line, ",");
				//first token is filmID, we don't need that
				st.nextToken();
				userID = Integer.parseInt(st.nextToken());
				maxUserID = userID>maxUserID ? userID : maxUserID;
				
				
				line = br.readLine();
			}
			
			System.out.println("max user id is "+maxUserID);
			
			br.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(-1);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	

}
