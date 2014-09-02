package upa;
import cc.mallet.util.*;
import cc.mallet.types.*;
import cc.mallet.pipe.*;
import cc.mallet.pipe.iterator.*;
import cc.mallet.topics.*;

import java.util.*;
import java.util.regex.*;
import java.io.*;

public class TopicModel_Aylin {

	/**
	 * find corpus size
	 * @param corpusDir: contains authorDir, each authorDir contains .txt files
	 * @return
	 */
	
	
	int getCorpusSize(InstanceList instances)
	{
		
	    int size = 0;
        for (Instance instance: instances) {
                if (! (instance.getData() instanceof FeatureSequence)) {
                        System.err.println("DocumentLengths is only applicable to FeatureSequence objects (use --keep-sequence when importing)");
                        System.exit(1);
                }
                
                FeatureSequence words = (FeatureSequence) instance.getData();
                size+=words.size();
        }
		return size;
	}
	
	/*
	 * 1. Set stoplist, stop words are the words that will be ignored with topic modeling
	 * 2. Set data
	 * 3. Split the data into train and test set
	 * 4. Run topic modeling on different number of topics on the training instances 
	 * and calculate perplexity of the model on the test instances
	 * 5. Take the model with lowest perplexity, this is the best model
	 * 6. Find topic ratio and save it
	 */
	public static void main(String[] args) throws Exception {

		TopicModel_Aylin tm = new TopicModel_Aylin();
		ArrayList<Pipe> pipeList = new ArrayList<Pipe>();       
 		pipeList.add( new CharSequenceLowercase() );
        pipeList.add( new CharSequence2TokenSequence(Pattern.compile("\\p{L}[\\p{L}\\p{P}]+\\p{L}")) );
		
		//1. Set stoplist, stop words are the words that will be ignored with topic modeling
		
		pipeList.add( new TokenSequenceRemoveStopwords(new File("stoplists/en.txt"), 
				"UTF-8", false, false, false) );
		
		pipeList.add( new TokenSequence2FeatureSequence() );

		InstanceList instances = new InstanceList (new SerialPipes(pipeList));
		InstanceList testInstances = new InstanceList (new SerialPipes(pipeList));

		//2. Set data
		String db = "";
		File dir = new File("/Users/Aylin/Documents/workspace/automated_tweet_detection_tests/convertedTweets/" + db);

		File dirTest = new File("/Users/Aylin/Documents/workspace/automated_tweet_detection_tests/plain_user_text/" + db);

				
		if(dir.exists())
		{
			File[] allUsers = dir.listFiles();
			for(File aUser: allUsers)
			{
				if(!aUser.isDirectory())
					continue;
				File[] allText = aUser.listFiles();
				
				for(File aText: allText){
					Reader fileReader = new InputStreamReader(new FileInputStream(aText), "UTF-8");
					instances.addThruPipe(new CsvIterator (fileReader, Pattern.compile("^(\\S*)[\\s,]*(\\S*)[\\s,]*(.*)$"),
													   3, 2, 1)); // data, label, name fields
				}

				
			}
		}
		
		
		if(dirTest.exists())
		{
			File[] allUsers = dirTest.listFiles();
			for(File aUser: allUsers)
			{
				if(!aUser.isDirectory())
					continue;
				File[] allText = aUser.listFiles();
				
				for(File aText: allText){
					Reader fileReader = new InputStreamReader(new FileInputStream(aText), "UTF-8");
					testInstances.addThruPipe(new CsvIterator (fileReader, Pattern.compile("^(\\S*)[\\s,]*(\\S*)[\\s,]*(.*)$"),
													   3, 2, 1)); // data, label, name fields
				}

				
			}
		}
		
		//3. Split the data into train and test set
		InstanceList[] instanceLists =
				instances.split(new Randoms(),
		//		new double[] {0.9, 0.1, 0.0});
			new double[] {1.0, 0.0, 0.0});

		InstanceList[] testInstanceLists =
				testInstances.split(new Randoms(),
		//		new double[] {0.9, 0.1, 0.0});
			new double[] {0.0, 1.0, 0.0});
		
		InstanceList trainingInstances = instanceLists[0];
		InstanceList testingInstances = testInstanceLists[1];
		
		int numTopics = 100;
		trainingInstances = instances;
//		
		System.out.println(trainingInstances.size());
		ParallelTopicModel bestModel = null;
		
		double minPerplexityScore = Double.MAX_VALUE;
		
//		4. Run topic modeling on different number of topics on the training instances 
//		 * and calculate perplexity of the model on the test instances
	//	for(numTopics = 25; numTopics<=200; numTopics+=25)

		{
			ParallelTopicModel model = new ParallelTopicModel(numTopics, 1.0, 0.01);
	
			model.addInstances(trainingInstances);
			model.setOptimizeInterval(20);

			// Use two parallel samplers, which each look at one half the corpus and combine
			//  statistics after every iteration.
			model.setNumThreads(2);
	
			// Run the model for 50 iterations and stop (this is for testing only, 
			//  for real applications, use 1000 to 2000 iterations)
		//	model.setNumIterations(1000);
			model.setNumIterations(50);

			model.estimate();
	
			//5. measure perplexity
			double perplexity = tm.getPerplexity(model, testingInstances);
			Util.writeFile(numTopics+"," + perplexity, "perplexity_"+dir.getName()+".csv", true);
			
			//save the model with lowest perplexity
			if(perplexity<minPerplexityScore)
			{
				bestModel = model;
				minPerplexityScore = perplexity;
				
			}
			
		}
		//save the model
		bestModel.printState(new File("model_"+dir.getName()));
		
        Alphabet dataAlphabet = instances.getDataAlphabet();
        FeatureSequence tokens = (FeatureSequence) bestModel.getData().get(0).instance.getData();
        LabelSequence topics = bestModel.getData().get(0).topicSequence;
        
        Formatter out = new Formatter(new StringBuilder(), Locale.US);
        for (int position = 0; position < tokens.getLength(); position++) {
            out.format("%s-%d ", dataAlphabet.lookupObject(tokens.getIndexAtPosition(position)), topics.getIndexAtPosition(position));
        }
        System.out.println(out);
        
        // Estimate the topic distribution of the first instance, 
        //  given the current Gibbs state.
        double[] topicDistribution = bestModel.getTopicProbabilities(1);

        // Get an array of sorted sets of word ID/count pairs
        ArrayList<TreeSet<IDSorter>> topicSortedWords = bestModel.getSortedWords();
        for (int topic = 0; topic < numTopics; topic++) {
            Iterator<IDSorter> iterator = topicSortedWords.get(topic).iterator();
            
            out = new Formatter(new StringBuilder(), Locale.US);
            out.format("%d\t%.3f\t", topic, topicDistribution[topic]);
            int rank = 0;
            while (iterator.hasNext() && rank < 9) {
                IDSorter idCountPair = iterator.next();
                out.format("%s (%.0f) ", dataAlphabet.lookupObject(idCountPair.getID()), idCountPair.getWeight());
                rank++;
            }
            System.out.println(out);
        }
      
//		File dirTest = new File("/Users/Aylin/Documents/workspace/automated_tweet_detection_tests/plain_user_text/" + db);
		//find topic ratio and save it 
		tm.findTopicRatio(instances,bestModel, db+"_topics_"+dir.getName()+"_text"+numTopics+".txt");
		//findTopicRatio(InstanceList instances, ParallelTopicModel model, String outputFilename)
		tm.findTopicRatioByUser(dirTest,bestModel,pipeList, db+"_topics_by_user_"+dir.getName()+"_text"+numTopics+".txt");
//findTopicRatioByUser(File userDir, ParallelTopicModel model, ArrayList<Pipe> pipeList, String outputFilename) throws UnsupportedEncodingException, FileNotFoundException {


  //      TopicInferencer inferencer = bestModel.getInferencer();

		
	}
	/**
	 * Measures perplexity of the model
	 * Lower perplexity is better.
	 * 
	 */
	private double getPerplexity(ParallelTopicModel model, InstanceList testingInstances) {
		
		MarginalProbEstimator evaluator = model.getProbEstimator();
		double logLikelihood = evaluator.evaluateLeftToRight(testingInstances, 10 , false, null);
		int nTokens = getCorpusSize(testingInstances);
//		System.out.println("corpus size testing instances "+getCorpusSize(testingInstances));
		System.out.println("corpus size testing instances "+testingInstances);
		double pp = Math.exp(-(logLikelihood/nTokens));
		return pp;
		
	}
	
