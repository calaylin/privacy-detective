import java.io.*;
import java.util.Scanner;

import org.apache.commons.io.FileUtils;

import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.InvalidFormatException;
import opennlp.tools.util.Span;

public class TweetNER {

 
    public String Tokens[];

    public static void main(String[] args) throws IOException
             {



 //      String input="Thanks @mutineermag for my lingering 12-hour hangover... " + "\n"
 //      		+ "\n"  + "\n" +"You'd think I'd have learned better by now." + "\n";
	    test_main input_test = new test_main();
	    String input_filename = "/Users/Aylin/Documents/workspace/automated_tweet_detection_tests/plain_user_text/200599938.txt";
		String userTweets = input_test.readFile(input_filename);						
	    
	    //use the following line in the database code, because this is the one that reads directly from text//
	//	int tweet_number = userTweets.split(System.getProperty("line.separator")).length;
// 		System.out.println(tweet_number);        
//       String names = toi.namefind(toi.Tokens);
       
		TweetNER obj = new TweetNER();
		System.out.println(obj.calculateNerIndex(obj, userTweets));
        
       
//        String org = toi.orgfind(toi.Tokens);
//        String loc = toi.locfind(toi.Tokens);
//        String time = toi.timefind(toi.Tokens);
//        String date = toi.datefind(toi.Tokens);
//        String money = toi.moneyfind(toi.Tokens);
//        String percentage = toi.percentagefind(toi.Tokens);*/


/*        System.out.print("person name is: "+names);
        System.out.println("person name is: "+names.length());

        System.out.print("organization name is: "+org);
        System.out.println("organization name is: "+org.length());

        System.out.print("location name is: "+loc);
        System.out.println("location name is: "+loc.length());

        System.out.print("time name is: "+time);
        System.out.println("time name is: "+time.length());

        System.out.print("date name is: "+date);
        System.out.println("date name is: "+date.length());

        System.out.print("money name is: "+money);
        System.out.println("money name is: "+money.length());

        System.out.print("percentage name is: "+percentage);
        System.out.println("percentage name is: "+percentage.length());
        
        
        boolean detection = false;
        if (names.length()!=0 | org.length()!=0 | loc.length()!=0)
        {detection=true;
        System.out.println(detection);
        
        }
*/
        
        
/*        System.out.println(loc);
        System.out.println(loc_count);

        System.out.println(org_count);
        System.out.println(name_count);
        System.out.println(time_count);
        System.out.println(date_count);
        System.out.println(money_count);
        System.out.println(percentage_count);*/

    }

    public int getNumLinesInString (Scanner tweets){
        int lines =0;
    	 while(tweets.hasNextLine())  {
    	        lines++;
    	 }
    	 return lines;
    }
    
    int getNumLinesInFile(File file) throws IOException {

        String content = FileUtils.readFileToString(file);
        return content.split(System.getProperty("line.separator")).length;
    }
    
    public int calculateNerIndex (TweetNER toi ,String user_tweets)
    
    {
		toi.tokenization(user_tweets);
		
    	 int name_count = toi.nameCount(toi.Tokens);
         int loc_count = toi.locCount(toi.Tokens);
         int time_count = toi.timeCount(toi.Tokens);
         int date_count = toi.dateCount(toi.Tokens);
         int money_count = toi.moneyCount(toi.Tokens);
         int percentage_count = toi.percentageCount(toi.Tokens);
         int org_count = toi.orgCount(toi.Tokens);
         
         
         return (name_count+ loc_count+time_count+date_count+money_count+percentage_count+org_count);
         //over number of sentences?
    	
    }
    
