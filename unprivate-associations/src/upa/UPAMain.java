package upa;

import java.util.LinkedList;

import java.sql.SQLException;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * The main program of UPA which will run the entire tool chain!
 *
 * @author Jonathan Walsh (jdw74@drexel.edu)
 */
public class UPAMain {

    /**
     * The main function!
     *
     * @param argv - Arguments to the program
     */
    public static void main( String argv[] ) {

        if ( argv.length != 2 ) {
            System.out.println( "USAGE: UPAMain <handle> <out_dir>" );
            System.exit(1);
        }

        String handle = argv[0];
        String outdir = argv[1];

        /*List of users to calculate later*/
        LinkedList<UPUser> usersToCalc = new LinkedList<UPUser>();

        /*Set up the database and service objects*/
        TwitterDatabase db = null;
        TwitterService ts = null;
        LangTweetConverter converter = null;
        try {
            db = new TwitterDatabase( TwitterDatabase.DEFAULT_CONFIG_PATH );
            ts = new TwitterService( db, TwitterService.DEFAULT_CONFIG_PATH );
            converter = new LangTweetConverter( db );
        } catch ( SQLException sqle ) {
            System.out.println( "DB Connection error!" + sqle.getMessage() );
            System.exit(2);
        } catch ( IOException ioe ) {
            System.out.println( "Failed to open configuration file: " + ioe.getMessage() );
            System.exit(2);
        }
        UserRiskCalculator userCalc = new UserRiskCalculator( db );

        /*Turn on database debugging*/
        db.enableDebug();

        /* Check to see if the user is already in the DB, if not, get them */
        System.out.println( "STATUS: Getting user data" );
        UPUser base = null;
        try {
            base = db.queryUserByHandle( handle );
            if ( base == null ) { /*Not in the DB, we need to get them*/
                base = ts.getUserByHandle( handle );
                if ( base == null ) {
                    System.out.println( "Failed to find user in Twitter Service" );
                    db.close();
                    System.exit(3);
                }
            }
        } catch ( SQLException sqle ) {
            System.out.println( "DB Error getting user" );
            System.exit(3);
        }

        /*Get tweets for this user*/
        System.out.println( "STATUS: Getting tweets for base user" );
        LinkedList<UPTweet> baseTweets = null;
        try {
            baseTweets = ts.getUserTimeline( handle );
        } catch ( SQLException sqle ) {
            System.out.println( "Failed getting base user timeline\n" );
            System.out.println( sqle.getMessage() );
            System.exit(4);
        }
        if ( baseTweets == null ) {
            System.out.println( "Failed getting base user timeline (null list)\n" );
            System.exit(4);
        }


        /* Do any conversions for this user text */
        System.out.println( "STATUS: Converting and scoring base user tweets" );
        for ( UPTweet tweet : baseTweets ) {
            try {
                converter.convertTweet( tweet );
            } catch ( SQLException sqle ) {
                System.err.println( "Failed to convert tweet text\n" );
                System.err.println( sqle.getMessage() );
            }
        }
                

        /* Get users that this user is following */  
        System.out.println( "STATUS: Getting users being followed by base" );
        LinkedList<UPUser> followed = null;
        followed = ts.getFollowing( base, 1000 );
        if ( followed == null ) {
            System.out.println( "Failed getting followed users.\n" );
            System.exit(5);
        }


        /*Get tweets for followed users*/
        System.out.println( "STATUS: Getting tweets for followed users" );
        for ( UPUser f : followed ) {
            usersToCalc.add( f );

            System.out.println( "STATUS: Getting tweets for: " + 
                                f.getHandle() );
            LinkedList<UPTweet> l = null;
            try {
                l = ts.getUserTimeline( f.getHandle(), 1000 );
            } catch ( SQLException sqle ) {
                System.out.println( "Failed getting timeline for user: " +
                                    f.getHandle() + " continuing" );
                System.out.println( sqle.getMessage() );
                continue;
            }
            if ( l == null ) {
                System.out.println( "Failed getting timeline for user: " +
                                    f.getHandle() + " continuing" );
                continue;
            }

            /* Do any conversions for this user text */
            for ( UPTweet tweet : l ) {
                try {
                    converter.convertTweet( tweet );
                } catch ( SQLException sqle ) {
                    System.err.println( "Failed to convert tweet text\n" );
                    System.err.println( sqle.getMessage() );
                }
            }
        } // for each user : get timeline tweets

        
        /*Calculate the score for all users, and store in the DB for later*/
        // base user
        int score = userCalc.getUserRiskScore( base );
        try {
            db.insertPersonPrivacyScore( base, score );
        } catch ( SQLException sqle ) {
            System.out.println( "Failed to insert user base score: " +
                                sqle.getMessage() );
            System.exit(5);
        }

        // all other users
        for ( UPUser u : usersToCalc ) {
            try {
                //calculate the user score
                score = userCalc.getUserRiskScore( u );
                db.insertPersonPrivacyScore( base, score );
            } catch ( SQLException sqle ) {
                System.out.println( "Failed to insert user score: " +
                                    sqle.getMessage() );
                System.exit(5);
            }
        }

        /*Generate the graph file!*/
        System.out.println( "STATUS: Generating graph file" );
        String contents = null;
        try {
            contents = GraphGenerator.getDotFormatGraph( handle, db );
        } catch ( SQLException sqle ) {
            System.out.println( "Failed generating graph!" );
            System.out.println( sqle.getMessage() );
            System.exit(5);
        }
        
        System.out.println( "STATUS: Writing graph file" );
        try {
            BufferedWriter out = new BufferedWriter( new FileWriter( outdir + "/" + handle + ".dot" ) );
            out.write( contents );
            out.close();
        } catch ( IOException ioe ) {
            System.out.println( "Failed to write dot file." );
            System.exit(6);
        }

        System.out.println( "STATUS: Done!" );

    }//main

}