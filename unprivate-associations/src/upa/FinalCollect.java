package upa;

import java.util.LinkedList;

import java.sql.SQLException;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * The main program to do a special collect for the last weekend
 *
 * @author Jonathan Walsh (jdw74@drexel.edu)
 */
public class FinalCollect {

    /**
     * The main function!
     *
     * @param argv - Arguments to the program
     */
    public static void main( String argv[] ) {

        String seedHandle = "sbaickercsn";
        int limit = 200;

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

        // get base user
        System.err.println( "\n++++ GETTING BASE USER: " + seedHandle + " ++++" );
        UPUser base = null;
        try {
            base = ts.getUserByHandle( seedHandle );
        } catch ( Exception ex ) {
            System.err.println( "Failed, bailing out" );
            System.err.println( ex.getMessage() );
            System.exit(1);
        }

        // get base user timeline
        System.err.println( "\n++++ GETTING BASE USER TIMELINE ++++" );
        try {
            LinkedList<UPTweet> list = ts.getUserTimeline( seedHandle );
            System.err.println( "++++ " + list.size() + " tweets" );
        } catch ( Exception ex ) {
            System.err.println( "Failed, bailing out" );
            System.err.println( ex.getMessage() );
            System.exit(1);
        }

        // Get followers of base user
        System.err.println( "\n++++ GETTING BASE USER FOLLOWERS ++++" );
        LinkedList<UPUser> followers = null;
        try {
            followers = ts.getFollowing( base, limit );
            System.err.println( "++++ " + followers.size() + " followers" );
        } catch ( Exception ex ) {
            System.err.println( "Failed, bailing out" );
            System.err.println( ex.getMessage() );
            System.exit(1);
        }

        for ( UPUser u : followers ) {
            if ( u.getVerified() ) {
                System.err.println( "++++ Skipping verified user: " + u.getHandle() + " ++++" );
                continue;
            }
            try {
                if ( db.queryNumTweetsForUser(u) > 10 ) {
                    System.err.println( "++++ Skipping user with downloaded timeline: " + u.getHandle() + " ++++" );
                    continue;
                }
            } catch ( SQLException sqle ) {
                System.err.println("Failed to get count of tweets for user: " + u.getHandle() + ", downloading tweets for them" );
            }
                
            String fHandle = null;
            try {
                fHandle = u.getHandle();
            } catch ( Exception e ) {
                System.err.println("Failed to get user handle for a follower" );
                continue;
            }
              
            System.err.println( "\n++++ GETTING BASE USER FOLLOWER TIMELINE: " + fHandle + " ++++" );
        
            try {
                ts.getUserTimeline( fHandle );
            } catch ( Exception e ) {
                System.err.println("Failed to get timeline for a follower: " + 
                                   fHandle );
                continue;
            }

            System.err.println( "\n++++ GETTING BASE USER FOLLOWER FOLLOWERS: " + fHandle + " ++++" );

            LinkedList<UPUser> fFollowers;
            try {
                fFollowers  = ts.getFollowing( u, limit/4 );
                System.err.println( "++++ " + fFollowers.size()+ " followers" );
            } catch ( Exception e ) {
                System.err.println("Failed to get followers for a follower: " + 
                                   fHandle );
                continue;
            }
            
            int count = 0;
            for ( UPUser fU : fFollowers ) {
                count++;
                if ( count > limit/4 ) { continue; }

                try {
                    if ( db.queryNumTweetsForUser(fU) > 10 ) {
                        System.err.println( "++++ Skipping follower follower with downloaded timeline: " + fU.getHandle() + " ++++" );
                        continue;
                    }
                } catch ( SQLException sqle ) {
                    System.err.println("Failed to get count of tweets for user: " + fU.getHandle() + ", downloading for them" );
                }

                String fUHandle = fU.getHandle();
                System.err.println( "\n++++ GETTING FOLLOWER FOLLOWER TIMELINE: " + fUHandle );
                try {
                    LinkedList<UPTweet> list = ts.getUserTimeline( fUHandle );
                    System.err.println( "++++ " + list.size() + " tweets" );
                } catch ( Exception e ) {
                    System.err.println("Failed to get timeline for follower follower: " + fUHandle );
                    continue;
                }
            }
            
        }


    }

}