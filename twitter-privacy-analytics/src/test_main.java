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
    	
    	test_main.calculateUserAccuracy(testDir);
    	
    	
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
    public static void calculateUserAccuracy (String dirName) throws IOException, ClassNotFoundException
    {
		String output_filename = "topics_inferred.txt";
    	Calendar cal = Calendar.getInstance();
    	cal.getTime();
    	SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
       	int month = cal.get(Calendar.MONTH);
       	int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
    	String filename = "results_twitter/results_tp_" + (month+1) + "_" + 
    	dayOfMonth + "_" + sdf.format(cal.getTime()) + "Aylin" + "0.1"+ ".csv";
    	    	
    	test_main test_files = new test_main();
    	TweetRiskCalculator tw_test_in = new TweetRiskCalculator();
    	
//    	String dir ="/Users/Aylin/Documents/workspace/automated_tweet_detection_tests/labeledUsersTest";
    	List file_paths = test_files.listTextFiles(dirName);
    	int textFileCount_in = file_paths.size();
    	Util.writeFile("Username, " + "Total Score (=TMscore for now), " +"Word count, "+ 
    	"Quote Count, "+"NER count, "+"TM0 Score, "+"Topic0No,"+"TM1 Score, "+"Topic1No,"
    	+"TM2 Score, "+"Topic2No,"+"TM3 Score, "+"Topic3No,"
				,	filename, true);
		TweetNER obj1 = new TweetNER();
    	for (int i=0; i < textFileCount_in; i++)
    	{		
    //		System.out.println(file_paths.get(i).toString());
    		String tweetText = test_files.readFile(file_paths.get(i).toString());						
    		String str_path = file_paths.get(i).toString();
    		float[] topicPrivacyScore;
    		
    	    topicPrivacyScore = tw_test_in.calcUserPrivacyScore(tweetText);
    	
    			Util.writeFile(file_paths.get(i).toString()+ "," + "TBD"
    					+ "," + TweetRiskCalculator.wordCountIndex(tweetText)
    					+ "," + TweetRiskCalculator.countQuotesIndex(tweetText)
    					+ "," + obj1.calculateNerIndex(obj1, tweetText)
    				//	+ "," + "X"
    					+ "," + topicPrivacyScore[0]
    	    			+ "," + topicPrivacyScore[1]
    	    	    	+ "," + topicPrivacyScore[2] 
    	    	    	+ "," + topicPrivacyScore[3]
    	    	    	+ "," + topicPrivacyScore[4]
    	    	    	+ "," + topicPrivacyScore[5]
    	    	    	+ "," + topicPrivacyScore[6]
    	    	        + "," + topicPrivacyScore[7]



    					,	filename, true);
    		
    			
    		} 	
    	
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
    
    public static float calculateAccuracy (String dirName) throws IOException
    {
    	Calendar cal = Calendar.getInstance();
    	cal.getTime();
    	SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
       	int month = cal.get(Calendar.MONTH);
       	int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);


    	String filename = "/Users/Aylin/Desktop/results_twitter/results_tp_" + (month+1) + "_" + 
    	dayOfMonth + "_" + sdf.format(cal.getTime()) + ".txt";
    	

    	
    	test_main test_files = new test_main();

    	String str1 = "explicitly_private";
    	String str2 = "kind_of_private";
    	String str3 = "not_private";
    	float count1 = 0; //# of explicitly private instances
    	float count2 = 0; //# of kind of private instances
    	float count3 = 0; //# of  non-private instances
    	float count1_result = 0; //# of explicitly private instances detected as private (1)
    	float count2_result = 0; //# of kind of private instances detected as private (1)
    	float count3_result = 0; //# of non-private instances detected as non-private (0)
    	TweetRiskCalculator tw_test_in = new TweetRiskCalculator();
    	
    	String dir ="/Users/Aylin/Documents/workspace/automated_tweet_detection_tests/tweetSampleSpace";
    	String dir_ep ="/Users/Aylin/Documents/workspace/automated_tweet_detection_tests/tweetSampleSpace/explicitly_private";
    	String dir_kp ="/Users/Aylin/Documents/workspace/automated_tweet_detection_tests/tweetSampleSpace/kind_of_private";
    	String dir_np ="/Users/Aylin/Documents/workspace/automated_tweet_detection_tests/tweetSampleSpace/not_private";

    	List file_paths = test_files.listTextFiles(dir);
    	List file_paths_ep = test_files.listTextFiles(dir_ep);
    	List file_paths_kp = test_files.listTextFiles(dir_kp);
    	List file_paths_np = test_files.listTextFiles(dir_np);

    	int textFileCount_in = file_paths.size();
/*    	int textFileCount_ep = file_paths_ep.size();
    	int textFileCount_kp = file_paths_kp.size();
    	int textFileCount_np = file_paths_np.size();
*/
    	System.out.println("There are " + file_paths_ep.size() + " explicitly private files in the folder.");
    	System.out.println("There are " + file_paths_kp.size() + " kind of private filesin the folder.");
    	System.out.println("There are " + file_paths_np.size() + " non-private files in the folder.");
    	
        	for (int i=0; i < textFileCount_in; i++)
    	{		
    //		System.out.println(file_paths.get(i).toString());
    		String tweetText = test_files.readFile(file_paths.get(i).toString());						
    		String str_path = file_paths.get(i).toString();
    		
    		if (str_path.toLowerCase().contains(str1.toLowerCase()))
    		{
    			Util.writeFile(file_paths.get(i).toString()+ "," + tw_test_in.calcTweetPrivacyScore(tweetText) 
    					+"\n"+  tweetText , 
    					filename, true);
    			if(tw_test_in.calcTweetPrivacyScore(tweetText)==1)
    			{count1_result++;}
    			count1++;
    			
    		}

    		if (str_path.toLowerCase().contains(str2.toLowerCase()))
    		{
    			Util.writeFile(file_paths.get(i).toString()+ "," + tw_test_in.calcTweetPrivacyScore(tweetText) 
    					+"\n"+  tweetText , 
    					filename, true);
    			if(tw_test_in.calcTweetPrivacyScore(tweetText)==1)
    			{count2_result++;}
    			count2++;
    			
    		}
    		
    		if (str_path.toLowerCase().contains(str3.toLowerCase()))
    		{
    			Util.writeFile(file_paths.get(i).toString()+ "," + tw_test_in.calcTweetPrivacyScore(tweetText)
    					+ "\n"+ tweetText, 
    					filename, true);
    			if(tw_test_in.calcTweetPrivacyScore(tweetText)==0)
    			{count3_result++;}
    			count3++;
    			
    		}
    		


		}
    	
        	
    	/*accuracy = all correct detections/no of all instances
    	true_positive = #of private instances detected as private/#private instances
    	true_negative = #of non-private instances detected as non-private/#non-private instances
    	false_positive = #of non-private instances that are detected as private/#non-private instances
    	false_negative = #of private instances detected as non_private/#private instances
    	*/
		float accuracy =(count1_result+count2_result+count3_result)/(count1+count2+count3);
		float accuracy_notricky= (count1_result+count3_result)/(count1+count3);
		float tp = (count1_result+count2_result)/(count1+count2);
		float tp_special = (count1_result/count1);
		float tp_tricky = (count2_result/count2);
		float tn =count3_result/count3;
		float fp =(count3-count3_result)/count3;
		float fn =((count1-count1_result)+(count2-count2_result))/(count1+count2);
		float fn_notricky =(count1-count1_result)/count1;
		float precision = tp / (tp + fp); //tp/(tp+fp)
		float recall = tp/(tp+fn);//tp/(tp+fn)
		float f_measure = 2 * ((precision * recall)/(precision + recall)) ; //2 times (precision times recall)/(precision + recall)

		 
		Util.writeFile("The accuracy is: "+Float.toString(accuracy)+".\n" ,
				filename, true);
		Util.writeFile("The accuracy without the tricky instances is: "+Float.toString(accuracy_notricky)+".\n" ,
				filename, true);
		Util.writeFile("The precision is: "+Float.toString(precision)+".\n" ,
				filename, true);
		Util.writeFile("The recall is: "+Float.toString(recall)+".\n" ,
				filename, true);
		Util.writeFile("The f_measure is: "+Float.toString(f_measure)+".\n" ,
				filename, true);

		
		
		Util.writeFile("The true-positive rate is: "+Float.toString(tp)+".\n" ,
				filename, true);
		Util.writeFile("The true-positive rate of explicitly private is: "+Float.toString(tp_special)+".\n" ,
				filename, true);
		Util.writeFile("The true-positive rate of kind of private (tricky) is: "+Float.toString(tp_tricky)+".\n" ,
				filename, true);
	
		Util.writeFile("The true-negative rate is: "+Float.toString(tn)+".\n" ,
				filename, true);	
		Util.writeFile("The false-positive rate is: "+Float.toString(fp)+".\n" ,
				filename, true);
		
		Util.writeFile("The false-negative rate is: "+Float.toString(fn)+".\n" ,
				filename, true);
		Util.writeFile("The false-negative rate without the tricky instances is: "+Float.toString(fn_notricky)+".\n" ,
				filename, true);

		Util.writeFile("TM on clustered tweets, NER location"+".\n" ,filename, true);


    	System.out.println("There are " + count1 + " explicitly private files.");
    	System.out.println("There are " + count2 + " kind of private files.");
    	System.out.println("There are " + count3 + " non-private files.");
    	
		return accuracy;    	
 	
    	
    }
    
    
    
    public String readFile(String fileName) throws IOException {
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