import cc.mallet.util.*;
import cc.mallet.types.*;
import cc.mallet.pipe.*;
import cc.mallet.pipe.iterator.*;
import cc.mallet.topics.*;

import java.text.DecimalFormat;
import java.util.*;
import java.util.regex.*;
import java.io.*;

public class TopicModelWriter {

    public static void main(String[] args) throws Exception {

        //input file
    	String input ="converted_alltweets.txt";
        String inputFileName = "_UserModels/"+ input;


        // Begin by importing documents from text to feature sequences
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
        InstanceList testInstances = new InstanceList (instances.getPipe());

        Reader insfileReader = new InputStreamReader(new FileInputStream(new File(inputFileName)), "UTF-8");
        //Reader testfileReader = new InputStreamReader(new FileInputStream(new File(args[1])), "UTF-8");

        instances.addThruPipe(new CsvIterator (insfileReader, Pattern.compile("^(\\S*)[\\s,]*(\\S*)[\\s,]*(.*)$"),
                                               3, 2, 1)); // data, label, name fields
        //testInstances.addThruPipe(new CsvIterator (testfileReader, Pattern.compile("^(\\S*)[\\s,]*(\\S*)[\\s,]*(.*)$"),
        //           3, 2, 1)); // data, label, name fields

        // Create a model with 100 topics, alpha_t = 0.01, beta_w = 0.01
        //  Note that the first parameter is passed as the sum over topics, while
        //  the second is
        int numTopics = 100;
        int topWordsNum =25;
        String dataName= input;

        //generate model path
        String modelPath="topic_model/" + dataName + "_" + Integer.toString(numTopics) +".model";
        String topWordPath=dataName +"_"+Integer.toString(numTopics)+"topics_top"+ Integer.toString(topWordsNum)+"word.csv" ;
        String topicDistrPath=dataName +"_"+Integer.toString(numTopics)+"t_dt.csv" ;

        ParallelTopicModel model = new ParallelTopicModel(numTopics, (double)50.0/numTopics , 0.01);
//        ParallelTopicModel model = new ParallelTopicModel(numTopics, 1.0 , 0.01);

        //add training data into model
        model.addInstances(instances);

        // Use two parallel samplers, which each look at one half the corpus and combine
        //  statistics after every iteration.
        model.setNumThreads(2);

        // Run the model for 50 iterations and stop (this is for testing only,
        //  for real applications, use 1000 to 2000 iterations)
        model.setNumIterations(1000);
        model.estimate();

        
        
        //write model to file
        try{
            model.write(new File(modelPath));
        }catch(Exception e){
            e.printStackTrace();
        }

        //get top words form model
        try{
              // Create file
              FileWriter fstream = new FileWriter(topWordPath);
              BufferedWriter out = new BufferedWriter(fstream);

              ArrayList<TreeSet<IDSorter>> topicSortedWords = model.getSortedWords();
              Alphabet dataAlphabet = instances.getDataAlphabet();

              // Show top 5 words in topics with proportions for the first document
              for (int topic = 0; topic < model.numTopics; topic++) {
                    Iterator<IDSorter> iterator = topicSortedWords.get(topic).iterator();

                    int rank = 0;
                    while (iterator.hasNext() && rank < topWordsNum) {
                        IDSorter idCountPair = iterator.next();
                        out.write(topic+"," + dataAlphabet.lookupObject(idCountPair.getID()) + ","
                        +idCountPair.getWeight() +"\n");
                        rank++;

                    }
                }
              //Close the output stream
              out.close();


        }catch (Exception e){//Catch exception if any
              System.err.println("Error: " + e.getMessage());
        }




        //get topic distribution for train data
        try{
              // Create file
              FileWriter fstream = new FileWriter(topicDistrPath);
              BufferedWriter out = new BufferedWriter(fstream);

              double[] topicsProb;

              for(int i=0; i < instances.size(); i++){

                  topicsProb = model.getTopicProbabilities(i);
                  out.write((String)instances.get(i).getName() + ",");

                  for(int j=0; j < topicsProb.length ; j++){
                      out.write(topicsProb[j] + ",");
                  }
                  out.write("\n");

              }
              //Close the output stream
              out.close();


        }catch (Exception e){//Catch exception if any
              System.err.println("Error: " + e.getMessage());
        }
        
        
/*        //run inference, get topic distribution for testing data
        try{
            // Create file
            FileWriter fstream = new FileWriter("plain_user_text/test/0.txt");
            BufferedWriter out = new BufferedWriter(fstream);

            TopicInferencer inferencer = model.getInferencer();
            double[] topicsProb;

            for(int i=0; i < testInstances.size(); i++){

                topicsProb = inferencer.getSampledDistribution(testInstances.get(i), 100, 1, 20);
                out.write((String)testInstances.get(i).getName() + ",");

                for(int j=0; j < topicsProb.length ; j++){
                              out.write(topicsProb[j] + ",");
                          }
                          out.write("\n");

                      }
                      //Close the output stream
                      out.close();


                }catch (Exception e){//Catch exception if any
                      System.err.println("Error: " + e.getMessage());
                }*/
        
    }

}