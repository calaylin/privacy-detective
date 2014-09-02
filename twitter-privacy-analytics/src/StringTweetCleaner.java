import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class StringTweetCleaner {

	/**
	 * @param args
	 * @throws IOException 
	 */
	
	public static void main(String[] args) throws IOException  {
		
	
	}
					
			
		// FInally close both reader and writer
		
		
	
	public String stringCleaner(String tweet) throws FileNotFoundException
	{
		
	// Change to location of directory where you are storing files
//		String tweet1 = "I'm at IGN Sprint Gaming Lounge (K Street, Harbor Drive, San Diego) w/ 3 others http://4sq.com/n3Hj4e";
		clusterReplacer fixer = new clusterReplacer();
	
	
	
	
			            String line;
			            String goodString = null;
			            
			      
			            	goodString = ""; //initialize output string to null
			            	String [] splitTweet = tweet.split(" ");//Split line by space
			            	goodString = fixer.replaceWords(splitTweet);
							goodString = goodString.trim();//get rid of any trailing or leading white space
							
							// originally had the updated strings printed out to show myself they were working
						//	System.out.println(goodString);  
						//	System.out.println(tweetFiles[t].getAbsolutePath()+fixedFile.getAbsolutePath());
	
							return goodString;
	}			            
		}