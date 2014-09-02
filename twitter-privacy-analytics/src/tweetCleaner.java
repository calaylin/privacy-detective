import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class tweetCleaner {
	

	
	
	static public ArrayList<String> badWords = new ArrayList<String>();
	static HashMap<String,String> replaceMap = new HashMap<String,String>();
	static{
		
		
		Scanner getMap = null;
		try {
			getMap = new Scanner(new File("lib/clusterKey.csv"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String mapValue = " ";
		while (getMap.hasNextLine()){
			String l = getMap.nextLine();
			String [] split_l = l.split(",");
			if(l.contains("Words Associated")){
				String test = split_l[0];
				mapValue = test;
			}
			else{
				String test = split_l[1];
				badWords.add(test.trim());
				replaceMap.put(test.trim(), mapValue);
			}
					
			
		}
	}
	
	
	
	


	/**
	 * @param args
	 * @throws FileNotFoundException 
	 * @throws IOException 
	 */
	
 public static String convertClusters(String tweet) throws FileNotFoundException{
	 	tweet = tweet.trim();
		String newTweet = "";
		String[] splitTweet = tweet.split(" ");
		
		
		for(String s:splitTweet){
			if(s.equals("")){
				continue;
			}
			s = s.trim();
			
			if(badWords.contains(s)){
			
			s = replaceMap.get(s);
			s = s.trim();
			
			}
			newTweet = newTweet +s+" ";

			
		}
		return newTweet;
	}//misspellings  
	 public static String correctMisspellings(String tweet){
		 spellCheck sc = new spellCheck();
		tweet = tweet.trim();

		String newTweet = "";
		String[] splitTweet = tweet.split(" ");
		
		for(String s:splitTweet){
			s = s.trim();
			if(s.matches(".*\\d.*")||s.matches("\\p{ASCII}*\\p{Punct}*\\p{ASCII}*")||s.equals("")){
			}
			else{
				s = sc.getCorrectedLine(s) ;
			}
			s = s.trim();
			newTweet = newTweet + s + " ";
		}
		
		return newTweet;
		
	}
	 
	public static String removeNonASCII(String tweet){
		String newTweet = "";
		tweet = tweet.trim();
		String[] splitTweet = tweet.split(" ");
		
		for(String s:splitTweet){
			
			s = s.trim();
			if(s.equals("")||s.matches(".*[^\\x00-\\x7F].*")){
				continue;
			}
			else{
				newTweet = newTweet + s + " ";
			}
			
		}
		
		return newTweet;
	}
	
	public static String replaceURLs(String tweet){
		String newTweet = "";
		String replaceString = "URL";
		tweet = tweet.trim();
		String[] splitTweet = tweet.split(" ");
		
		for(String s:splitTweet){
			
			s = s.trim();
			if(s.equals("")){
				continue;
			}
			if(s.matches("^[:().\"/]*(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|][:.\"()/ ]*")){
				s = replaceString;
			}
			
			newTweet = newTweet + s.trim() + " ";
		}
		
		return newTweet;
	}
	
	public static String replaceHandles(String tweet){
		String newTweet = "";
		String replaceString = "he";
		tweet = tweet.trim();
		String[] splitTweet = tweet.split(" ");
		
		for(String s:splitTweet){
			
			s = s.trim();
			if(s.equals("")){
				continue;
			}
			if(s.matches("(RT)*[;'():./|,!?]*@[a-zA-Z0-9_]{1,15}['():./,|!?;]*[a-zA-Z0-9]*")){
				s = replaceString;
			}
			
			newTweet = newTweet + s.trim() + " ";
		}
		
		return newTweet;
	}
	
	public static void main(String[] args) throws IOException  {
		
			
			String testTweet = "This is a sample. tweet, sample url: http://i.imgur.com/i3M66My.jpg , sampe handle: @sampHand";
			System.out.println("Original Tweet:			"+testTweet);	
			
			
			testTweet = tweetCleaner.convertClusters(testTweet);
			testTweet = tweetCleaner.removeNonASCII(testTweet);
			testTweet = tweetCleaner.replaceHandles(testTweet);
			testTweet = tweetCleaner.replaceURLs(testTweet);
			testTweet = tweetCleaner.correctMisspellings(testTweet);
			
			System.out.println("Corrected Tweet:		"+testTweet);	




			            
		
		
			}// End Main
		
	}// End Tweet cleaner
					
			
		
		
	
