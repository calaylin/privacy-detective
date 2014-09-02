import java.io.*;
import java.sql.SQLException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Formatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;

import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.InvalidFormatException;
import opennlp.tools.util.Span;
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

import org.apache.commons.*;
import org.apache.commons.lang3.StringUtils;


/**
 * Calculate and store the privacy risk of a given user .
 *
 * @author Aylin Caliskan-Islam (ac993@drexel.edu)
 */


//public int queryNumTweetsForUser( UPUser u ) throws SQLException {

public class TweetRiskCalculator {
	

//    TwitterDatabase db;
    public String Tokens[];


    /**
     * Initialization of the model, etc.
     *
     * @param db - The database connection
     */
    public TweetRiskCalculator( ) {
   //     this.db = db;

        //AYLIN PUT YOUR CODE HERE! TODO
    	
    }
    
    public static void main(String[] args) throws Exception {


		String input = "RTtest.txt";
		test_main obj = new test_main();
		String userTweets_clear = obj.readFile(input);
		
		userTweets_clear = tweetCleaner.replaceURLs(userTweets_clear); //conversion4
		System.out.println(userTweets_clear);
    	
  //  	String userTweets_clear = "blaal\n"
    //			+ "RT This isRT  a troublert rt choice I havea choice the ability a choice a choiceto control.";
    //	int count = StringUtils.countMatches(userTweets_clear, "a choice ");

//		System.out.println(TweetRiskCalculator.privacyDictionaryIndex(userTweets_clear));
		System.out.println(TweetRiskCalculator.countATIndex(userTweets_clear));
	
    }
    
    
    public float[] calcUserPrivacyScore(String userTweets_clear ) throws IOException, ClassNotFoundException   {
    	float ner_index=0;
    	float quotes =0;
    	float tweet_length =0;
    	float [] finalUserPrivacyIndex;
    	finalUserPrivacyIndex = new float[8];
    	/*        String tweet = "hello" + "\" "+ "\""+ "\""+ "\""+ "\""+ "\""+ "\""+ "\"";
        TweetRiskCalculator obj1 = new TweetRiskCalculator();
        System.out.println(obj1.wordCountIndex(tweet));*/
    	
//		TweetRiskCalculator testObj = new TweetRiskCalculator();
//		String input_filename_clear = "/Users/Aylin/Documents/workspace/automated_tweet_detection_tests/plain_user_text/test/0.txt";

		String output_filename = "topics_inferred.txt";
		test_main obj = new test_main();
//		String userTweets_clear = obj.readFile(input_filename_clear);
		


		//NER score
	//	TweetNER obj1 = new TweetNER();
//		System.out.println(obj1.calculateNerIndex(obj1, userTweets_clear));
	//	ner_index = obj1.calculateNerIndex(obj1, userTweets_clear);
		//Quote score
//		quotes = TweetRiskCalculator.countQuotesIndex(userTweets_clear)
	//			/ TweetRiskCalculator.wordCountIndex(userTweets_clear);
		//Tweet length score
	//	 tweet_length = TweetRiskCalculator.wordCountIndex(userTweets_clear)/500;
		 
		 //Topic Modeling Score
		//remove all newlines so that topic modeling takes it as one document.
		TweetRiskCalculator.topicModelInferencerIndex( userTweets_clear, output_filename);

		
		//final score calculation function
		  finalUserPrivacyIndex = TweetRiskCalculator.userTopicSensitivityIndex(output_filename) ;
	//	 finalUserPrivacyIndex = ner_index - quotes + tweet_length + TweetRiskCalculator.userTopicSensitivityIndex(output_filename);
    //	System.out.println(finalUserPrivacyIndex);
    	return finalUserPrivacyIndex;
    }

