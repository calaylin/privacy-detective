package upa.ui;

import upa.*;

import java.io.*;
import java.util.LinkedList;
import java.sql.SQLException;

/**
 * Implements the getfollowers command which is a set of operations to pull
 * follower information from the Twitter service.
 *
 * @author Jonathan Walsh (jdw74@drexel.edu)
 */
public class CommandGetFollowers {

    public static String help( String operation ) {
        String ret = "";
        if ( operation != null ) {
            ret = "Invalid operation or arguments for: " + operation + "\n";
        }
        ret += "  getfollowers id <userid> [limit]\tGets followers for a user id\n  getfollowers user <handle> [limit]\tGets followers for a user handle.\n";

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

        // for getfollowers, all operations have at least one argument
        if ( args == null || args.length < 1 ) return help(operation);

        if ( operation.equals( "id" ) ) {
            long userId = Long.parseLong( args[0] );
            int limit = TwitterService.NO_LIMIT;
            if ( args.length == 2 ) {
                limit = Integer.parseInt( args[1] );
            }
            
            String handle = "UNKNOWN";
            try {
                UPUser base = ui.twitterService.getUserById( userId );
                handle = base.getHandle();
                LinkedList<UPUser> followers = ui.twitterService.getFollowers( base, limit );
                ret = "Added " + followers.size() + " followers for " + handle;
            } catch ( Exception e ) {
                ret = "Failed to get followers for user: " + handle;
                if ( e != null ) ret += e.getMessage();
            }

        } else if ( operation.equals( "user" ) ) {
            String handle = args[0];
            int limit = TwitterService.NO_LIMIT;
            if ( args.length == 2 ) {
                limit = Integer.parseInt( args[1] );
            }
            
            try {
                UPUser base = ui.twitterService.getUserByHandle( handle );
                LinkedList<UPUser> followers = ui.twitterService.getFollowers( base, limit );
                ret = "Added " + followers.size() + " followers for " + handle;
            } catch ( Exception e ) {
                ret = "Failed to get followers for user: " + handle;
                if ( e != null ) ret += e.getMessage();
            }
            
        } else { 
            return help(operation);
        }
    
        return ret;
    }


}