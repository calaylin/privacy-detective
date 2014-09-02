package SentimentAnalysis;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.io.FileFilter;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;

/*accuracy = all correct detections/no of all instances
true_positive = #of private instances detected as private/#private instances
true_negative = #of non-private instances detected as non-private/#non-private instances
false_positive = #of non-private instances that are detected as private/#non-private instances
false_negative = #of private instances detected as non_private/#private instances
*/
public class test_main {
    public static void main(String[] args) throws IOException, ClassNotFoundException{
    	
    	
    	test_main get_files = new test_main();
//    	File dirf = new File("/Users/Aylin/Documents/workspace/automated_tweet_detection_tests/labeledUsersTest");
//    	int totalFileCount = get_files.getFilesCount(dirf);
//   	System.out.println(totalFileCount);
    	    	     	
    	String testDir ="/Users/Aylin/Documents/workspace/automated_tweet_detection_tests/30timelines/";
    	List file_paths = get_files.listTextFiles(testDir);
    	
    	
    	
//    	test_main.calcUserPrivacyScore(input_file_converted, input_file_clear);

    	
    	/*
    	TweetTopics test = new TweetTopics();
    	
    	test_main get_files = new test_main();

    	File dirf = new File("/Users/Aylin/Documents/workspace/automated_tweet_detection_tests/tweetSampleSpace");
//    	int totalFileCount = get_files.getFilesCount(dirf);
//   	System.out.println(totalFileCount);
    	    	     	
    	String dir ="/Users/Aylin/Documents/workspace/automated_tweet_detection_tests/tweetSampleSpace";
    	
    	


    	test_main.calculateAccuracy(dir);
    	List file_paths = get_files.listTextFiles(dir);
    	int textFileCount = file_paths.size();
    	System.out.println(textFileCount);*/
    	
    	/*for (int i=0; i < textFileCount; i++)
    	{		
    		System.out.println(file_paths.get(i).toString());
    		String tweetText = get_files.readFile(file_paths.get(i).toString());						
	
//		int a;
//	    a = test.compareTopics(tweetText);
//		System.out.println(a);
		
		TweetRiskCalculator tw_test = new TweetRiskCalculator();
		int result = tw_test.calcTweetPrivacyScore(tweetText);	
		String result_string = Integer.toString(result);

		Util.writeFile(file_paths.get(i).toString()+ "," + tw_test.calcTweetPrivacyScore(tweetText) , "/Users/Aylin/Desktop/results_bool.txt", true);
//		Util.writeFile(file_paths.get(i).toString()+ "\n" + tweetText + tw_test.calcTweetPrivacyScore(tweetText), "/Users/Aylin/Desktop/results.txt", true);

		}
 */   	
/*    	String tweetText = ".@GovChristie did sign into law one gun bill tonight ? it would create a school violence task force.";
		TweetRiskCalculator tw_test = new TweetRiskCalculator();
		tw_test.calcTweetPrivacyScore(tweetText);
    	*/
    	
}
    
    public static List<File> listFiles(String directoryName) {
        File directory = new File(directoryName);

        List<File> resultList = new ArrayList<File>();

        // get all the files from a directory
        File[] fList = directory.listFiles();
        resultList.addAll(Arrays.asList(fList));
        for (File file : fList) {
            if (file.isFile()) {
            	if (file.toString().endsWith(".txt"))
            	{  System.out.println(file.getAbsolutePath());}
            } else if (file.isDirectory()) {
                resultList.addAll(listFiles(file.getAbsolutePath()));
            }
        }
        //System.out.println(fList);
        return resultList;
    } 
    
    
    
    public static int getFilesCount(File file) {
    	  File[] files = file.listFiles();
    	  int count = 0;
    	  for (File f : files)
    	    if (f.isDirectory())
    	      count += getFilesCount(f);
    	    else
    	      count++;

    	  return count;
    	}
    
    
    public static List <File> listTextFiles(String dirPath)
    {

        File topDir = new File(dirPath);

        List<File> directories = new ArrayList<>();
        directories.add(topDir);

        List<File> textFiles = new ArrayList<>();

        List<String> filterWildcards = new ArrayList<>();
        filterWildcards.add("*.txt");
        filterWildcards.add("*.doc");

        FileFilter typeFilter = new WildcardFileFilter(filterWildcards);

        while (directories.isEmpty() == false)
        {
            List<File> subDirectories = new ArrayList();

            for(File f : directories)
            {
                subDirectories.addAll(Arrays.asList(f.listFiles((FileFilter)DirectoryFileFilter.INSTANCE)));
                textFiles.addAll(Arrays.asList(f.listFiles(typeFilter)));
            }

            directories.clear();
            directories.addAll(subDirectories);


        }

        return textFiles;

}
    
   
    
    
    public static String readFile(String fileName) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        try {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append("\n");
                line = br.readLine();
            }
            return sb.toString();
        } finally {
            br.close();
        }
    }
    
    
    
    
    
    
}