    /**
     * Calculate the privacy score for a single tweet, and update the DB
     *
     * @param tweet - The Tweet to caclulate the score for
     * @throws SQLException - For database errors
     * @return The privacy score for the tweet
     * @throws FileNotFoundException 
     */
    public int calcTweetPrivacyScore( String tweet) throws FileNotFoundException {
//tweet = plain text tweet
//tweet_clustered = converted tweet    	
    	TweetNER toi = new TweetNER();
        int score = 0;
    	int ner_score = toi.calculateNerIndex(toi,tweet);  //non-converted tweet NER   	
        float quote_score = countQuotesIndex(tweet);
    	int topic_score = 0;
        //AYLIN PUT YOUR CODE HERE! TODO

        /* To get the text of the tweet */
        String tweetText = "";

        /* To get the text as converted by Jordan */
        //String convertedTweet = db.queryConvertedTweet( tweet, LangTweetConverter.MY_TYPE );

        /* If it is a foreign language tweet, call this - I will implement 
           this soon*/
        // db.setTweetNotEnglish( tweet );
     
        StringTweetCleaner obj = new StringTweetCleaner();
        String tweet_clustered = obj.stringCleaner(tweet);
        
            
        
        TweetTopics test = new TweetTopics();
        topic_score = test.compareTopics(tweet_clustered); //clustered tweet for topic modeling

        
        if ((ner_score!=0 | topic_score!=0 ) & !tweet.contains("\"")  )
        	{score = 1;}

        /* If you want to insert a topic into the DB */
        // String topic; boolean privacyRelated
        //db.insertTopic( topic, privacyRelated ); // 
        //db.insertTopicTweetAssociation( tweet, topic );

        
        
        /* Insert the score into the DB */
        return score;
    }
    
    
   public static int countQuotesIndex (String userTweets){
   int quote_score =0;
   for (Character c: userTweets.toCharArray()) {
//       if (c.equals('\"')) {
       if (c.equals('\"')) {

       	quote_score++;
       }
   }

   return quote_score;

}
   
   public static int countRTIndex (String userTweets){
	   int counter = 0;

		   String str = "\n"+"RT ";
		   counter = StringUtils.countMatches(userTweets, str);
	//   if(userTweets.toLowerCase().contains(str2.toLowerCase()) == true)
	   if(userTweets.subSequence(0, 3).equals("RT "))
		   counter++;
	   return counter;
	   
	   }
	   
	   
   public static int counthttpIndex (String userTweets){
	   int counter = 0;

		   String str = "http";
		   counter = StringUtils.countMatches(userTweets.toLowerCase(), str);
	//   if(userTweets.toLowerCase().contains(str2.toLowerCase()) == true)
	   return counter;
	   
	   }
   
   public static int countHandleIndex (String userTweets){
	   int counter = 0;

		   String str = "#";
		   counter = StringUtils.countMatches(userTweets.toLowerCase(), str);
	//   if(userTweets.toLowerCase().contains(str2.toLowerCase()) == true)
	   return counter;
	   
	   }
   
   public static int countATIndex (String userTweets){
	   int counter = 0;

		   String str = "@";
		   counter = StringUtils.countMatches(userTweets.toLowerCase(), str);
	//   if(userTweets.toLowerCase().contains(str2.toLowerCase()) == true)
	   return counter;
	   
	   }
   
  public static String[]  uniqueWords (String userTweets){
 // String[] uniqueWords ={""};

//  String[] words = userTweets.split("[!-~]* ");
	   String[] words = userTweets.split( "\\s+");

  Set<String> uniqueWords = new HashSet<String>();

  for (String word : words) {
      uniqueWords.add(word);
  }
  words = uniqueWords.toArray(new String[0]);
  return words;

}
  
  
   public static float wordCountIndex(String userTweets){
	   //returns the word count separated by spaces
	   //will be used instead of the number of tweets
	    if (userTweets == null)
	       return 0;
	    return userTweets.trim().split("\\s+").length;
	}
  
   public float calculateNerIndex (TweetNER toi, String userTweets)
   
