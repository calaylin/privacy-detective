package upa;

import java.util.LinkedList;
import java.sql.SQLException;

/**
 * Calculate the risk present by a given user
 *
 * @author Jonathan Walsh (jdw74@drexel.edu)
 */
public class UserRiskCalculator {

    /** Local reference to the database object */
    private TwitterDatabase db;

    /**
     * Create the UserRiskCalculator object, loading any models.
     */
    public UserRiskCalculator( TwitterDatabase db ) {
        this.db = db;

        // TODO: Aylin code here to load the model(s)
    }

    /**
     * Return a score for private information disclosure for a given user.
     *
     * @param u - The user to calculate the disclosure score
     * @return The score for this user
     */
    public int getUserRiskScore( UPUser u ) {
        int score = 0;
        int totalTweets; 

        try {
            LinkedList<String> tweets = db.queryTweetStringsForUser( u );
            totalTweets = tweets.size();
            
        } catch ( SQLException sqle ) {
            return -1;
        }

        //TODO Aylin: Change this code to whatever you are going to do, and 
        //   return the score as an integer.  The calling code will put this 
        //   number into the database.
        



        
        return score;
    }

}
