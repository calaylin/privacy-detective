package upa;

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


/**
 * @author gavin
 * TODO need to stem,
 */
public class InputInferencer {
	public static void main(String[] args) throws FileNotFoundException, IOException, ClassNotFoundException {

		String topicModelFile = "models/twitter_topicModel_Feb2_200.model";
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
		InputInferencer obj = new InputInferencer();
		String userTweets = obj.readFile(input_filename);						  
		testing.addThruPipe(new Instance( userTweets, null, "test instance", null));

		int ntopic = topicModel.getNumTopics();
		double[] topicRatio = new double[ntopic]; //number of users have topic i as top topic


		System.out.println("Predictions:");
		TopicInferencer inferencer = topicModel.getInferencer();
		double[] testProbabilities = inferencer.getSampledDistribution(testing.get(0), 10, 1, 5);
	//	System.out.println("0\t" + testProbabilities[0]);

	//	inferencer.writeInferredDistributions(instances, new File("docTopicsFile.txt"), 50, 20, 1,0.01, 100);
		
		
		//Arrays.s.stestProbabilities
		
		for(int i=1; i<testProbabilities.length; i++)
		{
			
			topicRatio[i]+=testProbabilities[i];
		}
		
		TopicModel_Aylin obj1 =new TopicModel_Aylin();
		obj1.printTopics(topicModel,  "docTopicsFile_test.txt", topicRatio);





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
	











