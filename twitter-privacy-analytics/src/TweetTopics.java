
import cc. mallet.util.*;
import cc.mallet.types.*;
import cc.mallet.pipe.*;
import cc.mallet.pipe.iterator.*;
import cc.mallet.topics.*;

import java.util.*;
import java.util.logging.Logger;
import java.util.regex.*;
import java.io.*;
import java.util.HashMap;
import java.util.Map;


public class TweetTopics {
	
	public static void main(String[] args) throws Exception, ArrayIndexOutOfBoundsException {

		TweetTopics test = new TweetTopics();
		String tweetText = "sick family hiv hello how are you what are you doing this is not pdlkn";
		int a;
		a = test.compareTopics(tweetText);
//		System.out.println(a);
		
	}
	public int compareTopics(String tweet ){
		
//    public int compareTopics( UPTweet tweet ) throws SQLException {


		// Begin by importing documents from text to feature sequences
		ArrayList<Pipe> pipeList = new ArrayList<Pipe>();

		// Pipes: lowercase, tokenize, remove stopwords, map to features
		pipeList.add( new CharSequenceLowercase() );
		pipeList.add( new CharSequence2TokenSequence(Pattern.compile("\\p{L}[\\p{L}\\p{P}]+\\p{L}")) );
		pipeList.add( new TokenSequenceRemoveStopwords(new File("stoplists/en.txt"), "UTF-8", false, false, false) );
		pipeList.add( new TokenSequence2FeatureSequence() );

		InstanceList instances = new InstanceList (new SerialPipes(pipeList));
//		 silenceLogger( MalletLogger.getLogger(ParallelTopicModel.class.getName() ) );

//		String tweetText = "I'm at Gecko Books & Comics (Honolulu, HI) wondering around the store. http://t.co/vipNhlbQBg";
		
		Util.writeFile( tweet, "topic_test/documents/"+"twitter_small.txt", false);

		 File dir = new File("topic_test/");
//      String tweetText = tweet.getTweet();

		
				int i=0;
				if(dir.exists())
				{
					File[] allUsers = dir.listFiles();
					for(File aUser: allUsers)
					{
						if(!aUser.isDirectory())
							continue;
						File[] allText = aUser.listFiles();
						//File[] allText = dir.listFiles();
						
						//if(allText.length<1000) continue;
						if (i==0);// System.out.println(aUser);
						i++;
						for(File aText: allText){
						 if(i==1){
						//	System.out.println(aText);
							Reader fileReader = null;
							try {
								fileReader = new InputStreamReader(new FileInputStream(aText), "UTF-8");
							} catch (UnsupportedEncodingException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (FileNotFoundException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							instances.addThruPipe(new CsvIterator (fileReader, Pattern.compile("^(\\S*)[\\s,]*(\\S*)[\\s,]*(.*)$"),
															   3, 2, 1)); // data, label, name fields
						 }	}

						
					}
				}
		// Create a model with 100 topics, alpha_t = 0.01, beta_w = 0.01
		//  Note that the first parameter is passed as the sum over topics, while
		//  the second is 
				
		//numTopics is the number of topics you want to extract at the end, which is set to 5 now.
		//the default for numtopics was 20		
		int numTopics = 5;
		
	
		
		
		
		ParallelTopicModel model = new ParallelTopicModel(numTopics, 1.0, 0.01);

		model.addInstances(instances);

		model.setOptimizeInterval(20);
		// Use two parallel samplers, which each look at one half the corpus and combine
		//  statistics after every iteration.
		model.setNumThreads(2);

		// Run the model for 50 iterations and stop (this is for testing only, 
		//  for real applications, use 1000 to 2000 iterations)
		model.setNumIterations(50);
		try {
			model.estimate();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Show the words and topics in the first instance

		// The data alphabet maps word IDs to strings
		Alphabet dataAlphabet = instances.getDataAlphabet();
		
		FeatureSequence tokens = (FeatureSequence) model.getData().get(0).instance.getData();
		LabelSequence topics = model.getData().get(0).topicSequence;
		
		Formatter out = new Formatter(new StringBuilder(), Locale.US);
		for (int position = 0; position < tokens.getLength(); position++) {
			out.format("%s-%d ", dataAlphabet.lookupObject(tokens.getIndexAtPosition(position)), topics.getIndexAtPosition(position));
		}
		//System.out.println(out);
		
		// Estimate the topic distribution of the first instance, 
		//  given the current Gibbs state.
		double[] topicDistribution = model.getTopicProbabilities(0);
		
		//output numtopics with topics
		//instances.get(3).getData() is the 3rd document in the test file
	//	System.out.println(instances.get(1).getData()+" "+instances.get(0).getName());

		// Get an array of sorted sets of word ID/count pairs
		ArrayList<TreeSet<IDSorter>> topicSortedWords = model.getSortedWords();
		String a = "";
		int topic_length = 30; 
//		int total_words = numTopics*topic_length;
//		String[] arr = new String [total_words];
		String[] arr = new String [topic_length];

		int j=0;					

		// Show top 5 words in topics with proportions for the first document
		for (int topic = 0; topic < numTopics; topic++) {
			Iterator<IDSorter> iterator = topicSortedWords.get(topic).iterator();
			
			out = new Formatter(new StringBuilder(), Locale.US);
			out.format("%d\t%.3f\t", topic, topicDistribution[topic]);
			int rank = 0;
			//here you can specify how many words you want in 1 topic, right now it is set to 2.
			//the default was 5
			while (iterator.hasNext() && rank < topic_length) {
				IDSorter idCountPair = iterator.next();
				out.format("%s (%.0f) ", dataAlphabet.lookupObject(idCountPair.getID()), idCountPair.getWeight());
				rank++;
			//	String topic_names = dataAlphabet.lookupObject(idCountPair.getID()).toString();
				String b = dataAlphabet.lookupObject(idCountPair.getID()).toString();
				a = a + b + " ";				
			}
			//System.out.println(out);
			//System.out.println(a);
		}
		// The topic words are all elements of a string array
		String[] all_topics = a.split("\\s+");
		//System.out.println(all_topics[4]);
		
		
		Map<String, String> current_topics = new HashMap<String, String>();
		//The input text should be longer than the total_words.  Tweak parameters according to input.
//		for (int k=0; k < total_words; k++)
//		System.out.println(all_topics.length);

//		for (int k=0; k < topic_length; k++)
		for (int k=0; k < all_topics.length; k++)

		
		{
		current_topics.put(all_topics[k], " ");
		}
		
		for (Map.Entry<String, String> entry : current_topics.entrySet()) {
			 
			//System.out.println("A topic word in this file is '" + entry.getKey() + "'. ");
		}
		
		String csvFile = "src/privacy_ontology.csv";
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";
	 
		
	 
		Map<String, String> maps = new HashMap<String, String>();
		 
		try {
			br = new BufferedReader(new FileReader(csvFile));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			while ((line = br.readLine()) != null) {

				// use comma as separator
				String[] privacy_word = line.split(cvsSplitBy);
//		 Should I output both privacy topics or is one enough?  Do I even need to tell the privacy topic?
//			maps.put(privacy_word[0], privacy_word[1] + privacy_word[2]);
				maps.put(privacy_word[0], privacy_word[1]);

			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	 
			//loop map
/*			for (Map.Entry<String, String> entry : maps.entrySet()) {
	 
				System.out.println("Privacy phrase is '" + entry.getKey() + "' and the topic number is:"+ entry.getValue() + "]");
	 
			}*/
			
			
			
	 
			TweetTopics obj = new TweetTopics();
//		obj.compare(instances.get(2).getData(), maps);
//		obj.compare(current_topics, maps);

			
//		System.out.println(current_topics);


		String current_top = instances.get(0).getData().toString();	
		
/*		String new_topics = "";

		String[] top_words=current_top.split("\\s(?=\\()|(?<=\\:)\\s");
		System.out.println(top_words[1]);
		int i_end = top_words.length;
		System.out.println(instances.get(3).getDataAlphabet());
		 for(int in = 1; in < i_end; in = in + 2){
			new_topics.concat(top_words[in]);	     
			System.out.println(new_topics);
	
		 }*/
		 

//		obj.compare(top_words, maps);

		// Create a new instance with high probability of topic 0
		StringBuilder topicZeroText = new StringBuilder();
		Iterator<IDSorter> iterator = topicSortedWords.get(0).iterator();

		int rank = 0;
		while (iterator.hasNext() && rank < 5) {
			IDSorter idCountPair = iterator.next();
			topicZeroText.append(dataAlphabet.lookupObject(idCountPair.getID()) + " ");
			rank++;
		}

		// Create a new instance named "test instance" with empty target and source fields.
		InstanceList testing = new InstanceList(instances.getPipe());
		testing.addThruPipe(new Instance(topicZeroText.toString(), null, "test instance", null));

		TopicInferencer inferencer = model.getInferencer();
		double[] testProbabilities = inferencer.getSampledDistribution(testing.get(0), 10, 1, 5);
		//System.out.println("0\t" + testProbabilities[0]);
		
//		System.out.println(current_topics.size());
		
		
		int result_topic = obj.compare(current_topics, maps);
		System.out.println(result_topic + " is the topic detection result.");

		return result_topic;
		
	}


	
	
	
	public int compare(Map<String, String> map1,Map<String, String> map2)
	{
		int result = 0;
		String test = " ";
		String topic_no = "0";
		 for (Map.Entry<String, String> me : map2.entrySet())  
		        if(map1.containsKey(me.getKey()))  {
		          //  System.out.println("Found match -> " + me.getKey()); 	
		            test = me.getKey().toString();
		            topic_no = me.getValue();
		        }
		 
		 if (test == " "){
	//		 System.out.println("The input text does not contain any private information.");
         	result= 0;}
		 
         else{
         	result = 1;		 
		 int topic_number = Integer.parseInt(topic_no);
	//	 System.out.println(test);
		 	/*if (topic_number == 801){
		 		System.out.println("The input text contains private information and the privacy topic is 'intimacy'.");
		 	}
			if (topic_number == 811){
		 		System.out.println("The input text contains private information and the privacy topic is 'law'.");
		 	}
			if (topic_number == 821){
		 		System.out.println("The input text contains private information and the privacy topic is 'negative privacy'.");
		 	}
			if (topic_number == 831){
		 		System.out.println("The input text contains private information and the privacy topic is 'NormsRequisites'.");
		 	}
			if (topic_number == 841){
		 		System.out.println("The input text contains private information and the privacy topic is 'OpenVisible'.");
		 	}
			if (topic_number == 851){
		 		System.out.println("The input text contains private information and the privacy topic is 'OutcomeState'.");
		 	}
			if (topic_number == 861){
		 		System.out.println("The input text contains private information and the privacy topic is 'private secret'.");
		 	}
			if (topic_number == 871){
		 		System.out.println("The input text contains private information and the privacy topic is 'restriction'.");
		 	}
			if (topic_number == 881){
		 		System.out.println("The input text contains private information and the privacy topic is 'additional'.");
		 	}*/
         }
		 return result;
		 
	}

	

}