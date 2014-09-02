package upa;

import java.util.LinkedList;
import java.sql.SQLException;

import twitter4j.TwitterException;


/**
 * Runs the data collection routines, filling the database given a seed user. 
 * The program will attempt to get "tweet chains" which are sequences of 
 * replies between users.
 *
 * @author Jonathan Walsh (jdw74@drexel.edu)
 */
public class TwitterFiller {

    private static TwitterService twitterService;

    private static LinkedList<UPUser> getSomeUsers( String seedHandle, int limit ) {
        /*First we get the seed user*/
        UPUser seed = null;
        try {
            seed = twitterService.getUserByHandle( seedHandle );
            if ( seed == null ) {
                System.out.println( "Failed to find seed user from Twitter" );
                System.exit(2);
            }
        } catch ( Exception e ) {
            System.out.println( "Exception in finding seed user from Twitter" );
            System.out.println( e.getMessage() );
            System.exit(2);
        }

        /*Next we get followers of the seed user*/
        LinkedList<UPUser> ret = twitterService.getFollowers( seed, limit );
        ret.push( seed );

        return ret;
    }

    /**
     * Handle a tweet, getting any referenced users and reply tweets
     *
     * @param tweet - The tweet to handle
     * @return the total number of tweets added from this function.
     */
    private static int handleTweet( UPTweet tweet ) {
        int ret = 0;
        
        /*Check to see if this tweet is replying to a user*/
        long replyToUser = tweet.getReplyToUserId();
        if ( replyToUser != UPTweet.NO_REPLY_TO_USER ) {
            try {
                twitterService.getUserById( replyToUser );
            } catch ( Exception e ) {
                System.out.println( "Exception getting user " + replyToUser + ":" + e.getMessage() );
            }
        }
        
        /*Check to see if this tweet is a reply to another tweet*/
        long replyToTweet = tweet.getReplyToStatusId();
        if ( replyToTweet != UPTweet.NO_REPLY_TO_TWEET ) {
            try {
                UPTweet t = twitterService.getTweetById( replyToTweet );
                ret = handleTweet( t ) + 1;
            } catch ( Exception e ) {
                System.out.println( "Exception getting tweet " + replyToTweet + ":" + e.getMessage() );
            }
        }

        return ret;
    }

    /**
     * Pull down tweets for a list of users
     * 
     * @param users - The list of users to pull tweets for
     * @return The total number of added tweets
     */
    private static int getSomeTweets( LinkedList<UPUser> users ) {
        int tweetCount = 0;

        for ( UPUser user : users ) {
            String handle = user.getHandle();
            System.out.println( "Getting tweets for: " + handle );

            try {
                LinkedList<UPTweet> tList = twitterService.getUserTimeline(handle);
                tweetCount += tList.size();
            } catch ( Exception e ) {
                System.out.println( "Failed to get user timeline for user: " + user.getHandle() + " " + e.getMessage() );
            }
        }

        return tweetCount;
    }

    /**
     * Main function
     */
    public static void main( String argv[] ) {
        if ( argv.length != 2 ) {
              System.out.println( "ERROR: Must provide seed user list and limit!" );
            System.out.println( "USAGE: TwitterFiller <seedUser> <userLimit>" );
            System.exit(1);
        }

        int limit = Integer.parseInt(argv[1]);
        if ( limit <= 0 ) {
            System.out.println( "Limit must be greater than 0!" );
            System.exit(1);
        }
        String handle = argv[0];

        try {
            twitterService = new TwitterService();
        } catch ( SQLException sqle ) {
            System.out.println( "Failed to open DB connection" );
            System.exit(1);
        }

        /*Part 1: Get some people*/
        LinkedList<UPUser> added = getSomeUsers( handle, limit );

        /*Part 2 get some tweets*/
        int totalTweets = getSomeTweets( added );
        System.out.println( "Inserted " + totalTweets + " total tweets!" );
    }

}