
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Formatter;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.TreeSet;
import java.util.regex.Pattern;

import cc.mallet.pipe.CharSequence2TokenSequence;
import cc.mallet.pipe.CharSequenceLowercase;
import cc.mallet.pipe.Pipe;
import cc.mallet.pipe.SerialPipes;
import cc.mallet.pipe.TokenSequence2FeatureSequence;
import cc.mallet.pipe.TokenSequenceRemoveStopwords;
import cc.mallet.pipe.iterator.CsvIterator;
import cc.mallet.topics.ParallelTopicModel;
import cc.mallet.topics.TopicInferencer;
import cc.mallet.types.Alphabet;
import cc.mallet.types.FeatureSequence;
import cc.mallet.types.IDSorter;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import cc.mallet.types.LabelSequence;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import SentimentAnalysis.Config;
import SentimentAnalysis.Main;
import SentimentAnalysis.SentimentClassifier;
import SentimentAnalysis.Trainer;
import SentimentAnalysis.test_main;


public class InputInferencer {
	public static void main(String[] args) throws FileNotFoundException, IOException, ClassNotFoundException {
		
		//The following specifies the topic model file to infer topic probabilities from
		//decimal format is used in writing the topic probabilities to arff
		//DecimalFormat df = new DecimalFormat ("000");
		//DecimalFormat df = new DecimalFormat("##.#####");
	//	String topicModelFile = "topic_model/twitter_topicModel_Feb13_200_plain.model";
		String topicModelFile = "topic_model/twitter_topicModel_Feb2_200_converted.model";
	//	String topicModelFile = "topic_model/converted_alltweets.txt_50.model";

		int noTopics = 200;  //used for writing the arff attributes
		
		//Specifying the test arff filename
		Calendar cal = Calendar.getInstance();
    	cal.getTime();
    	SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
       	int month = cal.get(Calendar.MONTH);
       	int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
       	String time = sdf.format(cal.getTime());
    	//TODO when time changes, output_filename changes every time which needs to be corrected
       	String output_filename = "/Users/Aylin/Desktop/arffs/" +"test_"+ (month+1) + "." + 
    	dayOfMonth + "_"+ time + "_"  +noTopics+ "tm_ner_features" +".arff" ;

/*		
		//EXTRACTING TRAINING FEATURES
		//This is the training directory
		test_main obj1 = new test_main();
		String training_dir = "/Users/Aylin/Desktop/training_docs/";
    	List file_paths = test_main.listTextFiles(training_dir);

    	
    	
		//Specifying the train arff filename
     	String training_output_filename = "/Users/Aylin/Desktop/arffs/" + "train_"+(month+1) + "." + 
     	    	dayOfMonth + "_"+ time + "_"  +noTopics+ "tm_ner_features" +".arff" ;
       	//Writing the train arff
		Util.writeFile("@relation TwitterPrivacy"+"\n"+"\n", training_output_filename, true);
		//List all the topic features as numeric for now
		for(int i=0; i<=noTopics; i++){
			
			Util.writeFile("@attribute Topic-Probabilities-" +i+" numeric"+"\n", training_output_filename, true);
	
		}
		//Write ner features and the scores as classes, then start the data by calling the function
		//the privacy categories are coded as 00, 01... etc
		//01 is location, 02 is medical for now
		Util.writeFile("@attribute 'NER-Dates{-}' numeric" +"\n"+
		"@attribute 'NER-Loc{-}' numeric"  +"\n"+
		"@attribute 'NER-Money{-}' numeric"  +"\n"+
		"@attribute 'NER-Name{-}' numeric"  +"\n"+
		"@attribute 'NER-Org{-}' numeric"  +"\n"+
		"@attribute 'NER-Percentage{-}' numeric"  +"\n"+
		"@attribute 'NER-Time{-}' numeric"  +"\n"+
		"@attribute 'privacydicmatches' numeric"  +"\n"+		
		"@attribute 'quotecount' numeric"  +"\n"+
		"@attribute 'privatesentimentcount' numeric"  +"\n"+
		"@attribute 'notprivatesentimentcount' numeric"  +"\n"+
//		"@attribute 'filename' numeric"  +"\n"+
		"@attribute privacy_category {00,01,02,03,04,05,06,07,08, 09}"+"\n" + "\n"+
		"@data"+"\n", training_output_filename, true);
		
		//extract features from training documents in the training folder
    	for(int i=0; i< file_paths.size(); i++){
		String trainTweets = obj1.readFile(file_paths.get(i).toString());
		//remove all newlines so that topic modeling takes it as one document.
		String privacy_cat = file_paths.get(i).toString().substring(35,37); 
		System.out.println(file_paths.get(i));

		trainTweets = trainTweets.replaceAll("\\n", "");
		InputInferencer.TrainingTopicModelQuotesNERFeatures(trainTweets,training_output_filename, privacy_cat, topicModelFile, noTopics);

    	}
    	
*/	

		
	     
  	//Writing the test arff
  	//first specify relation
	Util.writeFile("@relation TwitterPrivacy"+"\n"+"\n", output_filename, true);
	//List all the topic features as numeric for now
	Util.writeFile("@attribute 'instanceID' String"  +"\n", output_filename, true);	

	for(int i=0; i<=noTopics; i++){
		
		Util.writeFile("@attribute Topic-Probabilities-" +i+" numeric"+"\n", output_filename, true);

	}
	//Write ner features and the scores as classes, then start the data by calling the function
	Util.writeFile("@attribute 'NER-Dates{-}' numeric" +"\n"+
	"@attribute 'NER-Loc{-}' numeric"  +"\n"+
	"@attribute 'NER-Money{-}' numeric"  +"\n"+
	"@attribute 'NER-Name{-}' numeric"  +"\n"+
	"@attribute 'NER-Org{-}' numeric"  +"\n"+
	"@attribute 'NER-Percentage{-}' numeric"  +"\n"+
	"@attribute 'NER-Time{-}' numeric"  +"\n"+
	"@attribute 'privacydicmatches' numeric"  +"\n"+		
	"@attribute 'quotecount' numeric"  +"\n"+	
	"@attribute 'RTcount' numeric"  +"\n"+	
	"@attribute 'httpcount' numeric"  +"\n"+	
	"@attribute 'handlecount' numeric"  +"\n"+	
	"@attribute 'ATcount' numeric"  +"\n"+	
	"@attribute 'privatesentimentcount' numeric"  +"\n"+
	"@attribute 'notprivatesentimentcount' numeric"  +"\n"+
	"@attribute privacy_category {01,02,03}"+"\n" + "\n"+
	"@data"+"\n", output_filename, true);

	
	
	
	
	//EXTRACT UNLABELED TEST FEATURES
	test_main obj2 = new test_main();
//	String test_dir = "/Users/Aylin/Desktop/test_docs_all_private/";
//	String test_dir = "/Users/Aylin/Desktop/chunkedUsers26March/";
	for(int j = 1; j <4; j++){
	String test_dir = "/Users/Aylin/Desktop/test_sets/sortedUserResults_100_60_30/" + j + "/";
	
//	String test_dir = "/Users/Aylin/Desktop/train_test_sets/alldb_users/" + j + "/";
//	String test_dir = "/Users/Aylin/Desktop/new_may_AMT_results/sortedUsers/" + j + "/";
		
	List test_file_paths = test_main.listTextFiles(test_dir);
   	for(int i=0; i< test_file_paths.size(); i++){
		String testTweets = obj2.readFile(test_file_paths.get(i).toString());
		//remove all newlines so that topic modeling takes it as one document.
		int testIDlength = test_file_paths.get(i).toString().length(); 

//		String testID = test_file_paths.get(i).toString().substring(41,testIDlength-4); 
		String testID = test_file_paths.get(i).toString().substring(61,testIDlength-4);  //100_60_30
//		String testID = test_file_paths.get(i).toString().substring(72,testIDlength-4);  //72 for 100_60_30_samenumber folder (100_60_30_samenumber)
//		String testID = test_file_paths.get(i).toString().substring(55,testIDlength-4); //new_may_AMT_results/sortedUsers/
//		String testID = test_file_paths.get(i).toString().substring(45,testIDlength-9); //test_docs_all_notprivate
		String privacy_cat = "0" + j;
		System.out.println(test_file_paths.get(i));

	//	testTweets = testTweets.replaceAll("\\n", "");
		
/*		testTweets = tweetCleaner.convertClusters(testTweets); //conversion1
		testTweets = tweetCleaner.removeNonASCII(testTweets); //conversion2
		testTweets = tweetCleaner.replaceHandles(testTweets); //conversion3
		testTweets = tweetCleaner.replaceURLs(testTweets); //conversion4
		testTweets = tweetCleaner.correctMisspellings(testTweets);//conversion5
		*/
		
		InputInferencer.TestingTopicModelQuotesNERFeatures(testTweets,output_filename, privacy_cat, topicModelFile, noTopics, testID );
   	}	}
   	
   	
/*		
		//extract test features
		String input_filename ="";
		InputInferencer testObj = new InputInferencer();
		 int [] idno={1,2,3};
		 
		 
		 for(int i=0; i< idno.length; i++){
			 for (int j = 0; j < 23; j++){
				 if (j <10){
		 input_filename = "/Users/Aylin/Desktop/usersByScore/" +idno[i] +"/00"+ j + ".txt";}
				 if (j >10){
						input_filename = "/Users/Aylin/Desktop/usersByScore/" +idno[i] +"/0"+ j + ".txt";}
								 
		test_main obj = new test_main();
		String userTweets = obj.readFile(input_filename);
		//remove all newlines so that topic modeling takes it as one document.
		userTweets = userTweets.replaceAll("\\n", "");
		
		testObj.TopicModelQuotesNERFeatures(userTweets,output_filename, idno[i], topicModelFile, noTopics);
	//	testObj.TestingTopicModelQuotesNERFeatures(userTweets,output_filename, topicModelFile, noTopics);

		
		 }
		
		 }
		 
*/		 
		
/*		String topicModelFile = "model/twitter_topicModel_Feb2_200.model";
		ObjectInputStream ois = new ObjectInputStream (new FileInputStream(topicModelFile));		
		ParallelTopicModel topicModel = (ParallelTopicModel)ois.readObject();
		int numberOfTopics = topicModel.getNumTopics();
		
		
//		TopicInferencer inferencer = TopicInferencer.read(new File(topicModelFile));
//	InstanceList instances = InstanceList.load (new File("plain_user_text/test/0.txt"));
		
        ArrayList<Pipe> pipeList = new ArrayList<Pipe>();

        // Pipes: lowercase, tokenize, remove stopwords, map to features
        pipeList.add( new CharSequenceLowercase() );
        //word format by Regular expression
        pipeList.add( new CharSequence2TokenSequence(Pattern.compile("\\p{L}[\\p{L}\\p{P}]+\\p{L}")) );
        pipeList.add( new TokenSequenceRemoveStopwords(new File("stoplists/en.txt"), "UTF-8", false, false, false) );
        //add bigram words
        //pipeList.add(new TokenSequenceNGrams(new int[] {2} ));

        //convert to feature
        pipeList.add( new TokenSequence2FeatureSequence() );

        InstanceList instances = new InstanceList (new SerialPipes(pipeList));

		InstanceList testing = new InstanceList(instances.getPipe());
		String input_filename = "/Users/Aylin/Documents/workspace/automated_tweet_detection_tests/plain_user_text/test/0.txt";
		test_main obj = new test_main();
		String userTweets = obj.readFile(input_filename);						  
		testing.addThruPipe(new Instance( userTweets, null, "test instance", null));

		int ntopic = topicModel.getNumTopics();
		double[] topicRatio = new double[ntopic]; //number of users have topic i as top topic


	//	System.out.println("Predictions:");
		TopicInferencer inferencer = topicModel.getInferencer();
		double[] testProbabilities = inferencer.getSampledDistribution(testing.get(0), 10, 1, 5);
	//	System.out.println("0\t" + testProbabilities[0]);

	//	inferencer.writeInferredDistributions(instances, new File("docTopicsFile.txt"), 50, 20, 1,0.01, 100);
				
		for(int i=1; i<testProbabilities.length; i++)
		{
			
			topicRatio[i]+=testProbabilities[i];
		}
		
		TopicModel_Aylin obj1 =new TopicModel_Aylin();
		obj1.printTopics(topicModel,  "topics_inferred.txt", topicRatio);
	*/
	}

	   
	   public static void topicModelInferencerIndex (String userTweets, String output_file) throws FileNotFoundException, IOException, ClassNotFoundException
	   {
		   
			String topicModelFile = "model/twitter_topicModel_Feb2_200.model";
			ObjectInputStream ois = new ObjectInputStream (new FileInputStream(topicModelFile));		
			ParallelTopicModel topicModel = (ParallelTopicModel)ois.readObject();
			int numberOfTopics = topicModel.getNumTopics();
	        ArrayList<Pipe> pipeList = new ArrayList<Pipe>();

	        // Pipes: lowercase, tokenize, remove stopwords, map to features
	        pipeList.add( new CharSequenceLowercase() );
	        //word format by Regular expression
	        pipeList.add( new CharSequence2TokenSequence(Pattern.compile("\\p{L}[\\p{L}\\p{P}]+\\p{L}")) );
	        pipeList.add( new TokenSequenceRemoveStopwords(new File("stoplists/en.txt"), "UTF-8", false, false, false) );
	        //add bigram words
	        //pipeList.add(new TokenSequenceNGrams(new int[] {2} ));
	        //convert to feature
	        pipeList.add( new TokenSequence2FeatureSequence() );

	        InstanceList instances = new InstanceList (new SerialPipes(pipeList));

			InstanceList testing = new InstanceList(instances.getPipe());					  
			testing.addThruPipe(new Instance(  userTweets, null, "test instance", null));

			int ntopic = topicModel.getNumTopics();
			double[] topicRatio = new double[ntopic]; //number of users have topic i as top topic


//			System.out.println("Predictions:");
			TopicInferencer inferencer = topicModel.getInferencer();
			double[] testProbabilities = inferencer.getSampledDistribution(testing.get(0), 10, 1, 5);
		//	System.out.println("0\t" + testProbabilities[0]);
			
			for(int i=0; i<testProbabilities.length; i++)
			{
				topicRatio[i]+=testProbabilities[i];
			}
			TopicModel_Aylin obj1 =new TopicModel_Aylin();
			obj1.printTopics(topicModel,  output_file, topicRatio);
	   	}
	   
	   
	   public static void topicModelInferencerProbabilities (String userTweets, String output_file, int classname) throws FileNotFoundException, IOException, ClassNotFoundException
	   {
		   
//			String topicModelFile = "model/twitter_topicModel_Feb2_200.model";
			String topicModelFile = "topic_model/twitter_topicModel_Feb13_200.model";

			ObjectInputStream ois = new ObjectInputStream (new FileInputStream(topicModelFile));		
			ParallelTopicModel topicModel = (ParallelTopicModel)ois.readObject();
			int numberOfTopics = topicModel.getNumTopics();
	        ArrayList<Pipe> pipeList = new ArrayList<Pipe>();

	        // Pipes: lowercase, tokenize, remove stopwords, map to features
	        pipeList.add( new CharSequenceLowercase() );
	        //word format by Regular expression
	        pipeList.add( new CharSequence2TokenSequence(Pattern.compile("\\p{L}[\\p{L}\\p{P}]+\\p{L}")) );
	        pipeList.add( new TokenSequenceRemoveStopwords(new File("stoplists/en.txt"), "UTF-8", false, false, false) );
	        //add bigram words
	        //pipeList.add(new TokenSequenceNGrams(new int[] {2} ));
	        //convert to feature
	        pipeList.add( new TokenSequence2FeatureSequence() );

	        InstanceList instances = new InstanceList (new SerialPipes(pipeList));

			InstanceList testing = new InstanceList(instances.getPipe());					  
			testing.addThruPipe(new Instance(  userTweets, null, "test instance", null));

			int ntopic = topicModel.getNumTopics();
			double[] topicRatio = new double[ntopic]; //number of users have topic i as top topic


//			System.out.println("Predictions:");
			DecimalFormat df = new DecimalFormat("##.#####");

			TopicInferencer inferencer = topicModel.getInferencer();
			double[] testProbabilities = inferencer.getSampledDistribution(testing.get(0), 10, 1, 5);
		//	System.out.println("0\t" + testProbabilities[0]);
			for(int i=0; i<testProbabilities.length; i++)
			{
				topicRatio[i]+=testProbabilities[i];
				float a=(float) topicRatio[i];
				Util.writeFile(df.format(testProbabilities[i])+"," , output_file, true);
//				Util.writeFile(a+"," , output_file, true);

				if  ((i+1)==testProbabilities.length){
					Util.writeFile((df.format(testProbabilities[i]))+","+classname+"\n" , output_file, true);

				}

			}
	   	}
	   
