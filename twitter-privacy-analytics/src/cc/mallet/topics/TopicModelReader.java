package cc.mallet.topics;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.regex.Pattern;

import cc.mallet.pipe.CharSequence2TokenSequence;
import cc.mallet.pipe.Pipe;
import cc.mallet.pipe.SerialPipes;
import cc.mallet.pipe.TokenSequence2FeatureSequence;
import cc.mallet.pipe.TokenSequenceRemoveStopwords;
import cc.mallet.pipe.iterator.CsvIterator;
import cc.mallet.topics.ParallelTopicModel;
import cc.mallet.topics.TopicInferencer;
import cc.mallet.topics.TopicModelDiagnostics;
import cc.mallet.types.Alphabet;
import cc.mallet.types.IDSorter;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;


public class TopicModelReader {

    private ParallelTopicModel model;
    //private TopicModelDiagnostics diagnostic;

    public TopicModelReader(String modelPath, int numTopWords){

        try {
            //read model
            model = ParallelTopicModel.read(new File(modelPath));
            //diagnostic = new TopicModelDiagnostics(model, numTopWords);

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    public double[][] getTopicSimilarity(){

        int numTopics=model.numTopics;
        int numTypes=model.numTypes;
        double beta = model.beta;
        double betaSum = model.betaSum;
        int[] tokenPerTopic =model.tokensPerTopic;
        int[][] typeTopicCounts = model.typeTopicCounts;

        int[][] typeTopicMatrix = new int[numTypes][numTopics];

        double[][] similarity = new double[numTopics][numTopics];


        //generate typeToipcMatrix
        for(int type=0 ; type<numTypes ; type++){

            int[] currentTypeTopicCounts = typeTopicCounts[type];
            int currentTopic;
            int currentValue;

            for(int index =0 ; index < currentTypeTopicCounts.length ; index++){

                currentTopic = currentTypeTopicCounts[index] & model.topicMask;
                currentValue = currentTypeTopicCounts[index] >> model.topicBits;

                typeTopicMatrix[type][currentTopic] = currentValue;
            }

        }


        //get similarity
        for(int i =0 ; i < numTopics ; i++){
             for(int j=i+1 ; j< numTopics ; j++){

                 double sumLogDiff=0.0;
                 double denum1 = tokenPerTopic[i]+betaSum;
                 double denum2 = tokenPerTopic[j]+betaSum;
                 double num1, num2;
                 double sim;
                 double sumDot=0.0;
                 double prob1, prob2, sumSq1=0.0, sumSq2=0.0;
                 double sum =0.0;

                 for(int k=0 ; k < numTypes ; k++){
                     num1 = typeTopicMatrix[k][i]+beta;
                     num2 = typeTopicMatrix[k][j]+beta;
                     /*
                     if(num1>0.0 && num2>0.0 ){
                         //calculate disimilarity
                         sumLogDiff+= Math.abs(Math.log(num1)-Math.log(denum1)-Math.log(num2)+Math.log(denum2));
                     }

                     prob1 = (num1/denum1);
                     prob2 = (num2/denum2);
                     //try cosine similarity
                     sumDot += (prob1 * prob2);
                     sumSq1 += (prob1 * prob1);
                     sumSq2 += (prob2 * prob2);
                     */

                     //euclean distance
                     prob1 = (num1/denum1);
                     prob2 = (num2/denum2);
                     sum+= Math.pow((prob1-prob2),2);

                 }//end k for

                 //get similarity
                 //sim = 1.0/(1.0+(sumLogDiff/numTypes));
                 //sim = sumLogDiff;
                 //sim =sumDot/ (Math.sqrt(sumSq1)*Math.sqrt(sumSq2)) ;
                 sim = Math.sqrt(sum);

                 //save result
                 similarity[i][j]=sim;
                 similarity[j][i]=sim;

             }//end j for
         }//end i for

         //for i==j
         for(int i =0 ; i < numTopics ; i++){
             //similarity[i][i]=1.0;
             similarity[i][i]=0.0;
         }

         return similarity;
    }

    public static void printMatrix(double[][] matrix){

         for(int i = 0 ; i<matrix.length ; i++){
            for(int j=0 ; j<matrix[i].length ; j++){
                System.out.print(String.format("%2.2f ", matrix[i][j]));
            }
            System.out.print("\n");
         }
    }

    public static void exportMatrix(double[][] matrix, String outFileName)throws Exception{

        FileWriter fstream = new FileWriter(outFileName);
        BufferedWriter out = new BufferedWriter(fstream);

        for(int i=0 ; i<matrix.length; i++){
            for(int j=0 ; j <matrix[i].length ; j++){
                out.append(matrix[i][j] + ",");
            }
            out.append("\n");
        }
        out.close();
    }

    public void exportTopicSimilarty(String fileName) throws Exception{

        exportMatrix(this.getTopicSimilarity(), fileName);
        System.out.println("export file to" + fileName);
    }
    public void printTopicSimilarity(){

         printMatrix(this.getTopicSimilarity());
     }

    public void getTopWord(String outPath, int K){

        assert K>0 : "K should be greater than 0." ;
        //get top words form model
                try{
                      // Create file
                      FileWriter fstream = new FileWriter(outPath);
                      BufferedWriter out = new BufferedWriter(fstream);

                      ArrayList<TreeSet<IDSorter>> topicSortedWords = model.getSortedWords();
                      Alphabet dataAlphabet = model.getAlphabet();

                      // Show top 5 words in topics with proportions for the first document
                      for (int topic = 0; topic < model.numTopics; topic++) {
                            Iterator<IDSorter> iterator = topicSortedWords.get(topic).iterator();

                            int rank = 0;
                            while (iterator.hasNext() && rank < K) {
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
    }

    //inference
    public void getTopicDistribution(String insFile, String outPath){

        //get topic distribution for train data
                try{
                    // Begin by importing documents from text to feature sequences
                    ArrayList<Pipe> pipeList = new ArrayList<Pipe>();

                    // Pipes: lowercase, tokenize, remove stopwords, map to features
                    //pipeList.add( new CharSequenceLowercase() );
                    pipeList.add( new CharSequence2TokenSequence(Pattern.compile("\\p{L}[\\p{L}\\p{P}]+\\p{L}")) );
                    pipeList.add( new TokenSequenceRemoveStopwords(new File("stoplist/en2.txt"), "UTF-8", false, false, false) );
                    pipeList.add( new TokenSequenceRemoveStopwords(new File("stoplist/fi.txt"), "UTF-8", false, false, false) );
                    pipeList.add( new TokenSequenceRemoveStopwords(new File("stoplist/fr.txt"), "UTF-8", false, false, false) );
                    pipeList.add( new TokenSequenceRemoveStopwords(new File("stoplist/de.txt"), "UTF-8", false, false, false) );
                    pipeList.add( new TokenSequence2FeatureSequence() );

                    InstanceList instances = new InstanceList (new SerialPipes(pipeList));
                    Reader insfileReader = new InputStreamReader(new FileInputStream(new File(insFile)), "UTF-8");
                    instances.addThruPipe(new CsvIterator (insfileReader, Pattern.compile("^(\\S*)[\\s,]*(\\S*)[\\s,]*(.*)$"),
                               3, 2, 1)); // data, label, name fields

                    // Create file
                      FileWriter fstream = new FileWriter(outPath);
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

    }
    //inference
    public double[] Inferencer(Instance ins){
        double[] topicsProb;
        TopicInferencer inferencer = model.getInferencer();
        topicsProb = inferencer.getSampledDistribution(ins, 100, 1, 20);

        return topicsProb;
    }

}