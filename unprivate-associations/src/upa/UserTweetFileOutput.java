package upa;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.sql.SQLException;

/**
 * Code to write all the tweets of a user in the DB into a file.
 *
 * @author Jonathan Walsh (jdw74@drexel.edu)
 */
public class UserTweetFileOutput {

    /**
     * Write all tweets stored for a user to a file and the ids to another file.
     *
     * @param db - The DB connection
     * @param user - The user to write tweets for
     * @param path - The directory to put both files in
     * @throws SQLException - DB errors
     * @throws IOException - File writing errors
     */
    public static void writeUserTweetFile( TwitterDatabase db,
                                           UPUser user,
                                           String path ) 
        throws SQLException,IOException {

        BufferedWriter tweetOut = new BufferedWriter( new FileWriter( path + "/" + user.getHandle() + ".tweets.txt" ) );
        BufferedWriter tweetIdOut = new BufferedWriter( new FileWriter( path + "/" + user.getHandle() + ".tweetids.txt" ) );
        
        LinkedList<UPTweet> tweetList = db.queryTweetsForUser( user );
        
        for ( UPTweet tweet : tweetList ) {
            tweetOut.write( tweet.getTweet() + "\n" );
            tweetIdOut.write( tweet.getTweetId() + "\n" );
        }
        
        tweetOut.close();
        tweetIdOut.close();
        
    }

    /**
     * Write all tweets stored for a user to a file and the ids to another 
     * file, for a conversion type.
     *
     * @param db - The DB connection
     * @param user - The user to write tweets for
     * @param path - The directory to put both files in
     * @param conversionType - The type of tweet conversion to query for
     * @throws SQLException - DB errors
     * @throws IOException - File writing errors
     */
    public static void writeUserConvTweetFile( TwitterDatabase db,
                                               UPUser user,
                                               String path,
                                               String conversionType )
        throws SQLException,IOException {

        /*Gets the ID (and makes sure it exists since an excep will throw)*/
        int convTypeId = db.queryConversionTypeId( conversionType );

        BufferedWriter tweetOut = new BufferedWriter( new FileWriter( path + "/" + user.getHandle() + "."+conversionType+".tweets.txt" ) );
        BufferedWriter tweetIdOut = new BufferedWriter( new FileWriter( path + "/" + user.getHandle() + "."+conversionType+".tweetids.txt" ) );
        
        LinkedList<UPTweet> tweetList = db.queryTweetsForUser( user );
        
        for ( UPTweet tweet : tweetList ) {
            String convTweetText = db.queryConvertedTweet( tweet, convTypeId );
            if ( convTweetText == null ) continue;

            tweetOut.write( convTweetText + "\n" );
            tweetIdOut.write( tweet.getTweetId() + "\n" );
        }
        
        tweetOut.close();
        tweetIdOut.close();
    }

}