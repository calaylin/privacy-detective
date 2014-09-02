package upa;

import java.util.LinkedList;

import java.io.PrintWriter;
import java.io.BufferedWriter;

import twitter4j.ResponseList;
import twitter4j.PagableResponseList;
import twitter4j.Paging;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

import java.sql.SQLException;

/**
 * Program to generate files containing user tweets.
 *
 * @author Jonathan Walsh (jdw74@drexel.edu)
 */
public class WriteTweetsToFiles {
    
    public static final long MAX_FILES_TO_WRITE = 50000;
    private static final String SEPARATOR = "\n";

    public static void main( String argv[] ) throws Exception {

        if ( argv.length != 1 && argv.length != 2 ) {
            System.err.println( "USAGE: WriteTweetsToFiles <out_dir> [userhandle]" );
            System.exit(1);
        }
        String base = argv[0] + "/";

        // Set stuff up
        TwitterDatabase db = new TwitterDatabase();

        long count = 0;

        /* Pull in the specified user, or all of them*/
        LinkedList<UPUser> ulist;
        if ( argv.length == 2 ) {
            ulist = new LinkedList<UPUser>();
            ulist.add( db.queryUserByHandle( argv[1] ) );
            System.out.println( "Got user: " + argv[1] );
        } else {
            ulist = db.queryAllUsers();
        }

        /* Get a user and make a file for them*/
        for ( UPUser u : ulist ) {
            String fName = base + u.getHandle();
            BufferedWriter out = new BufferedWriter( new PrintWriter( fName ) );
            
            /* Write out all of the tweets */
            LinkedList<UPTweet>  tlist = db.queryTweetsForUser( u );
            for ( UPTweet t : tlist ) {

                out.write( t.getTweet().replace("\n"," ") + SEPARATOR );

                count++;
                if ( count % (MAX_FILES_TO_WRITE / 10) == 0 ) {
                    System.out.println( count + "/" + MAX_FILES_TO_WRITE );
                }
                if ( count > MAX_FILES_TO_WRITE ) {
                    System.out.println( "Bailing out at " + count );
                    db.close();
                    System.exit(0);
                }
            } 

            out.close();
        }
        
        db.close();
    }

}