	/**
	 * Print top words in each topic
	 * @param model
	 */
	public void printTopics(ParallelTopicModel model, String outputFilename, double[] topicRatio) {
	    
		Alphabet alphabet = model.getAlphabet();
		int topic = 0;
		String topics = "";
	    for (TreeSet<IDSorter> set : model.getSortedWords()) {
	      
	      System.out.println("TOPIC: "+topic);
	      int rank = 0;
	       topics+= topic+", "+topicRatio[topic]+", ";
	      for (IDSorter s : set) {
	    	if (rank>20) break;
	        //Util.writeFile(allLines, fileName, append)
	    	topics+=alphabet.lookupObject(s.getID())+" ";
	        rank++;
	        
	      }
	      topic++;
	      topics+="\n";
	    //  Util.writeFile(topics, outputFilename, true);
	      //System.out.println();
	    }
	    Util.writeUTF8(topics, outputFilename, true);
	  }
	

	/**
	 * Identify  topic ratio 
	 */
	 
	public void findTopicRatio(InstanceList instances, ParallelTopicModel model, String outputFilename) {
		
		TopicInferencer inferencer = model.getInferencer();
		
		int ntopic = model.getNumTopics();
		double[] topicRatio = new double[ntopic];
		
		for(Instance anInstance:instances)
		{
			double[] testProbabilities = inferencer.getSampledDistribution(anInstance, 10, 1, 5);
			
			int maxTopic = 0;
			double maxProb = testProbabilities[0];
			
			for(int i=0; i<testProbabilities.length; i++)
			{
				topicRatio[i]+=testProbabilities[i];
			}
			
			
			
		
			
			
			
			
			
		}
		
		for(int i=0; i<ntopic; i++)
			topicRatio[i] = topicRatio[i]/instances.size();

		//print
		printTopics(model,  outputFilename, topicRatio);
		
		
		
	}
	/**
	 * 
	 * @param instances
	 * @param model
	 * @param outputFilename
	 * @throws FileNotFoundException 
	 * @throws UnsupportedEncodingException 
	 */
public void findTopicRatioByUser(File userDir, ParallelTopicModel model, ArrayList<Pipe> pipeList, String outputFilename) throws UnsupportedEncodingException, FileNotFoundException {
		
	TopicInferencer inferencer = model.getInferencer();
	int ntopic = model.getNumTopics();
	double[] topicRatiobyUser = new double[ntopic]; //number of users have topic i as top topic
	
	//get all documents by one user
	if(userDir.exists())
	{
		File[] allUsers = userDir.listFiles();
		InstanceList instances = new InstanceList (new SerialPipes(pipeList));
		InstanceList testInstances = new InstanceList (new SerialPipes(pipeList));

		for(File aUser: allUsers)
		{
			if(!aUser.isDirectory())
				continue;
			File[] allText = aUser.listFiles();
			
			//if(allText.length<1000) continue;
			
			for(File aText: allText){
				//System.out.println();
				Reader fileReader = new InputStreamReader(new FileInputStream(aText), "UTF-8");
				instances.addThruPipe(new CsvIterator (fileReader, Pattern.compile("^(\\S*)[\\s,]*(\\S*)[\\s,]*(.*)$"),
												   3, 2, 1)); // data, label, name fields
			}
			double[] topicRatio = new double[ntopic];
			
			//SortedMap<Double,Integer> topicRatio = new TreeMap<Double, Integer>()
			for(Instance anInstance:instances)
			{
				double[] testProbabilities = inferencer.getSampledDistribution(anInstance, 10, 1, 5);
				
				//Arrays.s.stestProbabilities
				
				for(int i=1; i<testProbabilities.length; i++)
				{
					
					topicRatio[i]+=testProbabilities[i];
				}
				
			}
			int top = 0;
			double max = topicRatio[0];
			for(int i=1; i<ntopic; i++)
			{
				if(topicRatio[i]>max)
				{
					top = i;
					max = topicRatio[i];
				}
			}
			
			for(int i=1; i<ntopic; i++)
			{
				if(topicRatio[i]>max)
				{
					top = i;
					max = topicRatio[i];
				}
			}
			//Arrays.s
//			printTopics(model, userDir+"_topicRatio_by_user.csv", topicRatiobyUser);

			Util.writeFile(""+top, userDir+"_topicRatio_per_user.csv", true);
			
		}
		
	
		
		for(int i=0; i<ntopic; i++){
			topicRatiobyUser[i]= topicRatiobyUser[i]/allUsers.length;
			//output+=topicRatiobyUser[i]+",";
			
		}
		printTopics(model, userDir+"_topicRatio_by_user.csv", topicRatiobyUser);
	}
	//infer topic 
		
		
		
		
		
		//print
		//printTopics(model,  outputFilename, topicRatio);
		
		
		
	}
}