   {
		
 //  	 int name_count = toi.nameCount(toi.Tokens);
        int loc_count = toi.locCount(toi.Tokens);
        int time_count = toi.timeCount(toi.Tokens);
  //      int date_count = toi.dateCount(toi.Tokens);
        int money_count = toi.moneyCount(toi.Tokens);
  //      int percentage_count = toi.percentageCount(toi.Tokens);
  //      int org_count = toi.orgCount(toi.Tokens);
        
        
  //      return (name_count+ loc_count+time_count+date_count+money_count+percentage_count+org_count);
        return (loc_count);

        //over number of sentences?
   	
   }
   
   
   public static int privacyDictionaryIndexOld (String userTweets)
   {
	   //returns the number of matches from the privacy dictionary
	   String [] privacy_words = {
			   "choice",	"access",	"accessed",	"accessible",	"advice",	"advice",	"affection",	
			   "afraid",	"aids",	"alone",	"amendment",	"anonymity",	"anonymous",	"anonymously",	
			   "attitudes",	"avoided",	"avoiding",	"behaving",	"behavior",	"behaviour",	"being watched",	
			   "beliefs",	"blacklisted",	"blacklisting",	"blackmail",	"block",	"blocked",	"blocking",	
			   "boundaries",	"boundary",	"breach",	"breached",	"breaches",	"breaching",	"bullied",	"bully",	
			   "bullying",	"cancer",	"care",	"care",	"chlamydia",	"choices",	"choose",	"chose",	"close",	
			   "closely ",	"closeness",	"coerce",	"collected",	"comment",	"commenting",	"comments",	
			   "commissioner",	"community",	"conceal",	"concealed",	"concealment",	"conceals",	"concern",	
			   "concerned",	"concerns",	"confide",	"confided",	"confidence",	"confidential",	"confidentiality",	
			   "consent",	"consented",	"consenting",	"control",	"controls",	"conversation",	"criminal",	"danger",	
			   "dangerous",	"dangers",	"data",	"database",	"deceit",	"deceitful",	"declaration",	"delete",	
			   "deleted",	"deleting",	"democratic ",	"depressed",	"details",	"dignified",	"dignity",	
			   "directive",	"disclose",	"disclosed",	"disclosing",	"disclosure",	"discreet",	"discreetly",	
			   "discretion",	"distress",	"distressed",	"distressing",	"disturb",	"disturbed",	"disturbing",	
			   "drunk",	"embarrass",	"embarrassed",	"embarrassing",	"embarrassment",	"emotional",	"emotions",	
			   "encourage",	"encouragement",	"endanger",	"endangering",	"ensure",	"exclude",	"excluded",	"excluding",	
			   "expectation",	"expose",	"exposed",	"exposes",	"exposing",	"exposure",	"fair",	"fairness",	"fairness",	
			   "family",	"fear",	"feel",	"feelings ",	"financial",	"follow ",	"force",	"forced",	"forcing",	
			   "freedom",	"friend",	"friend",	"friendship",	"gay ",	"gonorrhea",	"gossip",	"grounds",	"group",	
			   "groups",	"guidance",	"hacking",	"hangover",	"harassed",	"harassment",	"health",	"help",	"hepatitis",	
			   "herpes",	"hid",	"hidden",	"hide",	"hiding",	"history",	"HI",	"hiv",	"HIV",	"home",	"home",	
			   "homosexual",	"Honolulu",	"hpv",	"HPV",	"identity",	"illegal",	"indignity",	"individual ",	
			   "individual's ",	"individuals",	"infection",	"infringe",	"infringed",	"infringement",	"infringements",	
			   "injunction",	"inspecting",	"interfere",	"interfered",	"interference",	"interfering",	"intimate",	
			   "intoxicated",	"intrude",	"intruded",	"intrusion",	"intrusive",	"invade",	"invaded",	"invasion",	
			   "invasive",	"isolated",	"isolating",	"judge",	"judged",	"judgemental",	"judgmental",	"kin",	
			   "knock",	"knocked",	"knocking",	"knowledge",	"lack",	"land",	"law",	"lawful",	"laws",	"legal",	
			   "legitimate",	"lesbian",	"leukemia",	"LGBT",	"liberty",	"lie",	"lifestyle",	"limits",	"lock",	
			   "locked",	"lonely",	"lose ",	"loss",	"lying",	"lying ",	"medical ",	"menaced",	"menacing",	
			   "minority",	"miscarriage",	"modest",	"modesty",	"monitor",	"monitored",	"monitoring",	"moral ",	
			   "mutual ",	"newborn",	"obliged",	"offence",	"offend",	"offended",	"offenders",	"offending",	"offense",	
			   "offensive",	"offer ",	"only ",	"open",	"openly",	"option",	"ostracised",	"ousted",	"own",	
			   "passed away",	"password",	"patients' ",	"pattern",	"patterns of behavior",	"permission",	"personal",	
			   "policy",	"possession",	"possessions",	"post",	"posted",	"posting",	"posts",	"pregnant",	"prevent",	
			   "preventing",	"prevents",	"privacy",	"private",	"property",	"prostitute",	"protect",	"protected",	
			   "protecting",	"protection",	"protects",	"public",	"publication",	"publicise",	"publicity",	
			   "publicize",	"publish",	"published",	"publishes",	"publishing",	"quiet",	"racist",	"rape",	
			   "reasonable",	"records",	"refuse",	"refused",	"refusing",	"regulate",	"regulated",	"regulation",	
			   "regulations",	"relationship",	"release",	"report",	"reported",	"reporting",	"reports",	"reserved",	
			   "respect",	"respected",	"respectful",	"respecting",	"restrain",	"restrained",	"restraining",	"restraints",
			   "restrict",	"restricted",	"restricting",	"restrictions",	"restrictive",	"reveal",	"revealed",	"revealing",	
			   "reveals",	"revelation",	"revenge",	"rights",	"rip",	"risk",	"risks",	"ruling",	"rumor",	"rumors",	
			   "rumour",	"rumours",	"safe",	"safeguard",	"safeguarding",	"safeguards",	"safely",	"safer",	"safest",	
			   "safety",	"screwed",	"scrutinised",	"scrutinising",	"scrutinized",	"scrutinizing",	"sad",	"secluded",	"seclusion",	
			   "secret",	"secret",	"secretive",	"secretly",	"secrets",	"secure",	"securely",	"security",	"sense ",	"senses",	
			   "sensitive",	"sensitivity",	"separate",	"separated",	"separation",	"settings",	"sex",	"sex life",	"sexual ",	
			   "sexuality",	"share",	"shared",	"shares",	"sharing",	"shut",	"shy",	"sick",	"silence",	"silent",	"slam",	
			   "slammed",	"slamming",	"snooped",	"snoopers",	"snooping",	"solidarity",	"solitude",	"space",	"spam",	"spy",	
			   "spying",	"stalking",	"statute",	"suicide",	"support",	"supportive",	"suppress",	"suppressed",	"suppressing",	
			   "surveillance",	"surveilled",	"suspicion",	"sympathy",	"syphilis",	"tenants' ",	"thought",	"threat",	
			   "threatened",	"threatening",	"threats",	"track",	"tracked",	"tracking",	"trouble",	"troubled",	"troubling",	
			   "trust",	"trustworthy",	"uncomfortable",	"understanding",	"unfair",	"unfairly",	"unlawful",	"unmask",	"unmasking",
			   "violated",	"watched",	"welcoming",	"welfare ",	"women's "	   };
	   
//	   String[] words = userTweets.split("[!-~]* ");
	   String[] words = userTweets.split( "\\s+");
	  
/*	   Set<String> uniqueWords = new HashSet<String>();

	   for (String word : words) {
	       uniqueWords.add(word);
	   }*/
//	   words = uniqueWords.toArray(new String[0]);
	   
/*	   Set<String> uniqueprivacyWords = new HashSet<String>();

	   for (String pword : privacy_words) {
		   uniqueprivacyWords.add(pword);
	   }*/
	   
//	   uniqueprivacyWords.retainAll(uniqueWords);
	   
	   
	   Map<String, Integer> tweetWords = new HashMap<String, Integer>();
	   for (String word : words) {
	   if(!tweetWords.containsKey(word))
	   {
		   tweetWords.put(word, new Integer("1"));
	   }
	   else
	   {
		   tweetWords.put(word, tweetWords.get(word) + new Integer(1));
	   }
//		   tweetWords.put(word, 1);
	   }
//   return uniqueprivacyWords.size();
	   
	   Map<String, Integer> privacyWords = new HashMap<String, Integer>();
	   for (String pword : privacy_words) {
		   privacyWords.put(pword, 1);
	   }
	   
	   Map<String, Integer> result = tweetWords;
	   result.keySet().retainAll(privacyWords.keySet());
	   int counter = 0;
	
	   Object[] a = result.values().toArray();  
//	   System.out.println(result.values()); 
	   for(int f=0; f<a.length; f++){
		   counter =counter+ Integer.parseInt(a[f].toString());
//	   System.out.println(a[2]);
	   }
	   return counter;
}
   public static int privacyDictionaryIndex (String userTweets)
   {
	   
   String [] privacy_words = {
   "a choice",	"a lie",	"ability to control",	"able to control",	"access",	
   "accessed",	"accessible",	"advance directive",	"advice and support",	"afraid",	
   "alone",	"amendment",	"an option",	"anonymity",	"anonymous",	"anonymously",	
   "aren't fair",	"at home",	"attitudes and behavior",	"attitudes and behaviour",	"avoided",	
   "avoiding",	"be watched",	"behaving",	"behavior of individuals",	"behavior patterns",	
   "behaviour of individuals",	"behaviour patterns",	"being watched",	"beliefs and behavior",	
   "beliefs and behaviour",	"beyond everybody's control",	"beyond everyone's control",	
   "beyond her control",	"beyond his control",	"beyond my control",	"beyond our control",	
   "beyond somebody's control",	"beyond someone's control",	"beyond their control",	
   "beyond your control",	"blacklisted",	"blacklisting",	"blackmail ",	"block",	
   "blocked",	"blocking",	"boundaries",	"boundary",	"breach",	"breached",	"breaches",	
   "breaching",	"bullied",	"bully",	"bullying",	"can control",	"can not control",	
   "can't control",	"cannot control",	"care and support",	"choice between",	"choices",	
   "choose",	"chose",	"close friend",	"close friends",	"close the door",	
   "closely watched",	"closeness",	"coerce",	"collected",	"comment",	"commenting",	
   "comments",	"community",	"conceal",	"concealed",	"concealment",	"conceals",	"concern",	
   "concerned",	"concerns",	"confide",	"confided",	"confidence",	"confidential",	
   "confidential information",	"confidentiality",	"consent",	"consented",	"consenting",	
   "control it",	"control of",	"control over",	"controls",	"conversation ",	"could control",
   "could not control",	"couldn't control",	"criminal",	"danger",	"dangerous",	"dangers",	
   "data",	"database ",	"deceit",	"deceitful",	"declaration of rights",	"degree of control",
   "delete",	"deleted",	"deleting",	"democratic rights",	"details",	"dignified",	"dignity",	
   "disclose",	"disclosed",	"disclosing",	"disclosure",	"discreet",	"discreetly",	
   "discretion",	"distress",	"distressed",	"distressing",	"disturb",	"disturbed",	
   "disturbing",	"duty of confidentiality",	"embarrass",	"embarrassed",	"embarrassing",	
   "embarrassment",	"emotional support",	"emotions",	"employment rights",	
   "encourage and support",	"encouragement and support",	"endanger",	"endangering",	
   "ensure",	"everybody's choice",	"everybody's knowledge",	"everybody's option",	
   "everyone's choice",	"everyone's knowledge",	"everyone's option",	"exclude",	"excluded",	
   "excluding",	"expose",	"exposed",	"exposes",	"exposing",	"exposure",	"family",	
   "family history",	"fear ",	"feel welcome",	"feelings and behavior",	
   "feelings and behaviour",	"financial information",	"find support",	
   "follow everybody",	"follow everyone",	"follow her",	"follow him",	"follow me",	
   "follow our",	"follow somebody",	"follow someone",	"follow them",	"follow us",	
   "follow you",	"for everybody's support",	"for everyone's support",	"for her support",	
   "for his support",	"for my support",	"for our support",	"for somebody's support",	
   "for someone's support",	"for their support",	"for your support",	"force",	"forced",	
   "forcing",	"freedom",	"freedom of",	"freedom of choice",	"friend",	"friends",	
   "friendship",	"gave everybody support",	"gave everyone support",	"gave her support",	
   "gave him support",	"gave his support",	"gave me support",	"gave my support",	
   "gave our support",	"gave somebody support",	"gave someone support",	"gave support",	
   "gave their support",	"gave them support",	"gave us support",	"gave you support",	
   "gave your support",	"gay rights",	"get everybody's support",	"get everyone's support",	
   "get her support",	"get him support",	"get his support",	"get me support",	"get my support",	
   "get our support",	"get ourselves support",	"get somebody support",	"get someone support",	
   "get the support",	"get their support",	"get them support",	"get us support",	
   "get your support",	"give everybody support",	"give everyone support",	"give her support",	
   "give him support",	"give his support",	"give me support",	"give my support",	
   "give our support",	"give somebody support",	"give someone support",	"give support",	
   "give their support",	"give them support",	"give us support",	"give you support",	
   "give your support",	"gossip",	"group",	"groups",	"hacking",	"harassed",	"harassment",	
   "have choice",	"have control",	"he is lying",	"he was lying",	"he will be lying",	
   "help and support",	"her choice",	"her knowledge",	"her option",	"hid",	"hidden",	
   "hide",	"hiding",	"his choice",	"his knowledge",	"his option",	"human rights",	
   "i am lying",	"i was lying",	"i will be lying",	"identity",	"illegal",	"in control",	
   "in secret",	"indignity",	"individual choice",	"individual rights",	
   "individual's behavior",	"individual's behaviour",	"information commissioner",	"infringe",	
   "infringed",	"infringement",	"infringements",	"injunction",	"inspecting",	"interfere",	
   "interfered",	"interference",	"interfering",	"intimate",	"intrude",	"intruded",	"intrusion ",
   "intrusive",	"invade",	"invaded",	"invasion",	"invasive",	"isn't fair",	"isolated",	"isolating",
   "judge",	"judged",	"judgemental",	"judgmental",	"knock at",	"knock on",	"knocked at",	
   "knocked on",	"knocking at",	"knocking on",	"lack of choice",	"lack of control",	
   "land rights",	"law",	"lawful",	"legal rights",	"legitimate",	"liberty",	"lie about",	
   "lie to",	"lied about",	"lied to",	"lifestyle choice",	"limits",	"little choice",	
   "lock",	"locked",	"lonely",	"lose control",	"loss of control",	"lot of support",	
   "lying about",	"lying to",	"medical history",	"medical records",	"menaced",	"menacing",	
   "minority rights",	"modest",	"modesty",	"monitored",	"monitoring",	"moral support",	
   "mutual support",	"my choice",	"my knowledge",	"my option",	"no choice",	"no control",	
   "no knowledge",	"no option",	"not fair",	"obliged",	"offence",	"offend",	"offended",	
   "offenders",	"offending",	"offense",	"offensive",	"offer support",	"only choice",	
   "only option",	"open",	"openly",	"option of",	"option to",	"ostracised",	"ostracized",	
   "our choice",	"our knowledge",	"our option",	"ousted",	"own behavior",	"own behaviour",	
   "own choice",	"own home",	"own knowledge",	"own space",	"password",	"patients' rights",
   "pattern of behavior",	"pattern of behaviour",	"patterns of behavior",	"patterns of behaviour",	
   "permission",	"person's behavior",	"person's behaviour",	"personal",	"personal choice",	
   "personal information",	"personal possessions",	"personal space",	"policy",	"post",	"posted",
   "posting",	"posts",	"prevent",	"preventing",	"prevents",	"prior knowledge",	"privacy",	
   "privacy law",	"privacy laws",	"privacy settings",	"private",	"private information",	
   "problem behavior",	"problem behaviour",	"property rights",	"protect",	"protected",	
   "protecting",	"protection act",	"protection law",	"protects",	"public",	"public life",	
   "publication",	"publicise",	"publicity",	"publicize",	"publish",	"published",	
   "publishes",	"publishing",	"quiet",	"reasonable",	"reasonable expectation",	
   "reasonable grounds",	"reasonable suspicion",	"receive support",	"refuse",	"refused",	
   "refusing",	"regulate",	"regulated",	"regulation",	"regulations",	"relationship ",	
   "release",	"report",	"reported",	"reporting",	"reports",	"reserved",	"respect",	
   "respected",	"respectful",	"respecting",	"restrain",	"restrained",	"restraining",	
   "restraints",	"restrict",	"restricted",	"restricting",	"restrictions",	"restrictive",
   "reveal",	"revealed",	"revealing",	"reveals",	"revelation",	"risk",	"risks",	
   "ruling",	"rumor",	"rumors",	"rumour",	"rumours",	"safe",	"safeguard",	
   "safeguarding",	"safeguards",	"safely",	"safer",	"safest",	"safety",	
   "scrutinised",	"scrutinising",	"scrutinized",	"scrutinizing",	"secluded",	"seclusion",	
   "secret",	"secretive",	"secretly",	"secrets",	"secure",	"securely",	"security",	
   "sense of control",	"sensitive",	"sensitive information",	"sensitivity",	"separate",	
   "separated",	"separation",	"sex life",	"sexual behavior",	"sexual behaviour",	"sexual history",	
   "sexuality",	"share",	"shared",	"shares",	"sharing",	"she is lying",	"she was lying",	
   "she will be lying",	"shut",	"shy",	"silence",	"silent ",	"slam",	"slammed",	"slamming",
   "snooped",	"snoopers",	"snooping",	"solitude",	"somebody's choice",	"somebody's knowledge",	
   "somebody's option",	"someone's choice",	"someone's knowledge",	"someone's option",	"spam",	"spy",
   "spying",	"stalking",	"statute",	"support and advice",	"support and affection",	
   "support and care",	"support and encouragement",	"support and friendship",	
   "support and guidance",	"support and help",	"support and solidarity",	"support and sympathy",	
   "support and understanding",	"support between kin",	"support everybody",	"support everyone",
   "support from",	"support group",	"support groups",	"support her",	"support him",	
   "support his",	"support somebody",	"support someone",	"support their",	"support them",	
   "support us",	"support you",	"supportive",	"suppress",	"suppressed",	"suppressing",	
   "surveillance",	"surveilled",	"tenants' rights",	"the choice",	"the option",	
   "their choice",	"their knowledge",	"their option",	"their own",	"they are lying",	
   "they were lying",	"they will be lying",	"third party rights",	"this option",	
   "thought and behavior",	"thought and behaviour",	"threat",	"threatened",	
   "threatening",	"threats",	"to control",	"to lie",	"to monitor",	"track",	
   "tracked",	"tracking",	"trouble",	"troubled",	"troubling",	"trust",	
   "trustworthy",	"type of behavior",	"type of behaviour",	"types of behavior",	
   "types of behaviour",	"uncomfortable",	"unfair",	"unfairly",	"unlawful",	"unmask",	
   "unmasking",	"violated",	"welcoming",	"welfare rights",	"women's rights",	
   "you are lying",	"you were lying",	"you will be lying",	"your choice",	"your knowledge",	
   "your option"};

   
   
   
   int counter = 0;
   int privacyPhraseCount = privacy_words.length;
   for (int i =0; i<privacyPhraseCount; i++){
	   int strcounter=0;
	   String str = privacy_words[i].toString().toLowerCase();
//	   System.out.println(str);
//	   System.out.println(userTweets.toLowerCase());
	   strcounter = StringUtils.countMatches(userTweets.toLowerCase(), str);
//   if(userTweets.toLowerCase().contains(str2.toLowerCase()) == true)
	   counter=counter+strcounter;
   }   
   
   return counter;
   
   }
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   public static void topicModelInferencerIndex (String userTweets_clear, String output_file) throws FileNotFoundException, IOException, ClassNotFoundException
   {
//		String topicModelFile = "model/twitter_topicModel_Feb4_10.model";
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
		//Do all the text conversions here
		String userTweets_converted = userTweets_clear;
		userTweets_converted = tweetCleaner.convertClusters(userTweets_converted);				
		userTweets_converted = tweetCleaner.removeNonASCII(userTweets_converted);
		userTweets_converted = tweetCleaner.replaceHandles(userTweets_converted);
		userTweets_converted = tweetCleaner.replaceURLs(userTweets_converted);
		userTweets_converted = tweetCleaner.correctMisspellings(userTweets_converted);		
		userTweets_converted = userTweets_converted.replaceAll("\\n", "");

		testing.addThruPipe(new Instance(  userTweets_converted, null, "test instance", null));

		int ntopic = topicModel.getNumTopics();
		double[] topicRatio = new double[ntopic]; //number of users have topic i as top topic

//		System.out.println("Predictions:");
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
   
   
   
   
   public static   float []  userTopicSensitivityIndex(String topics_inferrred) throws NumberFormatException, IOException
   {
	   //200 hundred topics, the first one is not read.
	  //Aylin's topic sensitivity levels
	   /*    int [] topicSensitivity = {
	    		  	   
			   1,	3,	0,	2,	2,	1,	2,	1,	1,	0,	1,	2,	0,	0,	2,	1,	2,	1,	0,	2,
			   0,	1,	1,	2,	1,	0,	2,	3,	3,	1,	2,	2,	0,	0,	3,	2,	1,	1,	1,	1,
			   1,	3,	0,	0,	2,	1,	0,	3,	0,	3,	2,	2,	1,	2,	1,	2,	0,	3,	3,	0,
			   0,	2,	2,	0,	0,	0,	0,	2,	2,	0,	0,	2,	2,	1,	3,	0,	1,	3,	3,	0,
			   2,	0,	2,	3,	1,	1,	3,	3,	1,	1,	1,	1,	0,	2,	0,	1,	3,	1,	1,	1,
			   1,	0,	1,	2,	1,	1,	1,	2,	1,	0,	0,	1,	3,	0,	1,	1,	2,	2,	2,	0,
			   0,	0,	1,	0,	1,	1,	2,	2,	1,	1,	1,	2,	2,	1,	2,	2,	1,	1,	2,	2,
			   0,	0,	2,	1,	2,	2,	2,	1,	1,	0,	2,	2,	1,	1,	1,	2,	1,	2,	1,	2,
			   2,	0,	0,	2,	2,	0,	1,	1,	1,	1,	3,	0,	2,	0,	2,	1,	2,	1,	1,	1,
			   2,	1,	1,	1,	0,	1,	2,	2,	0,	0,	2,	2,	2,	1,	2,	1,	2,	1,	2,	0,
	   };*/
	   //252 totalm 1.26 prob
	        
	   //Jordan's topic sensitivity levels
	  int [] topicSensitivity = {
			1,	3,	0,	0,	1,	0,	2,	1,	0,	0,	1,	1,	0,	0,	0,	0,	3,	0,	0,	1,
			0,	0,	1,	3,	3,	0,	1,	1,	2,	0,	1,	0,	0,	0,	2,	1,	0,	1,	0,	0,
			1,	2,	0,	0,	2,	1,	0,	2,	0,	3,	2,	0,	0,	1,	0,	2,	0,	3,	2,	0,
			0,	0,	0,	0,	1,	0,	0,	0,	0,	0,	1,	1,	3,	1,	1,	0,	0,	2,	3,	0,
			2,	0,	0,	2,	1,	0,	2,	1,	0,	1,	1,	0,	0,	1,	1,	2,	2,	0,	0,	1,
			1,	0,	1,	0,	1,	0,	1,	1,	1,	0,	0,	0,	3,	0,	0,	0,	0,	1,	1,	0,
			0,	0,	0,	0,	1,	0,	0,	3,	0,	1,	2,	1,	1,	0,	1,	0,	0,	1,	0,	1,
			0,	1,	3,	0,	2,	1,	1,	0,	0,	0,	1,	3,	0,	2,	2,	1,	0,	1,	1,	2,
			2,	0,	0,	1,	1,	0,	2,	1,	0,	1,	1,	0,	1,	0,	2,	1,	2,	1,	0,	0,
			0,	1,	1,	1,	0,	1,	0,	1,	0,	0,	2,	2,	0,	0,	1,	1,	1,	2,	1,	0};
	   
//	   int [] topicSensitivity = {1,1,1,1,1,1,1,1,1,1};
	   
//	   System.out.println(topicSensitivity[199]);
	   
	   String csvFile = topics_inferrred;
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";
		//number of topics
	 float topicLength = 200;
	//	 float topicLength = 10;
	 	float[] result;
	 	result = new float[8];
		float topicIndex0 = 0;
		float topicIndex1 = 0;
		float topicIndex2 = 0;
		float topicIndex3 = 0;

		int count0 =0;
		int count1 =0;
		int count2 =0;
		int count3 =0;

			br = new BufferedReader(new FileReader(csvFile));
			for (int i=0; i < topicLength; i++)
			{ line = br.readLine();
				
			        // use comma as separator
				String[] probability = line.split(cvsSplitBy);
				 
				System.out.println("Topic number is " + probability[0] 
	                                 + " , probability=" + probability[1]);

				if (Float.parseFloat(probability[1]) > 0.001)
				{	
					if (topicSensitivity[i] ==0){
					count0++;
				//System.out.println(Float.parseFloat(probability[1]));
			//	topicIndex += Float.parseFloat(probability[1])* topicSensitivity[i];
			//		topicIndex =topicIndex + topicSensitivity[i];
					topicIndex0 += Float.parseFloat(probability[1]);
   }
					if (topicSensitivity[i] ==1){
						count1++;
					//System.out.println(Float.parseFloat(probability[1]));
				//	topicIndex += Float.parseFloat(probability[1])* topicSensitivity[i];
				//		topicIndex =topicIndex + topicSensitivity[i];
						topicIndex1 += Float.parseFloat(probability[1]);
	   }
					if (topicSensitivity[i] ==2){
						count2++;
					//System.out.println(Float.parseFloat(probability[1]));
				//	topicIndex += Float.parseFloat(probability[1])* topicSensitivity[i];
				//		topicIndex =topicIndex + topicSensitivity[i];
						topicIndex2 += Float.parseFloat(probability[1]);
	   }
					if (topicSensitivity[i] ==3){
						count3++;
					//System.out.println(Float.parseFloat(probability[1]));
				//	topicIndex += Float.parseFloat(probability[1])* topicSensitivity[i];
				//		topicIndex =topicIndex + topicSensitivity[i];
						topicIndex3 += Float.parseFloat(probability[1]);
	   }
				
				
				}
			System.out.println(topicIndex0+ "and" + count0);
			//return topicIndex;
			
}						System.out.println(topicIndex0+ " and " + count0);
			result[0]=topicIndex0;
			result[1]=count0;
			result[2]=topicIndex1;
			result[3]=count1;
			result[4]=topicIndex2;
			result[5]=count2;
			result[6]=topicIndex3;
			result[7]=count3;
			
			return(result);

   
   }
}