    public String percentagefind(String cnt[]) {
        InputStream is;
        TokenNameFinderModel tnf;
        NameFinderME nf;
        String sd = "";
        try {
            is = new FileInputStream(
                    "models/opennlp/ner/en-ner-percentage.bin");
            tnf = new TokenNameFinderModel(is);
            nf = new NameFinderME(tnf);

            Span sp[] = nf.find(cnt);

            String a[] = Span.spansToStrings(sp, cnt);
            StringBuilder fd = new StringBuilder();
            int l = a.length;

            for (int j = 0; j < l; j++) {
                fd = fd.append(a[j] + "\n");

            }
            sd = fd.toString();

        } catch (FileNotFoundException e) {

            e.printStackTrace();
        } catch (InvalidFormatException e) {

            e.printStackTrace();
        } catch (IOException e) {

            e.printStackTrace();
        }
        return sd;
    }
        
    
    
    public String moneyfind(String cnt[]) {
        InputStream is;
        TokenNameFinderModel tnf;
        NameFinderME nf;
        String sd = "";
        try {
            is = new FileInputStream(
                    "models/opennlp/ner/en-ner-money.bin");
            tnf = new TokenNameFinderModel(is);
            nf = new NameFinderME(tnf);

            Span sp[] = nf.find(cnt);

            String a[] = Span.spansToStrings(sp, cnt);
            StringBuilder fd = new StringBuilder();
            int l = a.length;

            for (int j = 0; j < l; j++) {
                fd = fd.append(a[j] + "\n");

            }
            sd = fd.toString();

        } catch (FileNotFoundException e) {

            e.printStackTrace();
        } catch (InvalidFormatException e) {

            e.printStackTrace();
        } catch (IOException e) {

            e.printStackTrace();
        }
        return sd;
    }
        
    
    
    public String datefind(String cnt[]) {
    InputStream is;
    TokenNameFinderModel tnf;
    NameFinderME nf;
    String sd = "";
    try {
        is = new FileInputStream(
                "models/opennlp/ner/en-ner-date.bin");
        tnf = new TokenNameFinderModel(is);
        nf = new NameFinderME(tnf);

        Span sp[] = nf.find(cnt);

        String a[] = Span.spansToStrings(sp, cnt);
        StringBuilder fd = new StringBuilder();
        int l = a.length;

        for (int j = 0; j < l; j++) {
            fd = fd.append(a[j] + "\n");

        }
        sd = fd.toString();

    } catch (FileNotFoundException e) {

        e.printStackTrace();
    } catch (InvalidFormatException e) {

        e.printStackTrace();
    } catch (IOException e) {

        e.printStackTrace();
    }
    return sd;
}
    
    
    	public String namefind(String cnt[]) {
        InputStream is;
        TokenNameFinderModel tnf;
        NameFinderME nf;
        String sd = "";
        try {
            is = new FileInputStream(
                    "models/opennlp/ner/en-ner-person.bin");
            tnf = new TokenNameFinderModel(is);
            nf = new NameFinderME(tnf);

            Span sp[] = nf.find(cnt);

            String a[] = Span.spansToStrings(sp, cnt);
            StringBuilder fd = new StringBuilder();
            int l = a.length;

            for (int j = 0; j < l; j++) {
                fd = fd.append(a[j] + "\n");

            }
            sd = fd.toString();

        } catch (FileNotFoundException e) {

            e.printStackTrace();
        } catch (InvalidFormatException e) {

            e.printStackTrace();
        } catch (IOException e) {

            e.printStackTrace();
        }
        return sd;
    }

    
    
	
    public String orgfind(String cnt[]) {
        InputStream is;
        TokenNameFinderModel tnf;
        NameFinderME nf;
        String sd = "";
        try {
            is = new FileInputStream(
                    "models/opennlp/ner/en-ner-organization.bin");
            tnf = new TokenNameFinderModel(is);
            nf = new NameFinderME(tnf);
            Span sp[] = nf.find(cnt);
            String a[] = Span.spansToStrings(sp, cnt);
            StringBuilder fd = new StringBuilder();
            int l = a.length;

            for (int j = 0; j < l; j++) {
                fd = fd.append(a[j] + "\n");

            }

            sd = fd.toString();

        } catch (FileNotFoundException e) {

            e.printStackTrace();
        } catch (InvalidFormatException e) {

            e.printStackTrace();
        } catch (IOException e) {

            e.printStackTrace();
        }
        return sd;

    }
    
    
    public String locfind(String cnt[]) {
        InputStream is;
        TokenNameFinderModel tnf;
        NameFinderME nf;
        String sd = "";
        try {
            is = new FileInputStream(
                    "models/opennlp/ner/en-ner-location.bin");
            tnf = new TokenNameFinderModel(is);
            nf = new NameFinderME(tnf);
            Span sp[] = nf.find(cnt);
            String a[] = Span.spansToStrings(sp, cnt);
            StringBuilder fd = new StringBuilder();
            int l = a.length;

            for (int j = 0; j < l; j++) {
                fd = fd.append(a[j] + "\n");

            }

            sd = fd.toString();

        } catch (FileNotFoundException e) {

            e.printStackTrace();
        } catch (InvalidFormatException e) {

            e.printStackTrace();
        } catch (IOException e) {

            e.printStackTrace();
        }
        return sd;

    }

    
    
