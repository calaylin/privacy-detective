package SentimentAnalysis;



import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {

    /**
     * @param args the command line arguments
     * @throws IOException 
     */
    public static void main(String[] args) throws IOException {
       // train(); //use this to train

      	int private_files=0;
    	int notprivate_files=0;
		
		String test_dir= "/Users/Aylin/Desktop/test_docs_all_private/";
		List test_file_paths = test_main.listTextFiles(test_dir);
	//   	for(int i=0; i< test_file_paths.size(); i++){
	   	for(int i=0; i< 1; i++){

		String testTweets = test_main.readFile(test_file_paths.get(i).toString());
			System.out.println(notprivatecounter_sentiment(testTweets));
			System.out.println(privatecounter_sentiment(testTweets));
				}
	   	
	   	
/*			
        BufferedReader br = new BufferedReader(new FileReader(test_file_paths.get(i).toString()));
        String line;
    	int private_counter=0;
    	int notprivate_counter=0;

        while ((line = br.readLine()) != null) {
            String a= classify(line);
            if (a.equals("private"))
            	private_counter++;
            if (a.equals("notprivate"))
              	notprivate_counter++;	

           // process the line.
        }
        br.close();
        if (private_counter > notprivate_counter){
        	private_files++;
        	 System.out.print("private");}
        if (private_counter == notprivate_counter){
       	 System.out.print("same");}
        if (private_counter < notprivate_counter){
        	notprivate_files++;
       	 System.out.print("notprivate");}
        
        System.out.print(" -- private_counter:" + private_counter + " | notprivate_counter:" + notprivate_counter + "\n");
    }
      	 System.out.print("private_files:" + private_files + "---notprivate_files:" + notprivate_files);
      	 
    
    */
    }

    
    
    public static int notprivatecounter_sentiment(String userText){

//		Util.writeFile(TweetRiskCalculator.privacyDictionaryIndex(userTweets)+",", output_file, true);
    	String lines[] = userText.split("\\r?\\n");

    	int notprivate_counter=0;

    	  for(int i=1;i<lines.length;i++){
    		  String a= classify(lines[i]);
              if (a.equals("notprivate"))
                	notprivate_counter++;	

    		  
       //       System.out.println(lines[i]);
          }
    	  
    	  return notprivate_counter;
    	
    }
    
    public static int privatecounter_sentiment(String userText){

//		Util.writeFile(TweetRiskCalculator.privacyDictionaryIndex(userTweets)+",", output_file, true);
    	String lines[] = userText.split("\\r?\\n");

    	int private_counter=0;

    	  for(int i=1;i<lines.length;i++){
    		  String a= classify(lines[i]);
              if (a.equals("private"))
                	private_counter++;		  
       //       System.out.println(lines[i]);
          }
    	  
    	  return private_counter;    	
    }
    /**
     * Train example function
     */
    public static void train(){
        try {
            Trainer t = new Trainer();
            t.Train();
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Clissify example function
     * @param str   Text to classify
     * @return Category name
     */
    public static String classify(String str){
        SentimentClassifier sc = new SentimentClassifier();
        return sc.classify(str);
    }
        
    
}
