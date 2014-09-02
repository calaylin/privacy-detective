import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;




public class clusterReplacer {
	ArrayList<String> badWords = new ArrayList<String>();
	HashMap<String,String> replaceMap = new HashMap<String,String>();

	
	public clusterReplacer() throws FileNotFoundException{
		
		
		
		
		String mapFile = "lib/clusterKey.csv";
		
		Scanner getMap = new Scanner(new File(mapFile));
		boolean isValue = false;
		String mapValue = " ";
		int n = 0;
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
	
	public boolean IsHandle(String s){
		String pattern = "(RT)*[;'():./|,!?]*@[a-zA-Z0-9_]{1,15}['():./,|!?;]*[a-zA-Z0-9]*";
		try {
            Pattern patt = Pattern.compile(pattern);
            Matcher matcher = patt.matcher(s);
            return matcher.matches();
        } catch (RuntimeException e) {
        return false;
    } 
	}
	   public boolean IsURL(String s) {
		   String pattern = "^[().\"/]*(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|][.\"()/ ]*";
	        try {
	            Pattern patt = Pattern.compile(pattern);
	            Matcher matcher = patt.matcher(s);
	            return matcher.matches();
	        } catch (RuntimeException e) {
	        return false;
	    }     
	   }
	   
	   public boolean IsNonASCII(String s){
		   String pattern = ".*[^\\x00-\\x7F].*";
		   try {
	            Pattern patt = Pattern.compile(pattern);
	            Matcher matcher = patt.matcher(s);
	            return matcher.matches();
	        } catch (RuntimeException e) {
	        return false;
	    }     
		   
	   }
	public String replaceWords(String s){
		if(this.badWords.contains(s)){
			s = this.replaceMap.get(s);
		}
		else if(s.matches(".*\\d.*")||s.matches("\\p{Punct}")){
			System.out.println(s + "::::::caught");
		}
		else if(s.matches("[a-zA-Z]*")){
			spellCheck sc = new spellCheck();
			s = sc.getCorrectedText(s);
			if(this.badWords.contains(s)){
				s = this.replaceMap.get(s);
			}
			
		}
		
		return s;
	}
	
	public String replaceWords(String[] splitTweet){
		String newTweetString = "";
		for(String s:splitTweet){
			
			if(this.badWords.contains(s)){
				s = this.replaceMap.get(s);
			}
			else if(this.IsNonASCII(s)){
				s = "";
			}
			else if(this.IsHandle(s)){
				s = "he";
			}
			else if(this.IsURL(s)){
				s = "URL";
			}
			else if(s.matches(".*\\d.*")||s.matches("\\p{Punct}")){
				
			}
			else if(s.matches("[a-zA-Z]*")){
				spellCheck sc = new spellCheck();
				s = sc.getCorrectedText(s);
				if(this.badWords.contains(s)){
					s = this.replaceMap.get(s);
				}
				
			}
			s = s.trim();
			
			newTweetString = newTweetString + " "+s;
			
			
			
		}
		
		newTweetString = newTweetString.trim();
		return newTweetString;
	}
	

}