	   public static void TopicModelQuotesNERFeatures (String userTweets, String output_file, int classname, String topicModelFile, int noTopics ) throws FileNotFoundException, IOException, ClassNotFoundException
	   {
		   //using userTweets_converted5 for topic model features, the rest is misspelling corrected tweets
		   String userTweets_converted0,userTweets_converted1, userTweets_converted2, userTweets_converted3, userTweets_converted4, userTweets_converted5;
		   
		   userTweets_converted0 = tweetCleaner.correctMisspellings(userTweets);//conversion5

		   userTweets_converted1 = tweetCleaner.convertClusters(userTweets); //conversion1
		   userTweets_converted2 = tweetCleaner.removeNonASCII(userTweets); //conversion2
		   userTweets_converted3 = tweetCleaner.replaceHandles(userTweets); //conversion3
		   userTweets_converted4 = tweetCleaner.replaceURLs(userTweets); //conversion4
		   userTweets_converted5 = tweetCleaner.correctMisspellings(userTweets);//conversion5
			
			ObjectInputStream ois = new ObjectInputStream (new FileInputStream(topicModelFile));		
			ParallelTopicModel topicModel = (ParallelTopicModel)ois.readObject();
			int numberOfTopics = topicModel.getNumTopics();
	        ArrayList<Pipe> pipeList = new ArrayList<Pipe>();

	        // Pipes: lowercase, tokenize, remove stopwords, map to features
	        pipeList.add( new CharSequenceLowercase() );
	        //word format by Regular expression
	        pipeList.add( new CharSequence2TokenSequence(Pattern.compile("\\p{L}[\\p{L}\\p{P}]+\\p{L}")) );
	        pipeList.add( new TokenSequenceRemoveStopwords(new File("stoplists/en.txt"), "UTF-8", false, false, false) );
	        //add bigram words
	        //pipeList.add(new TokenSequenceNGrams(new int[] {2} ));
	        //convert to feature
	        pipeList.add( new TokenSequence2FeatureSequence() );

	        InstanceList instances = new InstanceList (new SerialPipes(pipeList));

			InstanceList testing = new InstanceList(instances.getPipe());					  
			testing.addThruPipe(new Instance(  userTweets_converted5, null, "test instance", null));

			int ntopic = topicModel.getNumTopics();
			double[] topicRatio = new double[ntopic]; //number of users have topic i as top topic

			TweetNER toi = new TweetNER();
			toi.tokenization(userTweets_converted0);
			

//			System.out.println("Predictions:");
			DecimalFormat df = new DecimalFormat("##.#####");

			TopicInferencer inferencer = topicModel.getInferencer();
			double[] testProbabilities = inferencer.getSampledDistribution(testing.get(0), 10, 1, 5);
		//	System.out.println("0\t" + testProbabilities[0]);

			
			
			for(int i=0; i<testProbabilities.length; i++)
			{
				
				topicRatio[i]+=testProbabilities[i];
				float a=(float) topicRatio[i];
				Util.writeFile(df.format(testProbabilities[i]*500)+"," , output_file, true);
//				Util.writeFile(a+"," , output_file, true);

				if  ((i+1)==testProbabilities.length){
					Util.writeFile((df.format(testProbabilities[i]*500))+",", output_file, true);
			//		Util.writeFile(TweetRiskCalculator.uniqueWords(userTweets)+",", output_file, true);
					Util.writeFile(toi.nameCount(toi.Tokens)+",", output_file, true);
					Util.writeFile(toi.locCount(toi.Tokens)+",", output_file, true);
					Util.writeFile(toi.timeCount(toi.Tokens)+",", output_file, true);
					Util.writeFile(toi.dateCount(toi.Tokens)+",", output_file, true);
					Util.writeFile(toi.moneyCount(toi.Tokens)+",", output_file, true);
					Util.writeFile(toi.percentageCount(toi.Tokens)+",", output_file, true);
					//excluding organization count improved accuracy in one case
					Util.writeFile(toi.orgCount(toi.Tokens)+",", output_file, true);//classname is for the score label
				//	Util.writeFile(toi.orgCount(toi.Tokens)+","+classname+"\n" , output_file, true);//classname is for the score label
					//quotecount is helpful in the last case, but decreased accuracy in other cases
					Util.writeFile(TweetRiskCalculator.privacyDictionaryIndex(userTweets_converted0)+",", output_file, true);
					Util.writeFile(TweetRiskCalculator.countQuotesIndex(userTweets)+","+classname+"\n" , output_file, true);

				}

			}
	   	}
	   
