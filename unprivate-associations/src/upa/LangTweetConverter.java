package upa;

import java.sql.SQLException;

/**
 * Jordan's magic tweet converter program
 * 
 */
public class LangTweetConverter extends TweetConverter {

    /** The conversion method identifier*/
    public final static String MY_TYPE = "LANG"; // Do not change without updating schema!

    /**
     * Constructor
     *
     * @param db - The database reference
     */
    public LangTweetConverter( TwitterDatabase db ) {
        super( db, MY_TYPE );
    }
        
    /**
     * Convert a given tweet to another format.  This function may also 
     *  assign topics to the tweet.
     *
     * @param tweet - The tweet to convert
     * @return The converted contents of the tweet
     */
    public String convertTweet( UPTweet tweet ) throws SQLException {

        String newText = "";


        // TODO JORDAN PUT YOUR CODE HERE!!

        
        String oldText = tweet.getTweet();
        
        String[] splitOldText = oldText.split(" ");
        try{
        	clusterReplacer cr = new clusterReplacer();
        	newText = cr.replaceWords(splitOldText);
        }
        catch(Exception e){
        	System.out.println("Your csv file was not found. Please check the path specified in clusterReplacer.java");
        }
        
        db.insertConvertedTweet( tweet, MY_TYPE, newText );

        return newText;

    }

}
