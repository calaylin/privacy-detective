package upa;

import java.sql.SQLException;

/**
 * Abstract class definition for converting a tweet
 *
 * @author Jonathan Walsh (jdw74@drexel.edu)
 */
public abstract class TweetConverter {

    /** Database reference */
    protected TwitterDatabase db;

    /** Type storage for implementing classes */
    private String type;

    /**
     * Make a new TweetConverter
     *
     * @param db - The TwitterDatabase
     */
    public TweetConverter( TwitterDatabase db, String convType ) {
        if ( db == null ) {
            throw new NullPointerException("Got bad db ref to TweetConverter");
        }
        this.db = db;
        this.type = convType;
    }

    /**
     * Convert a given tweet to another format.  This function may also 
     *  assign topics to the tweet.
     *
     * @param tweet - The tweet to convert
     * @return The converted contents of the tweet
     */
    public abstract String convertTweet( UPTweet tweet ) throws SQLException;

    /**
     * Get the string name of this converter type
     *
     * @return The converter type name
     */
    public String getConvType() {
        return type;
    }
}