	   public static void TrainingTopicModelQuotesNERFeatures (String userTweets, String output_file, String classname, String topicModelFile, int noTopics ) throws FileNotFoundException, IOException, ClassNotFoundException
	   {
			ObjectInputStream ois = new ObjectInputStream (new FileInputStream(topicModelFile));		
			ParallelTopicModel topicModel = (ParallelTopicModel)ois.readObject();
			int numberOfTopics = topicModel.getNumTopics();
	        ArrayList<Pipe> pipeList = new ArrayList<Pipe>();

	        // Pipes: lowercase, tokenize, remove stopwords, map to features
	        pipeList.add( new CharSequenceLowercase() );
	        //word format by Regular expression
	        pipeList.add( new CharSequence2TokenSequence(Pattern.compile("\\p{L}[\\p{L}\\p{P}]+\\p{L}")) );
	        pipeList.add( new TokenSequenceRemoveStopwords(new File("stoplists/en.txt"), "UTF-8", false, false, false) );
	        //add bigram words
	        //pipeList.add(new TokenSequenceNGrams(new int[] {2} ));
	        //convert to feature
	        pipeList.add( new TokenSequence2FeatureSequence() );

	        InstanceList instances = new InstanceList (new SerialPipes(pipeList));

			InstanceList testing = new InstanceList(instances.getPipe());					  
			testing.addThruPipe(new Instance(  userTweets, null, "test instance", null));

			int ntopic = topicModel.getNumTopics();
			double[] topicRatio = new double[ntopic]; //number of users have topic i as top topic

			TweetNER toi = new TweetNER();
			toi.tokenization(userTweets);
			

//			System.out.println("Predictions:");
			DecimalFormat df = new DecimalFormat("##.#####");

			TopicInferencer inferencer = topicModel.getInferencer();
			double[] testProbabilities = inferencer.getSampledDistribution(testing.get(0), 10, 1, 5);
		//	System.out.println("0\t" + testProbabilities[0]);

			
			
			for(int i=0; i<testProbabilities.length; i++)
			{
				
				topicRatio[i]+=testProbabilities[i];
				float a=(float) topicRatio[i];
				Util.writeFile(df.format(testProbabilities[i])+"," , output_file, true);
//				Util.writeFile(a+"," , output_file, true);

				if  ((i+1)==testProbabilities.length){
					Util.writeFile((df.format(testProbabilities[i]))+",", output_file, true);
			//		Util.writeFile(TweetRiskCalculator.uniqueWords(userTweets)+",", output_file, true);
					Util.writeFile(toi.nameCount(toi.Tokens)/12+",", output_file, true);
					Util.writeFile(toi.locCount(toi.Tokens)/12+",", output_file, true);
					Util.writeFile(toi.timeCount(toi.Tokens)/12+",", output_file, true);
					Util.writeFile(toi.dateCount(toi.Tokens)/12+",", output_file, true);
					Util.writeFile(toi.moneyCount(toi.Tokens)/12+",", output_file, true);
					Util.writeFile(toi.percentageCount(toi.Tokens)/12+",", output_file, true);
					//excluding organization count improved accuracy in one case
					Util.writeFile(toi.orgCount(toi.Tokens)/12+",", output_file, true);//classname is for the score label
					Util.writeFile(TweetRiskCalculator.privacyDictionaryIndex(userTweets)/12+",", output_file, true);
				//	Util.writeFile(toi.orgCount(toi.Tokens)+","+classname+"\n" , output_file, true);//classname is for the score label
					//quotecount is helpful in the last case, but decreased accuracy in other cases
					Util.writeFile(TweetRiskCalculator.countQuotesIndex(userTweets)/12+","+classname+"\n" , output_file, true);
//					Util.writeFile(TweetRiskCalculator.countQuotesIndex(userTweets)+","+classname+"\n" , output_file, true);

				}

			}
	   	}
	   //THIS IS MAINLY USED.
	   public static void TestingTopicModelQuotesNERFeatures (String userTweets, String output_file,String classname, String topicModelFile, int noTopics, String testID ) throws FileNotFoundException, IOException, ClassNotFoundException

