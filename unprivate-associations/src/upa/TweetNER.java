package upa;
import java.io.*;

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

/*        TweetNER toi = new TweetNER();


        String input;

        input="Thanks @mutineermag for my lingering 12-hour hangover... You'd think I'd have learned better by now.";
                toi.tokenization(input);

        String names = toi.namefind(toi.Tokens);
        String org = toi.orgfind(toi.Tokens);
        String loc = toi.locfind(toi.Tokens);
        String time = toi.timefind(toi.Tokens);
        String date = toi.datefind(toi.Tokens);
        String money = toi.moneyfind(toi.Tokens);
        String percentage = toi.percentagefind(toi.Tokens);


        System.out.print("person name is: "+names);
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
        
        }*/


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

}