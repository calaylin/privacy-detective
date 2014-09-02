package upa.ui;

import upa.*;

import java.io.*;
import java.util.LinkedList;
import java.sql.SQLException;

/**
 * Implements the getfollowing command which is a set of operations to pull
 * users being followed from the Twitter service.
 *
 * @author Jonathan Walsh (jdw74@drexel.edu)
 */
public class CommandGetFollowing {

    public static String help( String operation ) {
        String ret = "";
        if ( operation != null ) {
            ret = "Invalid operation or arguments for: " + operation + "\n";
        }
        ret += "  getfollowing id <userid> [limit]\tGets followed users for a user id\n  getfollowing user <handle> [limit]\tGets followed users for a user handle.\n";

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

        // for getfollowing, all operations have at least one argument
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
                LinkedList<UPUser> followed = ui.twitterService.getFollowing( base, limit );
                ret = "Added " + followed.size() + " followed users for " + handle;
            } catch ( Exception e ) {
                ret = "Failed to get followed users for user: " + handle;
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
                LinkedList<UPUser> followed = ui.twitterService.getFollowing( base, limit );
                ret = "Added " + followed.size() + " followed users for " + handle;
            } catch ( Exception e ) {
                ret = "Failed to get followed users for user: " + handle;
                if ( e != null ) ret += e.getMessage();
            }
            
        } else { 
            return help(operation);
        }
    
        return ret;
    }


}