	   {
		   
		   String userTweets_converted0,userTweets_converted1, userTweets_converted2, userTweets_converted3, userTweets_converted4, userTweets_converted5;
		   
		   userTweets_converted0 = tweetCleaner.correctMisspellings(userTweets);//conversion5
		   userTweets_converted0 = tweetCleaner.removeNonASCII(userTweets);//conversion5
		   userTweets_converted0 = tweetCleaner.replaceHandles(userTweets);//conversion5
		   userTweets_converted0 = tweetCleaner.replaceURLs(userTweets);//conversion5

		   
		   userTweets_converted1 = tweetCleaner.convertClusters(userTweets); //conversion1
		   userTweets_converted2 = tweetCleaner.removeNonASCII(userTweets); //conversion2
		   userTweets_converted3 = tweetCleaner.replaceHandles(userTweets); //conversion3
		   userTweets_converted4 = tweetCleaner.replaceURLs(userTweets); //conversion4
		   userTweets_converted5 = tweetCleaner.correctMisspellings(userTweets);//conversion5
		   
		   
		   
		   
			ObjectInputStream ois = new ObjectInputStream (new FileInputStream(topicModelFile));		
			ParallelTopicModel topicModel = (ParallelTopicModel)ois.readObject();
			int numberOfTopics = topicModel.getNumTopics();
	        ArrayList<Pipe> pipeList = new ArrayList<Pipe>();

	        // Pipes: lowercase, tokenize, remove stopwords, map to features
	        pipeList.add( new CharSequenceLowercase() );
	        //word format by Regular expression
	        pipeList.add( new CharSequence2TokenSequence(Pattern.compile("\\p{L}[\\p{L}\\p{P}]+\\p{L}")) );
	        pipeList.add( new TokenSequenceRemoveStopwords(new File("stoplists/en.txt"), "UTF-8", false, false, false) );
	        //add bigram words
	        //pipeList.add(new TokenSequenceNGrams(new int[] {2} ));
	        //convert to feature
	        pipeList.add( new TokenSequence2FeatureSequence() );

	        InstanceList instances = new InstanceList (new SerialPipes(pipeList));

			InstanceList testing = new InstanceList(instances.getPipe());					  
			testing.addThruPipe(new Instance(  userTweets_converted5, null, "test instance", null));

			int ntopic = topicModel.getNumTopics();
			double[] topicRatio = new double[ntopic]; //number of users have topic i as top topic

			TweetNER toi = new TweetNER();
			toi.tokenization(userTweets_converted0);
			

//			System.out.println("Predictions:");
			DecimalFormat df = new DecimalFormat("##.#####");

			TopicInferencer inferencer = topicModel.getInferencer();
			double[] testProbabilities = inferencer.getSampledDistribution(testing.get(0), 10, 1, 5);
		//	System.out.println("0\t" + testProbabilities[0]);

			Util.writeFile( testID + ",", output_file, true);

			
			for(int i=0; i<testProbabilities.length; i++)
			{
				
				topicRatio[i]+=testProbabilities[i];
				float a=(float) topicRatio[i];
				Util.writeFile(df.format(testProbabilities[i]*500)+"," , output_file, true);
//				Util.writeFile(a+"," , output_file, true);

				if  ((i+1)==testProbabilities.length){
					Util.writeFile((df.format(testProbabilities[i]*250000))+",", output_file, true);
			//		Util.writeFile(TweetRiskCalculator.uniqueWords(userTweets)+",", output_file, true);
					Util.writeFile(toi.nameCount(toi.Tokens)+",", output_file, true);
					Util.writeFile(toi.locCount(toi.Tokens)+",", output_file, true);
					Util.writeFile(toi.timeCount(toi.Tokens)+",", output_file, true);
					Util.writeFile(toi.dateCount(toi.Tokens)+",", output_file, true);
					Util.writeFile(toi.moneyCount(toi.Tokens)+",", output_file, true);
					Util.writeFile(toi.percentageCount(toi.Tokens)+",", output_file, true);
					//excluding organization count improved accuracy in one case
					Util.writeFile(toi.orgCount(toi.Tokens)+",", output_file, true);//classname is for the score label
				//	Util.writeFile(toi.orgCount(toi.Tokens)+","+classname+"\n" , output_file, true);//classname is for the score label
					//quotecount is helpful in the last case, but decreased accuracy in other cases
					Util.writeFile(TweetRiskCalculator.privacyDictionaryIndex(userTweets)+",", output_file, true);
					Util.writeFile(TweetRiskCalculator.countQuotesIndex(userTweets) + "," 
					+ TweetRiskCalculator.countRTIndex(userTweets) + ","+
					TweetRiskCalculator.counthttpIndex(userTweets) + ","+
					TweetRiskCalculator.countHandleIndex(userTweets) + ","+
					TweetRiskCalculator.countATIndex(userTweets) + ","+
					Main.privatecounter_sentiment(userTweets)*10 + ","+
					Main.notprivatecounter_sentiment(userTweets)*10 + ","

							 +classname+"\n" , output_file, true);

				}

			}
	   	}
	   public static float userTopicSensitivityIndex(String topics_inferrred) throws NumberFormatException, IOException
	   {
		   //200 hundred topics, the first one is not read.
		   int [] topicSensitivity = {0, 1, 1, 1, 1,1, 1, 1, 1, 1,1, 1, 1, 1, 1,1, 1, 1, 1, 1, 1,
				   1, 1, 1, 1,1, 1, 1, 1, 1,  1, 1, 1, 1, 1,1, 1, 1, 1, 1, 1, 1, 1, 1, 1,1, 1, 1, 1, 1,
				   1, 1, 1, 1, 1,1, 1, 1, 1, 1, 1, 1, 1, 1, 1,1, 1, 1, 1, 1, 1, 1, 1, 1, 1,1, 1, 1, 1, 1,
				   1, 1, 1, 1, 1,1, 1, 1, 1, 1, 1, 1, 1, 1, 1,1, 1, 1, 1, 1,
				   1, 1, 1, 1, 1,1, 1, 1, 1, 1, 1, 1, 1, 1, 1,1, 1, 1, 1, 1, 1, 1, 1, 1, 1,1, 1, 1, 1, 1,
				   1, 1, 1, 1, 1,1, 1, 1, 1, 1, 1, 1, 1, 1, 1,1, 1, 1, 1, 1, 1, 1, 1, 1, 1,1, 1, 1, 1, 1,
				   1, 1, 1, 1, 1,1, 1, 1, 1, 1, 1, 1, 1, 1, 1,1, 1, 1, 1, 1, 1, 1, 1, 1, 1,1, 1, 1, 1, 1,
				   1, 1, 1, 1, 1,1, 1, 1, 1, 1};
		   
	//	   System.out.println(topicSensitivity[199]);
		   
		   String csvFile = topics_inferrred;
			BufferedReader br = null;
			String line = "";
			String cvsSplitBy = ",";
			//number of topics
		 float topicLength = 200;
		 float topicIndex = 0;
		 
				br = new BufferedReader(new FileReader(csvFile));
				for (int i=0; i < topicLength; i++)
				{ line = br.readLine();
					
				        // use comma as separator
					String[] probability = line.split(cvsSplitBy);
					 
					System.out.println("Topic number is " + probability[0] 
		                                 + " , probability=" + probability[1]);
					if (Float.parseFloat(probability[1]) > 0.1)
					{	
					//System.out.println(Float.parseFloat(probability[1]));
					topicIndex += Float.parseFloat(probability[1])* topicSensitivity[i];}
		   
	   }
				System.out.println(topicIndex);
				return topicIndex;
}}
	