    public String timefind(String cnt[]) {
        InputStream is;
        TokenNameFinderModel tnf;
        NameFinderME nf;
        String sd = "";
        try {
            is = new FileInputStream(
                    "models/opennlp/ner/en-ner-time.bin");
            tnf = new TokenNameFinderModel(is);
            nf = new NameFinderME(tnf);
            Span sp[] = nf.find(cnt);
            String a[] = Span.spansToStrings(sp, cnt);
            StringBuilder fd = new StringBuilder();
            int l = a.length;

            for (int j = 0; j < l; j++) {
                fd = fd.append(a[j] + "\n");

            }

            sd = fd.toString();

        } catch (FileNotFoundException e) {

            e.printStackTrace();
        } catch (InvalidFormatException e) {

            e.printStackTrace();
        } catch (IOException e) {

            e.printStackTrace();
        }
        return sd;

    }


    public void tokenization(String tokens) {

        InputStream is;
        TokenizerModel tm;

        try {
            is = new FileInputStream("models/opennlp/en-token.bin");
            tm = new TokenizerModel(is);
            Tokenizer tz = new TokenizerME(tm);
            Tokens = tz.tokenize(tokens);
            // System.out.println(Tokens[1]);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    
    public int nameCount(String cnt[]) {
        InputStream is;
        TokenNameFinderModel tnf;
        NameFinderME nf;
        String sd = "";
        try {
            is = new FileInputStream(
                    "models/opennlp/ner/en-ner-person.bin");
            tnf = new TokenNameFinderModel(is);
            nf = new NameFinderME(tnf);

            Span sp[] = nf.find(cnt);

            String a[] = Span.spansToStrings(sp, cnt);
            StringBuilder fd = new StringBuilder();
            int l = a.length;
            return l;

        } catch (FileNotFoundException e) {

            e.printStackTrace();
        } catch (InvalidFormatException e) {

            e.printStackTrace();
        } catch (IOException e) {

            e.printStackTrace();
        }
        return 0;
    }

    
    public int percentageCount(String cnt[]) {
        InputStream is;
        TokenNameFinderModel tnf;
        NameFinderME nf;
        String sd = "";
        try {
            is = new FileInputStream(
                    "models/opennlp/ner/en-ner-percentage.bin");
            tnf = new TokenNameFinderModel(is);
            nf = new NameFinderME(tnf);

            Span sp[] = nf.find(cnt);

            String a[] = Span.spansToStrings(sp, cnt);
            StringBuilder fd = new StringBuilder();
            int l = a.length;
            return l;

        } catch (FileNotFoundException e) {

            e.printStackTrace();
        } catch (InvalidFormatException e) {

            e.printStackTrace();
        } catch (IOException e) {

            e.printStackTrace();
        }
        return 0;
    }
        
    
    
    public int moneyCount(String cnt[]) {
        InputStream is;
        TokenNameFinderModel tnf;
        NameFinderME nf;
        String sd = "";
        try {
            is = new FileInputStream(
                    "models/opennlp/ner/en-ner-money.bin");
            tnf = new TokenNameFinderModel(is);
            nf = new NameFinderME(tnf);

            Span sp[] = nf.find(cnt);

            String a[] = Span.spansToStrings(sp, cnt);
            StringBuilder fd = new StringBuilder();
            int l = a.length;

            return l;
        } catch (FileNotFoundException e) {

            e.printStackTrace();
        } catch (InvalidFormatException e) {

            e.printStackTrace();
        } catch (IOException e) {

            e.printStackTrace();
        }
        return 0;
    }
        
    
    
    public int dateCount(String cnt[]) {
    InputStream is;
    TokenNameFinderModel tnf;
    NameFinderME nf;
    String sd = "";
    try {
        is = new FileInputStream(
                "models/opennlp/ner/en-ner-date.bin");
        tnf = new TokenNameFinderModel(is);
        nf = new NameFinderME(tnf);

        Span sp[] = nf.find(cnt);

        String a[] = Span.spansToStrings(sp, cnt);
        StringBuilder fd = new StringBuilder();
        int l = a.length;

        return l;

    } catch (FileNotFoundException e) {

        e.printStackTrace();
    } catch (InvalidFormatException e) {

        e.printStackTrace();
    } catch (IOException e) {

        e.printStackTrace();
    }
    return 0;
}
    
    
    
    
	
    public int orgCount(String cnt[]) {
        InputStream is;
        TokenNameFinderModel tnf;
        NameFinderME nf;
        String sd = "";
        try {
            is = new FileInputStream(
                    "models/opennlp/ner/en-ner-organization.bin");
            tnf = new TokenNameFinderModel(is);
            nf = new NameFinderME(tnf);
            Span sp[] = nf.find(cnt);
            String a[] = Span.spansToStrings(sp, cnt);
            StringBuilder fd = new StringBuilder();
            int l = a.length;

            return l;

        } catch (FileNotFoundException e) {

            e.printStackTrace();
        } catch (InvalidFormatException e) {

            e.printStackTrace();
        } catch (IOException e) {

            e.printStackTrace();
        }
        return 0;

    }
    
    
    public int locCount(String cnt[]) {
        InputStream is;
        TokenNameFinderModel tnf;
        NameFinderME nf;
        String sd = "";
        try {
            is = new FileInputStream(
                    "models/opennlp/ner/en-ner-location.bin");
            tnf = new TokenNameFinderModel(is);
            nf = new NameFinderME(tnf);
            Span sp[] = nf.find(cnt);
            String a[] = Span.spansToStrings(sp, cnt);
            StringBuilder fd = new StringBuilder();
            int l = a.length;

            return l;

        } catch (FileNotFoundException e) {

            e.printStackTrace();
        } catch (InvalidFormatException e) {

            e.printStackTrace();
        } catch (IOException e) {

            e.printStackTrace();
        }
        return 0;

    }

    
    
    public int timeCount(String cnt[]) {
        InputStream is;
        TokenNameFinderModel tnf;
        NameFinderME nf;
        String sd = "";
        try {
            is = new FileInputStream(
                    "models/opennlp/ner/en-ner-time.bin");
            tnf = new TokenNameFinderModel(is);
            nf = new NameFinderME(tnf);
            Span sp[] = nf.find(cnt);
            String a[] = Span.spansToStrings(sp, cnt);
            StringBuilder fd = new StringBuilder();
            int l = a.length;
            return l;

        } catch (FileNotFoundException e) {

            e.printStackTrace();
        } catch (InvalidFormatException e) {

            e.printStackTrace();
        } catch (IOException e) {

            e.printStackTrace();
        }
        return 0;

    }

}