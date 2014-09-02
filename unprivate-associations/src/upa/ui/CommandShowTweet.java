package upa.ui;

import java.io.*;
import upa.*;
import java.util.LinkedList;
import java.sql.SQLException;

/**
 * Implements the showtweet command which is a set of operations to display 
 * tweet information.
 *
 * @author Jonathan Walsh (jdw74@drexel.edu)
 */
public class CommandShowTweet {

    public static String help( String operation ) {
        String ret = "";
        if ( operation != null ) {
            ret = "Invalid operation or arguments for: " + operation + "\n";
        }
        ret += "  showtweet id <tweetId>\tPrints a tweet with a given tweete id.\n" +
            "  showtweet user <user> [limit]\tPrints all tweets for a given user name.\n" +
            "  showtweet chain <id>\t\tPrints all tweets in a reply chain starting\n\t\t\t\twith a given tweet id.\n" +
            "  showtweet regex <regex> [limit]\tPrints all tweets which match a psql\n\t\t\t\t\tregular expression.\n" +
            "  showtweet hashtag <hashTag> [limit]\tPrints all tweets which contain a given\n\t\t\t\t\t hash tag (ex #potatos).\n" +
            "  showtweet refuser <refUser> [limit]\tPrints all tweets which contain a\n\t\t\t\t\treference to another user (ex @stevie).\n";
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

        // for showtweet, all operations have at least one argument
        if ( args == null || args.length < 1 ) return help(operation);

        if ( operation.equals( "id" ) ) {
            UPTweet t;
            try {
                t = ui.db.queryTweetById( Long.parseLong( args[0] ) );
            } catch ( SQLException sqle ) {
                ret = "Unable to find tweet: " + sqle.getMessage();
                return ret;
            }
            if ( t == null ) {
                ret = "Unable to find tweet: Not found for ID";
            } else {
                ret = t.toString();
            }

        } else if ( operation.equals( "user" ) ) {
            LinkedList<UPTweet> list = new LinkedList<UPTweet>();
            try {
                if ( args.length == 1 ) {
                    list = ui.db.queryTweetsForUser( args[0] );
                } else if ( args.length == 2 ) {
                    int limit = Integer.parseInt( args[1] );
                    list = ui.db.queryTweetsForUser( args[0],
                                                     limit );
                }
            } catch ( SQLException sqle ) {
                ret = "Unable to find tweets for user: " + sqle.getMessage();
                return ret;
            }

            if ( list == null ) {
                ret = "Unable to find tweets for user";
                return ret;
            }

            for ( UPTweet t : list ) {
                ret += t.toString() + "\n";
            }

        } else if ( operation.equals( "chain" ) ) {
            ret = "TODO: NOTIMPL";
        } else if ( operation.equals( "regex" ) ) {
            LinkedList<UPTweet> list = new LinkedList<UPTweet>();
            try {
                if ( args.length == 1 ) {
                    list = ui.db.queryTweetsByRegex( args[0] );
                } else if ( args.length == 2 ) {
                    int limit = Integer.parseInt( args[1] );
                    list = ui.db.queryTweetsByRegex( args[0],
                                                     limit );
                }
            } catch ( SQLException sqle ) {
                ret = "Unable to find tweets for RegEx: " + sqle.getMessage();
                return ret;
            }

            if ( list == null ) {
                ret = "Unable to find tweets for RegEx";
                return ret;
            }

            for ( UPTweet t : list ) {
                ret += t.toString() + "\n";
            }

        } else if ( operation.equals( "hashtag" ) ) {

            LinkedList<UPTweet> list = new LinkedList<UPTweet>();
            try {
                if ( args.length == 1 ) {
                    list = ui.db.queryTweetsForHashTag( args[0] );
                } else if ( args.length == 2 ) {
                    int limit = Integer.parseInt( args[1] );
                    list = ui.db.queryTweetsForHashTag( args[0],
                                                        limit );
                }
            } catch ( SQLException sqle ) {
                ret = "Unable to find tweets for HashTag: " + sqle.getMessage();
                return ret;
            }

            if ( list == null ) {
                ret = "Unable to find tweets for HashTag";
                return ret;
            }

            for ( UPTweet t : list ) {
                ret += t.toString() + "\n";
            }

        } else if ( operation.equals( "refuser" ) ) {

            LinkedList<UPTweet> list = new LinkedList<UPTweet>();
            try {
                UPUser u = ui.db.queryUserByHandle( args[0] );
                
                if ( u == null ) {
                    ret = "Unknown user, cannot find mention tweets";
                    return ret;
                }

                if ( args.length == 1 ) {
                    list = ui.db.queryTweetsForMentionedUser( u );
                } else if ( args.length == 2 ) {
                    int limit = Integer.parseInt( args[1] );
                    list = ui.db.queryTweetsForMentionedUser( u, limit );
                }
            } catch ( SQLException sqle ) {
                ret = "Unable to find tweets for MentionedUser: " + sqle.getMessage();
                return ret;
            }

            if ( list == null ) {
                ret = "Unable to find tweets for MentionedUser";
                return ret;
            }

            for ( UPTweet t : list ) {
                ret += t.toString() + "\n";
            }

        } else { 
            return help(operation);
        }
    
        return ret;
    }


}