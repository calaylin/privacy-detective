package upa.ui;

import upa.*;

import java.io.*;
import java.util.LinkedList;
import java.sql.SQLException;

/**
 * Implements the gettweet command which is a set of operations to pull tweet 
 * information from the Twitter service.
 *
 * @author Jonathan Walsh (jdw74@drexel.edu)
 */
public class CommandGetTweet {

    public static String help( String operation ) {
        String ret = "";
        if ( operation != null ) {
            ret = "Invalid operation or arguments for: " + operation + "\n";
        }
        ret += "  gettweet id <tweetId>\t\tGets and stores a tweet for a given tweet id.\n  gettweet chain <tweetId>\tGets and stores tweets in a reply chain,\n\t\t\t\toriginating at an id.\n  gettweet user <handle> [limit]\tGets the timeline for a user by handle.\n  gettweet userid <id> [limit]\t\tGets the timeline for a user by id.\n";

        return ret;
    }

    /**
     * Run the actual command.
     */
    public static String run( String operation, String[] args ) {
        String ret = "";

        if ( operation == null ) {
            return help(null);
        } 

        // for getuser, all operations have at least one argument
        if ( args == null || args.length < 1 ) return help(operation);

        if ( operation.equals( "id" ) ) {

            long tweetId = Long.parseLong( args[0] );

            // see if the tweet is in the database already
            UPTweet t;
            try {
                t = ui.db.queryTweetById( tweetId );
            } catch ( SQLException sqle ) {
                ret = "Some sort of DB error querying by id: " + sqle.getMessage();
                return ret;
            }
            if ( t != null ) {
                return "Tweet already exists in database: " + tweetId;
            }

            //Get the tweet from the Twitter service
            try {
                t = ui.twitterService.getTweetById( tweetId );
            } catch ( Exception e ) {
                ret = "Unable to find tweet for id: " + tweetId +
                    "\n" + e.getMessage();
            }

            if ( t == null ) {
                ret = "Unable to find tweet for id (null): " + tweetId;
            } else {
                ret = t.toString();
            }

        } else if ( operation.equals( "chain" ) ) {
            ret = "TODO NOTIMPL";

        } else if ( operation.equals( "user" ) ) {
            String handle = args[0];
            int limit = TwitterService.NO_LIMIT;
            if ( args.length == 2 ) {
                limit = Integer.parseInt( args[1] );
            }

            // Get the user's tweets and put them in the DB
            try {
                LinkedList<UPTweet> timeline = ui.twitterService.getUserTimeline( handle, limit );
                ret = "Added " + timeline.size() + " tweets";
            } catch ( Exception e ) {
                ret = "Failed to get tweets for user: " + handle;
                if ( e != null ) ret += e.getMessage();
            }
            

        } else if ( operation.equals( "userid" ) ) {
            long userid = Long.parseLong( args[0] );
            int limit = TwitterService.NO_LIMIT;
            if ( args.length == 2 ) {
                limit = Integer.parseInt( args[1] );
            }

            String handle = "UNKNOWN";
            // Get the user's tweets and put them in the DB
            try {
                UPUser base = ui.twitterService.getUserById( userid );
                handle = base.getHandle();

                LinkedList<UPTweet> timeline = ui.twitterService.getUserTimeline( handle, limit );
                ret = "Added " + timeline.size() + " tweets";

            } catch ( Exception e ) {
                ret = "Failed to get tweets for user: " + handle;
                if ( e != null ) ret += e.getMessage();
            }
            
        } else { 
            return help(operation);
        }
    
        return ret;
    }


}